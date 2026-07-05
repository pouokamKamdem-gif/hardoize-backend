package com.digneequipe.hardoize.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "membres_groupe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembreGroupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id", nullable = false)
    private Groupe groupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String nomAffiche;

    private String telephone;

    @Column(nullable = false)
    @Builder.Default
    private String role = "vendeur";

    @Column(nullable = false)
    @Builder.Default
    private String bailHeure = "18:00";

    @Column(nullable = false)
    @Builder.Default
    private Boolean estConnecte = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estActif = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean connexionPermanente = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean syncEnAttente = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
