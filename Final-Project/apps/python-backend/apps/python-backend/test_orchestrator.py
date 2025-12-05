"""
Orquestador de Pruebas Completo para ESTIM API (Refactorizado)
============================================================

Este script ejecuta una suite completa de pruebas para el sistema ESTIM,
verificando todas las funcionalidades desde nivel unitario hasta integración,
siguiendo las mejores prácticas de pytest y asyncio.
"""

import pytest
import uuid

# Asegurarse de que src esté en el path es manejado por pytest ahora (ver pytest.ini)

# --- Pruebas Unitarias y de Integridad de Servicios ---

@pytest.mark.asyncio
async def test_shopping_service_initialization():
    """Verifica que el servicio de carrito se inicializa correctamente."""
    from estim_py_api.services.shopping_service import Cart
    cart = Cart()
    assert cart is not None
    print("SUCCESS: Servicio de Carrito OK")

def test_search_service_import():
    """Verifica que el servicio de búsqueda se puede importar."""
    from estim_py_api.services.search_service import SearchService
    assert SearchService is not None
    print("SUCCESS: Servicio de Busqueda OK")

def test_schemas_import():
    """Verifica que los esquemas de Pydantic se pueden importar."""
    from estim_py_api.schemas.game_schemas import GameOut
    assert GameOut is not None
    print("SUCCESS: Esquemas OK")

def test_database_model_import():
    """Verifica que el modelo de base de datos se puede importar."""
    from estim_py_api.db.database import Base
    assert Base is not None
    print("SUCCESS: Modelo de Base de Datos OK")

def test_fastapi_app_import():
    """Verifica que la aplicación FastAPI principal se puede importar."""
    from estim_py_api.app import app
    assert app is not None
    print("SUCCESS: Aplicacion FastAPI OK")

@pytest.mark.asyncio
async def test_cart_business_logic():
    """Prueba la lógica de negocio del carrito (agregar y calcular total)."""
    from estim_py_api.services.shopping_service import Cart
    cart = Cart()
    await cart.agregar_articulo('test', 'Prueba', 10.0)
    total = await cart.calcular_total()
    assert total == 10.0
    print("SUCCESS: Logica de Carrito OK")

def test_uuid_generation():
    """Prueba la generación de UUIDs."""
    uid = uuid.uuid4()
    assert isinstance(uid, uuid.UUID)
    print("SUCCESS: Generacion de UUID OK")


# --- Pruebas de API y Endpoints ---

def test_api_route_count():
    """Verifica que la aplicación FastAPI tiene un número suficiente de rutas."""
    from estim_py_api.app import app
    routes = [r.path for r in app.routes]
    assert len(routes) > 15
    print(f"SUCCESS: App con {len(routes)} rutas")


# --- Pruebas del Sistema de Autenticación ---

def test_auth_handler_import():
    """Verifica que el manejador de autenticación se puede importar."""
    from estim_py_api.security.auth_handler import create_access_token
    assert create_access_token is not None
    print("SUCCESS: Handler de Autenticacion OK")

def test_auth_schemas_import():
    """Verifica que los esquemas de autenticación se pueden importar."""
    from estim_py_api.schemas.auth_schemas import UserCreate, Token
    assert UserCreate is not None
    assert Token is not None
    print("SUCCESS: Esquemas de Autenticacion OK")


# --- Pruebas de Funcionalidades Específicas ---

def test_search_schemas_import():
    """Verifica que los esquemas de búsqueda se pueden importar."""
    from estim_py_api.schemas.game_schemas import GameSearchRequest
    assert GameSearchRequest is not None
    print("SUCCESS: Schemas de busqueda OK")

def test_cart_schemas_import():
    """Verifica que los esquemas del carrito se pueden importar."""
    from estim_py_api.schemas.game_schemas import CartItem, Cart
    assert CartItem is not None
    assert Cart is not None
    print("SUCCESS: Schemas de carrito OK")

# Nota: La prueba 'run_pytest_tests' se elimina porque es redundante.
# Pytest se encarga de descubrir y ejecutar todas las pruebas, incluyendo
# las que están en el directorio 'tests/'.
