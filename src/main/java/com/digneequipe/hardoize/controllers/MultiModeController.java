package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.services.MultiModeService;
import com.digneequipe.hardoize.services.PermissionService;
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
    private final PermissionService permissionService;

    // POST /api/multi/rejoindre
    @PostMapping("/rejoindre")
    public ResponseEntity<ApiResponse<Map<String,Object>>> rejoindre(
            @RequestBody Map<String,String> body,
            Authentication auth) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    "Groupe rejoint",
                    multiService.rejoindreGroupe(
                            body.get("codeQR"),
                            auth.getName(),
                            body.get("nomAffiche")
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/multi/sync/{groupeId}
    @GetMapping("/sync/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> sync(
            @PathVariable Long groupeId,
            @RequestParam(required = false) String depuis) {
        return ResponseEntity.ok(ApiResponse.ok(
                multiService.getSyncData(groupeId, depuis)
        ));
    }

    // POST /api/multi/operation
    @PostMapping("/operation")
    public ResponseEntity<ApiResponse<Map<String,Object>>> operation(
            @RequestBody Map<String,Object> payload,
            Authentication auth) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    "Opération traitée",
                    multiService.traiterOperation(payload, auth.getName())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/multi/dashboard/{groupeId}
    @GetMapping("/dashboard/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> dashboard(
            @PathVariable Long groupeId) {
        return ResponseEntity.ok(ApiResponse.ok(
                multiService.getDashboard(groupeId)
        ));
    }

    // POST /api/multi/deconnecter/{membreUuid}
    @PostMapping("/deconnecter/{membreUuid}")
    public ResponseEntity<ApiResponse<Void>> deconnecter(
            @PathVariable String membreUuid) {
        try {
            // Trouver le membre par UUID
            multiService.deconnecterMembreParUuid(membreUuid);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/multi/permissions/membre/{membreId}
    // Ajouté : GroupesScreen.js (ouvrirPermissions) appelle ce GET
    // pour pré-remplir le modal avant modification. Il n'existait
    // pas encore, donc l'appel échouait systématiquement et le
    // frontend retombait sur les permissions par défaut au lieu
    // des vraies valeurs enregistrées.
    @GetMapping("/permissions/membre/{membreId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getPermissions(
            @PathVariable Long membreId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    multiService.getPermissions(membreId)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/multi/permissions/membre/{membreId}
    // Chemin aligné sur celui utilisé par GroupesScreen.js
    // (sauvegarderPermissions) : auparavant "/permissions/{membreId}",
    // qui ne correspondait à aucun appel réel du frontend.
// PUT /api/multi/permissions/{membreUuid}
    @PutMapping("/permissions/{membreUuid}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> permissions(
            @PathVariable String membreUuid,
            @RequestBody Map<String,Boolean> body) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    "Permissions mises à jour",
                    permissionService.modifierParUuid(membreUuid, body)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/multi/activer/{groupeId}
    @PostMapping("/activer/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> activer(
            @PathVariable Long groupeId) {
        return ResponseEntity.ok(ApiResponse.ok(
                multiService.passerEnModeMulti(groupeId)
        ));
    }

    // POST /api/multi/desactiver/{groupeId}
    @PostMapping("/desactiver/{groupeId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> desactiver(
            @PathVariable Long groupeId) {
        return ResponseEntity.ok(ApiResponse.ok(
                multiService.passerEnModeSolo(groupeId)
        ));
    }
}