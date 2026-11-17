# BUG-016 — Disparar alerta sem mensagem não mostra erro de validação no formulário

- **Status:** Closed

- **Severidade:** Médio | **Tipo:** Bug de Validação / UX | **Fase:** 1
- **Problema:** Quando o operador clica em `"Disparar Alerta"` deixando o campo de mensagem em branco, o formulário não dispara o alerta nem exibe qualquer mensagem de erro ou indicação visual informando que o campo é obrigatório.
- **Arquivos envolvidos:**
  - [StageControlModal.tsx](../../new-holyrics/slices/stage/ui/components/StageControlModal.tsx#L48-L57) e [StageControlModal.tsx](../../new-holyrics/slices/stage/ui/components/StageControlModal.tsx#L121-L126)
- **Causa-raiz:** `handleTriggerAlert` retorna imediatamente (`if (!alertMessage.trim()) return`) sem definir estado de erro visual ou mensagem de validação inline.
- **Solução planejada:** Adicionar estado `alertError` exibindo mensagem vermelha abaixo do input `"Digite a mensagem do alerta para disparar."`, destaque na borda do campo e feedback de confirmação quando disparado.
