.PHONY: help dev build test check lint win-app win-tauri-dev win-tauri-build tauri-dev tauri-build

MONOREPO := lumen-lite
UI_DIR := $(MONOREPO)/apps/ui

help: ## Mostra todos os comandos disponiveis
	@echo ""
	@echo "LumenLite - Comandos Disponiveis"
	@echo "=================================="
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'
	@echo ""

dev: ## Sobe Daemon (:4040) + UI Web (:3000) em modo desenvolvimento
	cd $(MONOREPO) && bun run dev

build: ## Gera build de producao Web (UI + Broadcast)
	cd $(MONOREPO) && bun run build

test: ## Executa toda a suite de testes (bun test)
	cd $(MONOREPO) && bun test

check: ## Executa verificacao estrita de tipos TypeScript
	cd $(MONOREPO) && bun run check

lint: ## Executa ESLint e regras de TSDoc
	cd $(MONOREPO) && bun run lint

win-app: ## [Windows] Sobe Daemon + UI no WSL e abre janela desktop limpa via Edge (--app)
	@echo "Iniciando Daemon + UI no WSL e abrindo janela nativa no Windows..."
	@cd $(MONOREPO) && ( \
		bun run dev & \
		DEV_PID=$$!; \
		trap "kill $$DEV_PID 2>/dev/null || true" EXIT INT TERM; \
		echo "Aguardando Vite responder em http://localhost:3000 ..."; \
		for i in $$(seq 1 40); do \
			if curl -sf http://localhost:3000 >/dev/null 2>&1; then break; fi; \
			sleep 0.5; \
		done; \
		powershell.exe -NoProfile -Command "Start-Process 'msedge.exe' -ArgumentList '--app=http://localhost:3000','--window-size=1440,900'" || true; \
		wait $$DEV_PID \
	)

win-tauri-dev: ## [Windows] Sobe Daemon no WSL e roda Tauri v2 nativo no Windows (requer Rust MSVC no Windows)
	@echo "Iniciando Daemon no WSL e executando Tauri dev no host Windows..."
	@cd $(MONOREPO) && ( \
		bun run dev:daemon & \
		DAEMON_PID=$$!; \
		trap "kill $$DAEMON_PID 2>/dev/null || true" EXIT INT TERM; \
		WIN_PATH=$$(wslpath -w "$$(pwd)/apps/ui"); \
		powershell.exe -NoProfile -Command "Set-Location '$$WIN_PATH'; cargo tauri dev" || { \
			echo ""; \
			echo "[AVISO] 'cargo tauri' nao encontrado no Windows. Instale Rust MSVC via winget ou use 'make win-app'."; \
			exit 1; \
		}; \
		wait $$DAEMON_PID \
	)

win-tauri-build: ## [Windows] Compila o executavel .exe / instalador Tauri v2 nativo no Windows
	@WIN_PATH=$$(wslpath -w "$$(pwd)/$(UI_DIR)"); \
	powershell.exe -NoProfile -Command "Set-Location '$$WIN_PATH'; cargo tauri build"

tauri-dev: ## [Linux WSLg / macOS] Sobe Daemon + janela nativa Tauri v2
	cd $(MONOREPO) && bun run tauri:dev

tauri-build: ## [Linux WSLg / macOS] Compila pacote desktop nativo Tauri v2
	cd $(MONOREPO) && bun run tauri:build
