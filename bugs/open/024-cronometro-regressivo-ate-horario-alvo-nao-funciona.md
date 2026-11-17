# BUG-024 — Cronômetro regressivo não mostra contagem decrescente e opção "Até HH:MM" (ex.: até 7:30) não funciona

- **Status:** Open
- **Severidade:** Alto | **Área:** `slices/stage`

## Descrição / Problema
1. O cronômetro regressivo precisa exibir claramente o tempo **decrescendo** em tempo real.
2. Ao clicar na opção de contagem até um horário alvo (ex.: `até 7:30`), a contagem não está funcionando corretamente.

## Arquivos Envolvidos
- `new-holyrics/slices/stage/ui/components/StageControlModal.tsx`

## Plano de Ação
1. Investigar a causa-raiz do cálculo/disparo da opção "Até HH:MM" no `StageControlModal` e backend/store de palco.
2. Garantir exibição decrescente em tempo real.
3. Adicionar testes unitários/integração cobrindo contagem regressiva e modo até horário alvo.
