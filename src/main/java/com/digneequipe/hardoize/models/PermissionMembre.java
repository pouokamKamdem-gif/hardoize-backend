package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions_membres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PermissionMembre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MembreGroupe membre;

    // Permissions disponibles
    @Builder.Default
    private Boolean peutVendre       = true;  // accès par défaut

    @Builder.Default
    private Boolean peutVoirDettes   = false;

    @Builder.Default
    private Boolean peutGererStock   = false;

    @Builder.Default
    private Boolean peutVoirStats    = false;

    @Builder.Default
    private Boolean peutGererClients = false;

    @Builder.Default
    private Boolean peutVoirHistorique = false;
}