import pytest
from unittest.mock import Mock, patch
from sqlalchemy.orm import Session

from src.estim_py_api.services.search_service import SearchService
from tests.data_fixtures.game_sample_data import get_sample_games


def test_search_games_basic():
    """Test basic game search functionality"""
    # Create a mock database session
    mock_db = Mock(spec=Session)

    # Mock the database query
    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_filtered_query = Mock()
        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_filtered_query
        mock_filtered_query.order_by.return_value = mock_filtered_query
        mock_filtered_query.offset.return_value = mock_filtered_query
        mock_filtered_query.limit.return_value = mock_filtered_query
        mock_filtered_query.all.return_value = get_sample_games()

        # Run search
        results = SearchService.search_games(mock_db, search_term="test")

        # Verify the results
        assert isinstance(results, list)
        assert len(results) > 0  # Should contain sample data
        print("🔍 Búsqueda básica de juegos: Test completado exitosamente")


def test_search_games_empty_query():
    """Test search with empty query"""
    mock_db = Mock(spec=Session)

    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_filtered_query = Mock()
        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_filtered_query
        mock_filtered_query.order_by.return_value = mock_filtered_query
        mock_filtered_query.offset.return_value = mock_filtered_query
        mock_filtered_query.limit.return_value = mock_filtered_query
        mock_filtered_query.all.return_value = get_sample_games()

        # Run search with empty query
        results = SearchService.search_games(mock_db, search_term="")

        assert isinstance(results, list)
        print("🔍 Búsqueda con consulta vacía: Test completado exitosamente")


def test_search_by_genre():
    """Test searching by genre functionality"""
    mock_db = Mock(spec=Session)

    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_query
        mock_query.all.return_value = get_sample_games()

        # Run search by genre
        results = SearchService.search_by_genre(mock_db, genre="RPG")

        # Verify the results
        assert isinstance(results, list)
        print("🎭 Búsqueda por género: Test completado exitosamente")


def test_search_by_genre_empty():
    """Test searching by empty genre"""
    mock_db = Mock(spec=Session)

    # Run search with empty genre
    results = SearchService.search_by_genre(mock_db, genre="")

    # Should return empty list
    assert isinstance(results, list)
    assert len(results) == 0
    print("🎭 Búsqueda por género vacío: Test completado exitosamente")


def test_get_popular_games():
    """Test getting popular games"""
    mock_db = Mock(spec=Session)

    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_ordered_query = Mock()
        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_query
        mock_query.order_by.return_value = mock_ordered_query
        mock_ordered_query.offset.return_value = mock_ordered_query
        mock_ordered_query.limit.return_value = mock_ordered_query
        mock_ordered_query.all.return_value = get_sample_games()

        # Run get popular games
        results = SearchService.get_popular_games(mock_db)

        # Verify the results
        assert isinstance(results, list)
        assert len(results) > 0
        print("⭐ Juegos populares: Test completado exitosamente")


def test_get_recent_games():
    """Test getting recent games"""
    mock_db = Mock(spec=Session)

    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_filtered_query = Mock()
        mock_ordered_query = Mock()
        mock_notnull_query = Mock()

        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_filtered_query
        mock_filtered_query.filter.return_value = mock_notnull_query
        mock_notnull_query.order_by.return_value = mock_ordered_query
        mock_ordered_query.offset.return_value = mock_ordered_query
        mock_ordered_query.limit.return_value = mock_ordered_query
        mock_ordered_query.all.return_value = get_sample_games()

        # Run get recent games
        results = SearchService.get_recent_games(mock_db)

        # Verify the results
        assert isinstance(results, list)
        assert len(results) > 0
        print("🕒 Juegos recientes: Test completado exitosamente")


def test_search_games_with_price_range():
    """Test searching games with price range filters"""
    mock_db = Mock(spec=Session)

    with patch('src.estim_py_api.services.search_service.GameDB') as mock_game_db:
        mock_query = Mock()
        mock_filtered_query = Mock()
        mock_db.query.return_value = mock_query
        mock_query.filter.return_value = mock_filtered_query
        mock_filtered_query.filter.return_value = mock_filtered_query
        mock_filtered_query.order_by.return_value = mock_filtered_query
        mock_filtered_query.offset.return_value = mock_filtered_query
        mock_filtered_query.limit.return_value = mock_filtered_query
        mock_filtered_query.all.return_value = get_sample_games()

        # Run search with price range
        results = SearchService.search_games(
            mock_db,
            search_term="test",
            min_price=10.0,
            max_price=50.0
        )

        assert isinstance(results, list)
        print("💰 Búsqueda con rango de precios: Test completado exitosamente")


if __name__ == "__main__":
    pytest.main([__file__])