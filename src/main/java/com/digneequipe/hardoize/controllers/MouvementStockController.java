package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.MouvementStock;
import com.digneequipe.hardoize.services.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementService;

    // GET /api/mouvements-stock?groupeId=1
    @GetMapping
    public ResponseEntity<ApiResponse<List<MouvementStock>>> getAll(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(mouvementService.getByGroupe(groupeId))
        );
    }

    // GET /api/mouvements-stock/produit/{produitId}
    @GetMapping("/produit/{produitId}")
    public ResponseEntity<ApiResponse<List<MouvementStock>>> getByProduit(
            @PathVariable Long produitId) {
        return ResponseEntity.ok(
                ApiResponse.ok(mouvementService.getByProduit(produitId))
        );
    }

    // POST /api/mouvements-stock
    @PostMapping
    public ResponseEntity<ApiResponse<MouvementStock>> enregistrer(
            @RequestBody Map<String, Object> body,
            Authentication auth) {

        Long    produitId     = Long.valueOf(body.get("produitId").toString());
        String  type          = body.get("type").toString();
        String  motif         = body.containsKey("motif")
                ? body.get("motif").toString() : null;
        Integer quantite      = Integer.valueOf(body.get("quantite").toString());
        Double  prixUnitaire  = body.containsKey("prixUnitaire")
                ? Double.valueOf(body.get("prixUnitaire").toString()) : 0.0;
        Long    fournisseurId = body.containsKey("fournisseurId") &&
                body.get("fournisseurId") != null
                ? Long.valueOf(body.get("fournisseurId").toString()) : null;
        Long    groupeId      = body.containsKey("groupeId") &&
                body.get("groupeId") != null
                ? Long.valueOf(body.get("groupeId").toString()) : null;

        MouvementStock mvt = mouvementService.enregistrer(
                produitId, type, motif, quantite,
                prixUnitaire, fournisseurId, groupeId,
                auth.getName()
        );

        return ResponseEntity.ok(ApiResponse.ok("Mouvement enregistré", mvt));
    }
}
