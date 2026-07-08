
package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @Column(updatable = true)
    private java.time.LocalDateTime updatedAt;

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