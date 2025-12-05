import os
import uuid
from datetime import datetime

from sqlalchemy import (
    create_engine,
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
from sqlalchemy.orm import declarative_base, sessionmaker
from sqlalchemy.dialects.postgresql import UUID as PG_UUID

# Import bcrypt for password hashing
from passlib.context import CryptContext

# Create password context for hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Load environment variables
from dotenv import load_dotenv
load_dotenv()

db_user = os.getenv("DB_USER", "estim")
db_pass = os.getenv("DB_PASS", "estim")
db_host = os.getenv("DB_HOST", "localhost")
db_name = os.getenv("DB_NAME", "estim")

db_port_str = os.getenv("DB_PORT", "5432")
try:
    db_port = int(db_port_str)
except (ValueError, TypeError):
    db_port = 5432

DATABASE_URL = f"postgresql://{db_user}:{db_pass}@{db_host}:{db_port}/{db_name}"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


class UserDB(Base):
    __tablename__ = "users"

    id = Column(
        PG_UUID(as_uuid=True),
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


def create_user(db, username: str, email: str, password: str):
    """Create a new user with hashed password."""
    hashed_pwd = get_password_hash(password)
    db_user = UserDB(
        username=username,
        email=email,
        hashed_password=hashed_pwd
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user


def get_user_by_username(db, username: str):
    """Get a user by username."""
    return db.query(UserDB).filter(UserDB.username == username).first()


def get_user_by_email(db, email: str):
    """Get a user by email."""
    return db.query(UserDB).filter(UserDB.email == email).first()


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# Import the GameDB model here to keep all models in one file structure
from . import GameDB
