package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByGroupeIdOrderByCreatedAtDesc(Long groupeId);

    List<Vente> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);

    @Query("SELECT v FROM Vente v WHERE v.groupe.id = :groupeId " +
            "AND v.createdAt BETWEEN :debut AND :fin " +
            "ORDER BY v.createdAt DESC")
    List<Vente> findByPeriode(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM Vente v " +
            "WHERE v.groupe.id = :groupeId " +
            "AND v.createdAt BETWEEN :debut AND :fin")
    Double getTotalVentes(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM Vente v " +
            "WHERE v.groupe.id = :groupeId " +
            "AND v.typePaiement = 'especes' " +
            "AND v.createdAt BETWEEN :debut AND :fin")
    Double getTotalEspeces(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM Vente v " +
            "WHERE v.groupe.id = :groupeId " +
            "AND v.typePaiement = 'credit' " +
            "AND v.createdAt BETWEEN :debut AND :fin")
    Double getTotalCredit(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(l.prixUnitaire * l.quantite - l.produit.prixAchat * l.quantite), 0) " +
            "FROM Vente v JOIN v.lignes l WHERE v.groupe.id = :groupeId " +
            "AND v.createdAt BETWEEN :debut AND :fin")
    Double getBeneficeNet(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    Optional<Vente> findByUuid(String uuid);
}