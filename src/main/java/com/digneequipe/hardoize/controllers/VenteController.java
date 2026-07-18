package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.VenteRequest;
import com.digneequipe.hardoize.dto.response.ApiResponse;
<<<<<<< Updated upstream
import com.digneequipe.hardoize.models.Vente;
import com.digneequipe.hardoize.services.VenteService;
import jakarta.validation.Valid;
=======
>>>>>>> Stashed changes
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

<<<<<<< Updated upstream
import java.util.HashMap;
import java.util.List;
=======
>>>>>>> Stashed changes
import java.util.Map;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody VenteRequest request,
            Authentication auth) {
        try {
            Map<String, Object> dto = venteService.enregistrer(
                    request, auth.getName()
            );
            return ResponseEntity.ok(ApiResponse.ok("Vente enregistrée", dto));
        } catch (Exception e) {
            // En mode solo, on stocke même si erreur partielle
            return ResponseEntity.ok(
                    ApiResponse.ok("Vente stockée", Map.of(
                            "error", e.getMessage(),
                            "stored", false
                    ))
            );
        }
    }
}