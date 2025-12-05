import { useState, useEffect } from "react";
import GameCard from "../../components/GameCard";
import Button from "../../components/Button";
import { fetchMyLibrary } from "./libraryService";

export default function LibraryPage() {
  const [selectedCollection, setSelectedCollection] = useState("all");

  const [recentlyPlayed, setRecentlyPlayed] = useState([]);
  const [recommended, setRecommended] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    // If no token → treat as logged out, don't show any games
    if (!token) {
      setIsLoggedIn(false);
      setRecentlyPlayed([]);
      setRecommended([]);
      setLoading(false);
      return;
    }

    setIsLoggedIn(true);
    setLoading(true);
    setError(null);

    async function loadLibrary() {
      try {
        const entries = await fetchMyLibrary();

        console.log("Library entries from backend:", entries);

        const games = entries.map((entry) => ({
          id: entry.gameId, // UUID of the game
          title: entry.gameTitle ?? `Game ${String(entry.gameId).slice(0, 8)}`,
          image: entry.coverImageUrl ?? "https://via.placeholder.com/400x300?text=Game",
          category: entry.source ?? "Library",

          price: 0, // we don't have price here yet
          discount: 0,
          multiplayer: false,
        }));

        const mid = Math.ceil(games.length / 2);
        setRecentlyPlayed(games.slice(0, mid));
        setRecommended(games.slice(mid));
      } catch (err) {
        console.error("Failed to load library", err);
        setError("Could not load your library.");
        setRecentlyPlayed([]);
        setRecommended([]);
      } finally {
        setLoading(false);
      }
    }

    loadLibrary();
  }, []);

  return (
    <div style={styles.container}>
      <div style={styles.mainContainer}>
        <Sidebar
          selectedCollection={selectedCollection}
          setSelectedCollection={setSelectedCollection}
        />

        <MainContent
          isLoggedIn={isLoggedIn}
          loading={loading}
          error={error}
          recentlyPlayed={recentlyPlayed}
          recommended={recommended}
        />
      </div>
    </div>
  );
}

// Componente: Sidebar
function Sidebar({ selectedCollection, setSelectedCollection }) {
  const collections = ["All Games", "Favorites", "Recently Added"];

  const categories = [
    "Battle Royale Arena",
    "Mystery Mansion",
    "Formula Circuit",
    "Shadow Realms",
    "Counter Strike Legacy",
    "Civilization Dawn",
    "Indie Platformer",
    "Sports Championship",
  ];

  return (
    <aside style={styles.sidebar}>
      <div style={styles.sidebarSection}>
        <div style={styles.sidebarTitle}>⭕ COLLECTIONS</div>
        {collections.map((collection, index) => (
          <div
            key={index}
            style={styles.sidebarItem}
            onClick={() => setSelectedCollection(collection)}
          >
            {collection}
          </div>
        ))}
      </div>

      <div style={styles.sidebarSection}>
        <div style={styles.sidebarTitle}>⭐ CATEGORIES</div>
        {categories.map((category, index) => (
          <div key={index} style={styles.sidebarItem}>
            {category}
          </div>
        ))}
      </div>
    </aside>
  );
}

// Componente: MainContent
function MainContent({ isLoggedIn, loading, error, recentlyPlayed, recommended }) {
  if (!isLoggedIn) {
    return (
      <div style={styles.mainContent}>
        <div style={{ color: "#ccc", padding: "20px" }}>
          Please log in to see your library.
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div style={styles.mainContent}>
        <div style={{ color: "#ccc", padding: "20px" }}>Loading your library…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={styles.mainContent}>
        <div style={{ color: "#f88", padding: "20px" }}>{error}</div>
      </div>
    );
  }

  if (recentlyPlayed.length === 0 && recommended.length === 0) {
    return (
      <div style={styles.mainContent}>
        <div style={{ color: "#ccc", padding: "20px" }}>
          Your library is empty. Try adding a game!
        </div>
      </div>
    );
  }

  return (
    <div style={styles.mainContent}>
      {recentlyPlayed.length > 0 && (
        <Section icon="⏱️" title="Recently Played" games={recentlyPlayed} />
      )}

      {recommended.length > 0 && (
        <Section icon="⭐" title="Recommended For You" games={recommended} />
      )}
    </div>
  );
}

// Componente: Section
function Section({ icon, title, games }) {
  return (
    <div style={styles.section}>
      <div style={styles.sectionHeader}>
        <div style={styles.sectionTitle}>
          <span style={styles.icon}>{icon}</span>
          {title}
        </div>
        <div style={styles.navArrows}>
          <Button variant="ghost" size="small">
            ‹
          </Button>
          <Button variant="ghost" size="small">
            ›
          </Button>
        </div>
      </div>
      <div style={styles.gameGrid}>
        {games.map((game) => (
          <GameCard
            key={game.id}
            game={game}
            variant="simple"
            onClick={() => console.log("Playing:", game.title)}
          />
        ))}
      </div>
    </div>
  );
}

// Estilos optimizados (sin estilos de botones)
const styles = {
  container: {
    background: "linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%)",
    color: "#e0e0e0",
    minHeight: "100vh",
  },
  mainContainer: {
    display: "flex",
    padding: "30px",
    gap: "30px",
  },
  sidebar: {
    width: "220px",
    background: "rgba(20, 20, 20, 0.5)",
    borderRadius: "12px",
    padding: "20px",
    height: "fit-content",
  },
  sidebarSection: {
    marginBottom: "25px",
  },
  sidebarTitle: {
    color: "#888",
    fontSize: "11px",
    textTransform: "uppercase",
    letterSpacing: "1px",
    marginBottom: "15px",
    display: "flex",
    alignItems: "center",
    gap: "8px",
  },
  sidebarItem: {
    padding: "10px 15px",
    marginBottom: "5px",
    borderRadius: "6px",
    cursor: "pointer",
    transition: "all 0.3s",
    fontSize: "14px",
    color: "#ccc",
  },
  mainContent: {
    flex: 1,
  },
  section: {
    marginBottom: "50px",
  },
  sectionHeader: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: "20px",
  },
  sectionTitle: {
    fontSize: "20px",
    fontWeight: "600",
    display: "flex",
    alignItems: "center",
    gap: "10px",
  },
  icon: {
    color: "#ff6b35",
  },
  navArrows: {
    display: "flex",
    gap: "10px",
  },
  gameGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
    gap: "20px",
  },
};
