package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.SyncBatchRequest;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.services.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    // POST /api/sync/batch
    // Reçoit toutes les données à synchroniser en une seule requête
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> syncBatch(
            @RequestBody SyncBatchRequest request,
            Authentication auth) {

        Map<String, Object> result = syncService.syncBatch(
                request, auth.getName()
        );
        return ResponseEntity.ok(ApiResponse.ok("Sync réussie", result));
    }

    // GET /api/sync/status/{uuid}
    // Vérifie si un UUID existe déjà côté serveur
    @GetMapping("/status/{uuid}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUuid(
            @PathVariable String uuid) {
        Map<String, Object> status = syncService.checkUuid(uuid);
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}