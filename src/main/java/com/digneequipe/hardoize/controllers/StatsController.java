package com.digneequipe.hardoize.controllers;

import com.digneequipe.hardoize.dto.response.*;
import com.digneequipe.hardoize.services.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // GET /api/stats?groupeId=1&periode=semaine
    @GetMapping
    public ResponseEntity<ApiResponse<StatsResponse>> getStats(
            @RequestParam Long groupeId,
            @RequestParam(defaultValue = "semaine") String periode) {

        return ResponseEntity.ok(
                ApiResponse.ok(statsService.getStats(groupeId, periode))
        );
    }
}