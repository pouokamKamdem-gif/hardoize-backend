package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    List<MouvementStock> findByGroupeIdOrderByCreatedAtDesc(Long groupeId);

    List<MouvementStock> findByProduitIdOrderByCreatedAtDesc(Long produitId);

    @Query("SELECT COALESCE(SUM(m.montantTotal), 0) FROM MouvementStock m " +
            "WHERE m.groupe.id = :groupeId AND m.type = 'entree' " +
            "AND m.createdAt BETWEEN :debut AND :fin")
    Double getTotalEntrees(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    // Dans chaque Repository, ajoute :
    Optional<MouvementStock> findByUuid(String uuid);
    boolean existsByUuid(String uuid);}