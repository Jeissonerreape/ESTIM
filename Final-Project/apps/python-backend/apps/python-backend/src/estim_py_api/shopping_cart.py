# This file is maintained for backward compatibility
# The actual shopping cart implementation is located in src/estim_py_api/services/shopping_service.py

from .services.shopping_service import Cart, cart

__all__ = ["Cart", "cart"]