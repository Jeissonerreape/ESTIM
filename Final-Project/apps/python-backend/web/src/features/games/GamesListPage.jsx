import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAllGames } from "./gamesService";
import { addToCart } from "../cart/cartService";
import GameCard from "../../components/GameCard";
import Button from "../../components/Button";
import LoadingSpinner from "../../components/LoadingSpinner";
import Badge from "../../components/Badge";

export default function GamesListPage() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    loadGames();
  }, []);

  async function loadGames() {
    try {
      const data = await getAllGames();
      setGames(data || []); // ✅ Simplificado
    } catch (error) {
      console.error("Error loading games:", error);
      setGames([]); // ✅ Array vacío en caso de error
    } finally {
      setLoading(false);
    }
  }

  async function handleAddToCart(gameId) {
    try {
      await addToCart(gameId);
      alert("¡Juego agregado al carrito!");
      navigate(`/game/${gameId}`); 
    } catch (error) {
      alert("Error al agregar al carrito");
      navigate(`/game/${gameId}`);
    }
  }

  if (loading) {
    return <LoadingSpinner message="Cargando juegos..." />;
  }

  // Filtrar juegos
  const filteredGames = games.filter(game => {
    const matchesCategory = selectedCategory === "all" || game.category === selectedCategory;
    const matchesSearch = game.title.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  const categories = ["all", ...new Set(games.map(g => g.category))];

  return (
    <div style={styles.container}>
      {/* Header Section */}
      <div style={styles.headerSection}>
        <div>
          <h1 style={styles.pageTitle}>Todos los Juegos</h1>
          <p style={styles.pageSubtitle}>
            Explora nuestra colección completa de {games.length} juegos
          </p>
        </div>
        <Button 
          variant="primary" 
          size="large"
          icon="🛒"
          onClick={() => navigate("/cart")}
        >
          Ver Carrito
        </Button>
      </div>

      {/* Filters Section */}
      <div style={styles.filtersSection}>
        {/* Search Bar */}
        <div style={styles.searchContainer}>
          <input
            type="text"
            placeholder="🔍 Buscar juegos..."
            style={styles.searchInput}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        {/* Category Filters */}
        <div style={styles.categoryFilters}>
          {categories.map((category) => (
            <Button
              key={category}
              variant={selectedCategory === category ? "primary" : "ghost"}
              size="small"
              onClick={() => setSelectedCategory(category)}
            >
              {category === "all" ? "Todos" : category}
            </Button>
          ))}
        </div>
      </div>

      {/* Results Info */}
      <div style={styles.resultsInfo}>
        <p style={styles.resultsText}>
          Mostrando <strong>{filteredGames.length}</strong> juegos
        </p>
        {selectedCategory !== "all" && (
          <Badge variant="category">{selectedCategory}</Badge>
        )}
        {searchQuery && (
          <Badge variant="default">Buscando: "{searchQuery}"</Badge>
        )}
      </div>

      {/* Games Grid */}
      {filteredGames.length > 0 ? (
        <div style={styles.gamesGrid}>
          {filteredGames.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              variant="detailed"
              showPrice={true}
              showDiscount={true}
              showTags={true}
              onAddToCart={handleAddToCart}
              onClick={() => navigate(`/game/${game.id}`)}
            />
          ))}
        </div>
      ) : (
        <EmptyState 
          searchQuery={searchQuery}
          onReset={() => {
            setSearchQuery("");
            setSelectedCategory("all");
          }}
        />
      )}
    </div>
  );
}

// Componente: EmptyState
function EmptyState({ searchQuery, onReset }) {
  return (
    <div style={styles.emptyState}>
      <div style={styles.emptyIcon}>🔍</div>
      <h2 style={styles.emptyTitle}>No se encontraron juegos</h2>
      <p style={styles.emptyText}>
        {searchQuery 
          ? `No hay resultados para "${searchQuery}"`
          : "Intenta con otros filtros"
        }
      </p>
      <Button variant="primary" onClick={onReset}>
        Limpiar filtros
      </Button>
    </div>
  );
}

// Estilos
const styles = {
  container: {
    background: '#000',
    minHeight: '100vh',
    color: '#fff',
    padding: '40px'
  },
  headerSection: {
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
  filtersSection: {
    marginBottom: '30px'
  },
  searchContainer: {
    marginBottom: '20px'
  },
  searchInput: {
    width: '100%',
    maxWidth: '500px',
    background: '#1a1a1a',
    border: '1px solid #333',
    borderRadius: '8px',
    padding: '12px 16px',
    color: '#fff',
    fontSize: '14px',
    outline: 'none'
  },
  categoryFilters: {
    display: 'flex',
    gap: '10px',
    flexWrap: 'wrap'
  },
  resultsInfo: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    marginBottom: '30px',
    padding: '15px 20px',
    background: 'rgba(255, 255, 255, 0.02)',
    borderRadius: '8px',
    border: '1px solid #222'
  },
  resultsText: {
    margin: 0,
    fontSize: '14px',
    color: '#ccc'
  },
  gamesGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
    gap: '20px'
  },
  emptyState: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '80px 40px',
    textAlign: 'center'
  },
  emptyIcon: {
    fontSize: '64px',
    marginBottom: '20px',
    opacity: 0.5
  },
  emptyTitle: {
    fontSize: '24px',
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
