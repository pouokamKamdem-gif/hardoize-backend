package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lignes_ventes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    @JsonIgnoreProperties({"lignes", "hibernateLazyInitializer", "handler"})
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Produit produit;

    @Column(nullable = false)
    private String nomProduit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false)
    private Double prixAchat;    // prix au moment de la vente

    @Column(nullable = false)
    private Double prixUnitaire; // prix de vente unitaire
    private Double prixVente; // prix de vente unitaire

    @Column(nullable = false)
    private Double sousTotal;    // quantite * prixUnitaire

    @Column(nullable = false)
    private Double marge;        // (prixUnitaire - prixAchat) * quantite
}