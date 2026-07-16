package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.services.MultiModeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/multi")
@RequiredArgsConstructor
public class MultiModeController {

    private final MultiModeService multiService;

    // ── Rejoindre un groupe ───────────────────────────────────
    // POST /api/multi/rejoindre
    @PostMapping("/rejoindre")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rejoindre(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        try {
            Map<String, Object> result = multiService.rejoindreGroupe(
                    body.get("codeQR"),
                    auth.getName(),
                    body.get("nomAffiche"),
                    body.get("bailHeure")
            );
            return ResponseEntity.ok(
                    ApiResponse.ok("Groupe rejoint", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── Polling sync (toutes les 30s) ─────────────────────────
    // GET /api/multi/sync/{groupeId}
    @GetMapping("/sync/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sync(
            @PathVariable Long groupeId,
            @RequestParam(required = false) String depuis) {
        Map<String, Object> data = multiService.getSyncData(
                groupeId, depuis);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    // ── Exécuter une opération (vente, stock...) ──────────────
    // POST /api/multi/operation
    @PostMapping("/operation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> operation(
            @RequestBody Map<String, Object> payload,
            Authentication auth) {
        try {
            Long groupeId = Long.valueOf(
                    payload.get("groupeId").toString());
            Map<String, Object> result = multiService.traiterOperation(
                    payload, auth.getName(), groupeId);
            return ResponseEntity.ok(
                    ApiResponse.ok("Opération traitée", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── Dashboard propriétaire ────────────────────────────────
    // GET /api/multi/dashboard/{groupeId}
    @GetMapping("/dashboard/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard(
            @PathVariable Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(multiService.getDashboard(groupeId)));
    }

    // ── Déconnecter un membre ─────────────────────────────────
    // POST /api/multi/deconnecter/{membreId}
    @PostMapping("/deconnecter/{membreId}")
    public ResponseEntity<ApiResponse<Void>> deconnecter(
            @PathVariable Long membreId) {
        multiService.deconnecterMembre(membreId);
        return ResponseEntity.ok(ApiResponse.ok("Membre déconnecté", null));
    }

    // ── Modifier les permissions ──────────────────────────────
    // PUT /api/multi/permissions/{membreId}
    @PutMapping("/permissions/{membreId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> permissions(
            @PathVariable Long membreId,
            @RequestBody Map<String, Boolean> permissions) {
        Map<String, Object> result =
                multiService.modifierPermissions(membreId, permissions);
        return ResponseEntity.ok(
                ApiResponse.ok("Permissions mises à jour", result));
    }

    // ── Passer en mode multi ──────────────────────────────────
    // POST /api/multi/activer/{groupeId}
    @PostMapping("/activer/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> activer(
            @PathVariable Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(multiService.passerEnModeMulti(groupeId)));
    }

    // ── Passer en mode solo ───────────────────────────────────
    // POST /api/multi/desactiver/{groupeId}
    @PostMapping("/desactiver/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> desactiver(
            @PathVariable Long groupeId) {
        return ResponseEntity.ok(
                ApiResponse.ok(multiService.passerEnModeSolo(groupeId)));
    }
}