# BUG-020 — Texto `"Zero App Store"` vazando detalhe irrelevante no modal de controle remoto

- **Status:** Closed

- **Severidade:** Médio | **Tipo:** Poluição / UX | **Fase:** 5
- **Problema:** O título do modal exibe `"Pareamento Mobile via QR Code (Zero App Store)"`, incluindo uma expressão promocional/técnica que não agrega clareza ao operador.
- **Arquivos envolvidos:**
  - [RemotePairingModal.tsx](../../new-holyrics/slices/remote/ui/components/RemotePairingModal.tsx#L121-L123)
- **Causa-raiz:** Título hardcoded `"Pareamento Mobile via QR Code (Zero App Store)"`.
- **Solução planejada:** Alterar para `"Controle Remoto Mobile"`.
- **Mídia ([07-remote-control-pairing-modal.png](../media/07-remote-control-pairing-modal.png)):** veja preview em [BUG-19](#bug-19--configura%C3%A7%C3%B5es-do-servidor-remoto-ligardesligar-startup-sele%C3%A7%C3%A3o-de-ip-lan--0000-e-qr-code-real).
