import pytest
from src.estim_py_api.app import app

@pytest.mark.asyncio
async def test_root_endpoint(client):
    """Test the root endpoint"""
    response = await client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "message" in data
    assert data["message"] == "ESTIM API funcionando"
    print("🏠 Endpoint raíz: Test completado exitosamente")


@pytest.mark.asyncio
async def test_health_endpoint(client):
    """Test the health endpoint"""
    response = await client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert "status" in data
    assert data["status"] == "healthy"
    assert "services" in data
    print("🏥 Endpoint de salud: Test completado exitosamente")


@pytest.mark.asyncio
async def test_games_endpoint(client):
    """Test the games endpoint"""
    response = await client.get("/games/")
    assert response.status_code == 200
    data = response.json()
    # This might return an empty list if no games are in the test DB
    assert isinstance(data, list)
    print("🎮 Endpoint de juegos: Test completado exitosamente")


@pytest.mark.asyncio
async def test_shopping_cart_endpoints(client):
    """Test shopping cart endpoints"""
    # Add an item to cart (this will fail with 422 because game doesn't exist, but should fail in expected way)
    response = await client.post("/shopping_cart/items/invalid-uuid")
    # This should return 422 for invalid UUID or 404 for non-existent game
    
    # Test getting cart (should return empty cart)
    response = await client.get("/shopping_cart")
    assert response.status_code == 200
    data = response.json()
    assert "articulos" in data
    assert isinstance(data["articulos"], list)

    # Test getting cart total (should return 0)
    response = await client.get("/shopping_cart/total")
    assert response.status_code == 200
    data = response.json()
    assert "total" in data
    assert data["total"] == 0
    print("🛒 Endpoints de carrito de compras: Test completado exitosamente")


@pytest.mark.asyncio
async def test_search_endpoints(client):
    """Test search endpoints"""
    # Test basic search
    response = await client.get("/games/search/?q=test")
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)

    # Test genre search
    response = await client.get("/games/search/genre/?genre=Action")
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)

    # Test popular games
    response = await client.get("/games/popular/")
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)

    # Test recent games
    response = await client.get("/games/recent/")
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    print("🔍 Endpoints de búsqueda: Test completado exitosamente")


if __name__ == "__main__":
    pytest.main([__file__])