from pydantic import BaseModel
from typing import List, Dict


class CartItem(BaseModel):
    game_id: str
    title: str
    price: float


class CartOut(BaseModel):
    items: List[CartItem]
    total: float

    class Config:
        orm_mode = True
