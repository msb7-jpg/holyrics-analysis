# Padrão de Rastreamento de Bugs & Features (`LumenLite`)

Este documento formaliza a organização de **bugs** e **features** do projeto. Em vez de listas monolíticas (`BUGS.md`, `TODOS.md`, `checklist.md`), cada item possui seu próprio arquivo Markdown numerado sequencialmente na ordem de criação.

---

## 1. Estrutura de Diretórios

```text
bugs/
├── open/       # Bugs investigados ou reportados ainda não corrigidos
├── closed/     # Bugs corrigidos e validados
└── media/      # Capturas de tela e gabaritos visuais compartilhados

features/
├── open/       # Melhorias, refatorações e novas funcionalidades planejadas
└── closed/     # Funcionalidades e refatorações concluídas
```

---

## 2. Convenção de Nomenclatura

- **Formato do arquivo:** `NNN-slug-descritivo.md`
  - `NNN`: número sequencial com 3 dígitos (`001`, `002`, `003`, ...), atribuído na ordem cronológica de entrada dentro da respectiva categoria (`bugs` ou `features`).
  - `slug-descritivo`: resumo curto em `kebab-case`, sem acentos.
- **Imutabilidade do número:** ao concluir um item, **mova o arquivo** de `open/` para `closed/` preservando o mesmo número e slug (ex.: `bugs/open/023-...md` ➔ `bugs/closed/023-...md`). Nunca reutilize números.

---

## 3. Estrutura Padrão de Cada Arquivo

```markdown
# BUG-023 — Título curto e objetivo (ou FEAT-005 — ...)

- **Status:** Open | Closed
- **Severidade / Prioridade:** Crítico | Alto | Médio | Melhoria / UX
- **Área / Slice:** ex.: `slices/stage`, `slices/navigation`, `apps/ui`

## Descrição / Problema
Explicação clara do comportamento atual vs. esperado (ou objetivo da feature).

## Arquivos Envolvidos
- Lista de arquivos e símbolos relevantes.

## Causa-Raiz / Plano de Ação
1. Passos técnicos ou critérios de aceite.

## Mídias / Referências
- Links relativos para `bugs/media/...` quando houver capturas de tela.
```
