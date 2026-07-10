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
    private final LigneVenteRepository  ligneVenteRepo;
    private final ProduitRepository     produitRepo;
    private final ClientRepository      clientRepo;
    private final DetteRepository       detteRepository;
    private final GroupeRepository      groupeRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final MouvementStockRepository mouvementRepository;
    private final VenteRepository venteRepo;
    private final DetteRepository detteRepo;

    @Transactional
    public Map<String, Object> enregistrer(VenteRequest request,
                                           String telephoneUtilisateur) {
        if (request.getLignes() == null || request.getLignes().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        Utilisateur utilisateur = utilisateurRepo
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepo.findById(request.getGroupeId()).orElse(null);
        }

        Client client = null;
        if (request.getClientId() != null) {
            client = clientRepo.findById(request.getClientId()).orElse(null);
        }

        // Calculer totaux
        double montantTotal = 0.0;
        double beneficeNet  = 0.0;
        for (var ligne : request.getLignes()) {
            double sousTotal = ligne.getPrixUnitaire() * ligne.getQuantite();
            double prixAchat = ligne.getPrixAchat() != null
                    ? ligne.getPrixAchat() : 0.0;
            montantTotal += sousTotal;
            beneficeNet  += (ligne.getPrixUnitaire() - prixAchat) * ligne.getQuantite();
        }

        // Créer vente — PAS de vérification stock (mode solo = frontend vérifie)
        Vente vente = Vente.builder()
                .montantTotal(montantTotal)
                .beneficeNet(beneficeNet)
                .typePaiement(request.getTypePaiement())
                .client(client)
                .utilisateur(utilisateur)
                .groupe(groupe)
                .build();
        vente = venteRepo.save(vente);

        // Créer lignes
        List<Map<String, Object>> lignesDto = new ArrayList<>();
        for (var ligneReq : request.getLignes()) {
            Produit produit = null;
            try {
                produit = produitRepo.findById(ligneReq.getProduitId()).orElse(null);
            } catch (Exception ignored) {}

            double prixAchat = ligneReq.getPrixAchat() != null
                    ? ligneReq.getPrixAchat()
                    : (produit != null ? produit.getPrixAchat() : 0.0);
            double sousTotal = ligneReq.getPrixUnitaire() * ligneReq.getQuantite();
            double marge     = (ligneReq.getPrixUnitaire() - prixAchat)
                    * ligneReq.getQuantite();

            LigneVente ligne = LigneVente.builder()
                    .vente(vente)
                    .produit(produit)
                    .nomProduit(ligneReq.getNomProduit())
                    .quantite(ligneReq.getQuantite())
                    .prixAchat(prixAchat)
                    .prixUnitaire(ligneReq.getPrixUnitaire())
                    .sousTotal(sousTotal)
                    .marge(marge)
                    .build();
            ligneVenteRepo.save(ligne);

            Map<String, Object> lDto = new HashMap<>();
            lDto.put("id",          ligne.getId());
            lDto.put("nomProduit",  ligne.getNomProduit());
            lDto.put("quantite",    ligne.getQuantite());
            lDto.put("prixUnitaire",ligne.getPrixUnitaire());
            lDto.put("sousTotal",   ligne.getSousTotal());
            lDto.put("marge",       ligne.getMarge());
            lignesDto.add(lDto);
        }

        // Créer dette si crédit
        Map<String, Object> detteDto = null;
        if ("credit".equals(request.getTypePaiement())
                && client != null
                && request.getDateRemboursement() != null) {
            try {
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
                dette = detteRepo.save(dette);

                detteDto = new HashMap<>();
                detteDto.put("id",           dette.getId());
                detteDto.put("montantTotal", dette.getMontantTotal());
                detteDto.put("statut",       dette.getStatut());
            } catch (Exception ignored) {}
        }

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
}