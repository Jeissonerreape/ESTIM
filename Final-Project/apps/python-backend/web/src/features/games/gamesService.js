// src/features/games/gameService.js
import { pyGet } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

// Obtener todos los juegos de la tienda
export async function getAllGames() {
  // GET (PY): /games  (FastAPI lo tiene como /games/, redirige sin problema)
  return await pyGet(ENDPOINTS.GAMES);
}

// Obtener juegos por categoría (género)
// Usa el endpoint PY: GET /games/search/genre/?genre=...
export async function getGamesByCategory(category) {
  return await pyGet(ENDPOINTS.gamesByGenre(category));
}

// Obtener un juego específico por ID
// ⚠️ Requiere que el backend tenga GET /games/{game_id}
// Si aún no lo creas en main.py, esta función devolverá 404.
export async function getGameById(id) {
  return await pyGet(ENDPOINTS.gameById(id));
}

// Buscar juegos por texto
// GET /games/search/?q=...
export async function searchGames(query) {
  const path =
    query && query.trim()
      ? `${ENDPOINTS.GAMES_SEARCH}?q=${encodeURIComponent(query)}`
      : ENDPOINTS.GAMES_SEARCH;

  return await pyGet(path);
}
