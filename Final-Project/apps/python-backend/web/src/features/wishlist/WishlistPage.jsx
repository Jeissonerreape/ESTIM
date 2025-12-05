import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getWishlist, removeFromWishlist } from "./wishlistService";
import { addToCart } from "../cart/cartService";
import GameCard from "../../components/GameCard";
import Button from "../../components/Button";
import LoadingSpinner from "../../components/LoadingSpinner";
import Badge from "../../components/Badge";

export default function WishlistPage() {
  const [wishlist, setWishlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadWishlist();
  }, []);

    async function loadWishlist() {
      try {
        const data = await getWishlist(); // array de WishlistItemResponse

        if (!Array.isArray(data) || data.length === 0) {
          // Sin items → mostramos estado vacío real
          setWishlist([]);
          return;
        }

        // Mapear respuesta del backend a la forma que usa la UI
        const mapped = data.map((item) => {
          const id = item.gameId;
          const price =
            item.currentPrice !== null && item.currentPrice !== undefined
              ? Number(item.currentPrice)
              : 0;

          const title =
            item.gameTitle ||
            `Wishlist game ${String(id).slice(0, 8)}...`;

          const image =
            item.coverImageUrl ||
            "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop";

          return {
            id,               // usado para keys, rutas y handleRemove
            title,
            image,
            price,
            discount: 0,      // aún no tenemos descuentos dinámicos
            category: "Desconocido",
            multiplayer: false,
            addedAt: item.addedAt,
          };
        });

        setWishlist(mapped);
      } catch (error) {
        console.error("Error loading wishlist:", error);
        // En caso de error → mostramos vacía (o podrías usar EXAMPLE_WISHLIST para debug)
        setWishlist([]);
      } finally {
        setLoading(false);
      }
    }


  async function handleAddToCart(gameId) {
    try {
      await addToCart(gameId);
      alert("¡Juego agregado al carrito!");
    } catch (error) {
      alert("Error al agregar al carrito");
    }
  }

  async function handleRemoveFromWishlist(gameId) {
    try {
      await removeFromWishlist(gameId);
      setWishlist(wishlist.filter(game => game.id !== gameId));
      alert("Juego eliminado de la wishlist");
    } catch (error) {
      alert("Error al eliminar de la wishlist");
    }
  }

  async function handleAddAllToCart() {
    try {
      for (const game of wishlist) {
        await addToCart(game.id);
      }
      alert(`¡${wishlist.length} juegos agregados al carrito!`);
    } catch (error) {
      alert("Error al agregar juegos al carrito");
    }
  }

  if (loading) {
    return <LoadingSpinner message="Cargando wishlist..." />;
  }

  if (wishlist.length === 0) {
    return <EmptyWishlist navigate={navigate} />;
  }

  // Calcular estadísticas
  const totalPrice = wishlist.reduce((sum, game) => sum + game.price, 0);
  const totalDiscount = wishlist.reduce((sum, game) => {
    if (game.discount > 0) {
      return sum + (game.price * game.discount / 100);
    }
    return sum;
  }, 0);
  const finalPrice = totalPrice - totalDiscount;
  const gamesWithDiscount = wishlist.filter(game => game.discount > 0).length;

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.pageTitle}>Mi Wishlist</h1>
          <p style={styles.pageSubtitle}>
            Tienes {wishlist.length} juegos en tu lista de deseos
          </p>
        </div>
        <div style={styles.headerActions}>
          <Button 
            variant="secondary" 
            size="large"
            onClick={() => navigate("/store")}
          >
            Seguir comprando
          </Button>
          <Button 
            variant="primary" 
            size="large"
            icon="🛒"
            onClick={handleAddAllToCart}
          >
            Agregar todos al carrito
          </Button>
        </div>
      </div>

      {/* Stats Cards */}
      <div style={styles.statsContainer}>
        <StatsCard 
          icon="🎮" 
          value={wishlist.length} 
          label="Juegos"
        />
        <StatsCard 
          icon="💰" 
          value={`$${totalPrice.toFixed(2)}`} 
          label="Precio total"
        />
        <StatsCard 
          icon="🔥" 
          value={`$${totalDiscount.toFixed(2)}`} 
          label="Ahorro total"
          highlight
        />
        <StatsCard 
          icon="✨" 
          value={gamesWithDiscount} 
          label="Con descuento"
        />
      </div>

      {/* Games Grid */}
      <div style={styles.gamesSection}>
        <div style={styles.sectionHeader}>
          <h2 style={styles.sectionTitle}>Tus juegos guardados</h2>
          {gamesWithDiscount > 0 && (
            <Badge variant="success" icon="🔥">
              {gamesWithDiscount} juegos en oferta
            </Badge>
          )}
        </div>

        <div style={styles.gamesGrid}>
          {wishlist.map((game) => (
            <WishlistGameCard
              key={game.id}
              game={game}
              onAddToCart={handleAddToCart}
              onRemove={handleRemoveFromWishlist}
              onViewDetails={() => navigate(`/games/${game.id}`)}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

// Componente: StatsCard
function StatsCard({ icon, value, label, highlight = false }) {
  return (
    <div style={{
      ...styles.statsCard,
      ...(highlight && styles.statsCardHighlight)
    }}>
      <div style={styles.statsIcon}>{icon}</div>
      <div style={styles.statsValue}>{value}</div>
      <div style={styles.statsLabel}>{label}</div>
    </div>
  );
}

// Componente: WishlistGameCard
function WishlistGameCard({ game, onAddToCart, onRemove, onViewDetails }) {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <div 
      style={{
        ...styles.wishlistCard,
        ...(isHovered && styles.wishlistCardHover)
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Image */}
      <div style={styles.cardImage} onClick={onViewDetails}>
        <img src={game.image} alt={game.title} style={styles.image} />
        {game.discount > 0 && (
          <div style={styles.discountBadgePosition}>
            <Badge variant="discount" size="medium">
              -{game.discount}%
            </Badge>
          </div>
        )}
      </div>

      {/* Content */}
      <div style={styles.cardContent}>
        <h3 style={styles.cardTitle} onClick={onViewDetails}>
          {game.title}
        </h3>

        <div style={styles.cardTags}>
          <Badge variant="category" size="small">{game.category}</Badge>
          {game.multiplayer && (
            <Badge variant="default" size="small">Multiplayer</Badge>
          )}
        </div>

        {/* Price */}
        <div style={styles.cardPrice}>
          {game.discount > 0 ? (
            <>
              <span style={styles.originalPrice}>${game.price.toFixed(2)}</span>
              <span style={styles.finalPrice}>
                ${(game.price * (1 - game.discount / 100)).toFixed(2)}
              </span>
            </>
          ) : (
            <span style={styles.price}>${game.price.toFixed(2)}</span>
          )}
        </div>

        {/* Actions */}
        <div style={styles.cardActions}>
          <Button 
            variant="primary" 
            size="small"
            icon="🛒"
            fullWidth
            onClick={() => onAddToCart(game.id)}
          >
            Agregar al carrito
          </Button>
          <Button 
            variant="secondary" 
            size="small"
            icon="❌"
            onClick={() => onRemove(game.id)}
          >
            Quitar
          </Button>
        </div>
      </div>
    </div>
  );
}

// Componente: EmptyWishlist
function EmptyWishlist({ navigate }) {
  return (
    <div style={styles.emptyState}>
      <div style={styles.emptyIcon}>💝</div>
      <h2 style={styles.emptyTitle}>Tu wishlist está vacía</h2>
      <p style={styles.emptyText}>
        Explora nuestra tienda y guarda tus juegos favoritos
      </p>
      <Button 
        variant="primary" 
        size="large"
        icon="🎮"
        onClick={() => navigate("/store")}
      >
        Ir a la tienda
      </Button>
    </div>
  );
}

// DATOS DE EJEMPLO
const EXAMPLE_WISHLIST = [
  {
    id: 1,
    title: "Cyberpunk Legends 2077",
    image: "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400&h=300&fit=crop",
    price: 59.99,
    discount: 35,
    category: "Action",
    multiplayer: true
  },
  {
    id: 2,
    title: "Fantasy Quest Online",
    image: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop",
    price: 49.99,
    discount: 50,
    category: "RPG",
    multiplayer: true
  },
  {
    id: 3,
    title: "Speed Racing Ultimate",
    image: "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=400&h=300&fit=crop",
    price: 39.99,
    discount: 0,
    category: "Racing",
    multiplayer: true
  },
  {
    id: 4,
    title: "Space Odyssey",
    image: "https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=400&h=300&fit=crop",
    price: 64.99,
    discount: 20,
    category: "Simulation",
    multiplayer: true
  }
];

// Estilos
const styles = {
  container: {
    background: '#000',
    minHeight: '100vh',
    color: '#fff',
    padding: '40px'
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '40px',
    paddingBottom: '20px',
    borderBottom: '1px solid #222'
  },
  pageTitle: {
    fontSize: '36px',
    fontWeight: '700',
    margin: '0 0 8px 0'
  },
  pageSubtitle: {
    fontSize: '16px',
    color: '#888',
    margin: 0
  },
  headerActions: {
    display: 'flex',
    gap: '15px'
  },
  statsContainer: {
    display: 'grid',
    gridTemplateColumns: 'repeat(4, 1fr)',
    gap: '20px',
    marginBottom: '40px'
  },
  statsCard: {
    background: 'rgba(255, 255, 255, 0.02)',
    border: '1px solid #222',
    borderRadius: '12px',
    padding: '24px',
    textAlign: 'center'
  },
  statsCardHighlight: {
    background: 'rgba(255, 107, 53, 0.1)',
    border: '1px solid rgba(255, 107, 53, 0.3)'
  },
  statsIcon: {
    fontSize: '32px',
    marginBottom: '12px'
  },
  statsValue: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#fff',
    marginBottom: '8px'
  },
  statsLabel: {
    fontSize: '14px',
    color: '#888'
  },
  gamesSection: {
    marginTop: '40px'
  },
  sectionHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '25px'
  },
  sectionTitle: {
    fontSize: '24px',
    fontWeight: '700',
    margin: 0
  },
  gamesGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
    gap: '20px'
  },
  wishlistCard: {
    background: 'rgba(20, 20, 20, 0.8)',
    border: '1px solid #222',
    borderRadius: '12px',
    overflow: 'hidden',
    transition: 'all 0.3s',
    cursor: 'pointer'
  },
  wishlistCardHover: {
    transform: 'translateY(-5px)',
    borderColor: '#ff6b35',
    boxShadow: '0 10px 30px rgba(255, 107, 53, 0.3)'
  },
  cardImage: {
    position: 'relative',
    width: '100%',
    height: '160px',
    overflow: 'hidden',
    cursor: 'pointer'
  },
  image: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    transition: 'transform 0.3s'
  },
  discountBadgePosition: {
    position: 'absolute',
    top: '10px',
    left: '10px'
  },
  cardContent: {
    padding: '20px'
  },
  cardTitle: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#fff',
    margin: '0 0 12px 0',
    cursor: 'pointer'
  },
  cardTags: {
    display: 'flex',
    gap: '8px',
    marginBottom: '15px',
    flexWrap: 'wrap'
  },
  cardPrice: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    marginBottom: '15px',
    paddingTop: '15px',
    borderTop: '1px solid #333'
  },
  originalPrice: {
    fontSize: '14px',
    color: '#666',
    textDecoration: 'line-through'
  },
  finalPrice: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#ff6b35'
  },
  price: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#fff'
  },
  cardActions: {
    display: 'flex',
    gap: '10px'
  },
  emptyState: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: '70vh',
    textAlign: 'center'
  },
  emptyIcon: {
    fontSize: '64px',
    marginBottom: '20px',
    opacity: 0.5
  },
  emptyTitle: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#fff',
    margin: '0 0 10px 0'
  },
  emptyText: {
    fontSize: '16px',
    color: '#888',
    margin: '0 0 30px 0',
    maxWidth: '500px'
  }
};
