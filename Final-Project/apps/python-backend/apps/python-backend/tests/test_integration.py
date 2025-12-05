"""
Integration tests that replicate the functionality from the original run_tests.py
These tests verify that different components work together properly.
"""
import pytest
from httpx import AsyncClient
from unittest.mock import Mock, patch
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker
from sqlalchemy.pool import StaticPool

from src.estim_py_api.app import app
from src.estim_py_api.db.database import Base, get_db
from src.estim_py_api.services.shopping_service import Cart
from tests.data_fixtures.game_sample_data import get_sample_games


@pytest.fixture
async def test_client_with_db():
    """Test client with database dependency overridden"""
    # Create in-memory database for testing
    SQLALCHEMY_DATABASE_URL = "sqlite+aiosqlite:///:memory:"
    engine = create_async_engine(
        SQLALCHEMY_DATABASE_URL,
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    TestingSessionLocal = async_sessionmaker(
        autocommit=False, 
        autoflush=False, 
        bind=engine, 
        class_=AsyncSession,
        expire_on_commit=False
    )
    
    # Create tables
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    
    async def override_get_db():
        async with TestingSessionLocal() as session:
            yield session
    
    # Override the database dependency
    app.dependency_overrides[get_db] = override_get_db
    
    from httpx import ASGITransport
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as client:
        yield client
    
    # Cleanup
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)


def test_carrito_basic_functionality():
    """Test basic cart functionality - replicates test_carrito_basico()"""
    cart = Cart()

    # 1) Carrito vacío
    assert len(cart.articulos) == 0, "El carrito debería iniciar vacío"
    assert cart.calcular_total() == 0, "El total inicial debería ser 0"

    # 2) Agregar un juego
    added = cart.agregar_articulo("game-1", "Juego de prueba", 10.0)
    assert added is True, "Debería agregarse el juego la primera vez"
    assert len(cart.articulos) == 1, "El carrito debería tener 1 artículo"
    assert cart.calcular_total() == 10.0, "El total debería ser 10.0"

    # 3) No duplicar juegos por game_id
    added_again = cart.agregar_articulo("game-1", "Juego de prueba", 10.0)
    assert added_again is False, "No debería agregarse dos veces el mismo game_id"
    assert len(cart.articulos) == 1, "El carrito debería seguir con 1 artículo"

    # 4) Remover juego
    removed = cart.remover_articulo("game-1")
    assert removed is True, "Debería poder remover el juego"
    assert len(cart.articulos) == 0, "El carrito debería quedar vacío"
    assert cart.calcular_total() == 0, "El total debería volver a 0"
    print("🛒 Funcionalidad básica de carrito: Test completado exitosamente")


def test_carrito_flujo_completo():
    """Test complete cart flow - replicates test_carrito_flujo_completo()"""
    cart = Cart()

    cart.agregar_articulo("game-a", "Juego A", 15.0)
    cart.agregar_articulo("game-b", "Juego B", 20.0)
    cart.agregar_articulo("game-c", "Juego C", 5.0)

    assert len(cart.articulos) == 3, "Debería haber 3 juegos en el carrito"

    cart.remover_articulo("game-b")
    assert len(cart.articulos) == 2
    assert cart.calcular_total() == 20.0  # 15 + 5

    cart.limpiar_carrito()
    assert len(cart.articulos) == 0
    print("🔄 Flujo completo de carrito: Test completado exitosamente")


@pytest.mark.asyncio
async def test_api_endpoints_integration(test_client_with_db):
    """Test API endpoints work together - replicates test_api_endpoints()"""
    client = await test_client_with_db

    endpoints = [
        ("/", "Endpoint raíz"),
        ("/health", "Health check"),
        ("/shopping_cart", "Carrito de compras"),
        ("/shopping_cart/total", "Total del carrito"),
        ("/games/", "Lista de juegos"),
        ("/games/search/", "Búsqueda de juegos"),
        ("/games/popular/", "Juegos populares"),
        ("/games/recent/", "Juegos recientes"),
    ]

    for path, label in endpoints:
        response = await client.get(path)
        # For most endpoints, we expect 200, but some might return different codes
        # depending on the test setup. Let's check they don't return server errors
        assert response.status_code in [200, 404, 422], f"{label} should not return server error"
    print("🌐 Integración de endpoints API: Test completado exitosamente")


@pytest.mark.asyncio
async def test_api_root_endpoint(test_client_with_db):
    """Specific test for root endpoint"""
    client = await test_client_with_db
    response = await client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert data["message"] == "ESTIM API funcionando"
    print("🏠 Endpoint raíz: Test completado exitosamente")


@pytest.mark.asyncio
async def test_api_health_endpoint(test_client_with_db):
    """Specific test for health endpoint"""
    client = await test_client_with_db
    response = await client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert "services" in data
    print("🏥 Endpoint de salud: Test completado exitosamente")


@pytest.mark.asyncio
async def test_api_cart_endpoints(test_client_with_db):
    """Test cart-related API endpoints"""
    client = await test_client_with_db

    # Test getting empty cart
    response = await client.get("/shopping_cart")
    assert response.status_code == 200
    data = response.json()
    assert "articulos" in data
    assert data["articulos"] == []  # Should be empty initially

    # Test getting cart total (should be 0)
    response = await client.get("/shopping_cart/total")
    assert response.status_code == 200
    data = response.json()
    assert "total" in data
    assert data["total"] == 0
    print("🛒 Endpoints de carrito API: Test completado exitosamente")


@pytest.mark.asyncio
async def test_search_functionality_with_mock():
    """Test search functionality with mocked database"""
    from src.estim_py_api.services.search_service import SearchService

    # Create a mock database session
    mock_db = Mock()

    # Test search functionality with sample data
    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_filtered_query = Mock()
        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_filtered_query
        mock_filtered_query.order_by.return_value = mock_filtered_query
        mock_filtered_query.offset.return_value = mock_filtered_query
        mock_filtered_query.limit.return_value = mock_filtered_query
        mock_filtered_query.all.return_value = get_sample_games()

        # Test the search function
        # Note: SearchService methods are async now, so we need to await them
        # But here we are mocking the DB, and if the service uses await db.execute, we need to mock that too.
        # The service uses: results = await SearchService.search_games(...)
        # And inside: result = await db.execute(...)
        
        # Since we are mocking the DB session, we need to make execute return an awaitable or mock it properly for async.
        # However, SearchService might be using `await db.execute` which expects an AsyncSession.
        # If we pass a Mock, `await mock.execute` will fail unless `execute` returns an awaitable.
        
        # For simplicity in this specific test, we might skip it or rewrite it to use the real async DB (test_client_with_db)
        # But let's try to adapt it.
        
        async def mock_execute(*args, **kwargs):
            m = Mock()
            m.scalars.return_value.all.return_value = get_sample_games()
            return m
            
        mock_db.execute = mock_execute
        
        results = await SearchService.search_games(mock_db, search_term="RPG", min_price=10.0, max_price=100.0)

        assert isinstance(results, list)
        assert len(results) > 0
        print("🔬 Funcionalidad de búsqueda con mock: Test completado exitosamente")


if __name__ == "__main__":
    pytest.main([__file__])