package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.LigneVente;
import com.digneequipe.hardoize.repositories.LigneVenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lignes-ventes")
@RequiredArgsConstructor
public class LigneVenteController {

    private final LigneVenteRepository ligneRepo;

    @GetMapping("/vente/{venteId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getByVente(
            @PathVariable Long venteId) {

        List<LigneVente> lignes = ligneRepo.findByVenteId(venteId);
        List<Map<String, Object>> dto = new ArrayList<>();

        for (LigneVente l : lignes) {
            Map<String, Object> m = new HashMap<>();
            m.put("id",          l.getId());
            m.put("nomProduit",  l.getNomProduit());
            m.put("quantite",    l.getQuantite());
            m.put("prixAchat",   l.getPrixAchat());
            m.put("prixUnitaire",l.getPrixUnitaire());
            m.put("sousTotal",   l.getSousTotal());
            m.put("marge",       l.getMarge());
            dto.add(m);
        }

        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}