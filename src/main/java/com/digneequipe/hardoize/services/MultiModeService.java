package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MultiModeService {

    private final GroupeRepository           groupeRepo;
    private final MembreGroupeRepository     membreRepo;
    private final PermissionMembreRepository permissionRepo;
    private final UtilisateurRepository      utilisateurRepo;
    private final VenteService               venteService;

    // ── Rejoindre un groupe via QR ─────────────────────────────
    @Transactional
    public Map<String, Object> rejoindreGroupe(
            String codeQR, String telephone, String nomAffiche) {

        Groupe groupe = groupeRepo.findByCodeQR(codeQR)
                .orElseThrow(() ->
                        new RuntimeException("Code QR invalide ou expiré"));

        Utilisateur user = utilisateurRepo
                .findByTelephone(telephone)
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable"));

        // Vérifier si déjà membre
        MembreGroupe membre = membreRepo
                .findByGroupeIdAndTelephone(groupe.getId(), telephone)
                .map(m -> {
                    m.setEstConnecte(true);
                    if (nomAffiche != null) m.setNomAffiche(nomAffiche);
                    return membreRepo.save(m);
                })
                .orElseGet(() -> {
                    MembreGroupe m = MembreGroupe.builder()
                            .groupe(groupe)
                            .utilisateur(user)
                            .nomAffiche(nomAffiche != null
                                    ? nomAffiche : user.getNom())
                            .telephone(telephone)
                            .role("vendeur")
                            .bailHeure(groupe.getHeureFermeture())
                            .estConnecte(true)
                            .connexionPermanente(false)
                            .build();
                    m = membreRepo.save(m);

                    // Permissions par défaut
                    PermissionMembre perms = PermissionMembre.builder()
                            .membre(m)
                            .peutVendre(true)
                            .peutVoirDettes(false)
                            .peutGererStock(false)
                            .peutVoirStats(false)
                            .peutGererClients(false)
                            .peutVoirHistorique(false)
                            .build();
                    permissionRepo.save(perms);
                    return m;
                });

        // Passer en mode multi si 2+ membres
        long nbMembres = membreRepo.countByGroupeId(groupe.getId());
        if (nbMembres > 1) {
            groupe.setMode("multi");
            groupeRepo.save(groupe);
        }

        // Charger les permissions
        PermissionMembre perms = permissionRepo
                .findByMembreId(membre.getId()).orElse(null);

        Map<String, Object> result = new HashMap<>();
        result.put("membreId",    membre.getId());
        result.put("membreUuid",  membre.getUuid());
        result.put("groupeId",    groupe.getId());
        result.put("groupeUuid",  groupe.getUuid());
        result.put("groupeNom",   groupe.getNom());
        result.put("mode",        groupe.getMode());
        result.put("bailHeure",   membre.getBailHeure());
        result.put("permissions", buildPermissionsDto(perms));
        return result;
    }

    // ── Polling sync 30s ──────────────────────────────────────
    public Map<String, Object> getSyncData(
            Long groupeId, String depuis) {
        Map<String, Object> data = new HashMap<>();
        data.put("groupeId",  groupeId);
        data.put("mode",      getMode(groupeId));
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("ok",        true);
        return data;
    }

    // ── Exécuter une opération en mode multi ─────────────────
    @Transactional
    public Map<String, Object> traiterOperation(
            Map<String, Object> payload, String telephone) {

        String type     = s(payload, "type");
        String gUuidStr = s(payload, "groupeUuid");
        if (gUuidStr == null)
            throw new RuntimeException("groupeUuid manquant");

        Groupe groupe = groupeRepo.findByUuid(gUuidStr)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier permission
        MembreGroupe membre = membreRepo
                .findByGroupeIdAndTelephone(groupe.getId(), telephone)
                .orElseThrow(() ->
                        new RuntimeException("Membre introuvable dans ce groupe"));

        verifierPermission(membre.getId(), type);

        return switch (type != null ? type : "") {
            case "vente" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> data =
                        (Map<String, Object>) payload.get("data");
                if (data == null) data = payload;
                yield venteService.enregistrerMulti(data, telephone);
            }
            default -> throw new RuntimeException(
                    "Type d'opération non supporté: " + type);
        };
    }

    // ── Dashboard propriétaire ────────────────────────────────
    public Map<String, Object> getDashboard(Long groupeId) {
        List<MembreGroupe> membres =
                membreRepo.findByGroupeId(groupeId);

        List<Map<String, Object>> membresDto = new ArrayList<>();
        for (MembreGroupe m : membres) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",          m.getId());
            dto.put("uuid",        m.getUuid());
            dto.put("nomAffiche",  m.getNomAffiche());
            dto.put("telephone",   m.getTelephone());
            dto.put("role",        m.getRole());
            dto.put("estConnecte", m.getEstConnecte());
            dto.put("bailHeure",   m.getBailHeure());
            permissionRepo.findByMembreId(m.getId())
                    .ifPresent(p -> dto.put("permissions",
                            buildPermissionsDto(p)));
            membresDto.add(dto);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("membres",   membresDto);
        dashboard.put("mode",      getMode(groupeId));
        dashboard.put("groupeId",  groupeId);
        dashboard.put("timestamp", LocalDateTime.now().toString());
        return dashboard;
    }

    // ── Déconnecter un membre ─────────────────────────────────
    @Transactional
    public void deconnecterMembre(Long membreId) {
        membreRepo.findById(membreId).ifPresent(m -> {
            m.setEstConnecte(false);
            membreRepo.save(m);

            long nbConnectes = membreRepo
                    .findByGroupeId(m.getGroupe().getId())
                    .stream()
                    .filter(mb -> mb.getEstConnecte()
                            && !"proprietaire".equals(mb.getRole()))
                    .count();

            if (nbConnectes == 0) {
                Groupe g = m.getGroupe();
                g.setMode("solo");
                groupeRepo.save(g);
            }
        });
    }

    // ── Lire les permissions d'un membre ──────────────────────
    // Ajouté : GroupesScreen.js fait un GET /permissions/membre/{id}
    // pour pré-remplir le modal avant modification ; il n'y avait
    // pas de méthode correspondante côté service (ni de route côté
    // contrôleur), donc le frontend retombait toujours sur des
    // permissions par défaut au lieu des vraies valeurs.
    public Map<String, Object> getPermissions(Long membreId) {
        PermissionMembre p = permissionRepo
                .findByMembreId(membreId)
                .orElseThrow(() ->
                        new RuntimeException("Permissions introuvables"));
        return buildPermissionsDto(p);
    }

    // ── Modifier permissions ──────────────────────────────────
    @Transactional
    public Map<String, Object> modifierPermissions(
            Long membreId, Map<String, Boolean> body) {

        PermissionMembre p = permissionRepo
                .findByMembreId(membreId)
                .orElseThrow(() ->
                        new RuntimeException("Permissions introuvables"));

        if (body.containsKey("peutVendre"))
            p.setPeutVendre(body.get("peutVendre"));
        if (body.containsKey("peutVoirDettes"))
            p.setPeutVoirDettes(body.get("peutVoirDettes"));
        if (body.containsKey("peutGererStock"))
            p.setPeutGererStock(body.get("peutGererStock"));
        if (body.containsKey("peutVoirStats"))
            p.setPeutVoirStats(body.get("peutVoirStats"));
        if (body.containsKey("peutGererClients"))
            p.setPeutGererClients(body.get("peutGererClients"));
        if (body.containsKey("peutVoirHistorique"))
            p.setPeutVoirHistorique(body.get("peutVoirHistorique"));

        permissionRepo.save(p);
        return buildPermissionsDto(p);
    }

    // ── Passer en mode multi / solo ───────────────────────────
    @Transactional
    public Map<String, Object> passerEnModeMulti(Long groupeId) {
        Groupe g = groupeRepo.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
        g.setMode("multi");
        groupeRepo.save(g);
        return Map.of("groupeId", groupeId, "mode", "multi");
    }

    @Transactional
    public Map<String, Object> passerEnModeSolo(Long groupeId) {
        Groupe g = groupeRepo.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
        g.setMode("solo");
        groupeRepo.save(g);
        return Map.of("groupeId", groupeId, "mode", "solo");
    }

    public String getMode(Long groupeId) {
        return groupeRepo.findById(groupeId)
                .map(Groupe::getMode).orElse("solo");
    }

    // ── Helpers ───────────────────────────────────────────────
    private void verifierPermission(Long membreId, String type) {
        PermissionMembre p = permissionRepo
                .findByMembreId(membreId).orElse(null);
        if (p == null) return; // Pas de perms = accès refusé
        boolean ok = switch (type != null ? type : "") {
            case "vente"          -> p.getPeutVendre();
            case "mouvement_stock"-> p.getPeutGererStock();
            case "client"         -> p.getPeutGererClients();
            default               -> false;
        };
        if (!ok)
            throw new RuntimeException(
                    "Permission refusée pour: " + type);
    }

    private Map<String, Object> buildPermissionsDto(PermissionMembre p) {
        if (p == null) return Map.of("peutVendre", false);
        Map<String, Object> dto = new HashMap<>();
        dto.put("peutVendre",         p.getPeutVendre());
        dto.put("peutVoirDettes",     p.getPeutVoirDettes());
        dto.put("peutGererStock",     p.getPeutGererStock());
        dto.put("peutVoirStats",      p.getPeutVoirStats());
        dto.put("peutGererClients",   p.getPeutGererClients());
        dto.put("peutVoirHistorique", p.getPeutVoirHistorique());
        return dto;
    }

    private String s(Map<String,Object> m, String k) {
        Object v = m.get(k); return v != null ? v.toString() : null;
    }
}