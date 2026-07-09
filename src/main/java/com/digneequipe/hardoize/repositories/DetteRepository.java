package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Dette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetteRepository extends JpaRepository<Dette, Long> {

    @Query("SELECT d FROM Dette d WHERE d.groupe.id = :groupeId " +
            "AND d.statut != 'soldee' ORDER BY d.client.score ASC")
    List<Dette> findDettesActives(Long groupeId);

    List<Dette> findByClientIdOrderByCreatedAtDesc(Long clientId);

    @Query("SELECT COALESCE(SUM(d.montantTotal - d.montantRembourse), 0) " +
            "FROM Dette d WHERE d.groupe.id = :groupeId AND d.statut != 'soldee'")
    Double getTotalDettesActives(Long groupeId);

    @Query("SELECT COUNT(d) FROM Dette d WHERE d.groupe.id = :groupeId " +
            "AND d.statut != 'soldee' AND d.dateRemboursement < :now")
    Long getNombreRetards(Long groupeId, LocalDateTime now);

    @Query("SELECT d FROM Dette d WHERE d.statut != 'soldee' " +
            "AND d.dateRemboursement < :now")
    List<Dette> findDettesEnRetard(LocalDateTime now);

    @Modifying
    @Query("UPDATE Dette d SET d.montantRembourse = d.montantRembourse + :montant, " +
            "d.updatedAt = :now WHERE d.id = :id")
    void enregistrerRemboursement(Long id, Double montant, LocalDateTime now);

    @Modifying
    @Query("UPDATE Dette d SET d.statut = 'soldee', d.updatedAt = :now WHERE d.id = :id")
    void solderDette(Long id, LocalDateTime now);

    Optional<Dette> findByUuid(String uuid);
}