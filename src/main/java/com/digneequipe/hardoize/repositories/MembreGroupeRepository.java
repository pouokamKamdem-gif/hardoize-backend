package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.MembreGroupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembreGroupeRepository extends JpaRepository<MembreGroupe, Long> {

    List<MembreGroupe> findByGroupeIdAndEstActifTrue(Long groupeId);

    List<MembreGroupe> findByGroupeIdAndEstConnecteTrue(Long groupeId);

    Optional<MembreGroupe> findByGroupeIdAndUtilisateurId(Long groupeId, Long utilisateurId);

    List<MembreGroupe> findByUtilisateurId(Long utilisateurId);

    @Modifying
    @Query("UPDATE MembreGroupe m SET m.estConnecte = false " +
            "WHERE m.groupe.id = :groupeId AND m.connexionPermanente = false")
    void deconnecterTous(Long groupeId);

    Optional<MembreGroupe> findByUuid(String uuid);
}