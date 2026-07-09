package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByGroupeIdAndEstActifTrueOrderByScoreAsc(Long groupeId);

    Optional<Client> findByNumeroClientAndGroupeId(String numero, Long groupeId);

    @Query("SELECT c FROM Client c WHERE c.groupe.id = :groupeId " +
            "AND c.estActif = true " +
            "AND (LOWER(c.nomClient) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR c.numeroClient LIKE CONCAT('%', :q, '%'))")
    List<Client> rechercher(Long groupeId, String q);

    @Query("SELECT AVG(c.score) FROM Client c WHERE c.groupe.id = :groupeId AND c.estActif = true")
    Double getScoreMoyen(Long groupeId);

    @Modifying
    @Query("UPDATE Client c SET c.score = GREATEST(0, c.score - :pts) WHERE c.id = :id")
    void decrementerScore(Long id, Integer pts);

    @Modifying
    @Query("UPDATE Client c SET c.score = LEAST(100, c.score + :pts) WHERE c.id = :id")
    void incrementerScore(Long id, Integer pts);

    Optional<Client> findByUuid(String uuid);
}
