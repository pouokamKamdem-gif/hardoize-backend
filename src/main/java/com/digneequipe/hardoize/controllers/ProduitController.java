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
    @GetMapping
    public ResponseEntity<ApiResponse<List<Produit>>> getAll(
            @RequestParam Long groupeId,
            @RequestParam(required = false) String q) {

        List<Produit> produits = (q != null && !q.isBlank())
                ? produitService.rechercher(groupeId, q)
                : produitService.getByGroupe(groupeId);

        return ResponseEntity.ok(ApiResponse.ok(produits));
    }

    // GET /api/produits/stock-faible?groupeId=1
    @GetMapping("/stock-faible")
    public ResponseEntity<ApiResponse<List<Produit>>> getStockFaible(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(produitService.getStockFaible(groupeId))
        );
    }

    // POST /api/produits
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

    // PATCH /api/produits/{id}/desactiver
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<ApiResponse<Void>> desactiver(@PathVariable Long id) {
        produitService.setActif(id, false);
        return ResponseEntity.ok(ApiResponse.ok("Produit désactivé", null));
    }

    // PATCH /api/produits/{id}/reactiver
    @PatchMapping("/{id}/reactiver")
    public ResponseEntity<ApiResponse<Void>> reactiver(@PathVariable Long id) {
        produitService.setActif(id, true);
        return ResponseEntity.ok(ApiResponse.ok("Produit réactivé", null));
    }

    // PATCH /api/produits/{id}/stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Produit>> entreeStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer quantite = body.get("quantite");
        if (quantite == null || quantite <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Quantité invalide"));
        }
        return ResponseEntity.ok(
                ApiResponse.ok("Stock mis à jour", produitService.entreeStock(id, quantite))
        );
    }
}