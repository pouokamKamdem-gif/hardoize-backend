package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ventes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private String nomProduit;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantite;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double prixUnitaire;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double montantTotal;

    @NotBlank
    @Column(nullable = false)
    private String typePaiement; // "especes" | "credit"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @Column(nullable = false)
    @Builder.Default
    private Boolean syncEnAttente = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}