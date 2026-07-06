package com.digneequipe.hardoize.models;

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
    private Boolean peutVendre       = true;  // accès par défaut
    private Boolean peutVoirDettes   = false;
    private Boolean peutGererStock   = false;
    private Boolean peutVoirStats    = false;
    private Boolean peutGererClients = false;
    private Boolean peutVoirHistorique = false;
}