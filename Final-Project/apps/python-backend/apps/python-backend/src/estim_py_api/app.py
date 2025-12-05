import os
import uuid
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Depends, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text, and_
from fastapi.middleware.cors import CORSMiddleware
from typing import Dict, List, Optional, Any
from datetime import datetime, timedelta
from decimal import Decimal
from enum import Enum


from .db.database import get_db, AsyncSessionLocal, GameDB, UserDB
from .services.shopping_service import cart
from .services.search_service import SearchService
from .schemas.auth_schemas import UserCreate, UserLogin
from .schemas.game_schemas import (
    GameSearchRequest,
    GameOut,
    CartItem,
    Cart,
    AddToCartRequest,
    UpdateCartItemRequest,
    CheckoutRequest,
    GameFilterRequest
)
from estim_py_api.schemas.response_schemas import (
    GameSearchResponse,
    CartResponse,
    CartItemResponse,
    CheckoutResponse,
    OrderHistoryResponse,
    RecommendationResponse,
    FilteredGamesResponse
)
from passlib.context import CryptContext
import jwt
from jwt.exceptions import InvalidTokenError


# Password hashing context
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Secret key for JWT (should be set in environment)
SECRET_KEY = os.getenv("SECRET_KEY", "your-secret-key-change-this-in-production")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", "30"))

# OAuth2 scheme for token extraction
from fastapi.security import OAuth2PasswordBearer, HTTPBearer
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/token")
security_scheme = HTTPBearer()


async def get_current_user(token: str = Depends(oauth2_scheme)):
    """Get the current authenticated user from the token."""
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
    except InvalidTokenError:
        raise credentials_exception

    # In a real app, you would fetch the user from the database
    # For now, return mock user data
    return {"username": username, "id": "mock-user-id"}


async def get_current_active_user(current_user: dict = Depends(get_current_user)):
    """Get the current active authenticated user."""
    if not current_user.get("is_active", True):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Inactive user"
        )
    return current_user


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Lifespan context manager for startup and shutdown events.
    In production:
      - NO creamos tablas.
      - Asumimos que el esquema ya fue creado por migraciones SQL.
      - Solo hacemos un sanity check de conexión.
    """
    # Perform startup checks
    async with AsyncSessionLocal() as session:
        try:
            # Create a temporary session to test the database connection
            await session.execute(text("SELECT 1"))
            print("✅ Conexión a BD OK (esquema asumido existente)")
        except Exception as e:
            print(f"❌ Error conectando a BD: {e}")
            raise e

    yield  # This is where the application runs

    # Add any cleanup code here if needed during shutdown
    print("🛑 Aplicación cerrándose")


app = FastAPI(lifespan=lifespan)

# ==================== CORS ====================

_raw_origins = os.getenv("ESTIM_CORS_ORIGINS")

if _raw_origins:
    # Split comma-separated list and strip spaces
    allowed_origins = [
        origin.strip()
        for origin in _raw_origins.split(",")
        if origin.strip()
    ]
else:
    # Safe defaults for local dev + GitHub Pages
    allowed_origins = [
        "http://localhost:5173",          # Vite dev
        "https://jujgomezru.github.io",   # GitHub Pages (ESTIM under /ESTIM/)
    ]

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Database dependency for async sessions
async def get_db() -> AsyncSession:
    async with AsyncSessionLocal() as session:
        try:
            yield session
        finally:
            await session.close()


# ==================== ENDPOINTS BÁSICOS ====================

from pydantic import BaseModel


class HealthResponse(BaseModel):
    status: str
    services: dict


class TestDbResponse(BaseModel):
    database: str
    total_games: int


class CartItemResponse(BaseModel):
    game_id: str
    articulo: str
    precio: float


class CartAddResponse(BaseModel):
    message: str
    item: CartItemResponse


class CartResponse(BaseModel):
    articulos: list


class CartTotalResponse(BaseModel):
    total: float


@app.get("/")
async def root():
    return {"message": "ESTIM API funcionando"}


@app.get("/health", response_model=HealthResponse)
async def test_health():
    """Endpoint simple para verificar que la API funciona"""
    return {
        "status": "healthy",
        "services": {
            "api": "running",
            "search": "available",
            "cart": "available",
        },
    }


@app.get("/test-db", response_model=TestDbResponse)
async def test_db(db: AsyncSession = Depends(get_db)):
    """Probar que la BD responde y la tabla games existe."""
    try:
        from sqlalchemy import select, func
        result = await db.execute(select(func.count(GameDB.id)))
        count = result.scalar()
        return {"database": "connected", "total_games": count}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")


# ==================== CARRITO DE COMPRAS (IN-MEMORY) ====================

@app.post("/shopping_cart/items/{game_id}", response_model=CartAddResponse)
async def add_item(game_id: str, db: AsyncSession = Depends(get_db)):
    try:
        game_uuid = uuid.UUID(game_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="ID de juego inválido")

    from sqlalchemy import select
    stmt = select(GameDB).where(GameDB.id == game_uuid, GameDB.is_published == True)
    result = await db.execute(stmt)
    game = result.scalar_one_or_none()

    if not game:
        raise HTTPException(
            status_code=404, detail="Juego no encontrado o no publicado"
        )

    if await cart.agregar_articulo(str(game.id), game.title, float(game.price)):
        return {
            "message": "Juego agregado al carrito",
            "item": {
                "game_id": str(game.id),
                "articulo": game.title,
                "precio": float(game.price),
            },
        }
    else:
        raise HTTPException(
            status_code=400, detail="El juego ya está en el carrito"
        )


@app.delete("/shopping_cart/items/{game_id}")
async def delete_item(game_id: str):
    if not await cart.remover_articulo(game_id):
        raise HTTPException(
            status_code=404, detail="Juego no encontrado en el carrito"
        )
    return {"message": "Juego eliminado del carrito"}


@app.get("/shopping_cart", response_model=CartResponse)
async def get_cart():
    return {"articulos": cart.articulos}


@app.get("/shopping_cart/total", response_model=CartTotalResponse)
async def get_total():
    return {"total": await cart.calcular_total()}


@app.delete("/shopping_cart/clear")
async def clear_cart():
    await cart.limpiar_carrito()
    return {"message": "Carrito vaciado"}


# ==================== AUTENTICACIÓN ====================

from pydantic import BaseModel


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    """Create a JWT access token."""
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)

    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


@app.post("/auth/register", response_model=Dict[str, str])
async def register_user(user: UserCreate, db: AsyncSession = Depends(get_db)):
    """Register a new user."""
    from sqlalchemy import select, or_
    # Check if user already exists
    stmt = select(UserDB).where(
        or_(UserDB.username == user.username, UserDB.email == user.email)
    )
    result = await db.execute(stmt)
    existing_user = result.scalar_one_or_none()

    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username or email already registered"
        )

    # Hash the password
    hashed_password = pwd_context.hash(user.password)

    # Create new user
    db_user = UserDB(
        username=user.username,
        email=user.email,
        hashed_password=hashed_password
    )

    db.add(db_user)
    await db.commit()
    await db.refresh(db_user)

    return {"message": "User registered successfully"}


@app.post("/auth/token", response_model=Dict[str, str])
async def login_for_access_token(user_credentials: UserLogin, db: AsyncSession = Depends(get_db)):
    """OAuth2 compatible token login, get an access token for future requests."""
    from sqlalchemy import select
    # Find the user in the database
    stmt = select(UserDB).where(UserDB.username == user_credentials.username)
    result = await db.execute(stmt)
    user = result.scalar_one_or_none()

    if not user or not pwd_context.verify(user_credentials.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Inactive user"
        )

    # Create access token
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.username}, expires_delta=access_token_expires
    )

    return {"access_token": access_token, "token_type": "bearer"}


# ==================== JUEGOS Y BÚSQUEDA ====================

class GameResponse(BaseModel):
    id: str
    title: str
    price: float
    description: str
    average_rating: float


@app.get("/games/", response_model=List[GameResponse])
async def get_games(db: AsyncSession = Depends(get_db)):
    """Obtener todos los juegos publicados"""
    try:
        from sqlalchemy import select
        stmt = select(GameDB).where(GameDB.is_published == True).limit(20)
        result = await db.execute(stmt)
        games = result.scalars().all()

        return [
            {
                "id": str(game.id),
                "title": game.title,
                "price": float(game.price),
                "description": game.short_description or "",
                "average_rating": float(game.average_rating),
            }
            for game in games
        ]
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


class SearchGameResponse(BaseModel):
    id: str
    title: str
    price: float
    average_rating: float
    description: str
    genres: List[str]
    game_metadata: Dict | None


@app.get("/games/search/", response_model=List[SearchGameResponse])
async def search_games(
    q: str = "",
    min_price: float | None = None,
    max_price: float | None = None,
    skip: int = 0,
    limit: int = 20,
    db: AsyncSession = Depends(get_db),
):
    """Buscar juegos por título, descripción o características"""
    try:
        results = await SearchService.search_games(
            db=db,
            search_term=q,
            min_price=min_price,
            max_price=max_price,
            skip=skip,
            limit=limit,
        )
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/games/search/genre/", response_model=List[SearchGameResponse])
async def search_by_genre(
    genre: str,
    skip: int = 0,
    limit: int = 20,
    db: AsyncSession = Depends(get_db),
):
    """Buscar juegos por género"""
    try:
        results = await SearchService.search_by_genre(
            db=db,
            genre=genre,
            skip=skip,
            limit=limit,
        )
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


class PopularGameResponse(BaseModel):
    id: str
    title: str
    price: float
    average_rating: float
    description: str


@app.get("/games/popular/", response_model=List[PopularGameResponse])
async def get_popular_games(
    skip: int = 0,
    limit: int = 10,
    db: AsyncSession = Depends(get_db),
):
    """Obtener juegos populares"""
    try:
        results = await SearchService.get_popular_games(
            db=db,
            skip=skip,
            limit=limit,
        )
        # Format results to match the response model
        formatted_results = []
        for game in results:
            formatted_results.append({
                "id": game["id"],
                "title": game["title"],
                "price": game["price"],
                "average_rating": game["average_rating"],
                "description": game["description"]
            })
        return formatted_results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/games/recent/", response_model=List[PopularGameResponse])
async def get_recent_games(
    skip: int = 0,
    limit: int = 10,
    db: AsyncSession = Depends(get_db),
):
    """Obtener juegos recientemente lanzados"""
    try:
        results = await SearchService.get_recent_games(
            db=db,
            skip=skip,
            limit=limit,
        )
        # Format results to match the response model
        formatted_results = []
        for game in results:
            formatted_results.append({
                "id": game["id"],
                "title": game["title"],
                "price": game["price"],
                "average_rating": game["average_rating"],
                "description": game["description"]
            })
        return formatted_results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# ==================== NUEVAS FUNCIONALIDADES DE BÚSQUEDA ====================

@app.post("/games/search/advanced", response_model=GameSearchResponse)
async def advanced_search_games(
    search_params: GameSearchRequest,
    db: AsyncSession = Depends(get_db),
):
    """Buscar juegos con múltiples criterios de filtrado."""
    try:
        # Use the search service with more advanced parameters
        results = await SearchService.search_games(
            db=db,
            search_term=search_params.query or "",
            min_price=float(search_params.min_price) if search_params.min_price else None,
            max_price=float(search_params.max_price) if search_params.max_price else None,
            skip=search_params.skip,
            limit=search_params.limit,
        )

        # Apply additional filtering if genre is specified
        if search_params.genre:
            results = [
                game for game in results
                if search_params.genre.lower() in (game.get('genres', []) or [])
            ]

        return GameSearchResponse(
            games=[
                GameOut(
                    id=game["id"],
                    title=game["title"],
                    price=Decimal(str(game["price"])),
                    base_price=Decimal(str(game.get("base_price", game["price"]))),
                    is_published=True,
                    average_rating=Decimal(str(game["average_rating"])),
                    description=game["description"],
                    short_description=game.get("short_description", ""),
                    genres=game.get("genres", []),
                    age_rating=game.get("age_rating", ""),
                    system_requirements=game.get("system_requirements", {}),
                    game_metadata=game.get("game_metadata", {})
                ) for game in results
            ],
            total_count=len(results),
            page=search_params.skip // search_params.limit + 1,
            limit=search_params.limit,
            has_more=(len(results) * search_params.limit > len(results))
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/games/filter", response_model=FilteredGamesResponse)
async def filter_games(
    filters: GameFilterRequest,
    current_user: Dict = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db),
):
    """Filtrar juegos con múltiples criterios protegida."""
    try:
        query = db.query(GameDB).filter(GameDB.is_published == True)

        # Apply filters
        if filters.platform:
            query = query.filter(GameDB.system_requirements.has(filters.platform))

        if filters.min_price is not None:
            query = query.filter(GameDB.price >= filters.min_price)

        if filters.max_price is not None:
            query = query.filter(GameDB.price <= filters.max_price)

        if filters.min_rating is not None:
            query = query.filter(GameDB.average_rating >= filters.min_rating)

        if filters.on_sale:
            query = query.filter(GameDB.price < GameDB.base_price)

        # Apply tag-based filtering (this would need to be implemented in actual service)
        if filters.tags:
            # Complex tag filtering would require more complex queries
            # This is a simplified example
            pass

        # Apply sorting
        if filters.sort_by == "price":
            if filters.sort_order == "asc":
                query = query.order_by(GameDB.price.asc())
            else:
                query = query.order_by(GameDB.price.desc())
        elif filters.sort_by == "rating":
            if filters.sort_order == "asc":
                query = query.order_by(GameDB.average_rating.asc())
            else:
                query = query.order_by(GameDB.average_rating.desc())
        elif filters.sort_by == "newest":
                query = query.order_by(GameDB.release_date.desc())
        # Default: popularity (based on ratings and review count)
        else:
            query = query.order_by(GameDB.average_rating.desc(), GameDB.review_count.desc())

        # Apply pagination
        games = query.offset(filters.skip).limit(filters.limit).all()

        game_out_list = [
            GameOut(
                id=str(game.id),
                title=game.title,
                price=Decimal(str(game.price)),
                base_price=Decimal(str(game.base_price)) if game.base_price else Decimal(str(game.price)),
                is_published=game.is_published,
                average_rating=Decimal(str(game.average_rating)),
                description=game.description or "",
                short_description=game.short_description or "",
                genres=game.game_metadata.get("genre") if game.game_metadata else [],
                age_rating=game.age_rating or "",
                release_date=game.release_date.isoformat() if game.release_date else None,
                system_requirements=game.system_requirements or {},
                game_metadata=game.game_metadata or {}
            ) for game in games
        ]

        return FilteredGamesResponse(
            games=game_out_list,
            total_count=len(games),
            filters_applied=filters.dict(exclude_unset=True)
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# ==================== FUNCIONALIDADES DE CARRITO PROTEGIDAS ====================

@app.get("/cart", response_model=CartResponse)
async def get_cart_protected(current_user: Dict = Depends(get_current_active_user)):
    """Obtener el carrito actual del usuario autenticado."""
    items_with_details = []
    for item in cart.articulos:
        items_with_details.append(CartItem(
            game_id=item["game_id"],
            title=item["articulo"],
            price=Decimal(str(item["precio"])),
            quantity=1,  # Assuming quantity 1 for in-memory cart
            thumbnail=None
        ))

    cart_obj = Cart(
        items=items_with_details,
        total=Decimal(str(await cart.calcular_total())),
        item_count=len(items_with_details)
    )

    return CartResponse(cart=cart_obj)


@app.post("/cart/add/{game_id}", response_model=CartItemResponse)
async def add_to_cart_protected(
    game_id: str,
    request: AddToCartRequest,
    current_user: Dict = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    """Añadir un juego al carrito protegido."""
    try:
        game_uuid = uuid.UUID(game_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="ID de juego inválido")


    from sqlalchemy import select
    stmt = select(GameDB).where(GameDB.id == game_uuid, GameDB.is_published == True)
    result = await db.execute(stmt)
    game = result.scalar_one_or_none()


    if not game:
        raise HTTPException(
            status_code=404, detail="Juego no encontrado o no publicado"
        )

    if await cart.agregar_articulo(str(game.id), game.title, float(game.price)):
        return CartItemResponse(
            item=CartItem(
                game_id=str(game.id),
                title=game.title,
                price=Decimal(str(game.price)),
                quantity=request.quantity
            ),
            message="Juego agregado al carrito"
        )
    else:
        raise HTTPException(
            status_code=400, detail="El juego ya está en el carrito"
        )


@app.put("/cart/update/{game_id}", response_model=CartItemResponse)
async def update_cart_item(
    game_id: str,
    update_request: UpdateCartItemRequest,
    current_user: Dict = Depends(get_current_active_user)
):
    """Actualizar la cantidad de un artículo en el carrito."""
    # In the in-memory cart, we can't update quantities directly
    # This is a simplified implementation
    # In a real app, we would store cart items with quantities
    for item in cart.articulos:
        if item["game_id"] == game_id:
            return CartItemResponse(
                item=CartItem(
                    game_id=game_id,
                    title=item["articulo"],
                    price=Decimal(str(item["precio"])),
                    quantity=update_request.quantity
                ),
                message="Cantidad actualizada"
            )

    raise HTTPException(
        status_code=404, detail="Artículo no encontrado en el carrito"
    )


@app.delete("/cart/remove/{game_id}")
async def remove_from_cart_protected(
    game_id: str,
    current_user: Dict = Depends(get_current_active_user)
):
    """Eliminar un artículo del carrito protegido."""
    if not await cart.remover_articulo(game_id):
        raise HTTPException(
            status_code=404, detail="Juego no encontrado en el carrito"
        )
    return {"message": "Juego eliminado del carrito"}


@app.post("/checkout", response_model=CheckoutResponse)
async def process_checkout(
    checkout_request: CheckoutRequest,
    current_user: Dict = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    """Procesar el pago y crear una orden."""
    # Validate cart is not empty
    if len(cart.articulos) == 0:
        raise HTTPException(
            status_code=400, detail="No se puede procesar un carrito vacío"
        )

    # Calculate total
    total_amount = sum(float(item["precio"]) for item in cart.articulos)

    # In real implementation, we would:
    # 1. Create an order in the database
    # 2. Process payment through a payment gateway
    # 3. Send confirmation email

    # For now, simulate the process
    order_id = str(uuid.uuid4())

    # Clear the cart after successful checkout
    await cart.limpiar_carrito()

    return CheckoutResponse(
        order_id=order_id,
        status="completed",
        total_amount=Decimal(str(total_amount)),
        message="Compra procesada exitosamente"
    )


# ==================== HISTORIAL DE ORDENES ====================

@app.get("/orders/history", response_model=OrderHistoryResponse)
async def get_order_history(
    current_user: Dict = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    """Obtener el historial de órdenes del usuario actual."""
    # In a real implementation, would query the orders table
    # For now, return mock data
    orders = [
        {
            "id": "order-1",
            "total_amount": "29.99",
            "status": "completed",
            "created_at": "2023-12-01T10:00:00Z",
            "items": [
                {
                    "game_id": "game-1",
                    "title": "Test Game 1",
                    "price": "29.99",
                    "quantity": 1
                }
            ]
        }
    ]

    return OrderHistoryResponse(
        orders=orders,
        total_orders=len(orders)
    )


# ==================== RECOMENDACIONES ====================

@app.get("/recommendations", response_model=RecommendationResponse)
async def get_recommendations(
    current_user: Dict = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    """Obtener recomendaciones personalizadas para el usuario."""
    # In a real implementation, would use recommendation algorithms based on
    # user history, preferences, etc.
    # For now, return popular games as recommendations
    popular_games = (
        db.query(GameDB)
        .filter(GameDB.is_published == True)
        .order_by(GameDB.average_rating.desc(), GameDB.download_count.desc())
        .limit(5)
        .all()
    )

    recommendations = [
        GameOut(
            id=str(game.id),
            title=game.title,
            price=Decimal(str(game.price)),
            base_price=Decimal(str(game.base_price)) if game.base_price else Decimal(str(game.price)),
            is_published=game.is_published,
            average_rating=Decimal(str(game.average_rating)),
            description=game.description or "",
            short_description=game.short_description or "",
            genres=game.game_metadata.get("genre") if game.game_metadata else [],
            age_rating=game.age_rating or "",
            release_date=game.release_date.isoformat() if game.release_date else None,
            system_requirements=game.system_requirements or {},
            game_metadata=game.game_metadata or {}
        ) for game in popular_games
    ]

    return RecommendationResponse(
        games=recommendations,
        reason="Popular games based on user interests"
    )


@app.get("/games/{game_id}/related", response_model=List[GameOut])
async def get_related_games(
    game_id: str,
    current_user: Dict = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    """Obtener juegos relacionados a un juego específico."""
    try:
        # Parse the game_id to ensure it's a valid UUID
        game_uuid = uuid.UUID(game_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="ID de juego inválido")

    # Get the specified game
    target_game = db.query(GameDB).filter(
        GameDB.id == game_uuid,
        GameDB.is_published == True
    ).first()

    if not target_game:
        raise HTTPException(status_code=404, detail="Juego no encontrado")

    # Get games with similar genres or tags (simplified)
    # In a real implementation, this would use more sophisticated matching
    related_games = db.query(GameDB).filter(
        GameDB.id != target_game.id,
        GameDB.is_published == True
    )

    # Apply simple filter based on genre if available
    if target_game.game_metadata and "genre" in target_game.game_metadata:
        genres = target_game.game_metadata["genre"]
        if genres:
            genre_conditions = []
            for genre in genres:
                genre_conditions.append(GameDB.game_metadata.contains({"genre": [genre]}))

            if genre_conditions:
                related_games = related_games.filter(
                    or_(*genre_conditions)
                )

    # Limit results
    related_games = related_games.limit(5).all()

    return [
        GameOut(
            id=str(game.id),
            title=game.title,
            price=Decimal(str(game.price)),
            base_price=Decimal(str(game.base_price)) if game.base_price else Decimal(str(game.price)),
            is_published=game.is_published,
            average_rating=Decimal(str(game.average_rating)),
            description=game.description or "",
            short_description=game.short_description or "",
            genres=game.game_metadata.get("genre") if game.game_metadata else [],
            age_rating=game.age_rating or "",
            release_date=game.release_date.isoformat() if game.release_date else None,
            system_requirements=game.system_requirements or {},
            game_metadata=game.game_metadata or {}
        ) for game in related_games
    ]
