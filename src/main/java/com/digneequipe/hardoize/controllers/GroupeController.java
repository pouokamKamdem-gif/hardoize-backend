package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.services.GroupeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService groupeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Groupe>>> getAll(
            @RequestParam Long proprietaireId) {
        return ResponseEntity.ok(
                ApiResponse.ok(groupeService.getByProprietaire(proprietaireId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Groupe>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(groupeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @Valid @RequestBody GroupeRequest request,
            Authentication auth) {

        Groupe groupe = groupeService.creer(request, auth.getName());

        //DTO simple - pas d'entite JPA directement
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", groupe.getId());
        dto.put("nom", groupe.getNom());
        dto.put("description", groupe.getDescription());
        dto.put("codeQR", groupe.getCodeQR());
        dto.put("heureFermeture", groupe.getHeureFermeture());
        dto.put("createdAt", groupe.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.ok("Groupe cree", dto));
    }

    @GetMapping("/{id}/membres")
    public ResponseEntity<ApiResponse<List<MembreGroupe>>> getMembres(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(groupeService.getMembres(id)));
    }

    @GetMapping("/{id}/membres/connectes")
    public ResponseEntity<ApiResponse<List<MembreGroupe>>> getMembresConnectes(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(groupeService.getMembresConnectes(id))
        );
    }

    @PostMapping("/{id}/membres")
    public ResponseEntity<ApiResponse<MembreGroupe>> ajouterMembre(
            @PathVariable Long id,
            @Valid @RequestBody MembreRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Membre ajouté", groupeService.ajouterMembre(id, request))
        );
    }

    @PatchMapping("/membres/{membreId}")
    public ResponseEntity<ApiResponse<MembreGroupe>> modifierMembre(
            @PathVariable Long membreId,
            @RequestBody MembreRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Membre modifié", groupeService.modifierMembre(membreId, request))
        );
    }

    @PatchMapping("/membres/{membreId}/deconnecter")
    public ResponseEntity<ApiResponse<Void>> deconnecter(
            @PathVariable Long membreId) {
        groupeService.deconnecterMembre(membreId);
        return ResponseEntity.ok(ApiResponse.ok("Membre déconnecté", null));
    }

    @PostMapping("/{id}/deconnecter-tous")
    public ResponseEntity<ApiResponse<Void>> deconnecterTous(
            @PathVariable Long id) {
        groupeService.deconnecterTous(id);
        return ResponseEntity.ok(ApiResponse.ok("Tous déconnectés", null));
    }

    @PostMapping("/rejoindre")
    public ResponseEntity<ApiResponse<MembreGroupe>> rejoindre(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String codeQR     = body.get("codeQR");
        String nomAffiche = body.get("nom");
        String bailHeure  = body.get("bailHeure");

        return ResponseEntity.ok(
                ApiResponse.ok("Groupe rejoint",
                        groupeService.rejoindre(codeQR, auth.getName(), nomAffiche, bailHeure))
        );
    }
}