import os
from dotenv import load_dotenv
import uuid
from datetime import datetime
from typing import AsyncGenerator

# Async SQLAlchemy imports
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker
from sqlalchemy.orm import declarative_base

from sqlalchemy import (
    Column,
    String,
    Text,
    Boolean,
    DateTime,
    Date,
    JSON,
    Numeric,
    Integer,
    BigInteger,
)

load_dotenv()

# Use PostgreSQL if available, otherwise fall back to SQLite for testing
try:
    from sqlalchemy.dialects.postgresql import UUID as PG_UUID

    db_user = os.getenv("DB_USER", "estim")
    db_pass = os.getenv("DB_PASS", "estim")
    db_host = os.getenv("DB_HOST", "localhost")
    db_name = os.getenv("DB_NAME", "estim")

    db_port_str = os.getenv("DB_PORT", "5432")
    try:
        db_port = int(db_port_str)
    except (ValueError, TypeError):
        db_port = 5432

    DATABASE_URL = f"postgresql+asyncpg://{db_user}:{db_pass}@{db_host}:{db_port}/{db_name}"
    db_driver = "postgresql"
    engine = create_async_engine(
        DATABASE_URL,
        pool_size=5,
        max_overflow=10,
        pool_pre_ping=True,
        pool_recycle=300,
    )
except (ImportError, ModuleNotFoundError):
    # Fallback to SQLite for testing without PostgreSQL dependencies
    from sqlalchemy import String
    PG_UUID = String  # Use String as fallback type for SQLite
    DATABASE_URL = "sqlite+aiosqlite:///./test.db"
    db_driver = "sqlite"
    from sqlalchemy.ext.asyncio import create_async_engine
    engine = create_async_engine(
        DATABASE_URL,
        connect_args={"check_same_thread": False},
        pool_pre_ping=True,
    )

# Create async session factory
AsyncSessionLocal = async_sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False
)

Base = declarative_base()


class GameDB(Base):
    __tablename__ = "games"

    id = Column(
        PG_UUID(as_uuid=True) if globals().get('db_driver') == "postgresql" else String,
        primary_key=True,
        index=True,
        default=uuid.uuid4,
    )

    publisher_id = Column(
        PG_UUID(as_uuid=True) if globals().get('db_driver') == "postgresql" else String,
        nullable=False,
        index=True
    )

    title = Column(String(255), nullable=False, index=True)
    description = Column(Text)
    short_description = Column(String(500))

    price = Column(Numeric(10, 2), nullable=False, index=True)
    base_price = Column(Numeric(10, 2))

    is_published = Column(Boolean, nullable=False, default=False, index=True)

    release_date = Column(Date)

    age_rating = Column(String(50))

    system_requirements = Column(JSON)

    game_metadata = Column("metadata", JSON)

    average_rating = Column(Numeric(3, 2), nullable=False, default=0.0, index=True)
    review_count = Column(Integer, nullable=False, default=0)
    total_playtime = Column(BigInteger, nullable=False, default=0)
    download_count = Column(Integer, nullable=False, default=0)

    created_at = Column(DateTime(timezone=True), default=datetime.utcnow)
    updated_at = Column(
        DateTime(timezone=True),
        default=datetime.utcnow,
        onupdate=datetime.utcnow,
    )


from passlib.context import CryptContext
from sqlalchemy import select

# Create password context for hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


class UserDB(Base):
    __tablename__ = "users"

    id = Column(
        PG_UUID(as_uuid=True) if globals().get('db_driver') == "postgresql" else String,
        primary_key=True,
        index=True,
        default=uuid.uuid4,
    )
    username = Column(String(50), unique=True, index=True, nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    is_active = Column(Boolean, nullable=False, default=True)
    is_admin = Column(Boolean, nullable=False, default=False)

    created_at = Column(DateTime(timezone=True), default=datetime.utcnow)
    updated_at = Column(
        DateTime(timezone=True),
        default=datetime.utcnow,
        onupdate=datetime.utcnow,
    )


def get_password_hash(password: str) -> str:
    """Hash a password."""
    return pwd_context.hash(password)


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verify a plaintext password against a hashed password."""
    return pwd_context.verify(plain_password, hashed_password)


async def create_user(db: AsyncSession, username: str, email: str, password: str):
    """Create a new user with hashed password."""
    hashed_pwd = get_password_hash(password)
    db_user = UserDB(
        username=username,
        email=email,
        hashed_password=hashed_pwd
    )
    db.add(db_user)
    await db.commit()
    await db.refresh(db_user)
    return db_user


async def get_user_by_username(db: AsyncSession, username: str):
    """Get a user by username."""
    result = await db.execute(select(UserDB).filter(UserDB.username == username))
    return result.scalar_one_or_none()


async def get_user_by_email(db: AsyncSession, email: str):
    """Get a user by email."""
    result = await db.execute(select(UserDB).filter(UserDB.email == email))
    return result.scalar_one_or_none()


async def get_db() -> AsyncGenerator[AsyncSession, None]:
    """Dependency for getting async database session."""
    async with AsyncSessionLocal() as session:
        try:
            yield session
        finally:
            await session.close()