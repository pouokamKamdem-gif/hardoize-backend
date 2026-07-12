package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mouvements_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class MouvementStock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Produit produit;

    private String nomProduit;

    @Column(nullable = false)
    private String type;

    private String  motif;

    @Column(nullable = false)
    private Integer quantite;

    @Builder.Default private Double prixUnitaire = 0.0;
    @Builder.Default private Double montantTotal  = 0.0;
    @Builder.Default private Double montantPaye   = 0.0;

    private String modePaiement;

    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Fournisseur fournisseur;

    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Utilisateur utilisateur;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;
}