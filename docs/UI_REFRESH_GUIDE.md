# Guia de Refinamento Visual e UI Refresh — LumenLite (v1.4.0)

Este guia consolida o padrão de design visual **Slate Pro Studio Refined**, estabelecendo a evolução estética do LumenLite a partir da versão `v1.4.0`. O objetivo deste refinamento é eliminar o excesso de caixas ("boxiness"), resolver conflitos cromáticos e ampliar a legibilidade e o respiro espacial da cabine de projeção.

---

## 1. Análise: Pontos Positivos, Pontos Negativos e Solução

### Pontos Positivos Mantidos
- **Arquitetura Espacial em 4 Colunas:** Fluxo intuitivo *Preparar (Esquerda) -> Operar (Centro) -> Monitorar (Direita)* com proporções ergonomicamente validadas para telas Full HD e Ultrawide.
- **Interação Rápida sem Cliques Perdidos:** Atalhos globais imediatos (`Ctrl+K` para Spotlight, `F1`/`F2`/`F3` para Pânico, Setas de Teclado para avanço de estrofes).
- **Semantismo Claro de Estados:** Distinção inequívoca entre o que está em **Foco Local / Preview** (Azul Céu) versus o que está **Ao Vivo no Telão** (Esmeralda).

### Pontos Negativos Anteriores ("Antes do Refresh")
- **Excesso de Bordas e Caixas ("Boxiness"):** Cards envolvidos em bordas rígidas sobrepostas com contraste excessivo, criando fadiga visual durante cultos e apresentações prolongadas.
- **Ruído Repetitivo em Listas:** Ícones de nota musical (`<Music />`) repetidos em cada linha do catálogo de músicas, consumindo largura útil e competindo com a leitura do título.
- **Falta de Respiro Vertical nas Estrofes:** Estrofes com padding e gap apertados, prejudicando o escaneamento visual rápido pelo operador sob pressão.
- **Contraste Rígido no Preview do Telão:** Anel verde vibrante demarcando a saída 16:9, que desviava a atenção do conteúdo projetado e criava ruído no monitor de retorno.
- **Botões e Abas Desconectados:** Controles de pânico e abas da coluna esquerda dispersos em botões individuais sem agrupamento semântico.

### Soluções Implementadas
- **Elevação por Luminosidade (Surface Elevation by Luminance):** A profundidade visual agora é construída clareando ligeiramente as camadas superficiais em direção ao operador, substituindo bordas pesadas por variações sutis de tom (`#0B0D10` -> `#11141A` -> `#141820` / `#181C24`).
- **Segmented Controls Tipo macOS / Raycast:** Abas da coluna esquerda e alternadores de mídia unificados em pílulas únicas com transição deslizante suave.
- **Tipografia Tabular Monotípica (`tabular-nums font-mono`):** Alinhamento vertical perfeito para números de estrofes (`00`, `01`, `02`...), contadores de tempo decorrido e atalhos `<kbd>`.
- **Sombra Profunda e Suave:** Preview do telão agora utiliza `shadow-2xl shadow-black/90` com microborda de 5% de opacidade (`border-white/5`), conferindo acabamento cinematográfico sem bordas fluorescentes.

---

## 2. Teoria das Cores e Hierarquia de Superfícies

A paleta de superfícies adota uma escala de luminosidade escura contínua, onde elementos no mesmo plano de interação compartilham a mesma profundidade:

| Nível de Elevação | Código Hex | Aplicação no Layout | Classes Tailwind Recomendadas |
|---|---|---|---|
| **Nível 0 (Base do App)** | `#0B0D10` | Fundo principal da aplicação (`OperatorView`) e canaletas dos segmented controls | `bg-[#0B0D10]` |
| **Nível 1 (Painéis & Header)** | `#11141A` | Barra superior (`OperatorTopBar`), coluna esquerda e coluna direita | `bg-[#11141A] border-white/5` |
| **Nível 2 (Cards & Superfícies Ativas)** | `#141820` / `#181C24` | Cards de estrofes inativas, cards do roteiro, inputs de busca, player de áudio | `bg-[#141820] hover:bg-[#181C24]` |
| **Nível 2 Hover (Interatividade)** | `#1F242F` | Hover de botões, itens de menu e cards secundários | `hover:bg-[#1F242F]` |

---

## 3. Cores Semânticas de Operação

### 3.1. Estado em Foco / Workspace (Azul Céu)
Utilizado para indicar o item ou estrofe selecionado no workspace local, aguardando envio para o telão:
- **Fundo:** `#18202C`
- **Borda & Anel:** `border-sky-500/30 ring-1 ring-sky-500/40`
- **Barra Lateral Indicadora:** `w-[3px] bg-sky-500 rounded-l-lg`
- **Texto:** `text-slate-100 font-semibold`

### 3.2. Estado Ao Vivo no Telão (Esmeralda Profunda)
Utilizado exclusivamente para indicar o item ou estrofe que está sendo projetado para a congregação:
- **Fundo:** `bg-emerald-950/25` (translúcido profundo, sem saturação agressiva)
- **Borda & Anel:** `border-emerald-500/70 ring-1 ring-emerald-500/40`
- **Barra Lateral Indicadora:** `w-[3px] bg-emerald-500 rounded-l-lg`
- **Ponto Indicador:** `h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_8px_rgba(16,185,129,0.7)]`
- **Badge:** `AO VIVO` em `bg-emerald-500/20 text-emerald-300 font-mono text-[9px] font-bold`
- **Texto:** `text-white font-medium`

### 3.3. Barra Segmentada de Pânico (`PanicToolbar`)
Os três controles de emergência ficam unificados em um *segmented button group* (`rounded-lg bg-[#141820] p-0.5 border border-white/5`):
- **Inativo (Ghost):** `text-slate-400 hover:text-slate-200 hover:bg-white/5 px-2.5 py-1 text-[11px]`
- **Blackout Ativo (F1):** Acende em Carmim/Vermelho (`bg-rose-500/20 text-rose-300 ring-1 ring-rose-500/50`)
- **Ocultar Letra Ativo (F2):** Acende em Âmbar (`bg-amber-500/20 text-amber-300 ring-1 ring-amber-500/50`)
- **Congelar Ativo (F3):** Acende em Ciano/Azul Céu (`bg-cyan-500/20 text-cyan-300 ring-1 ring-cyan-500/50`)

---

## 4. As 5 Regras de Ouro do Design System

1. **Elevação por Luminosidade, Não por Contorno:**  
   Nunca use bordas de 1px com alta opacidade (`border-slate-700` ou `border-white/20`) para separar áreas adjacentes. Em vez disso, use degraus sutis na escala de cinza neutro (`#0B0D10` -> `#11141A` -> `#141820`) delimitados por `border-white/5`.

2. **Segmented Controls Unificados para Seleção de Modo:**  
   Abas, filtros de visualização e grupos de botões interdependentes devem residir em um container em pílula única com fundo Nível 0 (`bg-[#0B0D10]/80`) e espaçamento de 2 a 4px. A aba ativa se destaca com fundo Nível 2 e texto com peso médio/semi-bold.

3. **Eliminação de Ruído Visual Repetitivo:**  
   Em listas com dezenas de itens similares (ex: lista de hinos ou versículos), remova ícones idênticos repetidos em cada linha. Deixe a largura horizontal dedicada à tipografia e ao alinhamento do texto.

4. **Alinhamento Numérico com Fontes Tabulares (`tabular-nums font-mono`):**  
   Todo cronômetro, contador de tempo, numeração sequencial de estrofes (`00`, `01`, `02`...) e atalho de teclado (`<kbd>`) deve utilizar `tabular-nums font-mono` para evitar que a interface trema ou mude de largura durante a contagem.

5. **Restrição Semântica das Cores de Ação:**  
   Cores vivas (Esmeralda, Vermelho, Âmbar e Azul Céu) pertencem estritamente a estados de dados ativos:
   - Azul = Foco e Navegação.
   - Esmeralda = Projeção Ao Vivo.
   - Vermelho / Âmbar / Ciano = Modos de Pânico e Contingência.  
   Nenhuma cor saturada deve ser usada para fins puramente decorativos em planos de fundo ou divisórias.
