package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.DetteFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetteFournisseurRepository extends JpaRepository<DetteFournisseur, Long> {

    @Query("SELECT d FROM DetteFournisseur d WHERE d.groupe.id = :groupeId " +
            "AND d.statut != 'soldee' ORDER BY d.dateRemboursement ASC")
    List<DetteFournisseur> findDettesActives(Long groupeId);

    List<DetteFournisseur> findByFournisseurIdOrderByCreatedAtDesc(Long fournisseurId);

    @Query("SELECT COALESCE(SUM(d.montantTotal - d.montantRembourse), 0) " +
            "FROM DetteFournisseur d WHERE d.groupe.id = :groupeId " +
            "AND d.statut != 'soldee'")
    Double getTotalDettesActives(Long groupeId);

    @Modifying
    @Query("UPDATE DetteFournisseur d SET d.montantRembourse = d.montantRembourse + :montant, " +
            "d.updatedAt = :now WHERE d.id = :id")
    void enregistrerRemboursement(Long id, Double montant, LocalDateTime now);

    @Modifying
    @Query("UPDATE DetteFournisseur d SET d.statut = 'soldee', " +
            "d.updatedAt = :now WHERE d.id = :id")
    void solderDette(Long id, LocalDateTime now);

    Optional<DetteFournisseur> findByUuid(String uuid);
}