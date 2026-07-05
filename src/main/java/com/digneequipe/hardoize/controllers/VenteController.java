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

import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    // GET /api/ventes?groupeId=1
    @GetMapping
    public ResponseEntity<ApiResponse<List<Vente>>> getAll(
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
    public ResponseEntity<ApiResponse<Vente>> creer(
            @Valid @RequestBody VenteRequest request,
            Authentication auth) {
        Vente vente = venteService.enregistrer(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Vente enregistrée", vente));
    }
}