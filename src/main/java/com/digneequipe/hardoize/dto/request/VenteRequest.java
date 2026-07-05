package com.digneequipe.hardoize.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteRequest {

    @NotNull
    private Long produitId;

    @NotNull @Positive
    private Integer quantite;

    @NotNull @Positive
    private Double prixUnitaire;

    @NotNull @Positive
    private Double montantTotal;

    @NotBlank
    private String typePaiement;

    private Long    clientId;
    private Long    groupeId;
    private String  dateRemboursement;
}