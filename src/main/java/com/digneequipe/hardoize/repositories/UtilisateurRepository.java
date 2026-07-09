package com.digneequipe.hardoize.repositories;

import com.digneequipe.hardoize.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByTelephone(String telephone);
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByTelephone(String telephone);
    boolean existsByEmail(String email);

    Optional<Utilisateur> findByUuid(String uuid);
}