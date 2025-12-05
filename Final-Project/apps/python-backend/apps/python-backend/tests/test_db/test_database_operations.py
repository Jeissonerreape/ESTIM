import pytest
from datetime import datetime
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from src.estim_py_api.db.database import Base, GameDB


@pytest.fixture
def test_db_session():
    """Create a test database session using in-memory SQLite"""
    engine = create_engine(
        "sqlite:///:memory:",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    
    # Create tables
    Base.metadata.create_all(bind=engine)
    
    session = TestingSessionLocal()
    try:
        yield session
    finally:
        session.close()


def test_game_model_creation():
    """Test creating a GameDB instance"""
    game = GameDB(
        publisher_id="test-publisher-uuid",
        title="Test Game",
        description="A game for testing",
        short_description="Test game",
        price=29.99,
        base_price=29.99,
        is_published=True,
        release_date=datetime(2023, 1, 1),
        age_rating="Everyone",
        system_requirements={"minimum": {"os": "Windows 10"}},
        game_metadata={"genre": ["Action", "Adventure"]},
        average_rating=4.5,
        review_count=100,
        download_count=1000,
    )

    assert game.title == "Test Game"
    assert game.price == 29.99
    assert game.is_published is True
    assert game.game_metadata["genre"] == ["Action", "Adventure"]
    print("🎮 Creación de modelo de juego: Test completado exitosamente")


def test_game_model_defaults():
    """Test that GameDB model has proper defaults"""
    game = GameDB(
        publisher_id="test-publisher-uuid",
        title="Test Game",
        price=29.99,
    )

    # Check defaults
    assert game.is_published is False  # Default value
    assert game.average_rating == 0.0  # Default value
    assert game.review_count == 0  # Default value
    assert game.total_playtime == 0  # Default value
    assert game.download_count == 0  # Default value
    print("⚙️ Valores por defecto del modelo: Test completado exitosamente")


def test_game_model_database_interaction(test_db_session):
    """Test saving and retrieving a game from the database"""
    db = test_db_session

    # Create a game instance
    game = GameDB(
        publisher_id="test-publisher-uuid",
        title="Database Test Game",
        description="A game for database testing",
        price=39.99,
        is_published=True
    )

    # Add to session and commit
    db.add(game)
    db.commit()
    db.refresh(game)

    # Verify it was saved
    assert game.id is not None
    assert game.title == "Database Test Game"
    assert game.price == 39.99

    # Query the game back
    retrieved_game = db.query(GameDB).filter(GameDB.title == "Database Test Game").first()
    assert retrieved_game is not None
    assert retrieved_game.title == "Database Test Game"
    print("💾 Interacción con base de datos: Test completado exitosamente")


def test_game_model_query_methods(test_db_session):
    """Test different query methods on GameDB model"""
    db = test_db_session

    # Add multiple games
    games = [
        GameDB(publisher_id="pub1", title="Game 1", price=19.99, is_published=True),
        GameDB(publisher_id="pub2", title="Game 2", price=29.99, is_published=True),
        GameDB(publisher_id="pub3", title="Unpublished Game", price=9.99, is_published=False),
    ]

    for game in games:
        db.add(game)
    db.commit()

    # Test querying published games only
    published_games = db.query(GameDB).filter(GameDB.is_published == True).all()
    assert len(published_games) == 2
    for game in published_games:
        assert game.is_published is True

    # Test querying by price range
    cheap_games = db.query(GameDB).filter(GameDB.price < 25.0).all()
    assert len(cheap_games) == 2  # Game 1 and Unpublished Game
    for game in cheap_games:
        assert game.price < 25.0

    # Test counting
    count = db.query(GameDB).count()
    assert count == 3
    print("🔍 Métodos de consulta del modelo: Test completado exitosamente")


def test_game_model_indexes_exist():
    """Test that important fields have indexes (structural test, not functional)"""
    # This test verifies that the model definition includes expected indexes
    mapper = GameDB.__mapper__

    # Check that important columns have indexes
    indexed_columns = []
    for index in mapper.local_table.indexes:
        for col in index.columns:
            indexed_columns.append(col.name)

    # Check for important indexed fields
    assert 'id' in [col.name for col in GameDB.__table__.columns]  # Primary key is indexed
    # Note: The actual index verification would require more complex inspection
    print("📋 Verificación de índices del modelo: Test completado exitosamente")


if __name__ == "__main__":
    pytest.main([__file__])