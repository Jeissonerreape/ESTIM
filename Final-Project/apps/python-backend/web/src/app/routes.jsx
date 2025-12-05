import { HashRouter, Routes, Route } from "react-router-dom";

// COMPONENTS
import Layout from "../components/Layout";

// AUTH
import LoginPage from "../features/auth/LoginPage";
import RegisterPage from "../features/auth/RegisterPage";

// GAMES
import StorePage from "../features/games/StorePage";
import GamesListPage from "../features/games/GamesListPage";
import GameDetailPage from "../features/games/GameDetailPage";

// CART
import CartPage from "../features/cart/CartPage";
import PurchaseSuccessPage from "../features/cart/PurchaseSuccessPage";

// LIBRARY
import LibraryPage from "../features/library/LibraryPage";

// WISHLIST
import WishlistPage from "../features/wishlist/WishlistPage";

// EXTRAS
import SimpsonsPage from "../features/simpsons/SimpsonsPage";

export default function AppRoutes() {
  return (
    <HashRouter>
      <Routes>
        {/* AUTH */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route element={<Layout />}>
          {/* GAMES */}
          <Route path="/" element={<StorePage />} />
          <Route path="/games" element={<GamesListPage />} />
          <Route path="/game/:id" element={<GameDetailPage />} />

          {/* CART */}
          <Route path="/cart" element={<CartPage />} />
          <Route path="/cart/success" element={<PurchaseSuccessPage />} />

          {/* LIBRARY */}
          <Route path="/library" element={<LibraryPage />} />

          {/* WISHLIST */}
          <Route path="/wishlist" element={<WishlistPage />} />

          {/* EXTRAS */}
          <Route path="/simpsons" element={<SimpsonsPage />} />
        </Route>
      </Routes>
    </HashRouter>
  );
}
