package com.digneequipe.hardoize.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.Data;
import java.util.List;

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
    private List<LigneVenteRequest> lignes; // panier multi-produits
    private String   typePaiement;   // "especes" | "credit" | "mixte"
    private Long     clientId;
    private Long     groupeId;
    private String   dateRemboursement; // si credit, format JJ/MM/AAAA
    private Double   montantEspeces;    // si mixte

    @Data
    public static class LigneVenteRequest {
        private Long    produitId;
        private String  nomProduit;
        private Integer quantite;
        private Double  prixUnitaire;
        private Double  prixAchat;
    }
}