# BACKEND PYTHON — DOCUMENTACIÓN

## 🚀 Endpoints implementados

### 🔍 Endpoints básicos

| Método | Endpoint   | Descripción                  |
| ------ | ---------- | ---------------------------- |
| `GET`  | `/`        | Comprobación básica (health) |
| `GET`  | `/health`  | Estado del servicio          |
| `GET`  | `/test-db` | Verificar conexión a la BD   |

### 🔐 Autenticación y Autorización (Protegidas con JWT)

| Método | Endpoint           | Descripción                          |
| ------ | ------------------ | ------------------------------------ |
| `POST` | `/auth/register`   | Registrar nuevo usuario              |
| `POST` | `/auth/token`      | Iniciar sesión y obtener token JWT   |

### 🛒 Carrito de compras (Protegido con JWT)

| Método    | Endpoint                    | Descripción                               |
| --------- | --------------------------- | ----------------------------------------- |
| `POST`    | `/cart/add/{game_id}`       | Añadir un juego al carrito (protegido)    |
| `PUT`     | `/cart/update/{game_id}`    | Actualizar cantidad (protegido)           |
| `DELETE`  | `/cart/remove/{game_id}`    | Remover un juego del carrito (protegido)  |
| `GET`     | `/cart`                     | Consultar el carrito (protegido)          |
| `GET`     | `/cart/total`               | Obtener total del carrito (protegido)     |
| `DELETE`  | `/cart/clear`               | Vaciar el carrito (protegido)             |
| `POST`    | `/checkout`                 | Procesar compra (protegido)               |

### 🎮 Catálogo de juegos

| Método | Endpoint              | Descripción                         |
| ------ | --------------------- | ----------------------------------- |
| `GET`  | `/games/`             | Listado de juegos disponibles       |
| `GET`  | `/games/popular/`     | Juegos más populares                |
| `GET`  | `/games/recent/`      | Juegos recientes / últimos añadidos |
| `GET`  | `/games/search/`      | Buscar por nombre y rango de precio |
| `POST` | `/games/search/advanced` | Búsqueda avanzada con múltiples filtros |
| `POST` | `/games/filter`       | Filtrado por precio, género, rating, etc. |
| `GET`  | `/games/{game_id}/related` | Juegos relacionados con un juego específico |

### 📋 Perfil del Usuario (Protegido con JWT)

| Método | Endpoint           | Descripción                      |
| ------ | ------------------ | -------------------------------- |
| `GET`  | `/orders/history`  | Historial de órdenes (protegido) |
| `GET`  | `/recommendations` | Recomendaciones personalizadas (protegido) |

### ⚙️ Administración

| Método | Endpoint           | Descripción              |
| ------ | ------------------ | ------------------------ |
| `POST` | `/admin/seed-data` | Insertar datos de prueba |

---

## 🔄 Flujo de datos (resumen)

Cliente → Endpoint FastAPI → Lógica de negocio (servicios) → Acceso a datos (SQLAlchemy) → Respuesta JSON

---

## 📐 Principios aplicados (breve)

- SRP: cada módulo tiene una única responsabilidad (rutas, carrito, BD, seed).
- OCP/LSP/ISP/DIP: diseño pensado para ampliar sin romper; dependencias inyectadas y capas separadas.
- **Seguridad JWT**: Autenticación robusta con tokens JWT para rutas protegidas.
- **Clean Architecture**: Separación clara de capas (presentación, servicios, datos).
- **Pydantic v2**: Validación de datos y modelos de transferencia modernos.

---

## 🏗️ Arquitectura (simplificada)

PRESENTACIÓN (FastAPI endpoints con dependencias de seguridad)
⬇️
SERVICIOS (lógica de negocio con autenticación)
⬇️
ACCESO A DATOS (SQLAlchemy / modelos con seguridad)
⬇️
ALMACENAMIENTO (PostgreSQL segura con autenticación)

---

## 🧪 Pruebas unitarias

Se incluye `run_tests.py` para pruebas básicas del carrito (añadir, eliminar, total). Ejecuta las pruebas con el entorno virtual activo.

---

## 🛠️ Configuración para desarrollo (Windows / PowerShell)

Estos pasos asumen que estás en `apps/python-backend`.

1. Crear y activar el entorno virtual

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install --upgrade pip
```

2. Instalar dependencias

```powershell
pip install -r requirements.txt
```

Si la instalación falla por paquetes nativos (ej. `psycopg2-binary`, `greenlet`), revisa la sección de solución de problemas más abajo.

3. Seleccionar intérprete en VS Code

- Abre la carpeta/espacio de trabajo en VS Code.
- Selecciona el intérprete de Python apuntando a `apps/python-backend/.venv` (el repositorio incluye `.vscode/settings.json` con esa ruta).

4. Ejecutar la aplicación en modo desarrollo

```powershell
uvicorn main:app --reload --port 8000
```

La API quedará disponible en `http://127.0.0.1:8000/`.

5. Ejecutar las pruebas

```powershell
.\.venv\Scripts\Activate.ps1
python run_tests.py
```

---

## 🔧 Solución de problemas comunes en Windows

- Error al compilar `psycopg2-binary` o `greenlet`:
  1. Opción recomendada (más simple en Windows): usar Miniconda/Anaconda e instalar dependencias desde `conda-forge`:

```powershell
conda create -n dev python=3.11 -y
conda activate dev
conda install -c conda-forge --file requirements.txt
```

2. Alternativa: instalar Microsoft Visual C++ Build Tools y las cabeceras de desarrollo de PostgreSQL (para poder compilar ruedas desde fuente). Luego recrear `.venv` y volver a `pip install -r requirements.txt`.

3. Otra alternativa: usar una versión de CPython con ruedas precompiladas (por ejemplo 3.10/3.11). Recrear el `.venv` con esa versión y reinstalar.

- Si no necesitas conectar a PostgreSQL para pruebas rápidas, puedes stubear la capa de BD o usar SQLite temporalmente mientras resuelves las dependencias nativas.

---

## 🚀 Novedades Agregadas - Diciembre 2025

### 🔐 Autenticación y Seguridad JWT
- Sistema de registro e inicio de sesión con tokens JWT
- Rutas protegidas que requieren autenticación
- Hash de contraseñas con bcrypt
- Tokens con expiración configurable
- Middleware de autenticación OAuth2PasswordBearer

### 🛒 Carrito de Compras Avanzado y Protegido
- Funcionalidad completa de carrito protegida por JWT
- Añadir, actualizar y remover artículos del carrito
- Procesamiento seguro de checkout
- Gestión de cantidades y validaciones

### 🔍 Búsqueda Avanzada
- Búsqueda con múltiples criterios y filtros
- Filtrado por género, precio, rating, plataforma
- Búsqueda por tags y ofertas
- Ordenamiento configurable (popularidad, precio, rating, fecha)

### 📋 Perfil de Usuario
- Historial de órdenes
- Recomendaciones personalizadas
- Datos protegidos por autenticación

### 📐 Arquitectura de Schemas Modernos
- Organización modular de Pydantic schemas
- `auth_schemas.py` - Modelos para autenticación
- `game_schemas.py` - Modelos para juegos
- `cart_schemas.py` - Modelos para carrito de compras
- `response_schemas.py` - Modelos para respuestas API

### ⚡ Optimizado para ASGI
- Uso eficiente de dependencias asincrónicas
- Manejo adecuado de context managers
- Consultas SQL optimizadas con filtros eficientes
- Paginación implementada en endpoints de búsqueda

### 🧪 Pruebas Mejoradas
- Validación de endpoints protegidos
- Pruebas de autenticación JWT
- Verificación de funcionalidades de búsqueda avanzada
- Cobertura ampliada para nuevas funcionalidades

---

## Notas importantes

- Código principal: `src/estim_py_api/` (aquí residen `app.py`, `database.py`, `search_service.py`, `shopping_service.py`, etc.).
- Todos los endpoints están ahora protegidos por autenticación JWT donde corresponde.
- La arquitectura utiliza principios de Clean Architecture con capas bien definidas.
- La seguridad JWT protege todos los endpoints sensibles del carrito y perfil de usuario.
