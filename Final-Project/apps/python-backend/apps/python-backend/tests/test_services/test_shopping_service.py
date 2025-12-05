import pytest
from src.estim_py_api.services.shopping_service import Cart


def test_cart_initially_empty():
    """Test that a new cart is initially empty"""
    cart = Cart()
    assert len(cart.articulos) == 0
    assert cart.calcular_total() == 0
    print("✅ Carrito inicialmente vacío: Test completado exitosamente")


def test_add_item_to_cart():
    """Test adding an item to the cart"""
    cart = Cart()

    # Add an item
    result = cart.agregar_articulo("game-1", "Test Game", 10.0)
    assert result is True  # Should return True when item is added
    assert len(cart.articulos) == 1
    assert cart.calcular_total() == 10.0
    print("🛒 Artículo agregado al carrito: Test completado exitosamente")


def test_cannot_add_same_item_twice():
    """Test that the same game cannot be added twice to the cart"""
    cart = Cart()

    # Add an item
    result1 = cart.agregar_articulo("game-1", "Test Game", 10.0)
    assert result1 is True

    # Try to add the same item again
    result2 = cart.agregar_articulo("game-1", "Test Game", 10.0)
    assert result2 is False  # Should return False when item already exists
    assert len(cart.articulos) == 1  # Should still have only 1 item
    assert cart.calcular_total() == 10.0
    print("🚫 No se puede duplicar artículo: Test completado exitosamente")


def test_remove_item_from_cart():
    """Test removing an item from the cart"""
    cart = Cart()

    # Add an item
    cart.agregar_articulo("game-1", "Test Game", 10.0)
    assert len(cart.articulos) == 1
    assert cart.calcular_total() == 10.0

    # Remove the item
    result = cart.remover_articulo("game-1")
    assert result is True  # Should return True when item is removed
    assert len(cart.articulos) == 0
    assert cart.calcular_total() == 0
    print("🧹 Artículo removido del carrito: Test completado exitosamente")


def test_remove_nonexistent_item():
    """Test removing an item that doesn't exist in the cart"""
    cart = Cart()

    # Try to remove an item that doesn't exist
    result = cart.remover_articulo("nonexistent")
    assert result is False  # Should return False when item doesn't exist
    assert len(cart.articulos) == 0
    assert cart.calcular_total() == 0
    print("🔍 Intento de remover artículo inexistente: Test completado exitosamente")


def test_cart_total_calculation():
    """Test that the cart correctly calculates totals"""
    cart = Cart()

    # Add multiple items
    cart.agregar_articulo("game-1", "Game 1", 10.0)
    cart.agregar_articulo("game-2", "Game 2", 15.5)
    cart.agregar_articulo("game-3", "Game 3", 5.25)

    assert len(cart.articulos) == 3
    assert cart.calcular_total() == 30.75  # 10.0 + 15.5 + 5.25
    print("🧮 Cálculo total del carrito: Test completado exitosamente")


def test_clear_cart():
    """Test clearing all items from the cart"""
    cart = Cart()

    # Add multiple items
    cart.agregar_articulo("game-1", "Game 1", 10.0)
    cart.agregar_articulo("game-2", "Game 2", 15.5)
    assert len(cart.articulos) == 2
    assert cart.calcular_total() == 25.5

    # Clear the cart
    cart.limpiar_carrito()
    assert len(cart.articulos) == 0
    assert cart.calcular_total() == 0
    print("🗑️ Carrito limpiado: Test completado exitosamente")


def test_cart_item_structure():
    """Test that cart items have the correct structure"""
    cart = Cart()

    # Add an item
    cart.agregar_articulo("game-1", "Test Game", 10.0)

    # Check the structure of the cart item
    item = cart.articulos[0]
    assert "game_id" in item
    assert "articulo" in item
    assert "precio" in item
    assert item["game_id"] == "game-1"
    assert item["articulo"] == "Test Game"
    assert item["precio"] == 10.0
    print("📋 Estructura de artículo en carrito: Test completado exitosamente")


if __name__ == "__main__":
    pytest.main([__file__])