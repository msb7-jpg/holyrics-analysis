---
description: Regra de auto-atualização mandatória do Checklist e matriz de consulta documental obrigatória para o agente
globs: "**/*"
---

# Regra: Auto-Atualização do Checklist e Matriz Documental

Todo agente ou assistente trabalhando no ecossistema LumenLite deve seguir estas duas práticas obrigatórias:

## 1. Auto-Atualização Obrigatória do Checklist (`docs/CHECKLIST_ROADMAP.md`)

Ao concluir qualquer alteração de produção, implementação de fatia, refatoração arquitetural ou teste:
1. **Atualizar o Changelog Recente (`## 1. Changelog Recente`):** Documentar o que foi construído na versão correspondente.
2. **Atualizar o Checklist Geral de Requisitos (`## 2. Checklist Geral de Requisitos Funcionais (RF)`):** Marcar os itens finalizados com `[x]`, detalhando o que foi feito.
3. **Atualizar os Próximos Passos (`## 4. Próximos Passos Recomendados no Roadmap`):** Marcar a etapa concluída como `[CONCLUÍDO]` e a próxima ativa como `[EM ANDAMENTO]`.

## 2. Matriz de Consulta Documental Mandatória

Antes de codificar ou propor planos, consulte os documentos canônicos da tabela:

| Objetivo | Arquivo Obrigatório | O que verificar |
|---|---|---|
| **Status atual e roadmap** | `docs/CHECKLIST_ROADMAP.md` | O que já foi entregue e o próximo foco. |
| **Especificação de Requisitos** | `docs/new-holyrics/01-requirements.md` | Detalhes do RF (RF-01 a 34), classes legadas Java e nova solução. |
| **Toolchain e Tecnologias** | `docs/new-holyrics/02-tech-stack.md` | O que usamos (Bun, SQLite WAL, Vitest, Zod v4) e o que é proibido. |
| **Estrutura de Pastas** | `docs/new-holyrics/03-directory-structure.md` | Onde colocar arquivos em `packages/`, `slices/` ou `apps/`. |
| **UI/UX & Layout** | `docs/new-holyrics/05-design-ui-ux.md` | Padrão Slate Pro Studio, layout 4 colunas, sem "luzes de semáforo". |
| **Design Tokens** | `design-system/lumenlite/MASTER.md` | Cores CSS, fontes, margens e espaçamento. |
| **Código Original (Leitura)** | `src/com/limagiran/*` | Apenas consulta da lógica legada do Holyrics. |
