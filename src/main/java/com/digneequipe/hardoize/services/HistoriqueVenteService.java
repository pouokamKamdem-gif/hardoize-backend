package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.HistoriqueVente;
import com.digneequipe.hardoize.repositories.HistoriqueVenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoriqueVenteService {

    private final HistoriqueVenteRepository repository;

    public List<HistoriqueVente> getByGroupe(Long groupeId) {
        return repository.findByGroupeId(groupeId);
    }
    /**
     * Synchronise une liste d'historiques.
     * Si un historique existe déjà pour le même groupe et la même date,
     * il est mis à jour.
     */
    public List<HistoriqueVente> synchroniser(List<HistoriqueVente> historiques) {

        for (HistoriqueVente historique : historiques) {

            repository.findByUuid(historique.getUuid())
                    .ifPresent(existant -> historique.setId(existant.getId()));

            repository.save(historique);
        }

        return historiques;
    }
}