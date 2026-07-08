package com.digneequipe.hardoize.models;

import com.digneequipe.hardoize.models.Client;
import com.digneequipe.hardoize.models.Groupe;
import com.digneequipe.hardoize.models.LigneVente;
import com.digneequipe.hardoize.models.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    // Relation avec les lignes
    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"vente", "hibernateLazyInitializer", "handler"})
    @Builder.Default
    private List<LigneVente> lignes = new ArrayList<>();

    // Montant total calculé depuis les lignes
    private Double montantTotal;

    // Bénéfice net calculé depuis les lignes
    private Double beneficeNet;

    // Mode paiement global
    private String typePaiement; // "especes" | "credit" | "mixte"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler"})
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler"})
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres", "proprietaire", "hibernateLazyInitializer", "handler"})
    private Groupe groupe;

    @Column(updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}