package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    @Builder.Default
    private Double prixAchat = 0.0;

    @NotNull(message = "Le prix de vente est obligatoire")
    @Positive(message = "Le prix de vente doit être positif")
    @Column(nullable = false)
    private Double prixVente;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantiteStock = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockMinimum = 5;

    private String categorie;
    private String photoUri;

    // Relation avec le groupe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    // Relation avec l'utilisateur créateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estActif = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean syncEnAttente = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}