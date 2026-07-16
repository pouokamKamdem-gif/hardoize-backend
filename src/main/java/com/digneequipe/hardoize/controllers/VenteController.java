package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.LigneVente;
import com.digneequipe.hardoize.models.Vente;
import com.digneequipe.hardoize.repositories.LigneVenteRepository;
import com.digneequipe.hardoize.repositories.UniteProduitRepository;
import com.digneequipe.hardoize.repositories.VenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenteController {

    private final VenteRepository venteRepo ;
    private final LigneVenteRepository ligneVenteRepo;

    public VenteController(VenteRepository venteRepo, LigneVenteRepository ligneVenteRepo) {
        this.venteRepo = venteRepo;
        this.ligneVenteRepo = ligneVenteRepo;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam Long groupeId,
            @RequestParam(defaultValue = "false") boolean avecLignes) {

        List<Vente> ventes =
                venteRepo.findByGroupeIdOrderByCreatedAtDesc(groupeId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Vente v : ventes) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("uuid",          v.getUuid());
            dto.put("id",            v.getId());
            dto.put("montantTotal",  v.getMontantTotal());
            dto.put("beneficeNet",   v.getBeneficeNet());
            dto.put("typePaiement",  v.getTypePaiement());
            dto.put("clientUuid",    v.getClient() != null
                    ? v.getClient().getUuid() : null);
            dto.put("createdAt",     v.getCreatedAt());

            if (avecLignes) {
                List<LigneVente> lignes =
                        ligneVenteRepo.findByVenteId(v.getId());
                List<Map<String, Object>> lignesDto = new ArrayList<>();
                for (LigneVente l : lignes) {
                    Map<String, Object> lDto = new HashMap<>();
                    lDto.put("uuid",         l.getUuid());
                    lDto.put("produitUuid",  l.getProduit() != null
                            ? l.getProduit().getUuid() : null);
                    lDto.put("nomProduit",   l.getNomProduit());
                    lDto.put("quantite",     l.getQuantite());
                    lDto.put("uniteNom",     l.getUniteNom());
                    lDto.put("uniteFacteur", l.getUniteFacteur());
                    lDto.put("prixAchat",    l.getPrixAchat());
                    lDto.put("prixUnitaire", l.getPrixUnitaire());
                    lDto.put("sousTotal",    l.getSousTotal());
                    lDto.put("marge",        l.getMarge());
                    lignesDto.add(lDto);
                }
                dto.put("lignes", lignesDto);
            }

            result.add(dto);
        }

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
