# BUG-017 — Vazamento de rota técnica `/stage` no Modal de Palco e Alertas

- **Status:** Closed

- **Severidade:** Médio | **Tipo:** Poluição / Terminologia Técnica | **Fase:** 1
- **Problema:** Os textos do modal expõem detalhes internos de roteamento web (`/stage`) no botão `"Abrir /stage"` e no título `"RECADO EXCLUSIVO AO PÚLPITO (/STAGE)"`, poluindo a interface com termos técnicos desnecessários para o operador.
- **Arquivos envolvidos:**
  - [StageControlModal.tsx](../../new-holyrics/slices/stage/ui/components/StageControlModal.tsx#L93) e [StageControlModal.tsx](../../new-holyrics/slices/stage/ui/components/StageControlModal.tsx#L286)
- **Causa-raiz:** Strings literais `"Abrir /stage"` e `"Recado Exclusivo ao Púlpito (/stage)"` visíveis ao operador.
- **Solução planejada:** Alterar para `"Abrir Monitor de Palco"` e `"Recado Exclusivo ao Púlpito"`.
