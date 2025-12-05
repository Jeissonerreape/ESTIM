"""Runner de tests empaquetado dentro de estim_py_api.

Este archivo es una copia adaptada de `run_tests.py` original pero
importando los módulos desde el paquete `estim_py_api`.
"""

import time
import traceback

from fastapi.testclient import TestClient
from sqlalchemy import text
import requests

from estim_py_api import database
from estim_py_api.shopping_cart import Cart
from estim_py_api.search_service import SearchService

BANNER_LINE = "🎮" * 60


def check_db_available() -> bool:
    try:
        with database.engine.connect() as conn:
            conn.execute(text("SELECT 1"))
        print("✅ Base de datos disponible, se ejecutarán pruebas de búsqueda y API.")
        return True
    except Exception as e:
        print("ℹ️ No se pudo conectar a la base de datos, se omiten pruebas de búsqueda/API.")
        print(f"   Detalle: {e}")
        return False


def setup_test_client() -> TestClient:
    from estim_py_api.app import app
    try:
        return TestClient(app)
    except Exception as e:
        print("   ℹ️ TestClient no disponible (fallback a HTTP real):", e)

        class SimpleClient:
            def __init__(self, base_url: str):
                self.base = base_url.rstrip("/")

            def get(self, path: str, **kwargs):
                url = self.base + path
                return requests.get(url, **kwargs)

        return SimpleClient("http://127.0.0.1:8000")


def test_carrito_basico():
    print("🛒 EJECUTANDO PRUEBAS DEL CARRITO (básico)...")

    cart = Cart()

    assert len(cart.articulos) == 0
    assert cart.calcular_total() == 0

    added = cart.agregar_articulo("game-1", "Juego de prueba", 10.0)
    assert added is True
    assert len(cart.articulos) == 1
    assert cart.calcular_total() == 10.0

    added_again = cart.agregar_articulo("game-1", "Juego de prueba", 10.0)
    assert added_again is False
    assert len(cart.articulos) == 1

    removed = cart.remover_articulo("game-1")
    assert removed is True
    assert len(cart.articulos) == 0
    assert cart.calcular_total() == 0

    print("   ✅ Pruebas básicas de carrito OK.")


def test_carrito_flujo_completo():
    print("🧪 EJECUTANDO PRUEBA DE FLUJO COMPLETO DEL CARRITO...")

    cart = Cart()

    cart.agregar_articulo("game-a", "Juego A", 15.0)
    cart.agregar_articulo("game-b", "Juego B", 20.0)
    cart.agregar_articulo("game-c", "Juego C", 5.0)

    assert len(cart.articulos) == 3
    assert cart.calcular_total() == 40.0

    cart.remover_articulo("game-b")
    assert len(cart.articulos) == 2
    assert cart.calcular_total() == 20.0

    cart.limpiar_carrito()
    assert len(cart.articulos) == 0
    print("   ✅ Flujo completo de carrito OK.")


def test_servicio_busqueda(has_db: bool):
    print("🔍 EJECUTANDO PRUEBAS DE BÚSQUEDA...")

    if not has_db:
        print("   ℹ️ BD no disponible, omitiendo pruebas de búsqueda.")
        return

    db = database.SessionLocal()
    try:
        results = SearchService.search_games(db, search_term="RPG")
        assert isinstance(results, list)
        print(f"   🔸 Resultados búsqueda 'RPG': {len(results)}")

        results_genre = SearchService.search_by_genre(db, genre="Action")
        assert isinstance(results_genre, list)
        print(f"   🔸 Resultados género 'Action': {len(results_genre)}")

        results_popular = SearchService.get_popular_games(db)
        assert isinstance(results_popular, list)
        print(f"   🔸 Resultados populares: {len(results_popular)}")

        results_recent = SearchService.get_recent_games(db)
        assert isinstance(results_recent, list)
        print(f"   🔸 Resultados recientes: {len(results_recent)}")

    finally:
        db.close()

    print("   ✅ Pruebas del servicio de búsqueda OK.")


def test_api_endpoints(client: TestClient, has_db: bool):
    print("🌐 EJECUTANDO PRUEBAS DE API...")

    if not has_db:
        print("   ℹ️ BD no disponible, omitiendo pruebas de API.")
        return

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
        print(f"   🔸 Probando {path} ({label})...")
        resp = client.get(path)
        assert resp.status_code == 200, f"{label} - status {resp.status_code}"
        print(f"      ✅ {label} OK (status {resp.status_code})")

    print("   ✅ Todas las pruebas de API OK.")


def main():
    print(BANNER_LINE)
    print("🎯 PRUEBAS COMPLETAS - SISTEMA ESTIM")
    print("🎮 Carrito + Búsqueda + API")
    print(BANNER_LINE)
    print("\n🚀 INICIANDO SUITE COMPLETA DE PRUEBAS...\n")

    total_tests = 0
    total_passed = 0
    errores = []

    start_time = time.time()

    has_db = check_db_available()

    try:
        client = setup_test_client()
    except Exception as e:
        print("💥 Error creando TestClient:")
        traceback.print_exc()
        client = None
        has_db = False

    print("\n🎯 CARRITO DE COMPRAS")
    print("--------------------------------------------------")

    total_tests += 1
    try:
        test_carrito_basico()
        total_passed += 1
    except Exception as e:
        msg = f"Error en carrito (básico): {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    total_tests += 1
    try:
        test_carrito_flujo_completo()
        total_passed += 1
    except Exception as e:
        msg = f"Error en carrito (flujo completo): {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    print("\n🎯 SERVICIO DE BÚSQUEDA")
    print("--------------------------------------------------")
    total_tests += 1
    try:
        test_servicio_busqueda(has_db)
        if has_db:
            total_passed += 1
    except Exception as e:
        msg = f"Error en servicio de búsqueda: {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    print("\n🎯 ENDPOINTS API")
    print("--------------------------------------------------")
    total_tests += 1
    try:
        if client is not None:
            test_api_endpoints(client, has_db)
            if has_db:
                total_passed += 1
        else:
            print("   ℹ️ TestClient no disponible, omitiendo pruebas de API.")
    except Exception as e:
        msg = f"Error en endpoints API: {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    end_time = time.time()
    elapsed = end_time - start_time

    print("\n" + "📊" * 60)
    print("📈 RESUMEN COMPLETO DE PRUEBAS")
    print("📊" * 60 + "\n")

    print(f"⏱️  Tiempo total de ejecución: {elapsed:.2f} segundos\n")
    print(f"🎯 TOTAL GENERAL (planificadas): {total_tests} pruebas")
    print(f"✅ Pruebas exitosas: {total_passed}")
    print(f"❌ Pruebas fallidas: {len(errores)}")

    if errores:
        print("\nDetalles de errores:")
        for err in errores:
            print(f"   - {err}")
        print("\n💥 ALGUNAS PRUEBAS FALLARON")
        return 1

    print("\n🎉 TODAS LAS PRUEBAS QUE SE EJECUTARON PASARON CORRECTAMENTE")
    return 0


if __name__ == "__main__":
    exit(main())
