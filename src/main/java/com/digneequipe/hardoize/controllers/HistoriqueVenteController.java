package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.HistoriqueVente;
import com.digneequipe.hardoize.repositories.HistoriqueVenteRepository;
import com.digneequipe.hardoize.repositories.GroupeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/historique-ventes")
@RequiredArgsConstructor
public class HistoriqueVenteController {

    private final HistoriqueVenteRepository historiqueRepo;
    private final GroupeRepository          groupeRepo;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> enregistrer(
            @RequestBody Map<String, Object> body) {
        try {
            Long groupeId = body.get("groupeId") != null
                    ? Long.valueOf(body.get("groupeId").toString()) : null;

            HistoriqueVente h = new HistoriqueVente();
            h.setDate(body.getOrDefault("date","").toString());
            h.setTotalVentes(body.get("totalVentes") != null
                    ? Double.valueOf(body.get("totalVentes").toString()) : 0.0);
            h.setTotalEspeces(body.get("totalEspeces") != null
                    ? Double.valueOf(body.get("totalEspeces").toString()) : 0.0);
            h.setTotalCredit(body.get("totalCredit") != null
                    ? Double.valueOf(body.get("totalCredit").toString()) : 0.0);
            h.setBeneficeNet(body.get("beneficeNet") != null
                    ? Double.valueOf(body.get("beneficeNet").toString()) : 0.0);
            h.setNbVentes(body.get("nbVentes") != null
                    ? Integer.valueOf(body.get("nbVentes").toString()) : 0);

            if (groupeId != null) {
                groupeRepo.findById(groupeId).ifPresent(h::setGroupe);
            }

            h = historiqueRepo.save(h);
            return ResponseEntity.ok(ApiResponse.ok("Historique enregistré",
                    Map.of("id", h.getId())));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }
}