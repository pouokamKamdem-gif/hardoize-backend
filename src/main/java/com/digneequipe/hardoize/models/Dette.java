
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
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @JoinColumn(name = "vente_id", nullable = false)
    @JsonIgnoreProperties({"lignes", "dettes", "hibernateLazyInitializer", "handler"})
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id")
    private Vente vente;
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler"})
    private Client client;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double montantTotal;

    @Column(nullable = false)
    @Builder.Default
    private Double montantRembourse = 0.0;
    private Double montantRestant;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateRemboursement;
    private java.time.LocalDateTime dateRemboursement;
    private java.time.LocalDateTime dateSolde;

    @Column(nullable = false)
    @Builder.Default
    private String statut = "en_attente"; // "en_attente" | "soldee" | "en_retard"
    private String statut = "active"; // "active" | "soldee" | "en_retard"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler"})
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres", "proprietaire", "hibernateLazyInitializer", "handler"})
    private Groupe groupe;

    @Column(nullable = false)
    @Builder.Default
    private Boolean syncEnAttente = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private java.time.LocalDateTime createdAt;

    @Transient
    public Double getMontantRestant() {
        return montantTotal - montantRembourse;
    @PrePersist
    protected void onCreate() {
        createdAt      = java.time.LocalDateTime.now();
        montantRestant = montantTotal;
    }

    @Transient
    public boolean estEnRetard() {
        return !statut.equals("soldee") &&
                LocalDateTime.now().isAfter(dateRemboursement);
    @PreUpdate
    protected void onUpdate() {
        montantRestant = montantTotal - montantRembourse;
    }
}
}