# Reference: React 19, Hook Discipline, and Performance

This guide gathers compliance rules for the React 19 compiler, eliminating unnecessary effects, and best practices with `@reactuse/core`.

---

## 1. Core React 19 Rules

1. **No `React.FC` or `React.FunctionComponent`:**
   Declare components as standard JavaScript functions with explicitly typed props:
   ```tsx
   // ❌ BAD:
   export const MyCard: React.FC<MyCardProps> = ({ title }) => { ... }

   // ✅ GOOD:
   export function MyCard({ title, active = false }: MyCardProps) { ... }
   ```
2. **No `forwardRef`:**
   In React 19, `ref` is a native first-class prop in function components:
   ```tsx
   export function CustomInput({ ref, ...props }: CustomInputProps) {
     return <input ref={ref} {...props} />
   }
   ```
3. **No `<Context.Provider>`:**
   Render the context directly as a JSX element:
   ```tsx
   <ThemeContext value={currentTheme}>
     {children}
   </ThemeContext>
   ```

---

## 2. "You Might Not Need an Effect" (Preventing Cascading Renders)

- **Never use `useEffect` to synchronize state derived from props:**
  If a value can be computed from props or existing state, calculate it directly during rendering (using plain variables, or `useMemo` only if the calculation involves heavy computations).
- A `useEffect` that calls `setState` causes an immediate second paint on the screen, introduces visual flickering, and disrupts optimizations in the new React compiler.

---

## 3. Discipline in `useCallback` Usage

- Functions passed to native elements such as `<button onClick={handleClick}>` **do not need `useCallback`**. Browsers handle raw event handlers efficiently.
- Use `useCallback` **only** when:
  1. The function is passed down to a component wrapped with an explicit `React.memo`.
  2. The function is consumed by the dependency array (`dependencies`) of another hook that requires strict referential stability.

---

## 4. Utilitários Modernos com `@siberiacancode/reactuse`

Antes de escrever um `useEffect` com timers, listeners de janela ou observadores manuais, consulte as primitivas consolidadas do [`@siberiacancode/reactuse`](https://reactuse.org/):

- **Timers e Debounce:** `useDebounceValue`, `useDebounceCallback`, `useTimeout`, `useInterval`.
- **Sensores e Eventos do Navegador:** `useEventListener`, `useIntersectionObserver`, `useClickOutside`, `useResizeObserver`.
- **Estado e Alternância:** `useToggle`, `usePrevious`, `useLatest`.

> [!TIP]
> O pacote `@siberiacancode/reactuse` possui zero dependências de terceiros e suporte nativo ao React 19.

---

## 5. Gerenciamento de WebSocket com `useSyncExternalStore`

Nunca instancie conexões WebSocket dentro de `useEffect` em componentes visuais. Em vez disso:
1. Implemente uma classe store desacoplada do React (`LumenWebSocketStore`).
2. Conecte o estado com `useSyncExternalStore` (`useScreenState()`, `useWsConnected()`).
3. Elimine problemas de duplo disparo do React 19 `StrictMode` e evite conexões duplicadas.

---

## 5. Official References and Further Reading

- [React 19 Official Release Announcement](https://react.dev/blog/2024/12/05/react-19)
- [You Might Not Need an Effect (React Official Docs)](https://react.dev/learn/you-might-not-need-an-effect)
- [React Compiler Working Model](https://react.dev/learn/react-compiler)
- [@reactuse/core Documentation](https://reactuse.org/)
