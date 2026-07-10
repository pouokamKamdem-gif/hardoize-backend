package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.ClientRequest;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Client;
import com.digneequipe.hardoize.services.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Client>>> getAll(
            @RequestParam Long groupeId,
            @RequestParam(required = false) String q) {
        List<Client> clients = (q != null && !q.isBlank())
                ? clientService.rechercher(groupeId, q)
                : clientService.getByGroupe(groupeId);
        return ResponseEntity.ok(ApiResponse.ok(clients));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clientService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody ClientRequest request,
            Authentication auth) {
        try {
            Client c = clientService.creer(request, auth.getName());
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           c.getId());
            dto.put("nomClient",    c.getNomClient());
            dto.put("numeroClient", c.getNumeroClient());
            dto.put("score",        c.getScore());
            return ResponseEntity.ok(ApiResponse.ok("Client créé", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> modifier(
            @PathVariable Long id,
            @RequestBody ClientRequest request) {
        try {
            Client c = clientService.modifier(id, request);
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",        c.getId());
            dto.put("nomClient", c.getNomClient());
            return ResponseEntity.ok(ApiResponse.ok("Client modifié", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<ApiResponse<Void>> desactiver(@PathVariable Long id) {
        clientService.setActif(id, false);
        return ResponseEntity.ok(ApiResponse.ok("Client désactivé", null));
    }

    @PatchMapping("/{id}/score")
    public ResponseEntity<ApiResponse<Void>> updateScore(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Integer> body) {
        Integer score = body.get("score");
        if (score != null && score > 0) {
            clientService.incrementerScore(id, score);
        } else if (score != null && score < 0) {
            clientService.decrementerScore(id, Math.abs(score));
        }
        return ResponseEntity.ok(ApiResponse.ok("Score mis à jour", null));
    }
}