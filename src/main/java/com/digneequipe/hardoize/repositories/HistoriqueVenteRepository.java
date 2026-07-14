// Pattern identique pour tous — exemple GroupeRepository :
package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// HistoriqueVenteRepository
public interface HistoriqueVenteRepository extends JpaRepository<HistoriqueVente, Long> {
    Optional<HistoriqueVente> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
    Optional<HistoriqueVente> findByGroupeIdAndDate(Long groupeId, String date);
}