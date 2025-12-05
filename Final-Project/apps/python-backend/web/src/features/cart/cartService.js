// src/features/cart/cartService.js
import { pyGet, pyPost, pyDelete } from "../../api/apiClient";
import { ENDPOINTS } from "../../api/endpoints";

// Obtener todos los items del carrito
// GET (PY): /shopping_cart
export async function getCart() {
  const data = await pyGet(ENDPOINTS.CART);
  const rawItems = Array.isArray(data?.articulos) ? data.articulos : [];

  // Mapeamos a la forma que CartPage.jsx espera
  return rawItems.map((item) => ({
    id: item.game_id,              // para keys, remove, etc.
    title: item.articulo,          // título del juego
    price: item.precio,            // precio numérico
    discount: 0,                   // por ahora sin descuento
    image: "https://via.placeholder.com/120x80?text=Game", // placeholder
    description: "",               // puedes rellenar algo luego
  }));
}

// Agregar un juego al carrito
// POST (PY): /shopping_cart/items/{gameId}
export async function addToCart(gameId) {
  return await pyPost(ENDPOINTS.cartItem(gameId));
}

// Eliminar un juego del carrito
// DELETE (PY): /shopping_cart/items/{gameId}
export async function removeFromCart(gameId) {
  return await pyDelete(ENDPOINTS.cartItem(gameId));
}

// Vaciar todo el carrito
// DELETE (PY): /shopping_cart/clear
export async function clearCart() {
  return await pyDelete(ENDPOINTS.CART_CLEAR);
}

// (No implementado todavía en el backend)
export async function updateCartItem(gameId, quantity) {
  throw new Error(
    "updateCartItem aún no está implementado en el backend (no existe /shopping_cart/update)."
  );
}

// (No implementado todavía en el backend)
export async function checkout() {
  throw new Error(
    "checkout aún no está implementado en el backend (no existe /shopping_cart/checkout)."
  );
}
