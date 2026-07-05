package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.*;
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
public class FournisseurService {

    private final FournisseurRepository       fournisseurRepository;
    private final DetteFournisseurRepository  detteFournisseurRepository;
    private final GroupeRepository            groupeRepository;

    // ── CRUD Fournisseurs ─────────────────────────────────────
    public List<Fournisseur> getByGroupe(Long groupeId) {
        return fournisseurRepository
                .findByGroupeIdAndEstActifTrueOrderByNomAsc(groupeId);
    }

    public List<Fournisseur> rechercher(Long groupeId, String query) {
        return fournisseurRepository.rechercher(groupeId, query);
    }

    @Transactional
    public Fournisseur creer(FournisseurRequest request) {
        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId()).orElse(null);
        }

        Fournisseur fournisseur = Fournisseur.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .adresse(request.getAdresse())
                .photoUri(request.getPhotoUri())
                .groupe(groupe)
                .build();

        return fournisseurRepository.save(fournisseur);
    }

    @Transactional
    public Fournisseur modifier(Long id, FournisseurRequest request) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));

        f.setNom(request.getNom());
        f.setTelephone(request.getTelephone());
        f.setEmail(request.getEmail());
        f.setAdresse(request.getAdresse());
        if (request.getPhotoUri() != null) f.setPhotoUri(request.getPhotoUri());

        return fournisseurRepository.save(f);
    }

    @Transactional
    public void setActif(Long id, boolean actif) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));
        f.setEstActif(actif);
        fournisseurRepository.save(f);
    }

    // ── Dettes Fournisseurs ───────────────────────────────────
    public List<DetteFournisseur> getDettesActives(Long groupeId) {
        return detteFournisseurRepository.findDettesActives(groupeId);
    }

    public double getTotalDettesActives(Long groupeId) {
        Double total = detteFournisseurRepository.getTotalDettesActives(groupeId);
        return total != null ? total : 0.0;
    }

    @Transactional
    public DetteFournisseur creerDette(DetteFournisseurRequest request) {
        Fournisseur fournisseur = fournisseurRepository
                .findById(request.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId()).orElse(null);
        }

        LocalDateTime dateRemb = LocalDate
                .parse(request.getDateRemboursement(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(23, 59, 59);

        DetteFournisseur dette = DetteFournisseur.builder()
                .fournisseur(fournisseur)
                .nomFournisseur(fournisseur.getNom())
                .montantTotal(request.getMontantTotal())
                .dateRemboursement(dateRemb)
                .motif(request.getMotif())
                .groupe(groupe)
                .build();

        return detteFournisseurRepository.save(dette);
    }

    @Transactional
    public void rembourserDetteFournisseur(Long id, Double montant) {
        DetteFournisseur dette = detteFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dette fournisseur introuvable"));

        if (montant > dette.getMontantRestant()) {
            throw new RuntimeException("Montant supérieur au reste dû");
        }

        detteFournisseurRepository
                .enregistrerRemboursement(id, montant, LocalDateTime.now());

        detteFournisseurRepository.findById(id).ifPresent(d -> {
            if (d.getMontantRembourse() >= d.getMontantTotal()) {
                detteFournisseurRepository.solderDette(id, LocalDateTime.now());
            }
        });
    }

    @Transactional
    public void solderDetteFournisseur(Long id) {
        detteFournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dette fournisseur introuvable"));
        detteFournisseurRepository.solderDette(id, LocalDateTime.now());
    }
}