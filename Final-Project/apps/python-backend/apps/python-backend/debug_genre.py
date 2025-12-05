#!/usr/bin/env python3
"""
Debug script para probar la búsqueda por género
"""

import sys
import os

# Agregar el directorio actual al path
sys.path.append(os.path.dirname(__file__))

from src.estim_py_api.db.database import SessionLocal, GameDB

def debug_genre_search():
    """Debug independiente de la búsqueda por género"""
    print("🎯 INICIANDO DEBUG DE BÚSQUEDA POR GÉNERO")
    print("=" * 50)
    
    db = SessionLocal()
    
    try:
        # 1. Contar juegos en la base de datos
        total_games = db.query(GameDB).count()
        published_games = db.query(GameDB).filter(GameDB.is_published == True).count()
        games_with_metadata = db.query(GameDB).filter(GameDB.game_metadata.isnot(None)).count()
        
        print(f"📊 ESTADÍSTICAS DE LA BASE DE DATOS:")
        print(f"   Total de juegos: {total_games}")
        print(f"   Juegos publicados: {published_games}")
        print(f"   Juegos con metadata: {games_with_metadata}")
        print()
        
        # 2. Obtener todos los juegos publicados
        all_games = db.query(GameDB).filter(GameDB.is_published == True).all()
        
        print(f"🎮 TODOS LOS JUEGOS PUBLICADOS ({len(all_games)}):")
        for i, game in enumerate(all_games, 1):
            print(f"   {i}. {game.title}")
            print(f"      Metadata: {game.game_metadata}")
            if game.game_metadata:
                genres = game.game_metadata.get('genre', [])
                print(f"      Géneros: {genres}")
            print()
        
        # 3. Buscar manualmente juegos con género RPG
        print("🔍 BUSCANDO JUEGOS CON GÉNERO 'RPG':")
        rpg_games = []
        
        for game in all_games:
            if game.game_metadata and 'genre' in game.game_metadata:
                genres = game.game_metadata['genre']
                
                # Convertir a lista si es string
                if isinstance(genres, str):
                    genres = [genres]
                
                # Buscar RPG en los géneros
                if genres and any('rpg' in str(g).lower() for g in genres if g):
                    rpg_games.append(game)
                    print(f"   ✅ {game.title} -> {genres}")
        
        print(f"🎯 TOTAL DE JUEGOS RPG ENCONTRADOS: {len(rpg_games)}")
        
        # 4. Mostrar resultados formateados
        print("\n📋 RESULTADOS FORMATEADOS:")
        for game in rpg_games:
            result = {
                "id": str(game.id),
                "title": game.title,
                "price": float(game.price) if game.price else 0.0,
                "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                "description": game.short_description or "",
                "genres": game.game_metadata.get('genre', []) if game.game_metadata else []
            }
            print(f"   {result}")
            
    except Exception as e:
        print(f"💥 ERROR: {e}")
        import traceback
        traceback.print_exc()
    finally:
        db.close()

if __name__ == "__main__":
    debug_genre_search()