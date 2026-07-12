package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historique_paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class HistoriquePaiement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;  // "client" | "fournisseur"

    @Column(nullable = false)
    private String sens;  // "entrant" | "sortant"

    @Column(nullable = false)
    private Double montant;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Client client;

    private String nomClient;

    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Fournisseur fournisseur;

    private String nomFournisseur;

    @JoinColumn(name = "dette_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Dette dette;

    @JoinColumn(name = "vente_id")
    @JsonIgnoreProperties({"lignes","hibernateLazyInitializer","handler"})
    private Vente vente;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;
}