package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.PermissionMembre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface PermissionMembreRepository
        extends JpaRepository<PermissionMembre, Long> {

    Optional<PermissionMembre> findByMembreId(Long membreId);

    @Query("SELECT p FROM PermissionMembre p " +
            "WHERE p.membre.groupe.id = :groupeId " +
            "AND p.membre.utilisateur.id = :utilisateurId")
    Optional<PermissionMembre> findByGroupeAndUtilisateur(
            Long groupeId, Long utilisateurId
    );
}