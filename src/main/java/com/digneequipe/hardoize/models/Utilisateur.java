package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le telephone est obligatoire")
    @Column(nullable = false)
    private String telephone;

    @Email(message = "Email invalide")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    @Builder.Default
    private String role = "vendeur";

    private String photoUri;

    @Column(nullable = false)
    @Builder.Default
    private Float evaluation = 5.0f;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estActif = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    //Relation avec les groupes dont il est proprietaire
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Groupe> groupes;
}
