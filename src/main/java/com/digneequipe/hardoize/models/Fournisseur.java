package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Fournisseur extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String telephone;
    private String email;
    private String adresse;
    private String photoUri;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;

    @Builder.Default
    private Boolean estActif = true;
}