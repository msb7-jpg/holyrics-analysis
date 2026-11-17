# BUG-001 — Tecla `Esc` não fecha a Command Palette (`Ctrl+K`)

- **Status:** Closed

- **Severidade:** Alto | **Tipo:** Bug de Interação / Teclado | **Fase:** 1
- **Problema:** Ao abrir a paleta de comandos (`Ctrl+K`) e pressionar a tecla `Esc` com o cursor no campo de busca, o modal permanece aberto em vez de fechar.
- **Arquivos envolvidos:**
  - [SpotlightDialog.tsx](../../new-holyrics/apps/ui/src/operator/components/SpotlightDialog.tsx#L39-L95)
  - [useKeybindings.ts](../../new-holyrics/slices/keymap/ui/hooks/useKeybindings.ts#L48-L65)
  - [useOperatorShortcuts.ts](../../new-holyrics/slices/keymap/ui/hooks/useOperatorShortcuts.ts#L53-L85)
- **Causa-raiz:** Em `useKeybindings.ts#L50-L63`, ao pressionar `Escape` com foco no `<Command.Input>`, o atalho corresponde a `NAV_CLEAR`, executa `event.preventDefault()` e chama `onAction('NAV_CLEAR')`. Porém, `useOperatorShortcuts.ts#L53-L85` não possui `case 'NAV_CLEAR'`, descartando a ação, e `SpotlightDialog.tsx` não possui listener próprio de `Escape` nem fechamento ao clicar fora (backdrop).
- **Solução planejada:**
  1. Em `SpotlightDialog.tsx`, registrar listener `keydown` para `Escape` (fechando o modal via `onClose()`) e `onClick` no backdrop escuro.
  2. Em `useOperatorShortcuts.ts`, tratar `NAV_CLEAR` quando `isSpotlightOpen` for `true` chamando `onToggleSpotlight()`.
