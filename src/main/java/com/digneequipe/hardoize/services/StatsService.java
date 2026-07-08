package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.response.StatsResponse;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final VenteRepository      venteRepository;
    private final DetteRepository      detteRepository;
    private final ClientRepository     clientRepository;
    private final DetteFournisseurRepository detteFournisseurRepository;
    private final LigneVenteRepository ligneVenteRepository;

    /*
     * Calcule les statistiques pour une période donnée.
     * periode : "jour" | "semaine" | "mois" | "annee"
     */
    public StatsResponse getStats(Long groupeId, String periode) {
        LocalDateTime fin   = LocalDateTime.now();
        LocalDateTime debut = switch (periode) {
            case "jour"    -> LocalDate.now().atStartOfDay();
            case "semaine" -> LocalDate.now().minusDays(7).atStartOfDay();
            case "mois"    -> LocalDate.now().minusDays(30).atStartOfDay();
            case "annee"   -> LocalDate.now().minusDays(365).atStartOfDay();
            default        -> LocalDate.now().atStartOfDay();
        };

        // CA depuis les lignes de vente (calcul exact)
        Double ca          = ligneVenteRepository.getCATotal(groupeId, debut, fin);
        Double beneficeNet = ligneVenteRepository.getBeneficeNet(groupeId, debut, fin);
        Double dettesClients = detteRepository.getTotalDettesActives(groupeId);
        Double dettesFourn   = detteFournisseurRepository.getTotalDettesActives(groupeId);
        Double scoreMoyen    = clientRepository.getScoreMoyen(groupeId);
        Long   nbRetards     = detteRepository.getNombreRetards(groupeId, fin);

        return StatsResponse.builder()
                .totalVentes(ca != null ? ca : 0.0)
                .beneficeNet(beneficeNet != null ? beneficeNet : 0.0)
                .totalDettesClients(dettesClients != null ? dettesClients : 0.0)
                .totalDettessFournisseurs(dettesFourn != null ? dettesFourn : 0.0)
                .scoreMoyenClients(scoreMoyen != null ? scoreMoyen : 100.0)
                .nbRetards(nbRetards != null ? nbRetards : 0L)
                .periode(periode)
                .build();
    }
}