package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.VenteRequest;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Vente;
import com.digneequipe.hardoize.services.VenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    // GET /api/ventes?groupeId=1
    @GetMapping
    public ResponseEntity<ApiResponse<List<Vente>>> getAll(
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(venteService.getByGroupe(groupeId))
        );
    }

    // GET /api/ventes/total-jour?groupeId=1
    @GetMapping("/total-jour")
    public ResponseEntity<ApiResponse<Double>> getTotalJour(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(venteService.getTotalJour(groupeId))
        );
    }

    // POST /api/ventes
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @Valid @RequestBody VenteRequest request,
            @RequestBody VenteRequest request,
            Authentication auth) {
        Vente vente = venteService.enregistrer(request, auth.getName());

        // Retourner un Map simple au lieu de l'entité Hibernate
        Map<String, Object> dto = new HashMap<>();
        dto.put("id",           vente.getId());
        dto.put("nomProduit",   vente.getNomProduit());
        dto.put("quantite",     vente.getQuantite());
        dto.put("montantTotal", vente.getMontantTotal());
        dto.put("typePaiement", vente.getTypePaiement());
        dto.put("createdAt",    vente.getCreatedAt());

        Map<String, Object> dto = venteService.enregistrer(
                request, auth.getName()
        );
        return ResponseEntity.ok(ApiResponse.ok("Vente enregistrée", dto));
    }
}