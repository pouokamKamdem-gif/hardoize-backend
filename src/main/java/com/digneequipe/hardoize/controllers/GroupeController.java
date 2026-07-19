package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.services.GroupeService;
import com.digneequipe.hardoize.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService     groupeService;
    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String,Object>>> creer(
            @RequestBody Map<String,Object> body,
            Authentication auth) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    "Groupe créé",
                    groupeService.creer(body, auth.getName())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> modifier(
            @PathVariable Long id,
            @RequestBody Map<String,Object> body) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    "Groupe modifié",
                    groupeService.modifier(id, body)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getAll(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.getByProprietaire(auth.getName())
        ));
    }

    @GetMapping("/{id}/membres")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getMembres(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.getMembres(id)
        ));
    }

    @PutMapping("/membres/{membreId}/permissions")
    public ResponseEntity<ApiResponse<Map<String,Object>>> modifierPermissions(
            @PathVariable Long membreId,
            @RequestBody Map<String,Boolean> permissions) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Permissions mises à jour",
                permissionService.modifier(membreId, permissions)
        ));
    }

    @GetMapping("/membres/{membreId}/permissions")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getPermissions(
            @PathVariable Long membreId) {
        return ResponseEntity.ok(ApiResponse.ok(
                permissionService.getByMembre(membreId)
        ));
    }
}