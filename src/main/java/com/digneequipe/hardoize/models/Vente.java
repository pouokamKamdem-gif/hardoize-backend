package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Vente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<LigneVente> lignes = new ArrayList<>();

    @Column(nullable = false)
    private Double montantTotal;

    @Builder.Default
    private Double beneficeNet = 0.0;

    @Builder.Default
    private String typePaiement = "especes";

    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Client client;

    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Utilisateur utilisateur;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;
}