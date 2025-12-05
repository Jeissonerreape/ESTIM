import uuid
from datetime import datetime
from src.estim_py_api.db.database import GameDB


def get_sample_games():
    """Returns a list of sample GameDB instances for testing"""
    return [
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Cyberpunk 2077: Phantom Liberty",
            description="Una expansión de rol de acción en mundo abierto con una historia de espionaje thriller.",
            short_description="Expansión de acción RPG cyberpunk",
            price=39.99,
            base_price=49.99,
            is_published=True,
            release_date=datetime(2023, 9, 26),
            age_rating="Mature",
            system_requirements={
                "minimum": {
                    "os": "Windows 10",
                    "processor": "Intel Core i7-6700",
                    "memory": "12 GB RAM",
                    "graphics": "NVIDIA GeForce GTX 1060",
                    "storage": "70 GB"
                }
            },
            # METADATA OBLIGATORIA
            game_metadata={
                "genre": ["RPG", "Action", "Cyberpunk", "Open World"],
                "features": ["Open World", "Story Rich", "Character Customization"],
                "tags": ["futuristic", "first-person", "singleplayer"]
            },
            average_rating=4.7,
            review_count=28500,
            download_count=1500000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="The Legend of Zelda: Tears of the Kingdom",
            description="Explora las tierras y los cielos de Hyrule en esta secuela épica de Breath of the Wild.",
            short_description="Aventura de acción y exploración",
            price=59.99,
            base_price=59.99,
            is_published=True,
            release_date=datetime(2023, 5, 12),
            age_rating="Everyone 10+",
            system_requirements={
                "minimum": {
                    "os": "Nintendo Switch",
                    "storage": "18 GB"
                }
            },
            # METADATA OBLIGATORIA
            game_metadata={
                "genre": ["Adventure", "Action", "RPG", "Puzzle"],
                "features": ["Open World", "Puzzle", "Exploration"],
                "tags": ["fantasy", "adventure", "singleplayer"]
            },
            average_rating=4.9,
            review_count=45200,
            download_count=2800000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Baldur's Gate 3",
            description="Un juego de rol basado en Dungeons & Dragons con combate por turnos y narrativa profunda.",
            short_description="RPG de fantasía épica",
            price=59.99,
            base_price=59.99,
            is_published=True,
            release_date=datetime(2023, 8, 3),
            age_rating="Mature",
            system_requirements={
                "minimum": {
                    "os": "Windows 10",
                    "processor": "Intel I5 4690",
                    "memory": "8 GB RAM",
                    "graphics": "NVIDIA GTX 970",
                    "storage": "150 GB"
                }
            },
            # METADATA OBLIGATORIA
            game_metadata={
                "genre": ["RPG", "Fantasy", "Turn-Based", "Adventure"],
                "features": ["Story Rich", "Multiplayer", "Character Customization"],
                "tags": ["fantasy", "turn-based", "coop"]
            },
            average_rating=4.8,
            review_count=36800,
            download_count=5200000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Stardew Valley",
            description="Un juego de simulación de granja donde cultivas, crías animales y construyes relaciones.",
            short_description="Simulador de granja relajante",
            price=14.99,
            base_price=14.99,
            is_published=True,
            release_date=datetime(2016, 2, 26),
            age_rating="Everyone",
            system_requirements={
                "minimum": {
                    "os": "Windows 7",
                    "processor": "2 GHz",
                    "memory": "2 GB RAM",
                    "graphics": "256 mb video memory",
                    "storage": "500 MB"
                }
            },
            # METADATA OBLIGATORIA
            game_metadata={
                "genre": ["Simulation", "RPG", "Indie", "Farming"],
                "features": ["Farming", "Relaxing", "Crafting"],
                "tags": ["farming", "relaxing", "multiplayer"]
            },
            average_rating=4.8,
            review_count=428000,
            download_count=25000000
        )
    ]


def get_sample_game():
    """Returns a single sample GameDB instance for testing"""
    return get_sample_games()[0]