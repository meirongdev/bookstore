# Simple Makefile to build images and run docker-compose for local development

# adjust if your environment uses "docker-compose" instead of "docker compose"
DOCKER_COMPOSE ?= docker compose

BACKEND_IMAGE := meirongdev/bookstore-backend:local
FRONTEND_IMAGE := meirongdev/bookstore-frontend:local

# paths
BACKEND_DIR := backend
FRONTEND_DIR := frontend
ENV_FILE := .env

.PHONY: help env-check build-backend build-frontend build up up-detach down restart logs ps clean migrate

help:
	@printf "\nUsage:\n"
	@printf "  make build            # build both backend and frontend images\n"
	@printf "  make build-backend    # build backend image (runs mvn then docker build)\n"
	@printf "  make build-frontend   # build frontend image (runs npm build then docker build)\n"
	@printf "  make migrate          # run database migrations (downloads driver if needed)\n"
	@printf "  make up               # docker compose up (builds images)\n"
	@printf "  make up-detach        # docker compose up -d (detached)\n"
	@printf "  make down             # docker compose down (remove volumes & orphans)\n"
	@printf "  make restart          # restart compose services\n"
	@printf "  make logs             # follow logs\n"
	@printf "  make ps               # show compose ps\n"
	@printf "  make clean            # remove local images\n"
	@printf "\n"

env-check:
	@if [ -f $(ENV_FILE) ]; then \
		echo "Found $(ENV_FILE)"; \
	else \
		echo "Warning: $(ENV_FILE) not found. Create .env with required variables (POSTGRES_*, JWT_SECRET_KEY, STRIPE_SECRET_KEY)"; \
	fi

# Build backend jar and image
build-backend:
	@echo "==> build backend via spring-boot:build-image"
	@(cd $(BACKEND_DIR) && mvn -DskipTests spring-boot:build-image -Dspring-boot.build-image.imageName=$(BACKEND_IMAGE))

# Build frontend assets and docker image
build-frontend:
	@echo "==> build frontend"
	@(cd $(FRONTEND_DIR) && npm ci && npm run build)
	@echo "==> build frontend docker image"
	@docker build -t $(FRONTEND_IMAGE) -f $(FRONTEND_DIR)/Dockerfile $(FRONTEND_DIR)

# Build both
build: env-check build-backend build-frontend
	@echo "==> all images built"

# Bring up services (rebuild if needed)
up:
	@$(DOCKER_COMPOSE) up --build

up-detach:
	@$(DOCKER_COMPOSE) up -d --build

down:
	@$(DOCKER_COMPOSE) down --volumes --remove-orphans

restart: down up-detach

logs:
	@$(DOCKER_COMPOSE) logs -f --tail=200

ps:
	@$(DOCKER_COMPOSE) ps

# Remove local images built by Makefile
clean:
	-@docker rmi -f $(BACKEND_IMAGE) $(FRONTEND_IMAGE) || true
	@echo "Local images removed (if existed)."

# Run Liquibase migrations using the liquibase image.
# This target will:
#  - create ./backend/lib if missing
#  - download the PostgreSQL JDBC driver to ./backend/lib/postgresql.jar (if not present)
#  - start postgres and wait for it to be healthy
#  - run the liquibase migration (one-off container with --profile tools)
migrate:
	@mkdir -p $(BACKEND_DIR)/lib
	@if [ ! -f $(BACKEND_DIR)/lib/postgresql.jar ]; then \
		echo "Downloading PostgreSQL JDBC driver to $(BACKEND_DIR)/lib/postgresql.jar..."; \
		curl -fsSL -o $(BACKEND_DIR)/lib/postgresql.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.5.4/postgresql-42.5.4.jar || { echo "Failed to download JDBC driver; please download it manually and place it at $(BACKEND_DIR)/lib/postgresql.jar"; exit 1; }; \
	fi
	@echo "Starting postgres and waiting for it to be healthy..."
	@$(DOCKER_COMPOSE) up -d postgres
	@sh -c '\
		count=0; \
		until $(DOCKER_COMPOSE) exec -T postgres pg_isready -U ${POSTGRES_USER:-bookstoreadmin} -d ${POSTGRES_DB:-bookstore_db} >/dev/null 2>&1; do \
			sleep 2; count=$$((count+1)); if [ $$count -ge 30 ]; then echo "Postgres did not become healthy"; exit 1; fi; \
		done; \
	'
	@echo "Running liquibase migrations..."
	@$(DOCKER_COMPOSE) run --rm liquibase
	@echo "Migration complete!"
