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

import java.util.List;

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
    public ResponseEntity<ApiResponse<Client>> creer(
            @Valid @RequestBody ClientRequest request,
            Authentication auth) {
        return ResponseEntity.ok(
                ApiResponse.ok("Client créé", clientService.creer(request, auth.getName()))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Client modifié", clientService.modifier(id, request))
        );
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