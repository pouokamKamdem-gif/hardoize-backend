package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Produit;
import com.digneequipe.hardoize.models.UniteProduit;
import com.digneequipe.hardoize.repositories.ProduitRepository;
import com.digneequipe.hardoize.repositories.UniteProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitRepository produitRepo;
    private final UniteProduitRepository uniteProduitRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam Long groupeId) {
        List<Produit> produits = produitRepo.findByGroupeId(groupeId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produit p : produits) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("uuid",          p.getUuid());
            dto.put("id",            p.getId());
            dto.put("nom",           p.getNom());
            dto.put("categorie",     p.getCategorie());
            dto.put("prixAchat",     p.getPrixAchat());
            dto.put("prixVente",     p.getPrixVente());
            dto.put("quantiteStock", p.getQuantiteStock());
            dto.put("stockMinimum",  p.getStockMinimum());
            dto.put("createdAt",     p.getCreatedAt());

            // Inclure les unités
            List<UniteProduit> unites =
                    uniteProduitRepo.findByProduitIdOrderByOrdreAsc(p.getId());
            List<Map<String, Object>> unitesDto = new ArrayList<>();
            for (UniteProduit u : unites) {
                Map<String, Object> uDto = new HashMap<>();
                uDto.put("uuid",         u.getUuid());
                uDto.put("nom",          u.getNom());
                uDto.put("facteur",      u.getFacteur());
                uDto.put("prixAchat",    u.getPrixAchat());
                uDto.put("prixVente",    u.getPrixVente());
                uDto.put("estBase",      u.getEstBase());
                uDto.put("estReference", u.getEstReference());
                uDto.put("ordre",        u.getOrdre());
                unitesDto.add(uDto);
            }
            dto.put("unites", unitesDto);

            result.add(dto);
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}