package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.models.HistoriquePaiement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique-paiements")
@RequiredArgsConstructor
public class HistoriquePaiementController {

    private final HistoriquePaiementService service;

    /**
     * Synchronisation SQLite -> Supabase
     */
    @PostMapping
    public ResponseEntity<List<HistoriquePaiement>> synchroniser(
            @RequestBody List<HistoriquePaiement> historiques) {

        return ResponseEntity.ok(service.synchroniser(historiques));
    }

    /**
     * Récupérer les historiques d'un groupe.
     */
    @GetMapping("/{groupeId}")
    public ResponseEntity<List<HistoriquePaiement>> getByGroupe(
            @PathVariable Long groupeId) {

        return ResponseEntity.ok(service.getByGroupe(groupeId));
    }
}