package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.models.HistoriqueVente;
import com.digneequipe.hardoize.services.HistoriqueVenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique-ventes")
@RequiredArgsConstructor
public class HistoriqueVenteController {

    private final HistoriqueVenteService service;

    /**
     * Synchronisation depuis SQLite vers Supabase.
     */
    @PostMapping
    public ResponseEntity<List<HistoriqueVente>> synchroniser(
            @RequestBody List<HistoriqueVente> historiques) {

        return ResponseEntity.ok(service.synchroniser(historiques));
    }

    /**
     * Récupérer les historiques d'un groupe.
     */
    @GetMapping("/{groupeId}")
    public ResponseEntity<List<HistoriqueVente>> getByGroupe(
            @PathVariable Long groupeId) {

        return ResponseEntity.ok(service.getByGroupe(groupeId));
    }
}