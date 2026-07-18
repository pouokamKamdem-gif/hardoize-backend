  const renderProduit = ({ item }) => {
    const ligneExistante  = panier.find((l) => l.produit.id === item.id);
    const qteSelectionnee = ligneExistante?.quantite || 0;

<<<<<<< Updated upstream
import com.digneequipe.hardoize.dto.request.*;
import com.digneequipe.hardoize.dto.response.ApiResponse;
import com.digneequipe.hardoize.models.Produit;
import com.digneequipe.hardoize.services.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
    const couleurStock =
      item.quantiteStock <= 0 ? COLORS.SCORE_ROUGE :
      item.quantiteStock <= item.stockMinimum ? COLORS.SCORE_ORANGE :
      COLORS.SCORE_VERT;

    const contenuCarte = (
      <>
        {/* Voile sombre pour lisibilité du texte sur la photo */}
        <View style={styles.produitOverlay} />
>>>>>>> Stashed changes

        {/* Badge quantité sélectionnée */}
        {qteSelectionnee > 0 && (
          <View style={styles.badgeQte}>
            <Text style={styles.badgeQteTexte}>x{qteSelectionnee}</Text>
          </View>
        )}

<<<<<<< Updated upstream
    private final ProduitService produitService;

    // GET /api/produits?groupeId=1
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> creer(
            @RequestBody ProduitRequest request,
            Authentication auth) {
        try {
            Produit p = produitService.creer(request, auth.getName());
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",           p.getId());
            dto.put("nom",          p.getNom());
            dto.put("prixAchat",    p.getPrixAchat());
            dto.put("prixVente",    p.getPrixVente());
            dto.put("quantiteStock",p.getQuantiteStock());
            dto.put("createdAt",    p.getCreatedAt());
            return ResponseEntity.ok(ApiResponse.ok("Produit créé", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> modifier(
            @PathVariable Long id,
            @RequestBody ProduitRequest request) {
        try {
            Produit p = produitService.modifier(id, request);
            Map<String, Object> dto = new HashMap<>();
            dto.put("id",    p.getId());
            dto.put("nom",   p.getNom());
            return ResponseEntity.ok(ApiResponse.ok("Produit modifié", dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok("Stocké",
                    Map.of("error", e.getMessage())));
        }
    }
}
=======
        {/* Stock haut droite, avec fond pour lisibilité */}
        <View style={styles.stockBadge}>
          <Text style={[styles.produitStock, { color: couleurStock }]}>
            {item.quantiteStock}
          </Text>
        </View>

        {/* Bandeau bas : nom + prix sur fond semi-opaque */}
        <View style={styles.produitInfoBandeau}>
          <Text style={styles.produitNom} numberOfLines={2}>
            {item.nom}
          </Text>
          <Text style={styles.produitPrix}>
            {formatFCFA(item.prixVente)}
          </Text>
        </View>
      </>
    );

    return (
      <TouchableOpacity
        style={[
          styles.produitCard,
          {
            borderWidth: qteSelectionnee > 0 ? 2 : 0,
            borderColor: COLORS.ORANGE,
            opacity: item.quantiteStock <= 0 ? 0.5 : 1,
          },
        ]}
        onPress={() => handleTapProduit(item)}
        onLongPress={() => handleTapLongProduit(item)}
        activeOpacity={0.85}
        disabled={item.quantiteStock <= 0}
      >
        {item.photoUri ? (
          <ImageBackground
            source={{ uri: item.photoUri }}
            style={styles.produitImageFond}
            imageStyle={{ borderRadius: 14 }}
          >
            {contenuCarte}
          </ImageBackground>
        ) : (
          <View style={[styles.produitImageFond, { backgroundColor: cardCouleur }]}>
            <View style={styles.produitIconeFallback}>
              <Text style={{ fontSize: 30 }}>🛒</Text>
            </View>
            {contenuCarte}
          </View>
        )}
      </TouchableOpacity>
    );
  };
>>>>>>> Stashed changes
