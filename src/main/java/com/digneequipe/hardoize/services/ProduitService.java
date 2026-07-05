package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository    produitRepository;
    private final GroupeRepository     groupeRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ── Récupérer tous les produits d'un groupe ───────────────
    public List<Produit> getByGroupe(Long groupeId) {
        return produitRepository
                .findByGroupeIdAndEstActifTrueOrderByNomAsc(groupeId);
    }

    // ── Rechercher des produits ────────────────────────────────
    public List<Produit> rechercher(Long groupeId, String query) {
        return produitRepository.rechercherParNom(groupeId, query);
    }

    // ── Produits en stock faible ───────────────────────────────
    public List<Produit> getStockFaible(Long groupeId) {
        return produitRepository.findStockFaible(groupeId);
    }

    // ── Créer un produit ──────────────────────────────────────
    @Transactional
    public Produit creer(ProduitRequest request, String telephoneUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId())
                    .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
        }

        Produit produit = Produit.builder()
                .nom(request.getNom())
                .prixAchat(request.getPrixAchat())
                .prixVente(request.getPrixVente())
                .quantiteStock(request.getQuantiteStock())
                .stockMinimum(request.getStockMinimum())
                .categorie(request.getCategorie())
                .photoUri(request.getPhotoUri())
                .groupe(groupe)
                .utilisateur(utilisateur)
                .build();

        return produitRepository.save(produit);
    }

    // ── Modifier un produit ───────────────────────────────────
    @Transactional
    public Produit modifier(Long id, ProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        produit.setNom(request.getNom());
        produit.setPrixAchat(request.getPrixAchat());
        produit.setPrixVente(request.getPrixVente());
        produit.setQuantiteStock(request.getQuantiteStock());
        produit.setStockMinimum(request.getStockMinimum());
        produit.setCategorie(request.getCategorie());

        if (request.getPhotoUri() != null) {
            produit.setPhotoUri(request.getPhotoUri());
        }

        return produitRepository.save(produit);
    }

    // ── Désactiver un produit (soft delete) ───────────────────
    @Transactional
    public void setActif(Long id, boolean actif) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        produit.setEstActif(actif);
        produitRepository.save(produit);
    }

    // ── Entrée de stock ───────────────────────────────────────
    @Transactional
    public Produit entreeStock(Long id, Integer quantite) {
        produitRepository.incrementerStock(id, quantite);
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
    }
}