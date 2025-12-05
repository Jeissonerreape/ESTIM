// src/api/endpoints.js

// Me dice qué endpoints puede utilizar
export const ENDPOINTS = {
  // --- Auth (Java backend) ---
  REGISTER: "/auth/register",
  LOGIN: "/auth/login",

  // --- Cards / demo ---
  CARD: "/card",

  // --- Library (Java backend) ---
  MY_LIBRARY: "/me/library", // GET, POST
  myLibraryEntry: (gameId) => `/me/library/${gameId}`, // PATCH

  // --- Wishlist (Java backend) ---
  MY_WISHLIST: "/me/wishlist", // GET, POST
  myWishlistItem: (gameId) => `/me/wishlist/${gameId}`, // DELETE

  // --- Cart (Python backend) ---
  CART: "/shopping_cart", // GET
  cartItem: (gameId) => `/shopping_cart/items/${gameId}`, // POST (add) / DELETE (remove)
  CART_TOTAL: "/shopping_cart/total", // GET
  CART_CLEAR: "/shopping_cart/clear", // DELETE

  // --- Games catalog (Python backend) ---
  GAMES: "/games", // GET lista
  gameById: (gameId) => `/games/${gameId}`, // GET detalle (requiere el endpoint en backend)

  GAMES_SEARCH: "/games/search", // GET con ?q=...
  gamesByGenre: (genre) =>
    `/games/search/genre?genre=${encodeURIComponent(genre)}`, // GET

  GAMES_POPULAR: "/games/popular", // GET
  GAMES_RECENT: "/games/recent", // GET

  // --- Admin / seed ---
  ADMIN_SEED_DATA: "/admin/seed-data",

  // --- Simpsons (API externa demo) ---
  SIMPSON_CHARACTER: "https://thesimpsonsapi.com/api/characters/1",
};
