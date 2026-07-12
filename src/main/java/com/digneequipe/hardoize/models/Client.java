package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomClient;

    private String numeroClient;
    private String email;
    private String photoUri;

    @Builder.Default
    private Integer score;

    @JoinColumn(name = "groupe_id")
    @JsonIgnoreProperties({"membres","proprietaire","hibernateLazyInitializer","handler"})
    private Groupe groupe;

    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes","hibernateLazyInitializer","handler"})
    private Utilisateur utilisateur;

    @Builder.Default
    private Boolean estActif = true;
}