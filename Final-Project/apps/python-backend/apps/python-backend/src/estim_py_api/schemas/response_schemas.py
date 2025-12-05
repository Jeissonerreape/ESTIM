from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from decimal import Decimal
from .game_schemas import GameOut, Cart, CartItem


class GameSearchResponse(BaseModel):
    games: List[GameOut]
    total_count: int
    page: int
    limit: int
    has_more: bool


class CartResponse(BaseModel):
    cart: Cart


class CartItemResponse(BaseModel):
    item: CartItem
    message: str


class CheckoutResponse(BaseModel):
    order_id: str
    status: str
    total_amount: Decimal
    message: str


class OrderHistoryResponse(BaseModel):
    orders: List[Dict[str, Any]]
    total_orders: int


class RecommendationResponse(BaseModel):
    games: List[GameOut]
    reason: str


class FilteredGamesResponse(BaseModel):
    games: List[GameOut]
    total_count: int
    filters_applied: Dict[str, Any]