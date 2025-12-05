"""estim_py_api package

Paquete reorganizado con estructura N-tier:
 - app (FastAPI main app)
 - db (database models / sesión)
 - services (business logic)
 - schemas (pydantic schemas)

Se exponen símbolos para mantener compatibilidad con wrappers existentes.
"""

# Lazy imports para evitar problemas con la carga inicial de SQLAlchemy
def __getattr__(name):
    """Lazy loading de componentes para evitar problemas con SQLAlchemy en Python 3.13."""
    if name == "app":
        from .app import app
        return app
    elif name == "get_db":
        from .db.database import get_db
        return get_db
    elif name == "engine":
        from .db.database import engine
        return engine
    elif name == "SessionLocal":
        from .db.database import SessionLocal
        return SessionLocal
    elif name == "Base":
        from .db.database import Base
        return Base
    elif name == "GameDB":
        from .db.database import GameDB
        return GameDB
    elif name == "UserDB":
        from .db.database import UserDB
        return UserDB
    elif name == "Cart":
        from .services.shopping_service import Cart
        return Cart
    elif name == "SearchService":
        from .services.search_service import SearchService
        return SearchService
    else:
        raise AttributeError(f"module '{__name__}' has no attribute '{name}'")

__all__ = ["app", "get_db", "engine", "SessionLocal", "Base", "GameDB", "UserDB", "Cart", "SearchService"]
