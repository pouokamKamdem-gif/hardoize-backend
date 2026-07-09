package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.LigneVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LigneVenteRepository
        extends JpaRepository<LigneVente, Long> {

    List<LigneVente> findByVenteId(Long venteId);

    @Query("SELECT SUM(l.marge) FROM LigneVente l " +
            "WHERE l.vente.groupe.id = :groupeId " +
            "AND l.vente.createdAt BETWEEN :debut AND :fin")
    Double getBeneficeNet(Long groupeId,
                          LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT SUM(l.sousTotal) FROM LigneVente l " +
            "WHERE l.vente.groupe.id = :groupeId " +
            "AND l.vente.createdAt BETWEEN :debut AND :fin")
    Double getCATotal(Long groupeId,
                      LocalDateTime debut, LocalDateTime fin);

    // Dans chaque Repository, ajoute :
    Optional<LigneVente> findByUuid(String uuid);
    boolean existsByUuid(String uuid);}