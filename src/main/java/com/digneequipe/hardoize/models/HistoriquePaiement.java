package com.digneequipe.hardoize.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historique_paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriquePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    private String type;

    private String sens;

    private Double montant;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "fournisseur_id")
    private Long fournisseurId;

    @Column(name = "nom_fournisseur")
    private String nomFournisseur;

    @Column(name = "dette_id")
    private Long detteId;

    @Column(name = "vente_id")
    private Long venteId;

    @Column(name = "groupe_id")
    private Long groupeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isBlank()) {
            uuid = java.util.UUID.randomUUID().toString();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}