package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.services.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Fournisseur>>> getAll(
            @RequestParam Long groupeId,
            @RequestParam(required = false) String q) {
        List<Fournisseur> liste = (q != null && !q.isBlank())
                ? fournisseurService.rechercher(groupeId, q)
                : fournisseurService.getByGroupe(groupeId);
        return ResponseEntity.ok(ApiResponse.ok(liste));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Fournisseur>> creer(
            @Valid @RequestBody FournisseurRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Fournisseur créé", fournisseurService.creer(request))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Fournisseur>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody FournisseurRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Fournisseur modifié", fournisseurService.modifier(id, request))
        );
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<ApiResponse<Void>> desactiver(@PathVariable Long id) {
        fournisseurService.setActif(id, false);
        return ResponseEntity.ok(ApiResponse.ok("Fournisseur désactivé", null));
    }

    // ── Dettes Fournisseurs ───────────────────────────────────
    @GetMapping("/dettes")
    public ResponseEntity<ApiResponse<List<DetteFournisseur>>> getDettes(
            @RequestParam Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(fournisseurService.getDettesActives(groupeId))
        );
    }

    @PostMapping("/dettes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creerDette(
            @Valid @RequestBody DetteFournisseurRequest request) {
        DetteFournisseur dette = fournisseurService.creerDette(request);

        Map<String, Object> dto = new HashMap<>();
        dto.put("id",                dette.getId());
        dto.put("montantTotal",      dette.getMontantTotal());
        dto.put("dateRemboursement", dette.getDateRemboursement());
        dto.put("statut",            dette.getStatut());

        return ResponseEntity.ok(ApiResponse.ok("Dette créée", dto));
    }

    @PatchMapping("/dettes/{id}/rembourser")
    public ResponseEntity<ApiResponse<Void>> rembourser(
            @PathVariable Long id,
            @RequestBody RemboursementRequest request) {
        fournisseurService.rembourserDetteFournisseur(id, request.getMontant());
        return ResponseEntity.ok(ApiResponse.ok("Remboursement enregistré", null));
    }

    @PatchMapping("/dettes/{id}/solder")
    public ResponseEntity<ApiResponse<Void>> solder(@PathVariable Long id) {
        fournisseurService.solderDetteFournisseur(id);
        return ResponseEntity.ok(ApiResponse.ok("Dette soldée", null));
    }
}