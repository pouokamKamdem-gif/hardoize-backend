package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {

    List<Groupe> findByProprietaireIdAndEstActifTrue(Long proprietaireId);

    Optional<Groupe> findByCodeQR(String codeQR);

    Optional<Groupe> findByUuid(String uuid);
}