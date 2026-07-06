package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository      groupeRepository;
    private final MembreGroupeRepository membreRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PermissionService permissionService;

    public List<Groupe> getByProprietaire(Long proprietaireId) {
        return groupeRepository.findByProprietaireIdAndEstActifTrue(proprietaireId);
    }

    public Groupe getById(Long id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
    }

    public Groupe getByCodeQR(String codeQR) {
        return groupeRepository.findByCodeQR(codeQR)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
    }

    @Transactional
    public Groupe creer(GroupeRequest request, String telephoneProprietaire) {
        Utilisateur proprietaire = utilisateurRepository
                .findByTelephone(telephoneProprietaire)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = Groupe.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .proprietaire(proprietaire)
                .codeQR(UUID.randomUUID().toString())
                .photoUri(request.getPhotoUri())
                .heureFermeture(request.getHeureFermeture() != null
                        ? request.getHeureFermeture() : "18:00")
                .build();

        groupe = groupeRepository.save(groupe);

        // Propriétaire comme membre permanent
        MembreGroupe membre = MembreGroupe.builder()
                .groupe(groupe)
                .utilisateur(proprietaire)
                .nomAffiche(proprietaire.getNom())
                .telephone(proprietaire.getTelephone())
                .role("proprietaire")
                .bailHeure(groupe.getHeureFermeture())
                .estConnecte(true)
                .connexionPermanente(true)
                .build();

        membre = membreRepository.save(membre);

        // ── Permissions propriétaire (accès total) ────────────────
        PermissionMembre perms = permissionService.creerDefaut(membre.getId());
        // Le propriétaire a accès à tout
        perms.setPeutVendre(true);
        perms.setPeutVoirDettes(true);
        perms.setPeutGererStock(true);
        perms.setPeutVoirStats(true);
        perms.setPeutGererClients(true);
        perms.setPeutVoirHistorique(true);
        // Sauvegarder via le service
        permissionService.sauvegarder(perms);

        return groupe;
    }

    public List<MembreGroupe> getMembres(Long groupeId) {
        return membreRepository.findByGroupeIdAndEstActifTrue(groupeId);
    }

    public List<MembreGroupe> getMembresConnectes(Long groupeId) {
        return membreRepository.findByGroupeIdAndEstConnecteTrue(groupeId);
    }

    @Transactional
    public MembreGroupe ajouterMembre(Long groupeId, MembreRequest request) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        Utilisateur utilisateur = null;
        if (request.getUtilisateurId() != null) {
            utilisateur = utilisateurRepository
                    .findById(request.getUtilisateurId()).orElse(null);
        }

        MembreGroupe membre = MembreGroupe.builder()
                .groupe(groupe)
                .utilisateur(utilisateur)
                .nomAffiche(request.getNomAffiche())
                .telephone(request.getTelephone())
                .role(request.getRole())
                .bailHeure(request.getBailHeure())
                .estConnecte(true)
                .connexionPermanente(request.getConnexionPermanente())
                .build();

        membre = membreRepository.save(membre);
        //creation de la permission par defaut
        permissionService.creerDefaut(membre.getId());
        return membre;
    }

    @Transactional
    public MembreGroupe modifierMembre(Long membreId, MembreRequest request) {
        MembreGroupe membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre introuvable"));

        membre.setRole(request.getRole());
        membre.setBailHeure(request.getBailHeure());
        membre.setConnexionPermanente(request.getConnexionPermanente());

        membreRepository.save(membre);

        permissionService.creerDefaut(membre.getId());
        return membre;
    }

    @Transactional
    public void deconnecterMembre(Long membreId) {
        MembreGroupe membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre introuvable"));
        membre.setEstConnecte(false);
        membreRepository.save(membre);
    }

    @Transactional
    public void deconnecterTous(Long groupeId) {
        membreRepository.deconnecterTous(groupeId);
    }

    @Transactional
    public MembreGroupe rejoindre(String codeQR, String telephoneUtilisateur,
                                  String nomAffiche, String bailHeure) {
        Groupe groupe = groupeRepository.findByCodeQR(codeQR)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifier si déjà membre
        return membreRepository
                .findByGroupeIdAndUtilisateurId(groupe.getId(), utilisateur.getId())
                .map(membre -> {
                    // Membre existant → juste reconnecter
                    membre.setEstConnecte(true);
                    return membreRepository.save(membre);
                    // Pas de nouvelles permissions, elles existent déjà
                })
                .orElseGet(() -> {
                    // Nouveau membre
                    MembreGroupe nouveau = MembreGroupe.builder()
                            .groupe(groupe)
                            .utilisateur(utilisateur)
                            .nomAffiche(nomAffiche != null
                                    ? nomAffiche : utilisateur.getNom())
                            .telephone(utilisateur.getTelephone())
                            .role("vendeur")
                            .bailHeure(bailHeure != null
                                    ? bailHeure : groupe.getHeureFermeture())
                            .estConnecte(true)
                            .build();

                    nouveau = membreRepository.save(nouveau);

                    // ── Créer les permissions par défaut ──────────────
                    permissionService.creerDefaut(nouveau.getId());

                    return nouveau;
                });
    }
}