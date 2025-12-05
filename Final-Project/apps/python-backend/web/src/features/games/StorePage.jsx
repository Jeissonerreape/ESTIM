import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAllGames } from "./gamesService";
import { addToCart } from "../cart/cartService";
import GameCard from "../../components/GameCard";
import Button from "../../components/Button";
import LoadingSpinner from "../../components/LoadingSpinner";

export default function StorePage() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentSlide, setCurrentSlide] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    loadGames();
  }, []);

  async function loadGames() {
    try {
      const data = await getAllGames();
      
      if (!data || data.length === 0) {
        setGames(EXAMPLE_GAMES);
      } else {
        setGames(data);
      }
    } catch (error) {
      console.error("Error loading games:", error);
      setGames(EXAMPLE_GAMES);
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

  if (loading) {
    return <LoadingSpinner message="Cargando tienda..." />;
  }

  const featuredGames = games.slice(0, 3);
  const specialOffers = games.filter(g => g.discount > 0).slice(0, 4);
  const popularGames = games.slice(0, 4);

  return (
    <div style={styles.container}>
      <HeroCarousel 
        games={featuredGames}
        currentSlide={currentSlide}
        setCurrentSlide={setCurrentSlide}
      />

      <div style={styles.content}>
        <Section 
          icon="⚡"
          title="Special Offers"
          games={specialOffers}
          onAddToCart={handleAddToCart}
          showDiscount
        />

        <Section 
          icon="🔥"
          title="Most Popular"
          games={popularGames}
          onAddToCart={handleAddToCart}
        />
      </div>
    </div>
  );
}

// Componente: HeroCarousel
function HeroCarousel({ games, currentSlide, setCurrentSlide }) {
  const [isHovered, setIsHovered] = useState(false);

  useEffect(() => {
    if (!isHovered && games.length > 0) {
      const interval = setInterval(() => {
        setCurrentSlide((prev) => (prev + 1) % games.length);
      }, 5000);
      return () => clearInterval(interval);
    }
  }, [isHovered, games.length, setCurrentSlide]);

  if (!games.length) return null;

  const currentGame = games[currentSlide] || {};

  return (
    <div 
      style={styles.heroCarousel}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div style={styles.heroImageContainer}>
        <img 
          src={currentGame.image || "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1200&h=600&fit=crop"} 
          alt={currentGame.title}
          style={styles.heroImage}
        />
        <div style={styles.heroOverlay} />
      </div>

      <div style={styles.heroContent}>
        <div style={styles.heroTags}>
          <span style={styles.heroTag}>{currentGame.category || "Action"}</span>
          <span style={styles.heroTag}>RPG</span>
          <span style={styles.heroTag}>Open World</span>
        </div>

        <h1 style={styles.heroTitle}>{currentGame.title || "Cyber Legends 2077"}</h1>
        <p style={styles.heroDescription}>
          {currentGame.description || "Immerse yourself in a futuristic city full of dangers and adventures. Every decision counts."}
        </p>

        <div style={styles.heroPricing}>
          {currentGame.discount > 0 && (
            <div style={styles.heroDiscount}>-{currentGame.discount}%</div>
          )}
          <div style={styles.heroPriceContainer}>
            {currentGame.discount > 0 && (
              <span style={styles.heroOriginalPrice}>${currentGame.price}</span>
            )}
            <span style={styles.heroFinalPrice}>
              ${currentGame.discount > 0 
                ? (currentGame.price * (1 - currentGame.discount / 100)).toFixed(2)
                : currentGame.price?.toFixed(2) || "59.99"}
            </span>
          </div>
        </div>

        <Button variant="primary" size="large">
          Buy Now
        </Button>

        <div style={styles.carouselDots}>
          {games.map((_, index) => (
            <div
              key={index}
              style={{
                ...styles.carouselDot,
                ...(index === currentSlide && styles.carouselDotActive)
              }}
              onClick={() => setCurrentSlide(index)}
            />
          ))}
        </div>

        <Button 
          variant="icon" 
          size="icon"
          onClick={() => setCurrentSlide((currentSlide + 1) % games.length)}
          style={{ position: 'absolute', top: '50%', right: '40px', transform: 'translateY(-50%)', zIndex: 20 }}
        >
          ›
        </Button>
      </div>
    </div>
  );
}

// Componente: Section
function Section({ icon, title, games, onAddToCart, showDiscount }) {
  return (
    <section style={styles.section}>
      <div style={styles.sectionHeader}>
        <div style={styles.sectionTitle}>
          <span style={styles.sectionIcon}>{icon}</span>
          {title}
        </div>
        <div style={styles.seeAll}>
          See all
          <span style={styles.arrowRight}>›</span>
        </div>
      </div>

      <div style={styles.gameGrid}>
        {games.map((game) => (
          <GameCard
            key={game.id}
            game={game}
            variant="detailed"
            showPrice={true}
            showDiscount={showDiscount}
            showTags={true}
            onAddToCart={onAddToCart}
            onClick={() => console.log("Game clicked:", game.title)}
          />
        ))}
      </div>
    </section>
  );
}

// 🎮 DATOS DE EJEMPLO
const EXAMPLE_GAMES = [
  {
    id: 1,
    title: "Cyberpunk Legends 2077",
    description: "Immerse yourself in a futuristic city full of dangers and adventures. Every decision counts.",
    image: "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400&h=300&fit=crop",
    price: 59.99,
    discount: 35,
    category: "Action",
    multiplayer: true
  },
  {
    id: 2,
    title: "Fantasy Quest Online",
    description: "Epic MMORPG adventure in a magical world",
    image: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop",
    price: 49.99,
    discount: 50,
    category: "RPG",
    multiplayer: true
  },
  {
    id: 3,
    title: "Speed Racing Ultimate",
    description: "The ultimate racing experience",
    image: "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=400&h=300&fit=crop",
    price: 39.99,
    discount: 25,
    category: "Racing",
    multiplayer: true
  },
  {
    id: 4,
    title: "Dark Chronicles",
    description: "A dark and mysterious adventure awaits",
    image: "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&h=300&fit=crop",
    price: 44.99,
    discount: 0,
    category: "Adventure",
    multiplayer: false
  },
  {
    id: 5,
    title: "Tactical Warfare",
    description: "Strategic military combat simulation",
    image: "https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?w=400&h=300&fit=crop",
    price: 54.99,
    discount: 15,
    category: "Strategy",
    multiplayer: true
  },
  {
    id: 6,
    title: "Medieval Kingdoms",
    description: "Build your empire in medieval times",
    image: "https://images.unsplash.com/photo-1518709414768-a88981a4515d?w=400&h=300&fit=crop",
    price: 34.99,
    discount: 0,
    category: "Strategy",
    multiplayer: false
  },
  {
    id: 7,
    title: "Pixel Adventure",
    description: "Retro-style platformer with modern gameplay",
    image: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&h=300&fit=crop",
    price: 19.99,
    discount: 40,
    category: "Indie",
    multiplayer: false
  },
  {
    id: 8,
    title: "Space Odyssey",
    description: "Explore the vast universe",
    image: "https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=400&h=300&fit=crop",
    price: 64.99,
    discount: 20,
    category: "Simulation",
    multiplayer: true
  },
  {
    id: 9,
    title: "Dungeon Master",
    description: "Deep dungeon crawler with RPG elements",
    image: "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400&h=300&fit=crop",
    price: 29.99,
    discount: 0,
    category: "RPG",
    multiplayer: false
  },
  {
    id: 10,
    title: "Zombie Survival",
    description: "Survive the zombie apocalypse",
    image: "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=400&h=300&fit=crop",
    price: 42.99,
    discount: 30,
    category: "Action",
    multiplayer: true
  }
];

// Estilos optimizados (sin estilos de loading ni botones)
const styles = {
  container: {
    background: '#000',
    minHeight: '100vh',
    color: '#fff'
  },
  heroCarousel: {
    position: 'relative',
    height: '500px',
    overflow: 'hidden',
    background: '#000'
  },
  heroImageContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0
  },
  heroImage: {
    width: '100%',
    height: '100%',
    objectFit: 'cover'
  },
  heroOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'linear-gradient(to right, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.7) 40%, transparent 100%)'
  },
  heroContent: {
    position: 'relative',
    height: '100%',
    padding: '60px 40px',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    maxWidth: '600px',
    zIndex: 10
  },
  heroTags: {
    display: 'flex',
    gap: '10px',
    marginBottom: '15px'
  },
  heroTag: {
    background: 'rgba(255, 107, 53, 0.2)',
    border: '1px solid rgba(255, 107, 53, 0.5)',
    padding: '4px 12px',
    borderRadius: '4px',
    fontSize: '11px',
    color: '#ff6b35',
    fontWeight: '600'
  },
  heroTitle: {
    fontSize: '48px',
    fontWeight: '700',
    margin: '0 0 15px 0',
    lineHeight: '1.1'
  },
  heroDescription: {
    fontSize: '16px',
    color: '#ccc',
    lineHeight: '1.6',
    marginBottom: '25px'
  },
  heroPricing: {
    display: 'flex',
    alignItems: 'center',
    gap: '15px',
    marginBottom: '25px'
  },
  heroDiscount: {
    background: '#ff6b35',
    color: '#000',
    padding: '8px 16px',
    borderRadius: '4px',
    fontSize: '20px',
    fontWeight: '700'
  },
  heroPriceContainer: {
    display: 'flex',
    flexDirection: 'column',
    gap: '2px'
  },
  heroOriginalPrice: {
    fontSize: '14px',
    color: '#888',
    textDecoration: 'line-through'
  },
  heroFinalPrice: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#fff'
  },
  carouselDots: {
    position: 'absolute',
    bottom: '30px',
    left: '50%',
    transform: 'translateX(-50%)',
    display: 'flex',
    gap: '10px',
    zIndex: 20
  },
  carouselDot: {
    width: '40px',
    height: '4px',
    background: 'rgba(255, 255, 255, 0.3)',
    borderRadius: '2px',
    cursor: 'pointer',
    transition: 'all 0.3s'
  },
  carouselDotActive: {
    background: '#ff6b35',
    width: '60px'
  },
  content: {
    padding: '40px 40px 80px'
  },
  section: {
    marginBottom: '60px'
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
    display: 'flex',
    alignItems: 'center',
    gap: '12px'
  },
  sectionIcon: {
    fontSize: '28px'
  },
  seeAll: {
    color: '#ff6b35',
    fontSize: '14px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    gap: '5px',
    fontWeight: '600'
  },
  arrowRight: {
    fontSize: '18px'
  },
  gameGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(4, 1fr)',
    gap: '20px'
  }
};
