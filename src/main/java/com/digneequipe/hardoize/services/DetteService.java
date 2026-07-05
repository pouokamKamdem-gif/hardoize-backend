package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.DetteRequest;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetteService {

    private final DetteRepository    detteRepository;
    private final ClientRepository   clientRepository;
    private final GroupeRepository   groupeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Dette> getDettesActives(Long groupeId) {
        return detteRepository.findDettesActives(groupeId);
    }

    public List<Dette> getByClient(Long clientId) {
        return detteRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }

    public double getTotalDettesActives(Long groupeId) {
        Double total = detteRepository.getTotalDettesActives(groupeId);
        return total != null ? total : 0.0;
    }

    public long getNombreRetards(Long groupeId) {
        Long nb = detteRepository.getNombreRetards(groupeId, LocalDateTime.now());
        return nb != null ? nb : 0;
    }

    @Transactional
    public Dette creer(DetteRequest request, String telephoneUtilisateur) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId()).orElse(null);
        }

        LocalDateTime dateRemb = LocalDate
                .parse(request.getDateRemboursement(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atTime(23, 59, 59);

        Dette dette = Dette.builder()
                .client(client)
                .montantTotal(request.getMontantTotal())
                .dateRemboursement(dateRemb)
                .utilisateur(utilisateur)
                .groupe(groupe)
                .build();

        return detteRepository.save(dette);
    }

    @Transactional
    public void enregistrerRemboursement(Long id, Double montant) {
        Dette dette = detteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dette introuvable"));

        if (montant > dette.getMontantRestant()) {
            throw new RuntimeException("Montant supérieur au reste dû");
        }

        detteRepository.enregistrerRemboursement(id, montant, LocalDateTime.now());

        // Recharger pour vérifier si soldée
        detteRepository.findById(id).ifPresent(d -> {
            if (d.getMontantRembourse() >= d.getMontantTotal()) {
                detteRepository.solderDette(id, LocalDateTime.now());
            }
        });

        // Augmenter le score du client (+5)
        clientRepository.incrementerScore(dette.getClient().getId(), 5);
    }

    @Transactional
    public void solderDette(Long id) {
        Dette dette = detteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dette introuvable"));

        detteRepository.solderDette(id, LocalDateTime.now());

        // Bonus score +10 pour solde complet
        clientRepository.incrementerScore(dette.getClient().getId(), 10);
    }

    /*
     * Tâche planifiée : chaque jour à minuit,
     * décrémente le score des clients en retard (-2 pts/jour).
     * Cron : "0 0 0 * * *" = tous les jours à 00:00:00
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void mettreAJourScoresRetards() {
        List<Dette> dettesEnRetard = detteRepository
                .findDettesEnRetard(LocalDateTime.now());

        for (Dette dette : dettesEnRetard) {
            clientRepository.decrementerScore(
                    dette.getClient().getId(), 2
            );
        }
    }
}