package com.digneequipe.hardoize.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Column(nullable = false)
    private String nomClient;

    @NotBlank(message = "Le numero est obligatoire")
    @Column(nullable = false)
    private String numeroClient;

    private String email;
    private String photoUri;

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estActif = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dette> dettes;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isBlank()) {
            uuid = UUID.randomUUID().toString();
        }
    }

    //Retourne la couleur selon le score
    @Transient
    public String getCouleurScore(){
        if (score >= 80) return "#22C55E";
        if (score >= 60) return "#3B82F6";
        if (score >= 40) return "#EAB308";
        if (score >= 20) return "#F97316";
        return "#EF4444";
    }

    @Transient
    public String getStatut(){
        if (score >= 80) return "STABLE";
        if (score >= 40) return "MOYEN";
        return "URGENT";
    }
}
