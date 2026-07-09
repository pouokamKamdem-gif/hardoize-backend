package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.HistoriquePaiement;
import com.digneequipe.hardoize.models.HistoriqueVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoriquePaiementRepository extends JpaRepository<HistoriquePaiement, Long> {

    List<HistoriquePaiement> findByGroupeId(Long groupeId);

    Optional<HistoriquePaiement> findByUuid(String uuid);}