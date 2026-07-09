
package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dettes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Dette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    // Dette liée à une vente précise (obligatoire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    @JsonIgnoreProperties({"lignes", "dettes", "hibernateLazyInitializer", "handler"})
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler"})
    private Client client;

    private Double montantTotal;
    private Double montantRembourse = 0.0;
    private Double montantRestant;

    private java.time.LocalDateTime dateRemboursement;
    private java.time.LocalDateTime dateSolde;

    private String statut = "active"; // "active" | "soldee" | "en_retard"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler"})
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres", "proprietaire", "hibernateLazyInitializer", "handler"})
    private Groupe groupe;

    @Column(updatable = false)
    private java.time.LocalDateTime createdAt;

    @UpdateTimestamp
    private java.time.LocalDateTime updatedAt;

    @Column(name = "paiements_json", columnDefinition = "TEXT")
    private String paiementsJson;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isBlank()) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt      = java.time.LocalDateTime.now();
        montantRestant = montantTotal;
    }

    @PreUpdate
    protected void onUpdate() {
        montantRestant = montantTotal - montantRembourse;
    }
}