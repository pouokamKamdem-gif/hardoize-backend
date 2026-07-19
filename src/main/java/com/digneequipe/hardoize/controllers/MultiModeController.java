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

    // POST /api/multi/deconnecter/{membreId}
    @PostMapping("/deconnecter/{membreId}")
    public ResponseEntity<ApiResponse<Void>> deconnecter(
            @PathVariable Long membreId) {
        multiService.deconnecterMembre(membreId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // PUT /api/multi/permissions/{membreId}
    @PutMapping("/permissions/{membreId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> permissions(
            @PathVariable Long membreId,
            @RequestBody Map<String,Boolean> body) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Permissions mises à jour",
                multiService.modifierPermissions(membreId, body)
        ));
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