package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.LigneVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LigneVenteRepository
        extends JpaRepository<LigneVente, Long> {

    List<LigneVente> findByVenteId(Long venteId);

    @Query("SELECT SUM(l.marge) FROM LigneVente l " +
            "WHERE l.vente.groupe.id = :groupeId " +
            "AND l.vente.createdAt BETWEEN :debut AND :fin")
    Double getBeneficeNet(Long groupeId,
                          java.time.LocalDateTime debut,
                          java.time.LocalDateTime fin);

    @Query("SELECT SUM(l.sousTotal) FROM LigneVente l " +
            "WHERE l.vente.groupe.id = :groupeId " +
            "AND l.vente.createdAt BETWEEN :debut AND :fin")
    Double getCATotal(Long groupeId,
                      java.time.LocalDateTime debut,
                      java.time.LocalDateTime fin);

    @Query("SELECT l.nomProduit, SUM(l.quantite) as qte, " +
            "SUM(l.sousTotal) as ca " +
            "FROM LigneVente l " +
            "WHERE l.vente.groupe.id = :groupeId " +
            "GROUP BY l.nomProduit " +
            "ORDER BY ca DESC")
    List<Object[]> getTopProduits(Long groupeId);
}