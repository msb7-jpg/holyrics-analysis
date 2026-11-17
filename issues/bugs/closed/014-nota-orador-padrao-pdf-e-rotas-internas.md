# BUG-014 — `"NOTA DO ORADOR: Documento PDF importado."` por padrão e menções a `/stage` e `/remote/preacher`

- **Status:** Closed

- **Severidade:** Médio | **Tipo:** UX / Vazamento de Detalhes Internos | **Fase:** 4
- **Problema:** Ao importar um arquivo PDF, surge automaticamente um bloco confuso com o aviso `"NOTA DO ORADOR (VISÍVEL NO PALCO E CELULAR DO PASTOR): Documento PDF importado."` sem que o usuário tenha inserido nenhuma nota. Além disso, o subtítulo do modal expõe rotas técnicas internas (`/stage` e `/remote/preacher`) que não agregam valor ao operador.
- **Arquivos envolvidos:**
  - [pptx-extractor.ts](../../new-holyrics/slices/pptx/daemon/pptx-extractor.ts#L186-L193)
  - [PptxDeckModal.tsx](../../new-holyrics/slices/pptx/ui/components/PptxDeckModal.tsx#L174-L214)
- **Causa-raiz:** `pptx-extractor.ts#L191` insere `speakerNotes: 'Documento PDF importado.'` por padrão quando não encontra linhas via regex, e `PptxDeckModal.tsx#L175` exibe `(/stage e /remote/preacher)` diretamente ao operador.
- **Solução planejada:** Remover o texto padrão `speakerNotes: 'Documento PDF importado.'` (deixar `speakerNotes: ''` quando não houver notas reais) e substituir a frase técnica por *"Clique em qualquer slide para projetar no telão"*.
- **Mídia ([02-apresentar-pdf-iniciar-apresentacao.png](../media/02-apresentar-pdf-iniciar-apresentacao.png)):** veja preview em [BUG-12](#bug-12--apresenta%C3%A7%C3%A3o-de-pdfpptx-mostra-apenas-texto-sobre-fundo-ativo-em-vez-do-documento-real).
