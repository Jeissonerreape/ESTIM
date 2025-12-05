import os
import sys

# Ensure `src` is on sys.path so imports from `estim_py_api` package work
ROOT = os.path.dirname(__file__)
SRC = os.path.join(ROOT, "src")
if SRC not in sys.path:
    sys.path.insert(0, SRC)

from estim_py_api.app import app
