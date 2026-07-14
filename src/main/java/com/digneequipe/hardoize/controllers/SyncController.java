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

    // POST /api/sync/batch — Mode Solo
    // Le frontend envoie tout en une fois, on stocke sans vérifier
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Map<String,Object>>> syncBatch(
            @RequestBody SyncBatchRequest request,
            Authentication auth) {
        try {
            Map<String,Object> result = syncService.syncBatch(
                request, auth.getName());
            return ResponseEntity.ok(
                ApiResponse.ok("Sync réussie", result));
        } catch (Exception e) {
            return ResponseEntity.ok(
                ApiResponse.error("Erreur sync: " + e.getMessage()));
        }
    }

    // GET /api/sync/check/{uuid} — Vérifier si UUID existe
    @GetMapping("/check/{uuid}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> check(
            @PathVariable String uuid) {
        Map<String,Object> result = Map.of(
            "uuid",   uuid,
            "existe", syncService.existsByUuid(uuid)
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}