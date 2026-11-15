.PHONY: dev build test check check-legacy audit clean

# Cores para o terminal
GREEN=\033[0;32m
NC=\033[0m

help:
	@echo "Comandos disponíveis:"
	@echo "  make dev      - Inicia o servidor de desenvolvimento (Holyrics TS/React)"
	@echo "  make build    - Compila a aplicação para produção"
	@echo "  make test     - Roda todos os testes do novo ecossistema"
	@echo "  make check    - Verifica a tipagem e os arquivos TS/React (Bun)"
	@echo "  make audit    - Roda os scripts de auditoria (PORT_MAP.md e justificativas)"
	@echo "  make clean    - Remove as pastas dist e node_modules"

dev:
	@echo "${GREEN}Iniciando Holyrics (Novo)...${NC}"
	bun run dev

build:
	@echo "${GREEN}Compilando Holyrics...${NC}"
	bun run build

test:
	@echo "${GREEN}Rodando Testes...${NC}"
	bun test

check:
	@echo "${GREEN}Validando Tipagem...${NC}"
	bun run typecheck

audit:
	@echo "${GREEN}Rodando mapeador de porte (PORT_MAP)...${NC}"
	bun update_map.ts

clean:
	rm -rf dist node_modules
