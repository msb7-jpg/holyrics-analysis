# BUG-003 — Permitir redimensionar livremente abas, colunas e seções

- **Status:** Closed

- **Severidade:** Melhoria / UX | **Tipo:** Layout / Ergonomia | **Fase:** 2
- **Problema:** As colunas e seções da tela do operador possuem largura e altura fixas, impedindo o operador de aumentar ou diminuir o tamanho do painel esquerdo (roteiro/mídias), do palco central ou do monitor de saída à direita conforme sua necessidade.
- **Arquivos envolvidos:**
  - [OperatorView.tsx](../../new-holyrics/apps/ui/src/operator/OperatorView.tsx#L143-L249)
  - [OutputMonitorPanel.tsx](../../new-holyrics/slices/navigation/ui/components/OutputMonitorPanel.tsx#L35-L114)
- **Causa-raiz:** As colunas usam classes CSS Grid estáticas (`lg:grid-cols-[20fr_22fr_33fr_25fr]`) sem alças de arraste (`SplitPane`) e sem persistência de largura/altura.
- **Solução planejada:** Implementar componente `ResizableSplitPane` (divisórias verticais entre colunas esquerda/centro/direita e divisória horizontal no painel de saída direito entre o monitor 16:9 e Áudio/Fundos) com persistência em `localStorage`.
