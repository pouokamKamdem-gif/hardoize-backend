package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Produit;
import com.digneequipe.hardoize.services.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    // GET /api/produits?groupeId=1
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody ProduitRequest request,
            Authentication auth) {
        try {
            Produit p = produitService.creer(request, auth.getName());
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           p.getId());
            dto.put("nom",          p.getNom());
            dto.put("prixAchat",    p.getPrixAchat());
            dto.put("prixVente",    p.getPrixVente());
            dto.put("quantiteStock",p.getQuantiteStock());
            dto.put("createdAt",    p.getCreatedAt());
            return ResponseEntity.ok(ApiResponse.ok("Produit créé", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> modifier(
            @PathVariable Long id,
            @RequestBody ProduitRequest request) {
        try {
            Produit p = produitService.modifier(id, request);
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",    p.getId());
            dto.put("nom",   p.getNom());
            return ResponseEntity.ok(ApiResponse.ok("Produit modifié", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }
}