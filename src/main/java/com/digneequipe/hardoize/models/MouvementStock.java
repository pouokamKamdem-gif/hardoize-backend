package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private String nomProduit;

    @NotBlank
    @Column(nullable = false)
    private String type; // "entree" | "sortie"

    private String motif; // "vente" | "achat" | "retour" | "perte" | "inventaire"

    @NotNull
    @PositiveOrZero
    @Builder.Default
    private Integer quantite = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double prixUnitaire = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double montantTotal = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

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
