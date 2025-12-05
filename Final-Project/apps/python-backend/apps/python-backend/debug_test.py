import sys
import os
from pathlib import Path
SRC_PATH = Path(__file__).parent / "src"
sys.path.insert(0, str(SRC_PATH))

try:
    from sqlalchemy.ext.asyncio import create_async_engine
    print("SQLAlchemy Async Engine imported")
    import aiosqlite
    print("aiosqlite imported")
    from src.estim_py_api.app import app
    print("App imported")
except Exception as e:
    print(f"Error: {e}")
    import traceback
    traceback.print_exc()
