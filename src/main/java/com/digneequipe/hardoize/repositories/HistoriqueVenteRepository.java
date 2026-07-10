package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.HistoriqueVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoriqueVenteRepository
        extends JpaRepository<HistoriqueVente, Long> {
    Optional<HistoriqueVente> findByUuid(String uuid);

    List<HistoriqueVente> findByGroupeId(Long groupeId);
}