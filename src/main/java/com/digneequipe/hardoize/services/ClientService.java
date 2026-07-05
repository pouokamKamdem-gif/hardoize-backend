package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.ClientRequest;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository      clientRepository;
    private final GroupeRepository      groupeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Client> getByGroupe(Long groupeId) {
        return clientRepository
                .findByGroupeIdAndEstActifTrueOrderByScoreAsc(groupeId);
    }

    public List<Client> rechercher(Long groupeId, String query) {
        return clientRepository.rechercher(groupeId, query);
    }

    public Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
    }

    @Transactional
    public Client creer(ClientRequest request, String telephoneUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephoneUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Groupe groupe = null;
        if (request.getGroupeId() != null) {
            groupe = groupeRepository.findById(request.getGroupeId()).orElse(null);
        }

        // Vérifier si le client existe déjà dans ce groupe
        if (groupe != null) {
            clientRepository
                    .findByNumeroClientAndGroupeId(request.getNumeroClient(), groupe.getId())
                    .ifPresent(c -> {
                        throw new RuntimeException("Ce client existe déjà dans ce groupe");
                    });
        }

        Client client = Client.builder()
                .nomClient(request.getNomClient())
                .numeroClient(request.getNumeroClient())
                .email(request.getEmail())
                .photoUri(request.getPhotoUri())
                .groupe(groupe)
                .utilisateur(utilisateur)
                .build();

        return clientRepository.save(client);
    }

    @Transactional
    public Client modifier(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        client.setNomClient(request.getNomClient());
        client.setNumeroClient(request.getNumeroClient());
        client.setEmail(request.getEmail());
        if (request.getPhotoUri() != null) {
            client.setPhotoUri(request.getPhotoUri());
        }

        return clientRepository.save(client);
    }

    @Transactional
    public void setActif(Long id, boolean actif) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        client.setEstActif(actif);
        clientRepository.save(client);
    }

    @Transactional
    public void decrementerScore(Long id, Integer points) {
        clientRepository.decrementerScore(id, points);
    }

    @Transactional
    public void incrementerScore(Long id, Integer points) {
        clientRepository.incrementerScore(id, points);
    }

    public double getScoreMoyen(Long groupeId) {
        Double score = clientRepository.getScoreMoyen(groupeId);
        return score != null ? score : 100.0;
    }
}