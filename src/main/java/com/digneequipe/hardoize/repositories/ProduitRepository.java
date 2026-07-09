package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    List<Produit> findByGroupeIdAndEstActifTrueOrderByNomAsc(Long groupeId);

    @Query("SELECT p FROM Produit p WHERE p.groupe.id = :groupeId " +
            "AND p.estActif = true " +
            "AND LOWER(p.nom) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Produit> rechercherParNom(Long groupeId, String query);

    @Query("SELECT p FROM Produit p WHERE p.groupe.id = :groupeId " +
            "AND p.estActif = true " +
            "AND p.quantiteStock <= p.stockMinimum")
    List<Produit> findStockFaible(Long groupeId);

    @Modifying
    @Query("UPDATE Produit p SET p.quantiteStock = p.quantiteStock - :qte WHERE p.id = :id")
    void decrementerStock(Long id, Integer qte);

    @Modifying
    @Query("UPDATE Produit p SET p.quantiteStock = p.quantiteStock + :qte WHERE p.id = :id")
    void incrementerStock(Long id, Integer qte);

    Optional<Produit> findByUuid(String uuid);
}