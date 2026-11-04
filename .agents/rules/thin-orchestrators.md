---
description: Proíbe lógica de negócio complexa em arquivos de alto nível e pontos de entrada (Thin Orchestrators)
globs: "**/index.ts", "**/main.ts", "**/server.ts", "**/cli.ts"
---

# Regra: Arquivos de Alto Nível Enxutos ("Thin Orchestrators")

Pontos de entrada (`index.ts`, `server.ts`, `main.ts`, etc.) e arquivos de alto nível devem atuar estritamente como **orquestradores de fluxo**, composição de dependências e registro de rotas/serviços.

## Diretrizes Obrigatórias:

1. **Sem Lógica de Negócio Inline:**
   - É proibido conter regras de negócio, manipulações de payload, queries manuais ao banco de dados ou parsing complexo de requisições diretamente nesses arquivos.
2. **Delegação Imediata:**
   - Lógica de domínio e dados pertence a `slices/<slice>/daemon/` ou pacotes em `packages/*`.
   - Tratamento de rotas HTTP, parâmetros e CORS pertence a `apps/daemon/src/api/routes/*`.
   - Gerenciamento de WebSockets pertence a `apps/daemon/src/ws/*`.
3. **Sem Cadeias Excessivas de Condicionais:**
   - Evitar cascatas de `if (url.pathname === ...)` ou blocos `switch` gigantes no arquivo de entrada. Usar roteadores modulares com despachantes dedicados.
4. **Teto de Linhas:**
   - Pontos de entrada devem ser declarativos, limpos e preferencialmente não ultrapassar ~50 a 80 linhas de código.
