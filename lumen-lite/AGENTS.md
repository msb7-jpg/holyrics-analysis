# Guia de Engenharia e Regras de Desenvolvimento: LumenLite

Este repositório implementa o **LumenLite**, a modernização arquitetural e visual de alta performance do Holyrics.

## Princípios Obrigatórios

1. **Zero YAML:**
   - Toda configuração, roteiro litúrgico (`culto.json`), mapeamento de atalhos (`keybindings.json`) e automações (`hooks.json`) é estritamente **JSON** com validação nativa **Zod v4** e suporte ao `$schema` (Draft 2020-12 / Draft 7).
2. **Arquitetura Vertical de Fatias (`slices/*`):**
   - Cada fatia é autossuficiente e possui subpastas canônicas:
     - `src/contracts/`: Tipos, interfaces e schemas Zod.
     - `src/daemon/`: Serviços de backend, acesso ao SQLite ou comunicação em rede (Bun).
     - `src/ui/`: Componentes React 19 específicos da fatia.
3. **Núcleo Imutável (`packages/core`):**
   - A máquina de estados da projeção (FSM) é um Reducer puro `(State, Action) => State`.
   - Zero efeitos colaterais dentro do reducer. O despachante e o EventBus coordenam as transições de forma determinística.
4. **Design System "Slate Pro Studio":**
   - Paleta sóbria em tons de ardósia (Slate 900 / Slate 50).
   - Não utilizar cores berrantes de semáforo. Letras de hinos devem ser exibidas limpas, sem badges intrusivos `[CORO]` ou `[V1]`.
   - Seguir os tokens definidos em `design-system/lumenlite/MASTER.md`.
5. **Código Legado:**
   - As pastas `src/com/` e `extracted_*` são exclusivamente para referência de engenharia reversa. Nunca alterar ou misturar novo código nelas.
