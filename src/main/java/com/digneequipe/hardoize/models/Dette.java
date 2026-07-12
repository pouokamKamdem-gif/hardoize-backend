package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dettes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Dette extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "vente_id", nullable = false)
    @JsonIgnoreProperties({"lignes","hibernateLazyInitializer","handler"})
    private Vente vente;

    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Client client;

    @Column(nullable = false)
    private Double montantTotal;

    @Builder.Default private Double montantRembourse = 0.0;
    @Builder.Default private Double montantRestant   = 0.0;

    private LocalDateTime dateRemboursement;
    private LocalDateTime dateSolde;

    @Builder.Default
    private String statut = "active";

    private String paiementsJson;

    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Utilisateur utilisateur;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        montantRestant = montantTotal;
    }
}