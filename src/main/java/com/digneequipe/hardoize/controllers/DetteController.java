package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Dette;
import com.digneequipe.hardoize.services.DetteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dettes")
@RequiredArgsConstructor
public class DetteController {

    private final DetteService detteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Dette>>> getAll(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(detteService.getDettesActives(groupeId))
        );
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<Dette>>> getByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(
                ApiResponse.ok(detteService.getByClient(clientId))
        );
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Double>> getTotal(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(detteService.getTotalDettesActives(groupeId))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody DetteRequest request,
            Authentication auth) {
        try {
            Dette d = detteService.creer(request, auth.getName());
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           d.getId());
            dto.put("montantTotal", d.getMontantTotal());
            dto.put("statut",       d.getStatut());
            return ResponseEntity.ok(ApiResponse.ok("Dette créée", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PatchMapping("/{id}/rembourser")
    public ResponseEntity<ApiResponse<Void>> rembourser(
            @PathVariable Long id,
            @RequestBody RemboursementRequest request) {
        detteService.enregistrerRemboursement(id, request.getMontant());
        return ResponseEntity.ok(ApiResponse.ok("Remboursement enregistré", null));
    }

    @PatchMapping("/{id}/solder")
    public ResponseEntity<ApiResponse<Void>> solder(@PathVariable Long id) {
        detteService.solderDette(id);
        return ResponseEntity.ok(ApiResponse.ok("Dette soldée", null));
    }
}