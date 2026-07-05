package com.digneequipe.hardoize.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "dettes_fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetteFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @Column(nullable = false)
    private String nomFournisseur;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double montantTotal;

    @Column(nullable = false)
    @Builder.Default
    private Double montantRembourse = 0.0;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateRemboursement;

    private String motif;

    @Column(nullable = false)
    @Builder.Default
    private String statut = "en_attente";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @Column(nullable = false)
    @Builder.Default
    private Boolean syncEnAttente = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    public Double getMontantRestant() {
        return montantTotal - montantRembourse;
    }

    @Transient
    public boolean estEnRetard() {
        return !statut.equals("soldee") &&
                LocalDateTime.now().isAfter(dateRemboursement);
    }
}