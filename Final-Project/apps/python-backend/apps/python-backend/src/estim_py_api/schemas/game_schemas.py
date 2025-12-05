from pydantic import BaseModel, Field, validator
from typing import List, Optional, Dict, Any
from datetime import datetime
from decimal import Decimal


class GameSearchRequest(BaseModel):
    query: Optional[str] = Field(None, description="Texto de búsqueda general")
    genre: Optional[str] = Field(None, description="Género específico")
    min_price: Optional[Decimal] = Field(None, ge=0, description="Precio mínimo")
    max_price: Optional[Decimal] = Field(None, ge=0, description="Precio máximo")
    platform: Optional[str] = Field(None, description="Plataforma específica")
    min_rating: Optional[Decimal] = Field(None, ge=0, le=5, description="Rating mínimo")
    on_sale: Optional[bool] = Field(None, description="Solo juegos en oferta")
    tags: Optional[List[str]] = Field([], description="Tags para filtrar")
    skip: int = Field(0, ge=0)
    limit: int = Field(20, le=100)


class GameOut(BaseModel):
    id: str
    title: str
    price: Decimal
    base_price: Optional[Decimal] = None
    is_published: bool
    average_rating: Decimal
    description: Optional[str] = ""
    short_description: Optional[str] = ""
    genres: Optional[List[str]] = []
    age_rating: Optional[str] = ""
    release_date: Optional[str] = None
    system_requirements: Optional[Dict[str, Any]] = {}
    game_metadata: Optional[Dict[str, Any]] = {}
    
    class Config:
        from_attributes = True  # Replaces orm_mode for Pydantic v2


class CartItem(BaseModel):
    game_id: str
    title: str
    price: Decimal
    quantity: int = 1
    thumbnail: Optional[str] = None


class Cart(BaseModel):
    items: List[CartItem] = []
    total: Decimal = 0.0
    item_count: int = 0


class AddToCartRequest(BaseModel):
    game_id: str
    quantity: int = Field(1, ge=1, le=10)


class UpdateCartItemRequest(BaseModel):
    quantity: int = Field(ge=1, le=10)


class CheckoutRequest(BaseModel):
    payment_method: str
    shipping_address: Dict[str, Any]
    email: str = Field(..., pattern=r'^[\w\.-]+@[\w\.-]+\.\w+$')


class Order(BaseModel):
    id: str
    user_id: str
    total_amount: Decimal
    status: str
    items: List[CartItem]
    created_at: datetime


class User(BaseModel):
    id: str
    username: str
    email: str
    is_active: bool = True
    created_at: datetime


class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: Optional[str] = None


class GameFilterRequest(BaseModel):
    platform: Optional[str] = None
    genre: Optional[str] = None
    min_price: Optional[Decimal] = None
    max_price: Optional[Decimal] = None
    min_rating: Optional[Decimal] = None
    on_sale: Optional[bool] = False
    tags: Optional[List[str]] = []
    sort_by: Optional[str] = "popularity"  # "popularity", "price", "rating", "newest"
    sort_order: Optional[str] = "desc"  # "asc", "desc"
    skip: int = 0
    limit: int = 20