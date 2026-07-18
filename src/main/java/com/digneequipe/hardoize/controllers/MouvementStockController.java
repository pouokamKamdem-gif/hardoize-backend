package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.MouvementStock;
<<<<<<< Updated upstream
import com.digneequipe.hardoize.services.MouvementStockService;
=======
>>>>>>> Stashed changes
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
<<<<<<< Updated upstream
import java.util.List;
=======
>>>>>>> Stashed changes
import java.util.Map;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrer(
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        try {
            Long    produitId    = body.get("produitId") != null
                    ? Long.valueOf(body.get("produitId").toString()) : null;
            String  type         = body.getOrDefault("type","entree").toString();
            String  motif        = body.containsKey("motif")
                    ? body.get("motif").toString() : null;
            Integer quantite     = body.get("quantite") != null
                    ? Integer.valueOf(body.get("quantite").toString()) : 1;
            Double  prixUnitaire = body.get("prixUnitaire") != null
                    ? Double.valueOf(body.get("prixUnitaire").toString()) : 0.0;
            Long    fournisseurId= body.get("fournisseurId") != null
                    ? Long.valueOf(body.get("fournisseurId").toString()) : null;
            Long    groupeId     = body.get("groupeId") != null
                    ? Long.valueOf(body.get("groupeId").toString()) : null;

            MouvementStock mvt = mouvementService.enregistrer(
                    produitId, type, motif, quantite,
                    prixUnitaire, fournisseurId, groupeId,
                    auth.getName()
            );

            Map<String, Object> dto = new HashMap<>();
            dto.put("id",          mvt.getId());
            dto.put("nomProduit",  mvt.getNomProduit());
            dto.put("type",        mvt.getType());
            dto.put("quantite",    mvt.getQuantite());
            dto.put("montantTotal",mvt.getMontantTotal());
            dto.put("createdAt",   mvt.getCreatedAt());
            return ResponseEntity.ok(ApiResponse.ok("Mouvement enregistré", dto));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }
}
