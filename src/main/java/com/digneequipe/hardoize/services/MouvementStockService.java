package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final ProduitRepository        produitRepository;
    private final FournisseurRepository    fournisseurRepository;
    private final GroupeRepository         groupeRepository;
    private final UtilisateurRepository    utilisateurRepository;

    // ── Récupérer tous les mouvements d'un groupe ─────────────
    public List<MouvementStock> getByGroupe(Long groupeId) {
        return mouvementRepository
                .findByGroupeIdOrderByCreatedAtDesc(groupeId);
    }

    // ── Récupérer les mouvements d'un produit ─────────────────
    public List<MouvementStock> getByProduit(Long produitId) {
        return mouvementRepository
                .findByProduitIdOrderByCreatedAtDesc(produitId);
    }

    // ── Enregistrer un mouvement ──────────────────────────────
    @Transactional
    public MouvementStock enregistrer(
            Long produitId,
            String type,
            String motif,
            Integer quantite,
            Double prixUnitaire,
            Long fournisseurId,
            Long groupeId,
            String telephoneUtilisateur) {

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (groupeId != null) {
            groupe = groupeRepository.findById(groupeId).orElse(null);
        }

        Fournisseur fournisseur = null;
        if (fournisseurId != null) {
            fournisseur = fournisseurRepository
                    .findById(fournisseurId).orElse(null);
        }

        double montantTotal = (prixUnitaire != null ? prixUnitaire : 0.0) * quantite;

        // Mettre à jour le stock du produit
        if ("entree".equals(type)) {
            produitRepository.incrementerStock(produitId, quantite);
        } else if ("sortie".equals(type)) {
            if (produit.getQuantiteStock() < quantite) {
                throw new RuntimeException(
                        "Stock insuffisant pour " + produit.getNom()
                );
            }
            produitRepository.decrementerStock(produitId, quantite);
        }

        MouvementStock mouvement = MouvementStock.builder()
                .produit(produit)
                .nomProduit(produit.getNom())
                .type(type)
                .motif(motif)
                .quantite(quantite)
                .prixUnitaire(prixUnitaire != null ? prixUnitaire : 0.0)
                .montantTotal(montantTotal)
                .fournisseur(fournisseur)
                .utilisateur(utilisateur)
                .groupe(groupe)
                .build();

        return mouvementRepository.save(mouvement);
    }

    // ── Total des entrées sur une période ─────────────────────
    public double getTotalEntrees(Long groupeId,
                                  LocalDateTime debut,
                                  LocalDateTime fin) {
        Double total = mouvementRepository
                .getTotalEntrees(groupeId, debut, fin);
        return total != null ? total : 0.0;
    }
}