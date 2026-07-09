package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "membres_groupe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MembreGroupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id", nullable = false)
    @JsonIgnoreProperties({"membres", "proprietaire", "hibernateLazyInitializer", "handler"})
    private Groupe groupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    @JsonIgnoreProperties({"groupes", "hibernateLazyInitializer", "handler" })
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

    @Column(name = "permissions_json", columnDefinition = "TEXT")
    private String permissionsJson;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isBlank()) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
