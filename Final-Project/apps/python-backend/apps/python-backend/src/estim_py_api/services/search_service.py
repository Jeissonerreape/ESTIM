from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import or_, select, func
from typing import List, Dict, Optional
from estim_py_api.db.database import GameDB
import logging

logger = logging.getLogger(__name__)


class SearchService:
    @staticmethod
    async def search_by_genre(
        db: AsyncSession,
        genre: str,
        skip: int = 0,
        limit: int = 20
    ) -> List[Dict]:
        try:
            if not genre or not isinstance(genre, str):
                return []
            clean_genre = genre.strip().lower()
            if not clean_genre:
                return []

            stmt = select(GameDB).where(GameDB.is_published == True)
            result = await db.execute(stmt)
            all_games = result.scalars().all()

            filtered_games = []
            for game in all_games:
                try:
                    if not game.game_metadata:
                        continue
                    game_genres = game.game_metadata.get('genre', [])
                    if isinstance(game_genres, str):
                        game_genres = [game_genres]
                    genre_found = False
                    for g in game_genres:
                        if g and isinstance(g, str) and clean_genre in g.lower():
                            genre_found = True
                            break
                    if genre_found:
                        filtered_games.append(game)
                except Exception:
                    continue

            safe_skip = max(0, skip)
            safe_limit = max(1, min(limit, 100))
            start_idx = min(safe_skip, len(filtered_games))
            end_idx = min(safe_skip + safe_limit, len(filtered_games))
            paginated_games = filtered_games[start_idx:end_idx]

            results = []
            for game in paginated_games:
                try:
                    game_data = {
                        "id": str(game.id),
                        "title": game.title,
                        "price": float(game.price) if game.price else 0.0,
                        "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                        "description": game.short_description or "",
                        "genres": game.game_metadata.get("genre", []) if game.game_metadata else [],
                        "game_metadata": game.game_metadata
                    }
                    results.append(game_data)
                except Exception:
                    continue
            return results
        except Exception as e:
            logger.exception("Error en search_by_genre")
            return []

    @staticmethod
    async def search_games(
        db: AsyncSession,
        search_term: str = "",
        min_price: Optional[float] = None,
        max_price: Optional[float] = None,
        skip: int = 0,
        limit: int = 20
    ) -> List[Dict]:
        try:
            stmt = select(GameDB).where(GameDB.is_published == True)

            if search_term and search_term.strip():
                term = f"%{search_term.strip()}%"
                stmt = stmt.where(
                    or_(
                        GameDB.title.ilike(term),
                        GameDB.description.ilike(term),
                        GameDB.short_description.ilike(term)
                    )
                )
            if min_price is not None:
                try:
                    min_price_float = float(min_price)
                    stmt = stmt.where(GameDB.price >= min_price_float)
                except (ValueError, TypeError):
                    pass
            if max_price is not None:
                try:
                    max_price_float = float(max_price)
                    stmt = stmt.where(GameDB.price <= max_price_float)
                except (ValueError, TypeError):
                    pass

            safe_skip = max(0, skip)
            safe_limit = max(1, min(limit, 100))
            stmt = stmt.order_by(GameDB.average_rating.desc(), GameDB.title.asc())
            stmt = stmt.offset(safe_skip).limit(safe_limit)

            result = await db.execute(stmt)
            games = result.scalars().all()

            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                    "genres": game.game_metadata.get("genre", []) if game.game_metadata else [],
                    "game_metadata": game.game_metadata
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en search_games")
            return []

    @staticmethod
    async def get_popular_games(
        db: AsyncSession,
        skip: int = 0,
        limit: int = 10
    ) -> List[Dict]:
        try:
            stmt = select(GameDB).where(GameDB.is_published == True)
            stmt = stmt.order_by(
                GameDB.average_rating.desc(),
                GameDB.download_count.desc()
            ).offset(skip).limit(limit)

            result = await db.execute(stmt)
            games = result.scalars().all()

            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en get_popular_games")
            return []

    @staticmethod
    async def get_recent_games(
        db: AsyncSession,
        skip: int = 0,
        limit: int = 10
    ) -> List[Dict]:
        try:
            stmt = select(GameDB).where(
                GameDB.is_published == True,
                GameDB.release_date.isnot(None)
            )
            stmt = stmt.order_by(GameDB.release_date.desc()).offset(skip).limit(limit)

            result = await db.execute(stmt)
            games = result.scalars().all()

            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en get_recent_games")
            return []

    @staticmethod
    async def get_featured_games(
        db: AsyncSession,
        skip: int = 0,
        limit: int = 10
    ) -> List[Dict]:
        """Get featured games based on high ratings and recent uploads."""
        try:
            stmt = select(GameDB).where(
                GameDB.is_published == True,
                GameDB.average_rating >= 4.0  # Highly rated games
            )
            stmt = stmt.order_by(
                GameDB.average_rating.desc(),
                GameDB.download_count.desc()
            ).offset(skip).limit(limit)

            result = await db.execute(stmt)
            games = result.scalars().all()

            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en get_featured_games")
            return []

    @staticmethod
    async def get_new_games(
        db: AsyncSession,
        skip: int = 0,
        limit: int = 10
    ) -> List[Dict]:
        """Get newest games based on creation date."""
        try:
            stmt = select(GameDB).where(
                GameDB.is_published == True
            )
            stmt = stmt.order_by(
                GameDB.created_at.desc()
            ).offset(skip).limit(limit)

            result = await db.execute(stmt)
            games = result.scalars().all()

            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en get_new_games")
            return []

    @staticmethod
    async def filter_games(
        db: AsyncSession,
        platform: Optional[str] = None,
        genre: Optional[str] = None,
        min_price: Optional[float] = None,
        max_price: Optional[float] = None,
        min_rating: Optional[float] = None,
        on_sale: Optional[bool] = None,
        tags: Optional[List[str]] = None,
        skip: int = 0,
        limit: int = 20
    ) -> List[Dict]:
        """Filter games by multiple criteria."""
        try:
            stmt = select(GameDB).where(GameDB.is_published == True)

            # Platform filter (using system_requirements)
            if platform:
                stmt = stmt.where(
                    func.jsonb_exists(GameDB.system_requirements, platform)
                )

            # Genre filter (using game_metadata)
            if genre:
                stmt = stmt.where(
                    func.jsonb_contains(GameDB.game_metadata, {"genre": [genre]})
                )

            # Price filters
            if min_price is not None:
                try:
                    min_price_float = float(min_price)
                    stmt = stmt.where(GameDB.price >= min_price_float)
                except (ValueError, TypeError):
                    pass
            if max_price is not None:
                try:
                    max_price_float = float(max_price)
                    stmt = stmt.where(GameDB.price <= max_price_float)
                except (ValueError, TypeError):
                    pass

            # Rating filter
            if min_rating is not None:
                try:
                    min_rating_float = float(min_rating)
                    stmt = stmt.where(GameDB.average_rating >= min_rating_float)
                except (ValueError, TypeError):
                    pass

            # On sale filter (price < base_price)
            if on_sale:
                stmt = stmt.where(GameDB.price < GameDB.base_price)

            # Tags filter
            if tags:
                for tag in tags:
                    stmt = stmt.where(
                        func.jsonb_contains(GameDB.game_metadata, {"tags": [tag]})
                    )

            stmt = stmt.order_by(GameDB.average_rating.desc())
            stmt = stmt.offset(skip).limit(limit)

            result = await db.execute(stmt)
            games = result.scalars().all()

            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                    "genres": game.game_metadata.get("genre", []) if game.game_metadata else [],
                    "game_metadata": game.game_metadata
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en filter_games")
            return []

    @staticmethod
    async def get_related_games(
        db: AsyncSession,
        game_id: str,
        limit: int = 5
    ) -> List[Dict]:
        """Get games related to a specific game based on genre similarity."""
        try:
            # First get the target game to identify its genre(s)
            target_stmt = select(GameDB).where(GameDB.id == game_id, GameDB.is_published == True)
            target_result = await db.execute(target_stmt)
            target_game = target_result.scalar_one_or_none()

            if not target_game or not target_game.game_metadata:
                return []

            target_genres = target_game.game_metadata.get("genre", [])
            if not target_genres:
                return []

            # Find other games with similar genres
            stmt = select(GameDB).where(
                GameDB.is_published == True,
                GameDB.id != game_id  # Exclude target game
            )

            # Add condition for games with matching genres
            conditions = []
            for genre in target_genres:
                conditions.append(func.jsonb_contains(GameDB.game_metadata, {"genre": [genre]}))

            if conditions:
                from sqlalchemy import or_
                stmt = stmt.where(or_(*conditions))

            stmt = stmt.order_by(GameDB.average_rating.desc()).limit(limit)

            result = await db.execute(stmt)
            related_games = result.scalars().all()

            results = []
            for game in related_games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                    "genres": game.game_metadata.get("genre", []) if game.game_metadata else [],
                }
                results.append(game_data)
            return results
        except Exception:
            logger.exception("Error en get_related_games")
            return []
