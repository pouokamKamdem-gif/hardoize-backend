package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.HistoriqueVente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriqueVenteRepository
        extends JpaRepository<HistoriqueVente, Long> {
}