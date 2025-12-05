import { apiGet, apiPost, apiDelete } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

// Obtener toda la wishlist del usuario (GET /me/wishlist)
export async function getWishlist() {
  return await apiGet(ENDPOINTS.MY_WISHLIST);
}

// Agregar un juego a la wishlist (POST /me/wishlist)
export async function addToWishlist(gameId, notificationPreferences = null) {
  return await apiPost(ENDPOINTS.MY_WISHLIST, {
    gameId,
    notificationPreferences,
  });
}

// Eliminar un juego de la wishlist (DELETE /me/wishlist/{gameId})
export async function removeFromWishlist(gameId) {
  return await apiDelete(ENDPOINTS.myWishlistItem(gameId));
}

// Verificar si un juego está en la wishlist
// (no hay endpoint dedicado: usamos getWishlist y revisamos localmente)
export async function isInWishlist(gameId) {
  const items = await getWishlist();
  const idStr = String(gameId);
  return Array.isArray(items) && items.some((item) => item.gameId === idStr);
}

// Limpiar toda la wishlist
// (sin endpoint dedicado: borramos todos los ítems uno por uno)
export async function clearWishlist() {
  const items = await getWishlist();
  if (!Array.isArray(items)) return;
  await Promise.all(items.map((item) => removeFromWishlist(item.gameId)));
}

// Obtener cantidad de items en wishlist
export async function getWishlistCount() {
  const items = await getWishlist();
  return Array.isArray(items) ? items.length : 0;
}
