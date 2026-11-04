# Guia de Engenharia, Regras de Agentes e Matriz Documental: LumenLite

Este repositório implementa o **LumenLite**, a modernização arquitetural e visual de alta performance do Holyrics.
Todos os agentes e desenvolvedores autônomos que operam neste repositório devem seguir rigorosamente estes princípios.

---

## 1. Princípios de Engenharia e Arquitetura

1. **Zero YAML:**
   - Toda configuração, roteiro litúrgico (`culto.json`), mapeamento de atalhos (`keybindings.json`) e automações (`automation.json`) é estritamente **JSON** com validação nativa **Zod v4** e suporte a `$schema` (Draft 2020-12 / Draft 7).
2. **Arquitetura Vertical de Fatias (`slices/*`):**
   - Cada fatia é autossuficiente e possui subpastas canônicas:
     - `src/contracts/` ou `contracts/`: Tipos, interfaces e schemas Zod.
     - `src/daemon/` ou `daemon/`: Serviços de backend, acesso ao SQLite ou comunicação em rede (Bun).
     - `src/ui/` ou `ui/`: Componentes React 19 específicos da fatia.
3. **Núcleo Imutável (`packages/core`):**
   - A máquina de estados da projeção (FSM) é um Reducer puro `(State, Action) => State`.
   - Zero efeitos colaterais dentro do reducer. O despachante e o EventBus coordenam as transições de forma determinística.
4. **Design System "Slate Pro Studio":**
   - Paleta sóbria em tons de ardósia (`Slate 900` / `Slate 50`).
   - Não utilizar cores berrantes de semáforo. Letras de hinos devem ser exibidas limpas, sem badges intrusivos `[CORO]` ou `[V1]`.
   - Seguir os tokens definidos em `design-system/lumenlite/MASTER.md`.
5. **Padrão de Docstrings Ricas em Pontos de Entrada (`index.ts`):**
   - Todo arquivo `index.ts` de pacotes (`packages/*`), fatias verticais (`slices/*`) ou apps (`apps/*`) deve conter obrigatoriamente um cabeçalho TSDoc rico com:
     - `@module <nome-do-modulo>`
     - `@description <visao-geral-sintetica>`
     - `## Responsabilidades:` Lista explícita das tarefas do módulo.
     - `## Comunicação e Fronteiras:` O que o módulo dispara, consome e exporta para o restante do ecossistema.
   - Todo método, função exportada, classe e construtor deve possuir documentação TSDoc estrita com `@param` e `@returns` para passar 100% nas regras do ESLint/JSDoc (`bun run lint`).
6. **Nomenclatura Semântica de Identificadores (`id-length >= 3`):**
   - Proibido o uso de variáveis e identificadores com letras únicas ou nomes excessivamente curtos (mínimo de 3 caracteres, ex: `section` em vez de `s`, `event` em vez de `e`, `socket` em vez de `ws`, `database` em vez de `db`).
   - Exceções canônicas permitidas apenas para `id`, `on` e descartes convencionados com `_`.
7. **Logging Estruturado Assíncrono com Pino (`createLogger`):**
   - Proibido o uso de `console.log` síncrono bloqueante no backend e serviços core. Utilizar a factory `createLogger` de `@lumen/core` baseada no [Pino](https://github.com/pinojs/pino), que provê serialização assíncrona de altíssima performance, formatação com `pino-pretty` em desenvolvimento e logs JSON estruturados em produção.
8. **Arquivos de Alto Nível Enxutos e Orquestradores ("Thin Orchestrators"):**
   - Pontos de entrada (`index.ts`, `server.ts`, `main.ts`, etc.) e arquivos de alto nível devem atuar estritamente como **orquestradores de fluxo**, composição de dependências e registro de rotas/serviços.
   - É expressamente proibido concentrar regras de negócio, manipulações pesadas de payload, queries diretas ao banco de dados ou cadeias extensas de condicionais `if (url.pathname === ...)` nesses arquivos.
   - Toda regra de domínio ou acesso a dados pertence à sua respectiva fatia vertical (`slices/<modulo>/daemon`).
   - Todo tratamento de requisição HTTP, validação de parâmetros e respostas pertence a roteadores modulares (`apps/daemon/src/api/routes/*`).
   - O ciclo de vida de WebSockets deve residir em manipuladores dedicados (`apps/daemon/src/ws/*`).
   - Teto de complexidade recomendada: arquivos orquestradores de entrada devem ser concisos (~50 a 80 linhas de composição declarativa limpa).
9. **Código Legado (Somente Leitura):**
   - As pastas `src/com/` e `extracted_*` são exclusivamente para referência de engenharia reversa. Nunca alterar ou misturar novo código nelas.

---

## 2. Auto-Atualização Obrigatória do Checklist e Commits Semânticos

10. **Auto-Atualização Obrigatória do Checklist (`docs/CHECKLIST_ROADMAP.md`):**
    - Todo agente que concluir a implementação, teste ou refatoração de uma etapa, subetapa ou requisito DEVE OBRIGATORIAMENTE atualizar o arquivo [`docs/CHECKLIST_ROADMAP.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/CHECKLIST_ROADMAP.md) antes de finalizar sua resposta:
      1. **Changelog Recente (`## 1. Changelog Recente`):** Descrever as funcionalidades, correções e arquitetura entregues na versão correspondente.
      2. **Checklist Geral de Requisitos (`## 2. Checklist Geral de Requisitos Funcionais (RF)`):** Atualizar o status do requisito correspondente para `[x]` (Concluído) ou `[~]` (Em Andamento), registrando o que foi feito na descrição entre parênteses.
      3. **Próximos Passos (`## 4. Próximos Passos Recomendados no Roadmap`):** Marcar a etapa recém-concluída como `[CONCLUÍDO]` e definir a próxima como `[EM ANDAMENTO]`.

11. **Padrão Obrigatório de Commits Semânticos (Conventional Commits):**
    - Sempre que uma tarefa, funcionalidade, refatoração ou correção for concluída e validada (com testes e checagens estáticas passando), deve ser gerado um commit semântico seguindo o padrão [Conventional Commits](https://www.conventionalcommits.org/):
      - **Tipos Canônicos:** `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `perf:`, `chore:`.
      - **Escopo Recomendado:** Nome da fatia ou módulo afetado (ex.: `feat(liturgy): ...`, `refactor(ui): ...`, `feat(hitems): ...`).
      - **Mensagem Objetiva e em Português ou Inglês:** Descrever claramente o que foi introduzido ou corrigido, acompanhado de descrição detalhada das mudanças no corpo da mensagem.
      - **Pré-requisito para Commit:** Antes de criar o commit, certificar-se de que os testes (`bun test`), linter (`bun run lint`) e checagem de tipos (`bun run check`) passam com 100% de sucesso.

---

## 3. Matriz de Consulta Documental Mandatória

11. **Matriz Documental ("O que o agente DEVE consultar antes de codificar"):**
    - Antes de propor qualquer plano, arquitetura ou alteração de código, o agente deve consultar a matriz abaixo para encontrar os arquivos canônicos correspondentes:

| Objetivo / Atividade do Agente | Arquivo Canônico Obrigatório | O que verificar no documento |
|---|---|---|
| **Verificar status atual e próximos passos** | [`docs/CHECKLIST_ROADMAP.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/CHECKLIST_ROADMAP.md) | O que já foi entregue (`Changelog`), quais RFs estão `[x]`, `[~]` ou `[ ]`, e qual a etapa recomendada. |
| **Especificação detalhada de Requisitos (RF/RNF)** | [`docs/new-holyrics/01-requirements.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/new-holyrics/01-requirements.md) | Descrição exata de cada RF (RF-01 a RF-34), problemas da abordagem legada e nova abordagem exigida. |
| **Engenharia Reversa do Holyrics original** | [`docs/new-holyrics/01-requirements.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/new-holyrics/01-requirements.md) + `src/com/limagiran/*` | Mapeamento das classes Java originais mencionadas na seção 🕵️ do módulo correspondente. (Apenas leitura!). |
| **Regras de Dependências e Toolchain** | [`docs/new-holyrics/02-tech-stack.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/new-holyrics/02-tech-stack.md) | Tecnologias adotadas (Bun, Vitest, SQLite WAL, Zod v4, Vite+) e proibidas (YAML, dependências inchadas). |
| **Onde criar/modificar arquivos no Monorepo** | [`docs/new-holyrics/03-directory-structure.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/new-holyrics/03-directory-structure.md) | Árvore oficial de pastas (`packages/core`, `slices/<nome>/contracts\|daemon\|ui`, `apps/daemon`, `apps/ui`). |
| **Design, Layout de Telas e Experiência do Operador** | [`docs/new-holyrics/05-design-ui-ux.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/new-holyrics/05-design-ui-ux.md) | Layout de 4 colunas (*Slate Pro Studio*), filosofia sem "luzes de semáforo", badge `No ar`, tipografia limpa. |
| **Tokens CSS, Cores, Espaçamento e Tipografia** | [`design-system/lumenlite/MASTER.md`](file:///home/miguelsb/workspace/holyrics-analysis/design-system/lumenlite/MASTER.md) | Variáveis CSS `--bg-app`, `--bg-surface`, `--text-primary`, `--accent`, escalas de espaçamento e fontes. |
