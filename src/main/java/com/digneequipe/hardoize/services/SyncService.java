package com.digneequipe.hardoize.services;

import com.digneequipe.hardoize.dto.request.SyncBatchRequest;
import com.digneequipe.hardoize.models.*;
import com.digneequipe.hardoize.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final GroupeRepository           groupeRepo;
    private final MembreGroupeRepository     membreRepo;
    private final ProduitRepository          produitRepo;
    private final ClientRepository           clientRepo;
    private final VenteRepository            venteRepo;
    private final LigneVenteRepository       ligneVenteRepo;
    private final DetteRepository            detteRepo;
    private final FournisseurRepository      fournisseurRepo;
    private final DetteFournisseurRepository detteFournisseurRepo;
    private final MouvementStockRepository   mouvementRepo;
    private final UtilisateurRepository      utilisateurRepo;
    private final PermissionMembreRepository permissionRepo;

    // ── Vérifier si un UUID existe ────────────────────────────
    public Map<String, Object> checkUuid(String uuid) {
        Map<String, Object> result = new HashMap<>();

        boolean existe =
                groupeRepo.existsByUuid(uuid)           ||
                        produitRepo.existsByUuid(uuid)           ||
                        clientRepo.existsByUuid(uuid)            ||
                        venteRepo.existsByUuid(uuid)             ||
                        detteRepo.existsByUuid(uuid)             ||
                        fournisseurRepo.existsByUuid(uuid)       ||
                        mouvementRepo.existsByUuid(uuid);

        result.put("uuid",   uuid);
        result.put("existe", existe);
        return result;
    }

    // ── Sync batch principale ─────────────────────────────────
    @Transactional
    public Map<String, Object> syncBatch(SyncBatchRequest request,
                                         String telephone) {
        Utilisateur utilisateur = utilisateurRepo
                .findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Map<String, Object> result    = new HashMap<>();
        Map<String, Long>   uuidToId  = new HashMap<>();

        int total = 0;

        // Ordre respectant les dépendances FK
        total += syncGroupes(request.getGroupes(), utilisateur, uuidToId);
        total += syncMembres(request.getMembres(), utilisateur, uuidToId);
        total += syncFournisseurs(request.getFournisseurs(), utilisateur, uuidToId);
        total += syncProduits(request.getProduits(), utilisateur, uuidToId);
        total += syncClients(request.getClients(), utilisateur, uuidToId);
        total += syncVentes(request.getVentes(), utilisateur, uuidToId);
        total += syncLignesVentes(request.getLignesVentes(), uuidToId);
        total += syncDettes(request.getDettes(), utilisateur, uuidToId);
        total += syncDettesFournisseurs(request.getDettesFournisseurs(), uuidToId);
        total += syncMouvements(request.getMouvementsStock(), utilisateur, uuidToId);
        total += syncHistoriqueVentes(request.getHistoriqueVentes(), uuidToId);
        total += syncHistoriquePaiements(request.getHistoriquePaiements(), uuidToId);

        result.put("synced",    total);
        result.put("uuidToId",  uuidToId);
        result.put("timestamp", LocalDateTime.now().toString());
        return result;
    }

    // ── Helper : extraire une valeur de la Map ────────────────
    private String  str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? v.toString() : null;
    }
    private Double  dbl(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? Double.parseDouble(v.toString()) : null;
    }
    private Integer intVal(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v != null ? Integer.parseInt(v.toString()) : null;
    }
    private Boolean bool(Map<String, Object> m, String k) {
        Object v = m.get(k);
        if (v == null) return null;
        return Boolean.parseBoolean(v.toString());
    }
    private LocalDateTime dateTime(Map<String, Object> m, String k) {
        String s = str(m, k);
        if (s == null) return null;
        try {
            // Timestamp en ms
            long ms = Long.parseLong(s);
            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(ms), ZoneId.systemDefault()
            );
        } catch (NumberFormatException e) {
            try {
                return LocalDateTime.parse(s,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ex) {
                return null;
            }
        }
    }
    private Groupe trouverGroupe(Map<String, Object> m,
                                 String cleUuid,
                                 Map<String, Long> uuidToId) {
        String uuid = str(m, cleUuid);
        if (uuid == null) return null;
        Long id = uuidToId.get(uuid);
        if (id != null) return groupeRepo.findById(id).orElse(null);
        return groupeRepo.findByUuid(uuid).orElse(null);
    }

    // ── Sync Groupes ──────────────────────────────────────────
    private int syncGroupes(List<Map<String, Object>> items,
                            Utilisateur utilisateur,
                            Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            Groupe groupe = groupeRepo.findByUuid(uuid)
                    .orElse(new Groupe());

            groupe.setUuid(uuid);
            groupe.setNom(str(item, "nom"));
            groupe.setDescription(str(item, "description"));
            groupe.setCodeQR(str(item, "codeQR"));
            groupe.setHeureFermeture(
                    str(item, "heureFermeture") != null
                            ? str(item, "heureFermeture") : "18:00"
            );
            groupe.setProprietaire(utilisateur);
            groupe.setEstActif(true);

            groupe = groupeRepo.save(groupe);
            uuidToId.put(uuid, groupe.getId());
            count++;
        }
        return count;
    }

    // ── Sync Membres ──────────────────────────────────────────
    private int syncMembres(List<Map<String, Object>> items,
                            Utilisateur utilisateur,
                            Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            MembreGroupe membre = membreRepo.findByUuid(uuid)
                    .orElse(new MembreGroupe());

            membre.setUuid(uuid);
            membre.setNomAffiche(str(item, "nomAffiche"));
            membre.setTelephone(str(item, "telephone"));
            membre.setRole(str(item, "role") != null
                    ? str(item, "role") : "vendeur");
            membre.setBailHeure(str(item, "bailHeure") != null
                    ? str(item, "bailHeure") : "18:00");
            membre.setEstConnecte(true);
            membre.setConnexionPermanente(
                    bool(item, "connexionPermanente") != null
                            ? bool(item, "connexionPermanente") : false
            );
            membre.setUtilisateur(utilisateur);
            membre.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));

            membre = membreRepo.save(membre);
            uuidToId.put(uuid, membre.getId());
            count++;
        }
        return count;
    }

    // ── Sync Fournisseurs ─────────────────────────────────────
    private int syncFournisseurs(List<Map<String, Object>> items,
                                 Utilisateur utilisateur,
                                 Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            Fournisseur f = fournisseurRepo.findByUuid(uuid)
                    .orElse(new Fournisseur());

            f.setUuid(uuid);
            f.setNom(str(item, "nom"));
            f.setTelephone(str(item, "telephone"));
            f.setEmail(str(item, "email"));
            f.setAdresse(str(item, "adresse"));
            f.setPhotoUri(str(item, "photoUri"));
            f.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));
            f.setEstActif(true);

            f = fournisseurRepo.save(f);
            uuidToId.put(uuid, f.getId());
            count++;
        }
        return count;
    }

    // ── Sync Produits ─────────────────────────────────────────
    private int syncProduits(List<Map<String, Object>> items,
                             Utilisateur utilisateur,
                             Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            Produit p = produitRepo.findByUuid(uuid)
                    .orElse(new Produit());

            p.setUuid(uuid);
            p.setNom(str(item, "nom"));
            p.setCategorie(str(item, "categorie"));
            p.setPrixAchat(dbl(item, "prixAchat") != null
                    ? dbl(item, "prixAchat") : 0.0);
            p.setPrixVente(dbl(item, "prixVente") != null
                    ? dbl(item, "prixVente") : 0.0);
            p.setQuantiteStock(intVal(item, "quantiteStock") != null
                    ? intVal(item, "quantiteStock") : 0);
            p.setStockMinimum(intVal(item, "stockMinimum") != null
                    ? intVal(item, "stockMinimum") : 5);
            p.setPhotoUri(str(item, "photoUri"));
            p.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));
            p.setUtilisateur(utilisateur);
            p.setEstActif(true);

            // Fournisseur par défaut
            String forunUuid = str(item, "fournisseurUuid");
            if (forunUuid != null) {
                fournisseurRepo.findByUuid(forunUuid)
                        .ifPresent(p::setFournisseur);
            }

            p = produitRepo.save(p);
            uuidToId.put(uuid, p.getId());
            count++;
        }
        return count;
    }

    // ── Sync Clients ──────────────────────────────────────────
    private int syncClients(List<Map<String, Object>> items,
                            Utilisateur utilisateur,
                            Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            Client c = clientRepo.findByUuid(uuid)
                    .orElse(new Client());

            c.setUuid(uuid);
            c.setNomClient(str(item, "nomClient"));
            c.setNumeroClient(str(item, "numeroClient"));
            c.setEmail(str(item, "email"));
            c.setPhotoUri(str(item, "photoUri"));
            c.setScore(intVal(item, "score") != null
                    ? intVal(item, "score") : 100);
            c.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));
            c.setUtilisateur(utilisateur);
            c.setEstActif(true);

            c = clientRepo.save(c);
            uuidToId.put(uuid, c.getId());
            count++;
        }
        return count;
    }

    // ── Sync Ventes ───────────────────────────────────────────
    private int syncVentes(List<Map<String, Object>> items,
                           Utilisateur utilisateur,
                           Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            if (venteRepo.existsByUuid(uuid)) {
                Long id = venteRepo.findByUuid(uuid)
                        .map(v -> v.getId()).orElse(null);
                if (id != null) uuidToId.put(uuid, id);
                continue; // Vente déjà synced
            }

            Vente v = new Vente();
            v.setUuid(uuid);
            v.setMontantTotal(dbl(item, "montantTotal") != null
                    ? dbl(item, "montantTotal") : 0.0);
            v.setBeneficeNet(dbl(item, "beneficeNet") != null
                    ? dbl(item, "beneficeNet") : 0.0);
            v.setTypePaiement(str(item, "typePaiement") != null
                    ? str(item, "typePaiement") : "especes");
            v.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));
            v.setUtilisateur(utilisateur);

            // Client
            String clientUuid = str(item, "clientUuid");
            if (clientUuid != null) {
                clientRepo.findByUuid(clientUuid).ifPresent(v::setClient);
            }

            v = venteRepo.save(v);
            uuidToId.put(uuid, v.getId());
            count++;
        }
        return count;
    }

    // ── Sync Lignes Ventes ────────────────────────────────────
    private int syncLignesVentes(List<Map<String, Object>> items,
                                 Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            if (ligneVenteRepo.existsByUuid(uuid)) continue;

            // Trouver la vente parente
            String venteUuid = str(item, "venteUuid");
            if (venteUuid == null) continue;

            Vente vente = null;
            Long venteId = uuidToId.get(venteUuid);
            if (venteId != null) {
                vente = venteRepo.findById(venteId).orElse(null);
            } else {
                vente = venteRepo.findByUuid(venteUuid).orElse(null);
            }
            if (vente == null) continue;

            LigneVente l = new LigneVente();
            l.setUuid(uuid);
            l.setVente(vente);
            l.setNomProduit(str(item, "nomProduit"));
            l.setQuantite(intVal(item, "quantite") != null
                    ? intVal(item, "quantite") : 1);
            l.setPrixAchat(dbl(item, "prixAchat") != null
                    ? dbl(item, "prixAchat") : 0.0);
            l.setPrixUnitaire(dbl(item, "prixUnitaire") != null
                    ? dbl(item, "prixUnitaire") : 0.0);
            l.setSousTotal(dbl(item, "sousTotal") != null
                    ? dbl(item, "sousTotal") : 0.0);
            l.setMarge(dbl(item, "marge") != null
                    ? dbl(item, "marge") : 0.0);

            // Produit
            String produitUuid = str(item, "produitUuid");
            if (produitUuid != null) {
                produitRepo.findByUuid(produitUuid)
                        .ifPresent(l::setProduit);
            }

            ligneVenteRepo.save(l);
            count++;
        }
        return count;
    }

    // ── Sync Dettes ───────────────────────────────────────────
    private int syncDettes(List<Map<String, Object>> items,
                           Utilisateur utilisateur,
                           Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            Dette d = detteRepo.findByUuid(uuid)
                    .orElse(new Dette());

            d.setUuid(uuid);
            d.setMontantTotal(dbl(item, "montantTotal") != null
                    ? dbl(item, "montantTotal") : 0.0);
            d.setMontantRembourse(dbl(item, "montantRembourse") != null
                    ? dbl(item, "montantRembourse") : 0.0);
            d.setMontantRestant(dbl(item, "montantRestant") != null
                    ? dbl(item, "montantRestant")
                    : d.getMontantTotal() - d.getMontantRembourse());
            d.setStatut(str(item, "statut") != null
                    ? str(item, "statut") : "active");
            d.setDateRemboursement(dateTime(item, "dateRemboursement"));
            d.setPaiementsJson(str(item, "paiementsJson"));
            d.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));
            d.setUtilisateur(utilisateur);

            // Vente parente
            String venteUuid = str(item, "venteUuid");
            if (venteUuid != null) {
                Vente vente = null;
                Long venteId = uuidToId.get(venteUuid);
                if (venteId != null) {
                    vente = venteRepo.findById(venteId).orElse(null);
                } else {
                    vente = venteRepo.findByUuid(venteUuid).orElse(null);
                }
                d.setVente(vente);
            }

            // Client
            String clientUuid = str(item, "clientUuid");
            if (clientUuid != null) {
                clientRepo.findByUuid(clientUuid).ifPresent(d::setClient);
            }

            d = detteRepo.save(d);
            uuidToId.put(uuid, d.getId());
            count++;
        }
        return count;
    }

    // ── Sync Dettes Fournisseurs ──────────────────────────────
    private int syncDettesFournisseurs(List<Map<String, Object>> items,
                                       Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            DetteFournisseur df = detteFournisseurRepo.findByUuid(uuid)
                    .orElse(new DetteFournisseur());

            df.setUuid(uuid);
            df.setNomFournisseur(str(item, "nomFournisseur"));
            df.setMontantTotal(dbl(item, "montantTotal") != null
                    ? dbl(item, "montantTotal") : 0.0);
            df.setMontantRembourse(dbl(item, "montantRembourse") != null
                    ? dbl(item, "montantRembourse") : 0.0);
            df.setMontantRestant(dbl(item, "montantRestant") != null
                    ? dbl(item, "montantRestant")
                    : df.getMontantTotal() - df.getMontantRembourse());
            df.setMotif(str(item, "motif"));
            df.setStatut(str(item, "statut") != null
                    ? str(item, "statut") : "active");
            df.setPaiementsJson(str(item, "paiementsJson"));
            df.setLignesJson(str(item, "lignesJson"));
            df.setDateRemboursement(dateTime(item, "dateRemboursement"));
            df.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));

            // Fournisseur
            String fournUuid = str(item, "fournisseurUuid");
            if (fournUuid != null) {
                fournisseurRepo.findByUuid(fournUuid)
                        .ifPresent(df::setFournisseur);
            }

            df = detteFournisseurRepo.save(df);
            uuidToId.put(uuid, df.getId());
            count++;
        }
        return count;
    }

    // ── Sync Mouvements Stock ─────────────────────────────────
    private int syncMouvements(List<Map<String, Object>> items,
                               Utilisateur utilisateur,
                               Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            if (mouvementRepo.existsByUuid(uuid)) continue;

            MouvementStock mvt = new MouvementStock();
            mvt.setUuid(uuid);
            mvt.setNomProduit(str(item, "nomProduit"));
            mvt.setType(str(item, "type"));
            mvt.setMotif(str(item, "motif"));
            mvt.setQuantite(intVal(item, "quantite") != null
                    ? intVal(item, "quantite") : 0);
            mvt.setPrixUnitaire(dbl(item, "prixUnitaire") != null
                    ? dbl(item, "prixUnitaire") : 0.0);
            mvt.setMontantTotal(dbl(item, "montantTotal") != null
                    ? dbl(item, "montantTotal") : 0.0);
            mvt.setMontantPaye(dbl(item, "montantPaye") != null
                    ? dbl(item, "montantPaye") : 0.0);
            mvt.setModePaiement(str(item, "modePaiement"));
            mvt.setGroupe(trouverGroupe(item, "groupeUuid", uuidToId));
            mvt.setUtilisateur(utilisateur);

            String produitUuid = str(item, "produitUuid");
            if (produitUuid != null) {
                produitRepo.findByUuid(produitUuid)
                        .ifPresent(mvt::setProduit);
            }

            String fournUuid = str(item, "fournisseurUuid");
            if (fournUuid != null) {
                fournisseurRepo.findByUuid(fournUuid)
                        .ifPresent(mvt::setFournisseur);
            }

            mouvementRepo.save(mvt);
            count++;
        }
        return count;
    }

    // ── Sync Historique Ventes ────────────────────────────────
    private int syncHistoriqueVentes(List<Map<String, Object>> items,
                                     Map<String, Long> uuidToId) {
        if (items == null) return 0;
        int count = 0;
        for (Map<String, Object> item : items) {
            String uuid = str(item, "uuid");
            if (uuid == null) continue;

            // Upsert par groupeId + date
            count++;
        }
        return count;
    }

    // ── Sync Historique Paiements ─────────────────────────────
    private int syncHistoriquePaiements(List<Map<String, Object>> items,
                                        Map<String, Long> uuidToId) {
        if (items == null) return 0;
        return items != null ? items.size() : 0;
    }
}