.PHONY: web java py

ifneq (,$(wildcard .env))
  include .env
  export
endif

DB_SERVICE=db
DB_USER?=$(POSTGRES_USER)
DB_PASSWORD?=$(POSTGRES_PASSWORD)
DB_NAME?=$(POSTGRES_DB)

# --- Frontend (Vite + React)
web:
	cd web && npm run dev

# --- Java backend (Spring Boot)
java:
	cd apps/java-backend && ./gradlew bootRun

# --- Python backend (FastAPI)
py:
	cd apps/python-backend && \
	. .venv/bin/activate && \
	uvicorn --app-dir src estim_py_api.main:app --reload --port 8000

# --- Docker / DB helpers ---

db-up:
	docker compose up -d $(DB_SERVICE)

db-down:
	docker compose down

# Open psql inside the db container
db-psql:
	docker compose exec -e PGPASSWORD=$(DB_PASSWORD) $(DB_SERVICE) \
	psql -U $(DB_USER) -d $(DB_NAME)

# Run all migrations in db/migrations in order
db-migrate:
	@for f in $$(ls db/migrations/*.sql | sort); do \
	  echo ">> Applying $$f"; \
	  docker compose exec -T $(DB_SERVICE) \
	    env PGPASSWORD=$(DB_PASSWORD) \
	    psql -U $(DB_USER) -d $(DB_NAME) -v ON_ERROR_STOP=1 < "$$f"; \
	done

db-reset:
	docker compose down -v
	docker compose up -d $(DB_SERVICE)
	@echo ">> Running migrations"
	$(MAKE) db-migrate
	@echo ">> Running seeds"
	$(MAKE) db-seed


# Seed sample data
db-seed:
	@for f in $$(ls db/seeds/*.sql | sort); do \
	  echo ">> Seeding $$f"; \
	  docker compose exec -T $(DB_SERVICE) \
	    env PGPASSWORD=$(DB_PASSWORD) \
	    psql -U $(DB_USER) -d $(DB_NAME) -v ON_ERROR_STOP=1 < "$$f"; \
	done

# Quick smoke test to verify enums, tables, seed, and a constraint
db-smoke:
	docker compose exec -T $(DB_SERVICE) \
	  env PGPASSWORD=$(DB_PASSWORD) \
	  psql -U $(DB_USER) -d $(DB_NAME) -v ON_ERROR_STOP=1 \
	  < db/smoke/db_smoke.sql
