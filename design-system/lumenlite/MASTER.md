# Design System: LumenLite (Slate Pro Studio)

Este documento define a especificação oficial de design tokens, paleta e regras de UI/UX para o **LumenLite**.
Projetado sob a filosofia **Slate Pro**, oferece uma interface sóbria, elegante e de alta legibilidade, suportando tanto **Dark Mode** suave (sem o preto OLED agressivo) quanto **Light Mode** técnico.

---

## 1. Tokens de Cores (Paleta Slate & Neutral Studio)

### Dark Mode (Padrão de Cabine - Slate Suave)

```css
:root[data-theme="dark"] {
  /* Superfícies */
  --bg-app: #0F172A;          /* Slate 900: base de fundo sem ofuscamento */
  --bg-surface: #1E293B;      /* Slate 800: cartões e painéis laterais */
  --bg-elevated: #334155;     /* Slate 700: headers, barras e menus */
  --bg-hover: #475569;        /* Slate 600: hover sutil */

  /* Bordas sutis */
  --border-subtle: rgba(255, 255, 255, 0.08);
  --border-strong: rgba(255, 255, 255, 0.16);

  /* Textos */
  --text-primary: #F8FAFC;    /* Slate 50: leitura nítida e confortável */
  --text-secondary: #94A3B8;  /* Slate 400: subtítulos e informações secundárias */
  --text-muted: #64748B;      /* Slate 500: atalhos e números de slide */

  /* Estados de Projeção & Interação (Sem "semáforo") */
  --item-active-bg: rgba(51, 65, 85, 0.7);    /* Fundo sutil para o slide no telão */
  --item-active-border: #94A3B8;              /* Linha lateral fina e discreta */
  --item-focus-ring: #38BDF8;                 /* Anel de foco fino para navegação por teclado */

  /* Acento e Ações */
  --accent: #38BDF8;          /* Sky 400: destaque pontual elegante */
  --accent-hover: #0EA5E9;
  --destructive: #EF4444;     /* Vermelho sóbrio para pânico */
}
```

### Light Mode (Ambiente Iluminado / Estudo / Planejamento)

```css
:root[data-theme="light"] {
  /* Superfícies */
  --bg-app: #F8FAFC;          /* Slate 50: fundo de papel limpo e calmo */
  --bg-surface: #FFFFFF;      /* Branco puro: cartões e áreas de trabalho */
  --bg-elevated: #F1F5F9;     /* Slate 100: headers e barras */
  --bg-hover: #E2E8F0;        /* Slate 200: hover refinado */

  /* Bordas */
  --border-subtle: #E2E8F0;
  --border-strong: #CBD5E1;

  /* Textos */
  --text-primary: #0F172A;    /* Slate 900: alto contraste sem cansaço */
  --text-secondary: #475569;  /* Slate 600 */
  --text-muted: #94A3B8;      /* Slate 400 */

  /* Estados de Projeção & Interação */
  --item-active-bg: #E2E8F0;
  --item-active-border: #475569;
  --item-focus-ring: #0284C7;

  /* Acento e Ações */
  --accent: #0284C7;          /* Sky 600 */
  --accent-hover: #0369A1;
  --destructive: #DC2626;
}
```

---

## 2. Tipografia & Escala de Espaçamento

- **Família Tipográfica Primária:** `Inter` ou `Plus Jakarta Sans`
- **Monospaçado (Atalhos, Métricas e Relógio):** `JetBrains Mono` com números tabulares (`font-feature-settings: "tnum"`).
- **Escala de Espaçamento (8dp Rhythm):**
  - `--space-1`: 4px
  - `--space-2`: 8px
  - `--space-3`: 12px
  - `--space-4`: 16px
  - `--space-6`: 24px
- **Bordas Arredondadas:** `--radius-sm`: 4px, `--radius-md`: 6px, `--radius-lg`: 8px.

---

## 3. Diretriz de Ícones: Lucide Icons (`lucide-react`)

Apenas ícones vetoriais da biblioteca **Lucide** devem ser utilizados, mantendo traço padronizado (`strokeWidth={1.5}` ou `strokeWidth={2}`):

| Ação / Módulo | Componente Lucide | Finalidade |
|---|---|---|
| **Blackout (Pânico)** | `<EyeOff size={16} />` | Apaga o telão instantaneamente |
| **Limpar Texto** | `<FileText size={16} />` | Remove o texto mantendo o fundo |
| **Limpar Fundo** | `<ImageOff size={16} />` | Remove o fundo mantendo o texto |
| **Congelar Projeção** | `<Snowflake size={16} />` | Congela a saída do telão |
| **Bíblia** | `<BookOpen size={16} />` | Navegação nas escrituras |
| **Músicas / Letras** | `<Music size={16} />` | Acervo de louvores |
| **Mídias & Vídeos** | `<Film size={16} />` | Loops e imagens de fundo |
| **Apresentações PPTX** | `<Presentation size={16} />` | Slides e oratória |
| **Áudio / Playback** | `<Volume2 size={16} />` | Player e trilhas ambientes |
| **Quick Text** | `<Sparkles size={16} />` | Projeção rápida de texto avulso |
| **Automação (DAG)** | `<Workflow size={16} />` | Editor de nós e gatilhos |
| **Configurações** | `<Settings size={16} />` | Preferências do sistema |

---

## 4. Filosofia de Apresentação de Slides (Leitura Limpa)

- **Eliminação de Rótulos Pesados:** As estrofes não exibem tags visuais chamativas como `[V1]`, `[CORO]` ou `[PONTE]`. O operador lê a letra de forma pura e natural.
- **Numeração Mínima Discreta:** Cada estrofe exibe apenas um número sutil no canto superior direito (`1`, `2`, `3...`) em cor atenuada (`text-muted`).
- **Feedback Elegante:** O slide ativo no ar recebe uma borda lateral fina (`border-l-2 border-slate-400`) e um preenchimento tonal suave (`bg-surface/80`), sem bordas verdes brilhantes ou efeitos piscantes.
