package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.VenteRequest;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository       venteRepository;
    private final LigneVenteRepository  ligneVenteRepository;
    private final ProduitRepository     produitRepository;
    private final ClientRepository      clientRepository;
    private final DetteRepository       detteRepository;
    private final GroupeRepository      groupeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockRepository mouvementRepository;

    @Transactional
    public Map<String, Object> enregistrer(VenteRequest request,
                                           String telephoneUtilisateur) {
        // ── Validation ────────────────────────────────────────
        if (request.getLignes() == null || request.getLignes().isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId())
                    .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
        }

        Client client = null;
        if (request.getClientId() != null) {
            client = clientRepository.findById(request.getClientId())
                    .orElse(null);
        }

        // ── Calculer les totaux depuis les lignes ─────────────
        double montantTotal = 0.0;
        double beneficeNet  = 0.0;

        for (VenteRequest.LigneVenteRequest ligne : request.getLignes()) {
            Produit produit = produitRepository.findById(ligne.getProduitId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produit introuvable : " + ligne.getProduitId()));

            if (produit.getQuantiteStock() < ligne.getQuantite()) {
                throw new RuntimeException(
                        "Stock insuffisant pour " + produit.getNom() +
                                ". Disponible : " + produit.getQuantiteStock()
                );
            }

            double sousTotal = ligne.getPrixUnitaire() * ligne.getQuantite();
            double prixAchat = ligne.getPrixAchat() != null
                    ? ligne.getPrixAchat()
                    : produit.getPrixAchat();
            double marge = (ligne.getPrixUnitaire() - prixAchat)
                    * ligne.getQuantite();

            montantTotal += sousTotal;
            beneficeNet  += marge;
        }

        // ── Créer la vente ────────────────────────────────────
        Vente vente = Vente.builder()
                .montantTotal(montantTotal)
                .beneficeNet(beneficeNet)
                .typePaiement(request.getTypePaiement())
                .client(client)
                .utilisateur(utilisateur)
                .groupe(groupe)
                .build();

        vente = venteRepository.save(vente);

        // ── Créer les lignes de vente ─────────────────────────
        List<Map<String, Object>> lignesDto = new ArrayList<>();

        for (VenteRequest.LigneVenteRequest ligneReq : request.getLignes()) {
            Produit produit = produitRepository
                    .findById(ligneReq.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));

            double prixAchat = ligneReq.getPrixAchat() != null
                    ? ligneReq.getPrixAchat()
                    : produit.getPrixAchat();
            double sousTotal = ligneReq.getPrixUnitaire() * ligneReq.getQuantite();
            double marge = (ligneReq.getPrixUnitaire() - prixAchat)
                    * ligneReq.getQuantite();

            LigneVente ligne = LigneVente.builder()
                    .vente(vente)
                    .produit(produit)
                    .nomProduit(produit.getNom())
                    .quantite(ligneReq.getQuantite())
                    .prixAchat(prixAchat)
                    .prixUnitaire(ligneReq.getPrixUnitaire())
                    .sousTotal(sousTotal)
                    .marge(marge)
                    .build();

            ligne = ligneVenteRepository.save(ligne);

            // Décrémenter le stock
            produitRepository.decrementerStock(
                    produit.getId(), ligneReq.getQuantite()
            );

            // Enregistrer mouvement de stock (sortie vente)
            MouvementStock mouvement = MouvementStock.builder()
                    .produit(produit)
                    .nomProduit(produit.getNom())
                    .type("sortie")
                    .motif("vente")
                    .quantite(ligneReq.getQuantite())
                    .prixUnitaire(ligneReq.getPrixUnitaire())
                    .montantTotal(sousTotal)
                    .utilisateur(utilisateur)
                    .groupe(groupe)
                    .build();
            mouvementRepository.save(mouvement);

            // DTO ligne
            Map<String, Object> ligneMap = new HashMap<>();
            ligneMap.put("id",          ligne.getId());
            ligneMap.put("produitId",   produit.getId());
            ligneMap.put("nomProduit",  produit.getNom());
            ligneMap.put("quantite",    ligne.getQuantite());
            ligneMap.put("prixAchat",   ligne.getPrixAchat());
            ligneMap.put("prixUnitaire",ligne.getPrixUnitaire());
            ligneMap.put("sousTotal",   ligne.getSousTotal());
            ligneMap.put("marge",       ligne.getMarge());
            lignesDto.add(ligneMap);
        }

        // ── Créer la dette si crédit ──────────────────────────
        Map<String, Object> detteDto = null;
        if ("credit".equals(request.getTypePaiement()) && client != null
                && request.getDateRemboursement() != null) {

            LocalDateTime dateRemb = LocalDate
                    .parse(request.getDateRemboursement(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .atTime(23, 59, 59);

            Dette dette = Dette.builder()
                    .vente(vente)
                    .client(client)
                    .montantTotal(montantTotal)
                    .montantRembourse(0.0)
                    .montantRestant(montantTotal)
                    .dateRemboursement(dateRemb)
                    .utilisateur(utilisateur)
                    .groupe(groupe)
                    .build();

            dette = detteRepository.save(dette);

            detteDto = new HashMap<>();
            detteDto.put("id",           dette.getId());
            detteDto.put("montantTotal", dette.getMontantTotal());
            detteDto.put("statut",       dette.getStatut());
        }

        // ── Construire le DTO de réponse ──────────────────────
        Map<String, Object> dto = new HashMap<>();
        dto.put("id",          vente.getId());
        dto.put("montantTotal",vente.getMontantTotal());
        dto.put("beneficeNet", vente.getBeneficeNet());
        dto.put("typePaiement",vente.getTypePaiement());
        dto.put("lignes",      lignesDto);
        dto.put("createdAt",   vente.getCreatedAt());
        if (detteDto != null) dto.put("dette", detteDto);

        return dto;
    }

    public List<Map<String, Object>> getByGroupe(Long groupeId) {
        List<Vente> ventes = venteRepository
                .findByGroupeIdOrderByCreatedAtDesc(groupeId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Vente v : ventes) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           v.getId());
            dto.put("montantTotal", v.getMontantTotal());
            dto.put("beneficeNet",  v.getBeneficeNet());
            dto.put("typePaiement", v.getTypePaiement());
            dto.put("createdAt",    v.getCreatedAt());
            dto.put("clientNom",
                    v.getClient() != null ? v.getClient().getNomClient() : null);

            // Charger les lignes
            List<LigneVente> lignes = ligneVenteRepository
                    .findByVenteId(v.getId());
            List<Map<String, Object>> lignesDto = new ArrayList<>();
            for (LigneVente l : lignes) {
                Map<String, Object> lDto = new HashMap<>();
                lDto.put("id",          l.getId());
                lDto.put("nomProduit",  l.getNomProduit());
                lDto.put("quantite",    l.getQuantite());
                lDto.put("prixUnitaire",l.getPrixUnitaire());
                lDto.put("sousTotal",   l.getSousTotal());
                lDto.put("marge",       l.getMarge());
                lignesDto.add(lDto);
            }
            dto.put("lignes", lignesDto);
            result.add(dto);
        }
        return result;
    }

    public double getTotalJour(Long groupeId) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = LocalDateTime.now();
        Double total = ligneVenteRepository.getCATotal(groupeId, debut, fin);
        return total != null ? total : 0.0;
    }

    public double getBeneficeNet(Long groupeId,
                                 LocalDateTime debut,
                                 LocalDateTime fin) {
        Double b = ligneVenteRepository.getBeneficeNet(groupeId, debut, fin);
        return b != null ? b : 0.0;
    }
}