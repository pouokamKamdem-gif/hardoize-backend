package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMembreRepository permissionRepository;
    private final MembreGroupeRepository     membreRepository;

    // Créer permissions par défaut pour un nouveau membre
    @Transactional
    public PermissionMembre creerDefaut(Long membreId) {
        MembreGroupe membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre introuvable"));

        // Vérifier si les permissions existent déjà
        return permissionRepository.findByMembreId(membreId)
                .orElseGet(() -> {
                    PermissionMembre p = PermissionMembre.builder()
                            .membre(membre)
                            .peutVendre(true)       // seul accès par défaut
                            .peutVoirDettes(false)
                            .peutGererStock(false)
                            .peutVoirStats(false)
                            .peutGererClients(false)
                            .peutVoirHistorique(false)
                            .build();
                    return permissionRepository.save(p);
                });
    }

    // Modifier les permissions d'un membre
    @Transactional
    public PermissionMembre modifier(Long membreId,
                                     Map<String, Boolean> permissions) {
        PermissionMembre p = permissionRepository
                .findByMembreId(membreId)
                .orElseGet(() -> creerDefaut(membreId));

        if (permissions.containsKey("peutVendre"))
            p.setPeutVendre(permissions.get("peutVendre"));
        if (permissions.containsKey("peutVoirDettes"))
            p.setPeutVoirDettes(permissions.get("peutVoirDettes"));
        if (permissions.containsKey("peutGererStock"))
            p.setPeutGererStock(permissions.get("peutGererStock"));
        if (permissions.containsKey("peutVoirStats"))
            p.setPeutVoirStats(permissions.get("peutVoirStats"));
        if (permissions.containsKey("peutGererClients"))
            p.setPeutGererClients(permissions.get("peutGererClients"));
        if (permissions.containsKey("peutVoirHistorique"))
            p.setPeutVoirHistorique(permissions.get("peutVoirHistorique"));

        return permissionRepository.save(p);
    }

    public PermissionMembre getByMembre(Long membreId) {
        return permissionRepository.findByMembreId(membreId)
                .orElseGet(() -> creerDefaut(membreId));
    }

    @Transactional
    public PermissionMembre sauvegarder(PermissionMembre permission) {
        return permissionRepository.save(permission);
    }
}