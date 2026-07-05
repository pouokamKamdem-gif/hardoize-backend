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
    public ResponseEntity<ApiResponse<Produit>> creer(
            @Valid @RequestBody ProduitRequest request,
            Authentication auth) {
        Produit produit = produitService.creer(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Produit créé", produit));
    }

    // PUT /api/produits/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Produit>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Produit modifié", produitService.modifier(id, request))
        );
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