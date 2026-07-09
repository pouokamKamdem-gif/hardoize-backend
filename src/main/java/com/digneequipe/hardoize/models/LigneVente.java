package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lignes_ventes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    @JsonIgnoreProperties({"lignes","hibernateLazyInitializer","handler"})
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Produit produit;

    @Column(nullable = false)
    private String nomProduit;

    @Column(nullable = false)
    private Integer quantite;

    private Double prixAchat    = 0.0;
    private Double prixUnitaire;
    private Double sousTotal;
    private Double marge        = 0.0;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}