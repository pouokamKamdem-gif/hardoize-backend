package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    List<Fournisseur> findByGroupeIdAndEstActifTrueOrderByNomAsc(Long groupeId);

    @Query("SELECT f FROM Fournisseur f WHERE f.groupe.id = :groupeId " +
            "AND f.estActif = true " +
            "AND (LOWER(f.nom) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR f.telephone LIKE CONCAT('%', :q, '%'))")
    List<Fournisseur> rechercher(Long groupeId, String q);

    Optional<Fournisseur> findByUuid(String uuid);
}