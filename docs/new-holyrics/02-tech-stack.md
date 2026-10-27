# 2. Tecnologias, Ferramentas & Estratégia de Dependências

Este documento detalha o ferramental unificado, a matriz de decisões de dependências (o que evitamos vs. o que adotamos) e os formatos de configuração e contratos do sistema.

---

## 2.1. Toolchain Unificado Vite+ (`vp` CLI)

O projeto é construído e validado integralmente através do **Vite+**, a ferramenta unificada da VoidZero para o ecossistema moderno da web.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        VITE+ TOOLCHAIN (vp CLI)                        │
├────────────────────────────────────────────────────────────────────────┤
│  • Vite 8 + Rolldown  ──► Bundler de Produção Ultrarrápido em Rust     │
│  • Vitest v5          ──► Runner de Testes Unitários e Integração      │
│  • tsdown             ──► Empacotador de Bibliotecas (@lumen/core)     │
│  • Oxlint             ──► Linter em Rust (100x mais rápido que ESLint) │
│  • Oxfmt              ──► Formatador Nativo em Rust (estilo Prettier)  │
│  • Vite Task          ──► Orquestrador Monorepo com Cache Local        │
│  • Bun Managed        ──► Runtime TypeScript nativo de execução        │
└────────────────────────────────────────────────────────────────────────┘
```

### Comandos do Vite+ no Fluxo de Desenvolvimento:

| Ação Desejada            | Comando Vite+       | Descrição Técnica                                                                       |
| :----------------------- | :------------------ | :-------------------------------------------------------------------------------------- |
| **Instalação**           | `vp install`        | Instala dependências usando o Bun como package manager configurado.                     |
| **Linting Unificado**    | `bun run lint`      | Executa Oxlint (Rust) + ESLint Guardrails AST em sequência com saída stylish unificada. |
| **Diagnóstico Total**    | `bun run check:all` | Executa a validação monorepo completa: `vp check` + ESLint + Fallow.                    |
| **Checagem Completa**    | `vp check`          | Executa formatação (Oxfmt), linting (Oxlint) e checagem de tipos em paralelo.           |
| **Auto-Fix**             | `vp check --fix`    | Corrige automaticamente problemas de formatação e linting nos arquivos.                 |
| **Linting Rápido**       | `vp check --no-fmt` | Roda linting Oxlint e tipagem estrita sem formatação (< 600ms).                         |
| **Auditoria Monorepo**   | `bun run fallow`    | Analisa dead code, complexidade cognitiva (<20) e duplicações estruturais (AST clones). |
| **Execução de Testes**   | `vp test`           | Roda a suíte completa de testes Vitest v5 em todas as fatias verticais.                 |
| **Build da Aplicação**   | `vp build`          | Gera o bundle otimizado de produção da interface React via Rolldown.                    |
| **Build de Bibliotecas** | `vp pack`           | Empacota contratos e tipos em `@lumen/core` via tsdown gerando `.mjs` e `.d.mts`.       |
| **Dev Server**           | `vp dev`            | Inicia o servidor de desenvolvimento com Hot Module Replacement instantâneo.            |
| **Tarefas Monorepo**     | `vp run <tarefa>`   | Executa scripts do workspace com suporte a cache inteligente de compilação.             |

### Arquitetura de Qualidade Estática em 3 Camadas:

O sistema garante confiabilidade e velocidade de desenvolvimento através de uma divisão clara de responsabilidades entre ferramentas:

1. **Camada 1: Rust Fast-Path (`vp check` = Oxlint + Oxfmt + tsc)**:
   - **Oxlint**: Substitui ESLint no dia a dia, cobrindo mais de 200 regras em Rust (Core ESLint, React Hooks, JSX A11y, Unicorn, TypeScript, Vitest) executando em menos de 600ms.
   - **Oxfmt**: Formatador em Rust de alta performance configurado com aspas simples no código, aspas duplas no JSX e sem ponto-e-vírgula (`semi: false`).
   - **tsgolint**: Checagem de tipos estrita TypeScript em tempo real acoplada ao Oxlint.
2. **Camada 2: Design System & AST Guardrails (ESLint Especializado & Complementar)**:
   - **Execução Complementar**: Integrado ao Oxlint através do `eslint-plugin-oxlint` (`...oxlint.configs['flat/recommended']`), desativando regras redundantes e produzindo uma saída unificada no terminal (`--format stylish`).
   - **`@shadcn/lint`**: Aplicação de guardrails do design system (`shadcn/no-restyle`, `shadcn/no-raw-colors`, `shadcn/no-arbitrary-values`).
   - **Preservação do Design Intent**: Proibição estrita de substituir cores por variantes arbitrárias para silenciar linters; exige criação e registro no CSS global.
   - **`react/forbid-elements`**: Bloqueio de tags HTML nativas (`<button>`, `<input>`, `<select>`, `<textarea>`) fora de `components/ui`.
   - **Safe React Hooks**: Wrapper protetor de `exhaustive-deps` que remove auto-fix perigoso e sugestões da IDE para prevenir loops infinitos e tearing.
3. **Camada 3: Auditoria Estrutural Monorepo (Fallow)**:
   - **Dead Code**: Eliminação contínua de exports órfãos, tipos não consumidos e dependências esquecidas.
   - **Cognitive Complexity (<20)**: Barreira contra God Components e violações da Regra dos 250 linhas.
   - **AST Clone Groups (`fallow dupes`)**: Detecção de duplicações estruturais entre fatias verticais.
     _(Para especificações completas, consulte `.agents/skills/frontend-coding-standards/references/lint-and-quality-standards.md`)._

---

## 2.2. Matriz de Decisão: Bibliotecas Mantidas vs. Capacidades Nativas

Para honrar os requisitos de **tempo de boot sub-1.2s**, **consumo de RAM reduzido em 80% (<120MB)** e **manutenibilidade a longo prazo**, o LumenLite adota uma política implacável contra pacotes obsoletos, abandonados ou redundantes:

| Domínio / Slice                    | O que NÃO usamos (Obsoleto / Redundante)            | Solução Adotada (Moderna / Ativa)               | Racional Técnico e Benefícios                                                                                                                 |
| :--------------------------------- | :-------------------------------------------------- | :---------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------- |
| **JSON Schema & Contratos**        | `zod-to-json-schema` ❌ _(Desnecessário)_           | **`zod` v4 nativo (`z.toJSONSchema()`)** ✅     | O Zod 4 possui geração nativa de JSON Schema (Draft 2020-12 / 7) e registries (`z.globalRegistry`). Zero adaptadores de terceiros.            |
| **Automação Visual em Grafo**      | Construção manual de canvas ❌ _(Custo proibitivo)_ | **`@xyflow/react` (React Flow v12+)** ✅        | O padrão mundial para interfaces em nós. Oferece minimapa, curvas bezier, arrasto de blocos e serialização direta para o `hooks.json`.        |
| **Streaming ZIP & Backups**        | `jszip`, `adm-zip` ❌ _(Lentos / Legados)_          | **`fflate`** ✅                                 | O descompactador mais rápido do ecossistema JS/Wasm. Zero dependências, streaming em memória para backups Holyrics e arquivos PPTX.           |
| **Parsing de XML (Apresentações)** | DOM-based parsers, `xml2js` ❌ _(Pesados)_          | **`fast-xml-parser` (v4)** ✅                   | Parser puramente funcional e tipado. Extrai as anotações do pastor de `notesSlides.xml` em sub-milissegundos.                                 |
| **Virtualização de Listas**        | Renderização completa no DOM ❌ _(Lentidão)_        | **`@tanstack/react-virtual`** ✅                | Headless, do mesmo criador da nossa arquitetura (TkDodo). Virtualiza buscas de 5.000 músicas e 31.000 versículos sem sobrecarregar a memória. |
| **Transmissão & Live Streaming**   | Drivers proprietários / polling ❌                  | **`obs-websocket-js` (v5)** ✅                  | SDK oficial mantido pelo projeto OBS Studio. Permite trocar cenas de câmeras e ativadores de Lower Thirds via WebSocket RPC v5.               |
| **CLI Runner do Daemon**           | `commander`, `yargs` ❌ _(Pesados)_                 | **`citty` (UnJS) ou `util.parseArgs` (Bun)** ✅ | Framework ESM ultraleve de subcomandos tipados para o binário `lumen`.                                                                        |
| **Parser de Versículos Bíblicos**  | `bcv_parser` ❌ _(Legado CoffeeScript)_             | **Micro-parser nativo TS (`slices/bible`)** ✅  | ~80 linhas de TypeScript moderno com dicionário estático dos 66 livros da Bíblia. Cobre 100% dos padrões PT-BR sem pacotes legados.           |
| **Tipografia Auto-Ajustável**      | `fitty`, `textfit` ❌ _(Layout thrashing)_          | **`ResizeObserver` + CSS Container Queries** ✅ | Escala dinâmica de tipografia acelerada por GPU sem forçar recálculos síncronos de layout no DOM.                                             |
| **Atalhos de Teclado**             | `tinykeys`, `react-hotkeys-hook` ❌                 | **Listener nativo W3C (`event.code`)** ✅       | 25 linhas de hook React lendo o `keybindings.json` diretamente via despachante da FSM.                                                        |
| **Máquina de Estados (Telão)**     | `xstate` ❌ _(Complexidade / 60KB)_                 | **FSM Reducer puro (`@lumen/core`)** ✅         | Função pura imutável `(State, Action) => State`. Determinística, zero dependências e imune a race conditions.                                 |
| **Transporte em Tempo Real**       | `socket.io` ❌ _(Overhead de protocolo)_            | **`Bun.serve({ websocket })` nativo** ✅        | WebSocket puro com buffer binário/JSON local, latência <1ms e reconexão automática simples.                                                   |

---

## 2.3. Eliminação Estrita de YAML: Adoção de JSON + JSON Schema

O LumenLite adota **100% JSON** para todos os arquivos de configuração, liturgia e automação do sistema:

### Justificativas de Engenharia para Eliminação do YAML:

1. **Desempenho de Parsing:** `JSON.parse` é implementado nativamente em C++ no V8 e Bun, rodando até 20x mais rápido do que bibliotecas de parsing YAML em JavaScript.
2. **Determinismo Absoluto:** YAML introduz armadilhas graves em tempo de execução (ex: o "The Norway Problem", onde o código de país `NO` vira o booleano `false`, ou erros silenciosos de indentação de espaços).
3. **Autocompletion e Validação em Tempo Real no Editor:** Com a diretiva `$schema`, qualquer voluntário ou operador que abrir o arquivo `culto.json` no VS Code ou qualquer editor moderno recebe documentação de hover, autocomplete automático e checagem de erros de sintaxe instantânea.

### Geração Nativa de JSON Schema com Zod v4 (Zero Adaptadores Externos):

Ao invés de adotar pacotes externos redundantes como `zod-to-json-schema`, o LumenLite utiliza as capacidades nativas do **Zod 4**:

- **`z.toJSONSchema(schema)` Nativo:** Converte diretamente os schemas de contratos TypeScript em arquivos JSON Schema compatíveis com o padrão Draft 2020-12 / Draft 7.
- **`z.globalRegistry`:** Permite linkar múltiplos schemas interconectados usando `$defs` e `$ref` sem boilerplate.

```ts
// Exemplo no daemon de compilação de schemas:
import * as z from 'zod'
import { LiturgySchema } from '@slices/liturgy/contracts'

const liturgyJsonSchema = z.toJSONSchema(LiturgySchema, {
  target: 'draft-2020-12',
})
await Bun.write('liturgy.schema.json', JSON.stringify(liturgyJsonSchema, null, 2))
```

---

## 2.4. Especificações de Dados em JSON

### Especificação do `culto.json` ("Culto como Código"):

```json
{
  "$schema": "./liturgy.schema.json",
  "id": "culto-domingo-noite-2026-10-25",
  "title": "Culto de Celebração - Noite",
  "date": "2026-10-25T19:00:00Z",
  "theme": {
    "defaultBackground": "/media/loops/adoracao_azul_1080p.mp4",
    "fontFamily": "Inter Display",
    "textColor": "#FFFFFF",
    "textShadow": "0 4px 12px rgba(0,0,0,0.85)"
  },
  "items": [
    {
      "id": "item-01",
      "type": "MEDIA",
      "title": "Contagem Regressiva 5min",
      "mediaSrc": "/media/videos/countdown_5min.mp4",
      "autoAdvance": true
    },
    {
      "id": "item-02",
      "type": "SONG",
      "title": "Porque Ele Vive",
      "songId": "song-holyrics-1042",
      "arrangement": ["V1", "CHORUS", "V2", "CHORUS", "BRIDGE", "CHORUS", "TAG"],
      "backgroundOverride": "/media/loops/warm_particles.mp4"
    },
    {
      "id": "item-03",
      "type": "BIBLE",
      "title": "Leitura Litúrgica",
      "reference": "João 3:16-18",
      "version": "NVI",
      "dualVersion": "NAA"
    },
    {
      "id": "item-04",
      "type": "PPTX",
      "title": "Mensagem Pastoral",
      "fileSrc": "/sermoes/2026-10-25-fe-inabalavel.pptx"
    }
  ],
  "hooksEnabled": true
}
```

### Especificação do `keybindings.json`:

```json
{
  "$schema": "./keybindings.schema.json",
  "bindings": {
    "Space": "nav next",
    "Backspace": "nav return",
    "ArrowRight": "nav next",
    "ArrowLeft": "nav prev",
    "F1": "panic clear-text",
    "F2": "panic logo",
    "F3": "panic blackout",
    "KeyB": "quick bible",
    "KeyS": "quick song",
    "Control+KeyK": "spotlight open",
    "Digit1": "slide goto 0",
    "Digit2": "slide goto 1",
    "Digit3": "slide goto 2",
    "Digit4": "slide goto 3"
  }
}
```
