package com.digneequipe.hardoize.dto.request;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SyncBatchRequest {
    // Chaque entité est une Map avec ses champs
    // La clé "uuid" est obligatoire dans chaque entité
    private List<Map<String, Object>> groupes;
    private List<Map<String, Object>> membres;
    private List<Map<String, Object>> produits;
    private List<Map<String, Object>> clients;
    private List<Map<String, Object>> ventes;
    private List<Map<String, Object>> lignesVentes;
    private List<Map<String, Object>> dettes;
    private List<Map<String, Object>> fournisseurs;
    private List<Map<String, Object>> dettesFournisseurs;
    private List<Map<String, Object>> mouvementsStock;
    private List<Map<String, Object>> historiquePaiements;
    private List<Map<String, Object>> historiqueVentes;
}