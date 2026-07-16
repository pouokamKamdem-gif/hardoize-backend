package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MultiModeService {

    private final GroupeRepository         groupeRepo;
    private final MembreGroupeRepository   membreRepo;
    private final PermissionMembreRepository permissionRepo;
    private final OperationMultiRepository operationRepo;
    private final VenteService             venteService;
    private final ObjectMapper             objectMapper;

    // ── Passer en mode multi ───────────────────────────────────
    @Transactional
    public Map<String, Object> passerEnModeMulti(Long groupeId) {
        Groupe groupe = groupeRepo.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        groupe.setMode("multi");
        groupeRepo.save(groupe);

        Map<String, Object> result = new HashMap<>();
        result.put("groupeId", groupeId);
        result.put("mode",     "multi");
        result.put("message",  "Mode multi activé");
        return result;
    }

    // ── Passer en mode solo ────────────────────────────────────
    @Transactional
    public Map<String, Object> passerEnModeSolo(Long groupeId) {
        Groupe groupe = groupeRepo.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        groupe.setMode("solo");
        groupeRepo.save(groupe);

        Map<String, Object> result = new HashMap<>();
        result.put("groupeId", groupeId);
        result.put("mode",     "solo");
        return result;
    }

    // ── Vérifier le mode actuel ────────────────────────────────
    public String getMode(Long groupeId) {
        return groupeRepo.findById(groupeId)
                .map(Groupe::getMode)
                .orElse("solo");
    }

    // ── Rejoindre un groupe via QR code ───────────────────────
    @Transactional
    public Map<String, Object> rejoindreGroupe(
            String codeQR, String telephone,
            String nomAffiche, String bailHeure) {

        Groupe groupe = groupeRepo.findByCodeQR(codeQR)
                .orElseThrow(() ->
                        new RuntimeException("Code QR invalide ou expiré"));

        // Vérifier si déjà membre
        Optional<MembreGroupe> existant = membreRepo
                .findByGroupeIdAndTelephone(groupe.getId(), telephone);

        MembreGroupe membre;
        if (existant.isPresent()) {
            membre = existant.get();
            membre.setEstConnecte(true);
            membre.setNomAffiche(nomAffiche);
            membreRepo.save(membre);
        } else {
            membre = MembreGroupe.builder()
                    .groupe(groupe)
                    .nomAffiche(nomAffiche != null
                            ? nomAffiche : telephone)
                    .telephone(telephone)
                    .role("vendeur")
                    .bailHeure(bailHeure != null
                            ? bailHeure : groupe.getHeureFermeture())
                    .estConnecte(true)
                    .connexionPermanente(false)
                    .build();
            membre = membreRepo.save(membre);

            // Permissions par défaut (vente uniquement)
            PermissionMembre perms = PermissionMembre.builder()
                    .membre(membre)
                    .peutVendre(true)
                    .peutVoirDettes(false)
                    .peutGererStock(false)
                    .peutVoirStats(false)
                    .peutGererClients(false)
                    .peutVoirHistorique(false)
                    .build();
            permissionRepo.save(perms);
        }

        // Passer en mode multi si nécessaire
        long nbMembres = membreRepo.countByGroupeId(groupe.getId());
        if (nbMembres > 1 && "solo".equals(groupe.getMode())) {
            groupe.setMode("multi");
            groupeRepo.save(groupe);
        }

        // Charger les permissions
        PermissionMembre perms = permissionRepo
                .findByMembreId(membre.getId())
                .orElse(null);

        Map<String, Object> result = new HashMap<>();
        result.put("membreId",    membre.getId());
        result.put("groupeId",    groupe.getId());
        result.put("groupeNom",   groupe.getNom());
        result.put("mode",        groupe.getMode());
        result.put("bailHeure",   membre.getBailHeure());
        result.put("permissions", buildPermissionsDto(perms));
        return result;
    }

    // ── Sync polling (GET toutes les 30s) ─────────────────────
    public Map<String, Object> getSyncData(
            Long groupeId, String depuis) {

        Map<String, Object> data = new HashMap<>();

        // Timestamp depuis lequel on veut les nouvelles données
        LocalDateTime depuisTime = depuis != null
                ? LocalDateTime.parse(depuis)
                : LocalDateTime.now().minusMinutes(1);

        // Nouvelles ventes depuis le dernier poll
        // (simplifié — en production, filtrer par updatedAt > depuisTime)
        data.put("timestamp",   LocalDateTime.now().toString());
        data.put("groupeId",    groupeId);
        data.put("mode",        getMode(groupeId));
        data.put("nbEnAttente",
                operationRepo.countByGroupeIdAndStatut(
                        groupeId, "en_attente"));

        return data;
    }

    // ── Traiter une opération multi (FIFO) ────────────────────
    @Transactional
    public Map<String, Object> traiterOperation(
            Map<String, Object> payload,
            String telephone,
            Long groupeId) {

        String type = payload.get("type") != null
                ? payload.get("type").toString() : "";

        // Vérifier les permissions du membre
        MembreGroupe membre = membreRepo
                .findByGroupeIdAndTelephone(groupeId, telephone)
                .orElseThrow(() ->
                        new RuntimeException("Membre introuvable dans ce groupe"));

        verifierPermission(membre.getId(), type);

        // Traiter selon le type
        Map<String, Object> result = new HashMap<>();

        switch (type) {
            case "vente":
                result = traiterVente(payload, telephone);
                break;
            case "mouvement_stock":
                result = traiterMouvementStock(payload, telephone);
                break;
            default:
                throw new RuntimeException("Type d'opération inconnu: " + type);
        }

        result.put("type", type);
        return result;
    }

    // ── Vérifier permission selon type d'opération ────────────
    private void verifierPermission(Long membreId, String type) {
        PermissionMembre perms = permissionRepo
                .findByMembreId(membreId)
                .orElseThrow(() ->
                        new RuntimeException("Permissions introuvables"));

        boolean autorise = switch (type) {
            case "vente"          -> perms.getPeutVendre();
            case "mouvement_stock"-> perms.getPeutGererStock();
            case "dette"          -> perms.getPeutVoirDettes();
            case "client"         -> perms.getPeutGererClients();
            default               -> false;
        };

        if (!autorise) {
            throw new RuntimeException(
                    "Permission refusée pour l'opération: " + type
            );
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> traiterVente(
            Map<String, Object> payload, String telephone) {
        try {
            com.digneequipe.hardoize.dto.request.VenteRequest req =
                    objectMapper.convertValue(
                            payload.get("data"),
                            com.digneequipe.hardoize.dto.request.VenteRequest.class
                    );
            return venteService.enregistrer(req, telephone);
        } catch (Exception e) {
            throw new RuntimeException("Erreur traitement vente: " + e.getMessage());
        }
    }

    private Map<String, Object> traiterMouvementStock(
            Map<String, Object> payload, String telephone) {
        // Déléguer au service stock
        Map<String, Object> result = new HashMap<>();
        result.put("statut", "traite");
        return result;
    }

    // ── Dashboard propriétaire ────────────────────────────────
    public Map<String, Object> getDashboard(Long groupeId) {
        Map<String, Object> dashboard = new HashMap<>();

        // Membres connectés
        List<MembreGroupe> membres =
                membreRepo.findByGroupeId(groupeId);
        List<Map<String, Object>> membresDto = new ArrayList<>();

        for (MembreGroupe m : membres) {
            Map<String, Object> mDto = new HashMap<>();
            mDto.put("id",          m.getId());
            mDto.put("nomAffiche",  m.getNomAffiche());
            mDto.put("telephone",   m.getTelephone());
            mDto.put("estConnecte", m.getEstConnecte());
            mDto.put("bailHeure",   m.getBailHeure());
            mDto.put("role",        m.getRole());

            permissionRepo.findByMembreId(m.getId()).ifPresent(p -> {
                mDto.put("permissions", buildPermissionsDto(p));
            });

            membresDto.add(mDto);
        }

        dashboard.put("membres",    membresDto);
        dashboard.put("mode",       getMode(groupeId));
        dashboard.put("groupeId",   groupeId);
        dashboard.put("timestamp",  LocalDateTime.now().toString());

        return dashboard;
    }

    // ── Déconnecter un membre ─────────────────────────────────
    @Transactional
    public void deconnecterMembre(Long membreId) {
        membreRepo.findById(membreId).ifPresent(m -> {
            m.setEstConnecte(false);
            membreRepo.save(m);

            // Si plus aucun membre connecté sauf propriétaire
            // → repasser en mode solo
            long nbConnectes = membreRepo
                    .findByGroupeId(m.getGroupe().getId())
                    .stream()
                    .filter(mb -> mb.getEstConnecte()
                            && !"proprietaire".equals(mb.getRole()))
                    .count();

            if (nbConnectes == 0) {
                m.getGroupe().setMode("solo");
                groupeRepo.save(m.getGroupe());
            }
        });
    }

    // ── Modifier les permissions d'un membre ──────────────────
    @Transactional
    public Map<String, Object> modifierPermissions(
            Long membreId, Map<String, Boolean> permissions) {

        PermissionMembre perms = permissionRepo
                .findByMembreId(membreId)
                .orElseThrow(() ->
                        new RuntimeException("Permissions introuvables"));

        if (permissions.containsKey("peutVendre"))
            perms.setPeutVendre(permissions.get("peutVendre"));
        if (permissions.containsKey("peutVoirDettes"))
            perms.setPeutVoirDettes(permissions.get("peutVoirDettes"));
        if (permissions.containsKey("peutGererStock"))
            perms.setPeutGererStock(permissions.get("peutGererStock"));
        if (permissions.containsKey("peutVoirStats"))
            perms.setPeutVoirStats(permissions.get("peutVoirStats"));
        if (permissions.containsKey("peutGererClients"))
            perms.setPeutGererClients(permissions.get("peutGererClients"));
        if (permissions.containsKey("peutVoirHistorique"))
            perms.setPeutVoirHistorique(permissions.get("peutVoirHistorique"));

        permissionRepo.save(perms);

        return buildPermissionsDto(perms);
    }

    private Map<String, Object> buildPermissionsDto(PermissionMembre p) {
        if (p == null) {
            return Map.of("peutVendre", true);
        }
        Map<String, Object> dto = new HashMap<>();
        dto.put("peutVendre",         p.getPeutVendre());
        dto.put("peutVoirDettes",     p.getPeutVoirDettes());
        dto.put("peutGererStock",     p.getPeutGererStock());
        dto.put("peutVoirStats",      p.getPeutVoirStats());
        dto.put("peutGererClients",   p.getPeutGererClients());
        dto.put("peutVoirHistorique", p.getPeutVoirHistorique());
        return dto;
    }
}