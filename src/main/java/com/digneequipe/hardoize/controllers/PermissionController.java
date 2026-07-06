package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.PermissionMembre;
import com.digneequipe.hardoize.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    // GET /api/permissions/membre/{membreId}
    @GetMapping("/membre/{membreId}")
    public ResponseEntity<ApiResponse<PermissionMembre>> getByMembre(
            @PathVariable Long membreId) {
        return ResponseEntity.ok(
                ApiResponse.ok(permissionService.getByMembre(membreId))
        );
    }

    // PUT /api/permissions/membre/{membreId}
    @PutMapping("/membre/{membreId}")
    public ResponseEntity<ApiResponse<PermissionMembre>> modifier(
            @PathVariable Long membreId,
            @RequestBody Map<String, Boolean> permissions) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Permissions mises à jour",
                        permissionService.modifier(membreId, permissions)
                )
        );
    }
}