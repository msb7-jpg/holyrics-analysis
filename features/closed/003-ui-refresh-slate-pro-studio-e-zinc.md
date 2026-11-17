# FEAT-003 — UI Refresh: Design System Slate Pro Studio para Tema Neutro Zinc & Geist

- **Status:** Closed
- **Área:** `apps/ui`, `slices/*`

## Motivação
Eliminar o excesso de caixas rígidas, saturação azulada e múltiplos sinais neon de "Ao Vivo", migrando para uma hierarquia neutra acromática (`zinc-950` / `zinc-900` / `zinc-800`), tipografia `Geist Variable` / `Geist Mono` empacotada offline e `LiveIndicator` discreto.

## O que foi atingido
- Migração completa dos primitivos em `apps/ui/src/components/ui/`, console principal, Spotlight (`Ctrl+K`), modais secundários e telas remotas.
- Substituição de badges de texto `AO VIVO` por `LiveIndicator` animado de forma sutil.
