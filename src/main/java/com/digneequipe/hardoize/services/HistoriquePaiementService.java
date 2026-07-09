package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.HistoriquePaiement;
import com.digneequipe.hardoize.repositories.HistoriquePaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoriquePaiementService {

    private final HistoriquePaiementRepository repository;

    /**
     * Synchronisation des historiques de paiements.
     */
    public List<HistoriquePaiement> synchroniser(List<HistoriquePaiement> historiques) {

        for (HistoriquePaiement historique : historiques) {

            repository.findByUuid(historique.getUuid())
                    .ifPresent(existant -> historique.setId(existant.getId()));

            repository.save(historique);
        }

        return historiques;
    }

    /**
     * Retourne tous les historiques d'un groupe.
     */
    public List<HistoriquePaiement> getByGroupe(Long groupeId) {

        return repository.findByGroupeId(groupeId);
    }
}