import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getGameById } from "./gamesService";
import { addToCart } from "../cart/cartService";
import { addToWishlist } from "../wishlist/wishlistService";
import LoadingSpinner from "../../components/LoadingSpinner";
import Button from "../../components/Button";
import Badge from "../../components/Badge";

export default function GameDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(0);

  useEffect(() => {
    loadGame();
  }, [id]);

  async function loadGame() {
    try {
      const data = await getGameById(id);
      setGame(data);
    } catch (error) {
      console.error("Error loading game:", error);
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return <LoadingSpinner message="Cargando detalles..." />;
  }

  if (!game) {
    return <EmptyState navigate={navigate} />;
  }

  return (
    <div style={styles.container}>
      {/* Breadcrumb */}
      <Breadcrumb game={game} navigate={navigate} />

      {/* Main Layout */}
      <div style={styles.mainLayout}>
        {/* Left: Images + Content */}
        <div style={styles.leftColumn}>
          <ImageGallery 
            game={game}
            selectedImage={selectedImage}
            setSelectedImage={setSelectedImage}
          />
          
          <AboutSection game={game} />
          
          <ReviewsSection game={game} />
        </div>

        {/* Right: Purchase Panel */}
        <PurchasePanel game={game} />
      </div>
    </div>
  );
}

// Breadcrumb
function Breadcrumb({ game, navigate }) {
  return (
    <div style={styles.breadcrumb}>
      <span style={styles.breadcrumbLink} onClick={() => navigate("/")}>Home</span>
      <span style={styles.separator}>›</span>
      <span style={styles.breadcrumbLink} onClick={() => navigate(`/games?category=${game.category}`)}>{game.category}</span>
      <span style={styles.separator}>›</span>
      <span style={styles.breadcrumbCurrent}>{game.title}</span>
    </div>
  );
}

// Image Gallery
function ImageGallery({ game, selectedImage, setSelectedImage }) {
  const images = game.images || [game.image];

  return (
    <div style={styles.gallerySection}>
      {/* Main Image */}
      <div style={styles.mainImageContainer}>
        <img 
          src={images[selectedImage]} 
          alt={game.title}
          style={styles.mainImage}
        />
      </div>

      {/* Thumbnails */}
      <div style={styles.thumbnailsRow}>
        {images.map((img, index) => (
          <div
            key={index}
            style={{
              ...styles.thumbnail,
              ...(selectedImage === index && styles.thumbnailActive)
            }}
            onClick={() => setSelectedImage(index)}
          >
            <img src={img} alt={`Screenshot ${index + 1}`} style={styles.thumbnailImg} />
          </div>
        ))}
      </div>
    </div>
  );
}

// About Section
function AboutSection({ game }) {
  return (
    <div style={styles.aboutSection}>
      <h2 style={styles.sectionTitle}>About This Game</h2>
      <p style={styles.aboutText}>
        {game.longDescription || game.description}
      </p>

      <div style={styles.metaGrid}>
        <div style={styles.metaItem}>
          <span style={styles.metaLabel}>Developer:</span>
          <span style={styles.metaValue}>{game.developer}</span>
        </div>
        <div style={styles.metaItem}>
          <span style={styles.metaLabel}>Publisher:</span>
          <span style={styles.metaValue}>{game.publisher}</span>
        </div>
      </div>
    </div>
  );
}

// Reviews Section
function ReviewsSection({ game }) {
  return (
    <div style={styles.reviewsSection}>
      <h2 style={styles.sectionTitle}>Reviews</h2>
      <div style={styles.reviewsContent}>
        <div style={styles.ratingBadge}>
          <span style={styles.thumbsUp}>👍</span>
          <span style={styles.ratingPercent}>{game.rating}%</span>
        </div>
        <div style={styles.reviewsInfo}>
          <div style={styles.reviewBar}>
            <div style={{ ...styles.reviewBarFill, width: `${game.rating}%` }} />
          </div>
          <p style={styles.reviewCount}>{game.reviewCount?.toLocaleString()} user reviews</p>
        </div>
      </div>
    </div>
  );
}

// Purchase Panel
function PurchasePanel({ game }) {
  async function handleAddToCart() {
    try {
      await addToCart(game.id);
      alert("¡Juego agregado al carrito!");
    } catch (error) {
      alert("Error al agregar al carrito");
    }
  }

  async function handleAddToWishlist() {
    try {
      await addToWishlist(game.id);
      alert("¡Agregado a la wishlist!");
    } catch (error) {
      alert("Error al agregar a wishlist");
    }
  }

  return (
    <div style={styles.purchasePanel}>
      {/* Header with game image */}
      <div style={styles.panelHeader}>
        <img src={game.image} alt={game.title} style={styles.panelHeaderImg} />
      </div>

      {/* Content */}
      <div style={styles.panelBody}>
        <h1 style={styles.panelTitle}>{game.title}</h1>
        <p style={styles.panelSubtitle}>{game.description}</p>

        {/* Tags */}
        <div style={styles.tagsRow}>
          {game.tags?.map((tag, idx) => (
            <Badge key={idx} variant="category" size="small">{tag}</Badge>
          ))}
        </div>

        {/* Release Date */}
        <div style={styles.infoRow}>
          <span style={styles.infoIcon}>📅</span>
          <div>
            <div style={styles.infoLabel}>Release Date:</div>
            <div style={styles.infoValue}>{game.releaseDate}</div>
          </div>
        </div>

        {/* Platforms */}
        <div style={styles.infoRow}>
          <span style={styles.infoIcon}>💻</span>
          <div>
            <div style={styles.infoLabel}>Platforms:</div>
            <div style={styles.platformBadges}>
              {game.platforms?.map((p, idx) => (
                <Badge key={idx} variant="default" size="small">{p}</Badge>
              ))}
            </div>
          </div>
        </div>

        {/* Price */}
        <div style={styles.priceBox}>
          <div style={styles.priceLabel}>Price:</div>
          <div style={styles.priceValue}>${game.price?.toFixed(2)}</div>
        </div>

        {/* Buttons */}
        <div style={styles.actionsBox}>
          <Button 
            variant="primary" 
            size="large"
            icon="🛒"
            fullWidth
            onClick={handleAddToCart}
          >
            Add to Cart
          </Button>

          <div style={styles.secondaryRow}>
            <Button 
              variant="ghost" 
              size="medium"
              icon="💝"
              onClick={handleAddToWishlist}
              style={{ flex: 1 }}
            >
              Add to Wishlist
            </Button>
            <Button 
              variant="ghost" 
              size="medium"
              icon="🔗"
            >
              
            </Button>
            <Button 
              variant="ghost" 
              size="medium"
              icon="🔗"
            >
              
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

// Empty State
function EmptyState({ navigate }) {
  return (
    <div style={styles.emptyState}>
      <div style={styles.emptyIcon}>🎮</div>
      <h1>Game not found</h1>
      <Button variant="primary" onClick={() => navigate("/games")}>
        Back to Store
      </Button>
    </div>
  );
}

// Styles
const styles = {
  container: {
    background: '#0e0e0e',
    minHeight: '100vh',
    color: '#fff',
    padding: '20px 40px'
  },
  breadcrumb: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    marginBottom: '30px',
    fontSize: '13px'
  },
  breadcrumbLink: {
    color: '#999',
    cursor: 'pointer',
    transition: 'color 0.2s'
  },
  separator: {
    color: '#555'
  },
  breadcrumbCurrent: {
    color: '#fff'
  },
  mainLayout: {
    display: 'grid',
    gridTemplateColumns: '1fr 380px',
    gap: '30px',
    maxWidth: '1600px',
    margin: '0 auto'
  },
  leftColumn: {
    display: 'flex',
    flexDirection: 'column',
    gap: '30px'
  },
  gallerySection: {
    display: 'flex',
    flexDirection: 'column',
    gap: '15px'
  },
  mainImageContainer: {
    width: '100%',
    height: '500px',
    borderRadius: '8px',
    overflow: 'hidden',
    background: '#1a1a1a'
  },
  mainImage: {
    width: '100%',
    height: '100%',
    objectFit: 'cover'
  },
  thumbnailsRow: {
    display: 'grid',
    gridTemplateColumns: 'repeat(6, 1fr)',
    gap: '10px'
  },
  thumbnail: {
    height: '90px',
    borderRadius: '6px',
    overflow: 'hidden',
    cursor: 'pointer',
    border: '2px solid transparent',
    transition: 'border-color 0.2s',
    background: '#1a1a1a'
  },
  thumbnailActive: {
    borderColor: '#ff6b35'
  },
  thumbnailImg: {
    width: '100%',
    height: '100%',
    objectFit: 'cover'
  },
  aboutSection: {
    background: '#1a1a1a',
    borderRadius: '8px',
    padding: '30px'
  },
  sectionTitle: {
    fontSize: '22px',
    fontWeight: '600',
    marginBottom: '20px',
    margin: '0 0 20px 0'
  },
  aboutText: {
    fontSize: '15px',
    lineHeight: '1.7',
    color: '#bbb',
    marginBottom: '30px'
  },
  metaGrid: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '15px'
  },
  metaItem: {
    display: 'flex',
    flexDirection: 'column',
    gap: '5px'
  },
  metaLabel: {
    fontSize: '13px',
    color: '#888',
    fontWeight: '500'
  },
  metaValue: {
    fontSize: '15px',
    color: '#ff6b35',
    fontWeight: '500'
  },
  reviewsSection: {
    background: '#1a1a1a',
    borderRadius: '8px',
    padding: '30px'
  },
  reviewsContent: {
    display: 'flex',
    alignItems: 'center',
    gap: '20px'
  },
  ratingBadge: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    background: 'rgba(255, 107, 53, 0.1)',
    padding: '15px 20px',
    borderRadius: '8px',
    border: '2px solid rgba(255, 107, 53, 0.3)'
  },
  thumbsUp: {
    fontSize: '24px'
  },
  ratingPercent: {
    fontSize: '22px',
    fontWeight: '700',
    color: '#ff6b35'
  },
  reviewsInfo: {
    flex: 1
  },
  reviewBar: {
    width: '100%',
    height: '10px',
    background: '#2a2a2a',
    borderRadius: '5px',
    overflow: 'hidden',
    marginBottom: '10px'
  },
  reviewBarFill: {
    height: '100%',
    background: 'linear-gradient(90deg, #ff6b35, #ff8c5a)',
    transition: 'width 0.5s ease'
  },
  reviewCount: {
    fontSize: '14px',
    color: '#888',
    margin: 0
  },
  purchasePanel: {
    position: 'sticky',
    top: '20px',
    background: '#1a1a1a',
    borderRadius: '8px',
    overflow: 'hidden',
    height: 'fit-content'
  },
  panelHeader: {
    width: '100%',
    height: '180px',
    overflow: 'hidden'
  },
  panelHeaderImg: {
    width: '100%',
    height: '100%',
    objectFit: 'cover'
  },
  panelBody: {
    padding: '25px'
  },
  panelTitle: {
    fontSize: '24px',
    fontWeight: '700',
    margin: '0 0 8px 0'
  },
  panelSubtitle: {
    fontSize: '14px',
    color: '#999',
    marginBottom: '20px',
    lineHeight: '1.5'
  },
  tagsRow: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '8px',
    marginBottom: '25px'
  },
  infoRow: {
    display: 'flex',
    gap: '12px',
    marginBottom: '20px'
  },
  infoIcon: {
    fontSize: '18px',
    marginTop: '2px'
  },
  infoLabel: {
    fontSize: '13px',
    color: '#888',
    marginBottom: '5px'
  },
  infoValue: {
    fontSize: '14px',
    color: '#fff',
    fontWeight: '500'
  },
  platformBadges: {
    display: 'flex',
    gap: '6px',
    flexWrap: 'wrap'
  },
  priceBox: {
    background: '#0e0e0e',
    borderRadius: '6px',
    padding: '20px',
    marginTop: '25px',
    marginBottom: '25px'
  },
  priceLabel: {
    fontSize: '13px',
    color: '#888',
    marginBottom: '8px'
  },
  priceValue: {
    fontSize: '36px',
    fontWeight: '700',
    color: '#ff6b35'
  },
  actionsBox: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  },
  secondaryRow: {
    display: 'flex',
    gap: '8px'
  },
  emptyState: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: '70vh',
    gap: '20px'
  },
  emptyIcon: {
    fontSize: '64px',
    opacity: 0.3
  }
};
