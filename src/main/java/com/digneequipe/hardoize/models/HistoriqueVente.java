package com.digneequipe.hardoize.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "historique_ventes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"groupe_id", "date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(name = "groupe_id", nullable = false)
    private Long groupeId;

    private String date;

    @Column(name = "total_ventes")
    private Double totalVentes = 0.0;

    @Column(name = "total_especes")
    private Double totalEspeces = 0.0;

    @Column(name = "total_credit")
    private Double totalCredit = 0.0;

    @Column(name = "benefice_net")
    private Double beneficeNet = 0.0;

    @Column(name = "nb_ventes")
    private Integer nbVentes = 0;

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