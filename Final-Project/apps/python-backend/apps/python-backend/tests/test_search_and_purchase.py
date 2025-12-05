"""
Test endpoints para las nuevas funcionalidades de búsqueda y compra
"""

import sys
import os
# Asegurar que src esté en el path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "src"))

from src.estim_py_api.app import app
import sys
import os
import pytest
# Asegurar que src esté en el path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "src"))

from src.estim_py_api.app import app


@pytest.mark.asyncio
async def test_search_games_endpoint(client):
    """Test del endpoint de búsqueda de juegos."""
    response = await client.get("/games/search/")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    print("🔍 Endpoint de búsqueda de juegos: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_search_games_with_filters(client):
    """Test del endpoint de búsqueda con filtros."""
    response = await client.get("/games/search/?q=zelda&min_price=20&max_price=70")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    print("🔍 Endpoint de búsqueda con filtros: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_games_by_genre(client):
    """Test del endpoint para obtener juegos por género."""
    response = await client.get("/games/search/genre/?genre=Action")
    assert response.status_code in [200, 404, 422]  # Puede devolver 404 si no hay juegos de ese género
    
    if response.status_code == 200:
        data = response.json()
        assert isinstance(data, list)
    print("🎭 Endpoint de juegos por género: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_featured_games(client):
    """Test del endpoint para juegos destacados."""
    response = await client.get("/games/popular/")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    print("✨ Endpoint de juegos destacados: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_new_games(client):
    """Test del endpoint para juegos nuevos."""
    response = await client.get("/games/recent/")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    print("🆕 Endpoint de juegos nuevos: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_cart_authenticated(client):
    """Test del endpoint para obtener el carrito (requiere autenticación)."""
    # Este endpoint requiere token de autenticación
    response = await client.get("/cart")
    # Puede devolver 401 sin token o 200 con token válido
    assert response.status_code in [200, 401]  
    
    if response.status_code == 200:
        data = response.json()
        assert "articulos" in data or "items" in data
    print("🛒 Endpoint de obtener carrito: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_add_to_cart_authenticated(client):
    """Test del endpoint para añadir al carrito (requiere autenticación)."""
    # Este endpoint requiere token de autenticación
    response = await client.post("/cart/add/nonexistent-game-id", json={"quantity": 1})
    # Puede devolver 401 sin token o 404 con token pero juego no existente
    assert response.status_code in [401, 404, 422] 
    
    print("📥 Endpoint de añadir al carrito: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_update_cart_item_authenticated(client):
    """Test del endpoint para actualizar artículo en carrito (requiere autenticación)."""
    response = await client.put("/cart/update/nonexistent-game-id", 
                         json={"quantity": 2})
    assert response.status_code in [401, 404, 422]
    
    print("🔄 Endpoint de actualizar artículo: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_remove_from_cart_authenticated(client):
    """Test del endpoint para remover artículo del carrito (requiere autenticación)."""
    response = await client.delete("/cart/remove/nonexistent-game-id")
    assert response.status_code in [401, 404, 422]
    
    print("📤 Endpoint de remover artículo: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_cart_total(client):
    """Test del endpoint para obtener total del carrito."""
    response = await client.get("/cart/total")
    assert response.status_code in [200, 401]
    
    if response.status_code == 200:
        data = response.json()
        assert "total" in data
    print("💰 Endpoint de total del carrito: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_checkout_endpoint(client):
    """Test del endpoint para procesar compra."""
    response = await client.post("/checkout", json={
        "payment_method": "credit_card",
        "shipping_address": {
            "street": "123 Main St",
            "city": "Anytown",
            "state": "CA",
            "zip": "12345",
            "country": "USA"
        },
        "email": "test@example.com"
    })
    assert response.status_code in [200, 401, 422]
    
    print("💳 Endpoint de checkout: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_purchase_history(client):
    """Test del endpoint para historial de compras."""
    response = await client.get("/orders/history")
    assert response.status_code in [200, 401]
    
    if response.status_code == 200:
        data = response.json()
        assert isinstance(data, list)
    print("📦 Endpoint de historial de compras: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_recommendations(client):
    """Test del endpoint para recomendaciones."""
    response = await client.get("/recommendations")
    assert response.status_code in [200, 401]
    
    if response.status_code == 200:
        data = response.json()
        assert isinstance(data, list)
    print("🎯 Endpoint de recomendaciones: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_get_user_library(client):
    """Test del endpoint para biblioteca del usuario."""
    response = await client.get("/library")
    assert response.status_code in [200, 401]
    
    if response.status_code == 200:
        data = response.json()
        assert isinstance(data, list)
    print("📚 Endpoint de biblioteca de usuario: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_advanced_filters_endpoint(client):
    """Test del endpoint para filtros avanzados."""
    response = await client.post("/games/filter", json={"on_sale": True, "min_rating": 4.0})
    # Note: changed from GET to POST as per app.py definition
    assert response.status_code in [200, 401]
    
    if response.status_code == 200:
        data = response.json()
        assert "games" in data
    print("筛选 Endpoint de filtros avanzados: ✅ Test completado exitosamente")


@pytest.mark.asyncio
async def test_related_games_endpoint(client):
    """Test del endpoint para juegos relacionados."""
    # Usamos un ID de juego válido si existen juegos en la base de datos
    response = await client.get("/games/nonexistent-id/related")
    assert response.status_code in [200, 404, 422]
    
    if response.status_code == 200:
        data = response.json()
        assert isinstance(data, list)
    print("🔗 Endpoint de juegos relacionados: ✅ Test completado exitosamente")