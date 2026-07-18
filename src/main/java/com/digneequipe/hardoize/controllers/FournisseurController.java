package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.*;
<<<<<<< Updated upstream
import com.digneequipe.hardoize.services.FournisseurService;
import jakarta.validation.Valid;
=======
>>>>>>> Stashed changes
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody FournisseurRequest request) {
        try {
            Fournisseur f = fournisseurService.creer(request);
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",  f.getId());
            dto.put("nom", f.getNom());
            return ResponseEntity.ok(ApiResponse.ok("Fournisseur créé", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> modifier(
            @PathVariable Long id,
            @RequestBody FournisseurRequest request) {
        try {
            Fournisseur f = fournisseurService.modifier(id, request);
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",  f.getId());
            dto.put("nom", f.getNom());
            return ResponseEntity.ok(ApiResponse.ok("Fournisseur modifié", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PostMapping("/dettes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> creerDette(
            @RequestBody DetteFournisseurRequest request) {
        try {
            DetteFournisseur df = fournisseurService.creerDette(request);
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           df.getId());
            dto.put("montantTotal", df.getMontantTotal());
            dto.put("statut",       df.getStatut());
            return ResponseEntity.ok(ApiResponse.ok("Dette créée", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
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