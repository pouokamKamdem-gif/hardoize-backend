
package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    @Column(nullable = false)
    private String nom;

    private String telephone;
    private String email;
    private String adresse;
    private String photoUri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estActif = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean syncEnAttente = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
