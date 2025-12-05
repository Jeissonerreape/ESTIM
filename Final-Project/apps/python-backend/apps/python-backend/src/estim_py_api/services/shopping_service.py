from typing import List, Dict


class Cart:
    def __init__(self):
        # Cada item tendrá: game_id, articulo (título), precio
        self.articulos: List[Dict] = []

    async def agregar_articulo(self, game_id: str, articulo: str, precio: float) -> bool:
        """
        Devuelve True si se agregó un nuevo juego.
        Devuelve False si el juego ya estaba en el carrito.
        """
        for item in self.articulos:
            if item["game_id"] == game_id:
                return False  # ya existe

        self.articulos.append({
            "game_id": game_id,
            "articulo": articulo,
            "precio": precio
        })
        return True

    async def remover_articulo(self, game_id: str) -> bool:
        """
        Elimina por game_id. Devuelve True si lo encontró y eliminó,
        False si no estaba en el carrito.
        """
        for item in list(self.articulos):
            if item["game_id"] == game_id:
                self.articulos.remove(item)
                return True
        return False

    async def calcular_total(self) -> float:
        return sum(item["precio"] for item in self.articulos)

    async def limpiar_carrito(self) -> None:
        self.articulos.clear()

    # Add the English method names for API consistency in services layer
    async def add_item(self, game_id: str, title: str, price: float) -> bool:
        return await self.agregar_articulo(game_id, title, price)

    async def remove_item(self, game_id: str) -> bool:
        return await self.remover_articulo(game_id)

    async def total(self) -> float:
        return await self.calcular_total()

    async def clear(self) -> None:
        await self.limpiar_carrito()


# Instancia global para compatibilidad con la estructura de servicios
cart = Cart()
