package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membres_groupe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class MembreGroupe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;

    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Utilisateur utilisateur;

    private String nomAffiche;
    private String telephone;

    @Builder.Default
    private String role = "vendeur";

    @Builder.Default
    private String bailHeure = "18:00";

    @Builder.Default
    private Boolean estConnecte = false;

    @Builder.Default
    private Boolean estActif = true;

    @Builder.Default
    private Boolean connexionPermanente = false;
}