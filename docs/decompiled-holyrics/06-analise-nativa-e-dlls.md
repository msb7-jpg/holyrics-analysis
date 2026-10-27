# 06 - Análise de Binários Nativos e DLLs

Além do código Java descompilado em `src/`, o Holyrics utiliza bibliotecas de link dinâmico nativas do Windows (`.dll`) empacotadas no executável para comunicação direta com o sistema operacional e persistência de alto desempenho.

Todas as DLLs foram extraídas e encontram-se disponíveis em:
```
workspace/holyrics-analysis/extracted_dlls/
```

---

## 📑 Inventário de Bibliotecas Nativas

| DLL | Caminho Relativo | Arquitetura | Finalidade |
|---|---|---|---|
| `sqlitejdbc.dll` | `org/sqlite/native/Windows/x86_64/` | Windows PE x86_64 (64-bit) | Motor de banco de dados SQLite compilado em C (JNI) |
| `sqlitejdbc.dll` | `org/sqlite/native/Windows/x86/` | Windows PE x86 (32-bit) | Versão 32 bits de compatibilidade para SQLite |
| `jnidispatch.dll` | `com/sun/jna/win32-amd64/` | Windows PE x86_64 (64-bit) | Ponte nativa JNA para chamadas da API Win32 |
| `jnidispatch.dll` | `com/sun/jna/win32-x86/` | Windows PE x86 (32-bit) | Ponte nativa JNA para sistemas 32 bits |
| `jnidispatch.dll` | `com/sun/jna/w32ce-arm/` | Windows CE ARM | Build legado de compatibilidade |

---

## 🔍 Detalhamento das Funções Nativas

### 1. `sqlitejdbc.dll` (Motor SQLite)
- **Papel:** Em vez de usar uma reimplementação Java de SQLite, o driver Xerial utiliza uma biblioteca compilada diretamente do código fonte em C do SQLite para garantir performance máxima em leituras e escrita em disco.
- **Assinatura JNI:** Exporta símbolos nativos que correspondem diretamente aos métodos nativos Java da classe `org.sqlite.core.NativeDB`:
  - `Java_org_sqlite_core_NativeDB__1open_1utf8`: Abertura da base `holyrics.db`.
  - `Java_org_sqlite_core_NativeDB_exec_1utf8`: Execução direta de comandos SQL DDL/DML.
  - `Java_org_sqlite_core_NativeDB_prepare_1utf8`: Preparação de *PreparedStatements*.
  - `Java_org_sqlite_core_NativeDB_step`: Navegação iterativa pelas linhas dos *ResultSets*.

### 2. `jnidispatch.dll` (Ponte Win32 via JNA)
- **Papel:** O Java padrão não provê controle avançado sobre a barra de tarefas do Windows, identificação de múltiplos monitores físicos e posicionamento absoluto sem bordas.
- **Chamadas de Baixo Nível:** Através de `jnidispatch.dll`, o Holyrics invoca funções de `user32.dll`, `kernel32.dll` e `gdi32.dll`:
  - Posicionamento da janela do projetor em modo `WS_POPUP` cobrindo exatamente as coordenadas da segunda placa de vídeo (`EnumDisplayMonitors`).
  - Prevenção de suspensão de tela ou descanso de tela do Windows enquanto o culto estiver ativo (`SetThreadExecutionState(ES_CONTINUOUS | ES_DISPLAY_REQUIRED)`).
  - Captura de teclas globais de atalho mesmo quando a janela do Holyrics não estiver em primeiro plano.

---

## 🛠 Como Inspecionar as DLLs com Ghidra e REA

O ambiente já está completamente configurado com **Ghidra 12.1.4** e **REA 6.8.0**.

### 1. Inspeção Rápida de Cabeçalhos e Seções com REA:
```bash
GHIDRA_INSTALL_DIR="$HOME/tools/ghidra_12.1.4_PUBLIC" \
rea inspect-artifact extracted_dlls/org/sqlite/native/Windows/x86_64/sqlitejdbc.dll
```

### 2. Análise Profunda no Ghidra (Interface Gráfica):
Para abrir o Ghidra com desassemblador interativo, grafo de funções e descompilador C/C++:
```bash
$HOME/tools/ghidra_12.1.4_PUBLIC/ghidraRun
```
Basta criar um novo projeto (ex: `holyrics-native`), arrastar a DLL desejada para dentro da janela e executar a auto-análise.

### 3. Análise em Modo Headless (Linha de Comando):
Caso deseje gerar relatórios em lote sem interface gráfica:
```bash
$HOME/tools/ghidra_12.1.4_PUBLIC/support/analyzeHeadless \
  /tmp/ghidra_proj TempProject \
  -import extracted_dlls/org/sqlite/native/Windows/x86_64/sqlitejdbc.dll
```
