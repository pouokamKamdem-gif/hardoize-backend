package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.HistoriqueVente;
import com.digneequipe.hardoize.models.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoriqueVenteRepository extends JpaRepository<HistoriqueVente, Long> {

    Optional<HistoriqueVente> findByGroupeIdAndDate(Long groupeId, String date);

    List<HistoriqueVente> findByGroupeId(Long groupeId);

    Optional<HistoriqueVente> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
}
