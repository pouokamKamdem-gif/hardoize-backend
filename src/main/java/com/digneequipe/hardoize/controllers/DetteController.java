package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Dette;
import com.digneequipe.hardoize.services.DetteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dettes")
@RequiredArgsConstructor
public class DetteController {

    private final DetteService detteService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody DetteRequest request,
            Authentication auth) {
        try {
            Dette d = detteService.creer(request, auth.getName());
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           d.getId());
            dto.put("montantTotal", d.getMontantTotal());
            dto.put("statut",       d.getStatut());
            return ResponseEntity.ok(ApiResponse.ok("Dette créée", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }
}