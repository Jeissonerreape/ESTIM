from fastapi import FastAPI, Depends, HTTPException
from typing import List

from estim_py_api.services import search_service, shopping_service
from estim_py_api.schemas import game_schemas, cart_schemas
from estim_py_api.db.database import get_db

app = FastAPI(title="Estim API (reorganized)")


@app.get("/", tags=["health"])
def root():
    return {"status": "ok", "service": "estim-py-api"}


@app.get("/health", tags=["health"])
def health():
    return {"status": "healthy"}


@app.get("/games/", response_model=List[game_schemas.GameOut])
def get_games(limit: int = 10, db=Depends(get_db)):
    return search_service.get_popular_games(db, limit=limit)
