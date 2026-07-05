package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.VenteRequest;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository       venteRepository;
    private final ProduitRepository     produitRepository;
    private final ClientRepository      clientRepository;
    private final DetteRepository       detteRepository;
    private final GroupeRepository      groupeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockRepository mouvementRepository;

    // ── Enregistrer une vente ─────────────────────────────────
    @Transactional
    public Vente enregistrer(VenteRequest request, String telephoneUtilisateur) {

        // Récupérer les entités liées
        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId()).orElse(null);
        }

        Client client = null;
        if (request.getClientId() != null) {
            client = clientRepository.findById(request.getClientId()).orElse(null);
        }

        // Vérifier le stock disponible
        if (produit.getQuantiteStock() < request.getQuantite()) {
            throw new RuntimeException(
                    "Stock insuffisant pour " + produit.getNom() +
                            ". Disponible : " + produit.getQuantiteStock()
            );
        }

        // Créer la vente
        Vente vente = Vente.builder()
                .produit(produit)
                .nomProduit(produit.getNom())
                .quantite(request.getQuantite())
                .prixUnitaire(request.getPrixUnitaire())
                .montantTotal(request.getMontantTotal())
                .typePaiement(request.getTypePaiement())
                .client(client)
                .utilisateur(utilisateur)
                .groupe(groupe)
                .build();

        vente = venteRepository.save(vente);

        // Décrémenter le stock
        produitRepository.decrementerStock(produit.getId(), request.getQuantite());

        // Enregistrer le mouvement de stock (sortie)
        MouvementStock mouvement = MouvementStock.builder()
                .produit(produit)
                .nomProduit(produit.getNom())
                .type("sortie")
                .motif("vente")
                .quantite(request.getQuantite())
                .prixUnitaire(request.getPrixUnitaire())
                .montantTotal(request.getMontantTotal())
                .utilisateur(utilisateur)
                .groupe(groupe)
                .build();
        mouvementRepository.save(mouvement);

        // Si crédit → créer la dette
        if ("credit".equals(request.getTypePaiement()) &&
                client != null &&
                request.getDateRemboursement() != null) {

            LocalDateTime dateRemb = LocalDate
                    .parse(request.getDateRemboursement(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .atTime(23, 59, 59);

            Dette dette = Dette.builder()
                    .client(client)
                    .vente(vente)
                    .montantTotal(request.getMontantTotal())
                    .dateRemboursement(dateRemb)
                    .utilisateur(utilisateur)
                    .groupe(groupe)
                    .build();

            detteRepository.save(dette);
        }

        return vente;
    }

    // ── Récupérer les ventes d'un groupe ──────────────────────
    public List<Vente> getByGroupe(Long groupeId) {
        return venteRepository.findByGroupeIdOrderByCreatedAtDesc(groupeId);
    }

    // ── Stats ventes pour le dashboard ────────────────────────
    public double getTotalJour(Long groupeId) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = LocalDateTime.now();
        Double total = venteRepository.getTotalVentes(groupeId, debut, fin);
        return total != null ? total : 0.0;
    }

    public double getBeneficeNet(Long groupeId, LocalDateTime debut, LocalDateTime fin) {
        Double benefice = venteRepository.getBeneficeNet(groupeId, debut, fin);
        return benefice != null ? benefice : 0.0;
    }
}