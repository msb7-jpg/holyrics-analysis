# 05. Especificação de Design & UI/UX: LumenLite

Este documento consolida a especificação visual e a experiência de usuário do **LumenLite**, derivada dos requisitos de [01-requirements.md](file:///home/miguelsb/workspace/holyrics-analysis/docs/new-holyrics/01-requirements.md) e alinhada à direção de design **Slate Pro Studio**.

---

## 1. Filosofia de Design & Diretrizes de Usabilidade

A interface foi concebida para oferecer alta produtividade, máxima legibilidade e conforto visual, afastando-se tanto de cores estressantes ("luzes de semáforo") quanto de pretos puros de OLED que causam contraste excessivo.

### Pilares Fundamentais:
1. **Paleta Slate Pro (Neutro Técnico):** Fundo suave em tons de ardósia e grafite (`Slate 900` / `#0F172A`), com suporte equilibrado tanto a Dark Mode quanto a Light Mode (`Slate 50` / `#F8FAFC`).
2. **Alta Densidade sem Poluição:** Informações críticas visíveis em um painel de 4 colunas redimensionáveis, sem diálogos modais bloqueando o telão.
3. **Feedback Visual Sóbrio e Discreto (Sem "Semáforo"):**
   - **Slide no Ar:** Fundo ligeiramente elevado com uma linha lateral fina de 2px à esquerda e badge minimalista `No ar`.
   - **Foco de Teclado:** Anel fino de 1px a 2px (`ring-1 ring-slate-400/50`).
   - **Ações de Emergência:** Apenas alertas em vermelho sóbrio (`#EF4444` / `bg-red-500/10`) quando o Blackout estiver efetivamente acionado.
4. **Leitura Pura e Natural das Letras:** Eliminação de rótulos chamativos como `[V1]`, `[CORO]` e `[PONTE]`. As estrofes são exibidas como blocos tipográficos limpos com numeração discreta (`1`, `2`, `3...`).
5. **Iconografia Padronizada via Lucide Icons:** Consistência visual e traço vetorial profissional de 1.5px/2px em toda a aplicação.

---

## 2. Tokens de Design (Slate Pro)

Os tokens detalhados foram documentados em [`design-system/lumenlite/MASTER.md`](file:///home/miguelsb/workspace/holyrics-analysis/design-system/lumenlite/MASTER.md).

```css
:root[data-theme="dark"] {
  --bg-app: #0F172A;          /* Slate 900 */
  --bg-surface: #1E293B;      /* Slate 800 */
  --bg-elevated: #334155;     /* Slate 700 */
  --bg-hover: #475569;        /* Slate 600 */

  --border-subtle: rgba(255, 255, 255, 0.08);
  --border-strong: rgba(255, 255, 255, 0.16);

  --text-primary: #F8FAFC;    /* Slate 50 */
  --text-secondary: #94A3B8;  /* Slate 400 */
  --text-muted: #64748B;      /* Slate 500 */

  --item-active-bg: rgba(51, 65, 85, 0.7);
  --item-active-border: #94A3B8;
  --accent: #38BDF8;          /* Sky 400 */
}

:root[data-theme="light"] {
  --bg-app: #F8FAFC;          /* Slate 50 */
  --bg-surface: #FFFFFF;
  --bg-elevated: #F1F5F9;     /* Slate 100 */
  --bg-hover: #E2E8F0;        /* Slate 200 */

  --border-subtle: #E2E8F0;
  --border-strong: #CBD5E1;

  --text-primary: #0F172A;    /* Slate 900 */
  --text-secondary: #475569;  /* Slate 600 */
  --text-muted: #94A3B8;      /* Slate 400 */

  --item-active-bg: #E2E8F0;
  --item-active-border: #475569;
  --accent: #0284C7;          /* Sky 600 */
}
```

---

## 3. Console do Operador (Layout 4-Split)

A interface de desktop (Tauri v2 + React 19) utiliza um console dividido em 4 colunas verticais redimensionáveis (*Split Panes*):

```
+---------------------------------------------------------------------------------------------------------+
| [TOP BAR] LumenLite | Telão: HDMI-1 (1080p@60Hz) | OBS: Conectado (ws://127.0.0.1:4040) | 19:42:10      |
| [AÇÕES RÁPIDAS]: [ EyeOff: Blackout ] [ FileText: Ocultar Letra ] [ Snowflake: Congelar ] | 🔍 Cmd+K     |
+---------------------------------------------------------------------------------------------------------+
| COLUNA 1 (22%)     | COLUNA 2 (24%)     | COLUNA 3 (32%)               | COLUNA 4 (22%)                 |
| Roteiro do Culto   | Acervo & Pesquisa  | Estrofes / Slides da Música  | Monitor do Telão & Áudio       |
| (culto.json)       | Instantânea (FTS5) | (Leitura Limpa Tipográfica)  | Saída em Tempo Real            |
|--------------------+--------------------+------------------------------+--------------------------------|
| • Oração Inicial   | [🔍 jo 3 16 / hino]| Bondade de Deus              | [MONITOR DO TELÃO]             |
| • Louvor: Grande   | ------------------ | ---------------------------- | +----------------------------+ |
| • Louvor: Bondade  | 📖 Bíblia          | Te amo, ó Deus               | |      TE AMO, Ó DEUS        | |
|   de Deus          | 🎵 Músicas         | Tua graça nunca falha...     | |    TUA GRAÇA NUNCA...      | |
| • Pregação Pr.     | 📢 Avisos          |                              | +----------------------------+ |
| • Santa Ceia       | 🖼️ Mídias          | [No ar]                    1 | 1920x1080 @ 60 FPS             |
|                    | ✨ Quick Text      | ---------------------------- | ------------------------------ |
|                    |                    | Com Sua voz Tu me guias      | [PRÓXIMO SLIDE]                |
|                    |                    | Pelo fogo em Tua presença... | Com Sua voz Tu me guias        |
|                    |                    |                            2 | Pelo fogo em Tua presença...   |
|                    |                    | ---------------------------- | ------------------------------ |
|                    |                    | Tua fidelidade é grande      | [ÁUDIO AMBIENTE]               |
|                    |                    | Tua fidelidade é grande...   | Volume: -12dB [Crossfade]      |
|                    |                    |                            3 | Faixa: Instrumental Pré-Culto  |
+--------------------+--------------------+------------------------------+--------------------------------+
| [STATUS BAR] ⌨️ Espaço: Avançar | ⌫/Esc: Retornar | F5: Bíblia | DAG: 4 nós ativos | 0.8ms IPC           |
+---------------------------------------------------------------------------------------------------------+
```

### Detalhes das Colunas:
- **Coluna 1 (Roteiro Litúrgico):** Itens do `culto.json`. Drag & Drop universal para reordenar louvores ou soltar novos arquivos de mídia (RF-22C) e Menu de Contexto (RF-22B).
- **Coluna 2 (Acervo & Busca Instantânea):** Campo de busca único com micro-parser bíblico determinístico (ex: digita `jo 3 16` $\rightarrow$ abre João 3:16) e busca FTS5 instantânea em músicas. Abas com ícones Lucide (`BookOpen`, `Music`, `Film`, `Sparkles`).
- **Coluna 3 (Grade de Slides Tipográfica):** Apresentação das estrofes com foco total no texto. Sem rótulos como `[V1]`. O slide ativo tem um indicador sutil à esquerda e numeração discreta.
- **Coluna 4 (Monitor & Áudio):** Canvas acelerado por GPU espelhando a projeção real, visão do próximo slide para antecipação e deck de controle de áudio com equalizador (RF-28).

---

## 4. Telas Auxiliares & Remotos

1. **Monitor de Palco (`/stage` - RF-02):** Foco em alto contraste neutro no púlpito, relógio com números tabulares, cronômetro de oratória e notas do orador extraídas de PPTX/PDF (RF-21).
2. **OBS Studio (`/broadcast` - RF-23):** Canal alfa 100% transparente com transições sutis em `motion/react` (fade + 12px de elevação suave em 300ms) e alertas de rodapé.
3. **Web Remote do Pastor (`/remote/preacher` - RF-26A):** Interface web móvel limpa com botões de toque generosos para avanço/recuo, cronômetro e anotações.
4. **Console Web do Operador (`/remote/operator` - RF-26B):** PWA leve pareada via QR Code com controle volante dos itens da liturgia e botões de pânico.

---

## 5. Mapeamento de Atalhos de Teclado Clássicos

| Tecla / Atalho | Ação | Ícone Lucide | Efeito na Projeção |
|---|---|---|---|
| `Espaço` / `↓` | Avançar Slide | `ChevronDown` | Avança para a próxima estrofe |
| `Backspace` / `↑` | Voltar Slide | `ChevronUp` | Retorna à estrofe anterior |
| `Esc` | Return Stack (RF-20) | `Undo2` | Retorna imediatamente ao louvor após versículo de improviso |
| `F1` / `B` | **Blackout** (RF-03) | `EyeOff` | Corta o telão para preto com fade suave de áudio |
| `F2` / `T` | **Clear Text** (RF-03) | `FileText` | Oculta apenas o texto, mantendo o fundo |
| `F3` / `C` | **Clear Background** (RF-03) | `ImageOff` | Oculta o fundo, preservando a letra |
| `F4` / `F` | **Freeze** (RF-04) | `Snowflake` | Congela o telão para pesquisas discretas |
| `Cmd + K` / `Ctrl + K` | **Spotlight** | `Search` | Busca unificada em milissegundos |
| `Ctrl + T` | **Quick Text** (RF-22A) | `Sparkles` | Projeção rápida de texto avulso |
