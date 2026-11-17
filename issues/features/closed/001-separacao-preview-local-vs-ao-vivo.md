# FEAT-001 — Separação de Preview Local vs. Ao Vivo na Coluna Central

- **Status:** Closed
- **Área:** `apps/ui/src/operator/OperatorView.tsx`, `apps/ui/src/operator/hooks/useOperatorController.ts`

## Motivação
Evitar que clicar em uma música, passagem ou apresentação na coluna esquerda alterasse acidentalmente o que a congregação estava vendo no telão.

## O que foi atingido
- O conteúdo exibido na Coluna Central passou a ser governado por `workspaceItem` (estado local do operador), totalmente desacoplado de `state.mode`.
- Clique simples apenas inspeciona no Workspace; duplo clique ou `Enter` projeta no telão.
