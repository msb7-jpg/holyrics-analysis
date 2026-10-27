# 01 - Metodologia de Engenharia Reversa

Este documento formaliza a metodologia técnica utilizada para inspecionar, descompactar, extrair e descompilar o aplicativo **Holyrics**. A abordagem combina análise de executáveis nativos Windows (PE32), extração de contêineres e descompilação de bytecode Java.

---

## 🧭 Visão Geral do Ciclo de Engenharia Reversa

A análise de uma aplicação híbrida (wrapper nativo Windows + bytecode Java embutido + bibliotecas C/C++ compartilhadas) segue um fluxo em 6 etapas:

```
[ Instalador Inno Setup (.exe) ]
               │
               ▼  1. Desempacotamento de contêiner (innoextract)
[ Holyrics.exe (PE32 Wrapper) ]
               │
               ▼  2. Identificação de payload e offset (7-Zip / file / strings)
[ Arquivo JAR/ZIP Embutido ]
         ┌─────┴────────────────────────┐
         ▼                              ▼
3. Classes de Terceiros        3. Bytecode Proprietário
   (WebLaF, VLCJ, JavaFX)         (com.limagiran.*)
                                        │
                                        ▼  4. Descompilação de alta fidelidade (CFR)
                                 [ Código-Fonte .java (src/) ]
                                        │
                                        ▼  5. Mapeamento nativo (Ghidra & REA)
                                 [ DLLs (sqlitejdbc, jnidispatch) ]
                                        │
                                        ▼  6. Síntese Arquitetural
                                 [ Documentação e Diagramas ]
```

---

## Etapa 1: Descoberta e Triagem do Instalador

O ponto de partida foi o arquivo de instalação distribuído aos usuários finais: `holyrics-setup-2.2.0.exe`.

1. **Inspeção de cabeçalhos binários:**
   Executáveis de instalação modernos costumam ser criados com NSIS ou Inno Setup. A busca por strings nos primeiros megabytes do instalador revelou a assinatura:
   ```
   Inno Setup Setup Data (5.5.7)
   ```
2. **Extração sem emulação Windows:**
   Em vez de executar o instalador sob Wine ou em máquina virtual, utilizou-se o `innoextract` estático (versão 1.9), que faz a leitura declarativa dos blocos comprimidos e extrai o arquivo final sem alterar o registro ou estado do sistema operacional:
   ```bash
   innoextract -e -I "app/Holyrics.exe" "holyrics-setup-2.2.0.exe"
   ```

---

## Etapa 2: Análise da Estrutura Binária do Executável

O arquivo gerado `app/Holyrics.exe` (71 MB) aparentava ser um binário Windows PE32. No entanto, analisadores de formato demonstraram que se tratava de um executável híbrido no padrão Launch4j / WinRun4J:

1. **Offset do Arquivo ZIP:**
   O executável possui um cabeçalho PE de inicialização nos primeiros 622.592 bytes (608 KB). Imediatamente a seguir (offset `622592`), inicia-se uma estrutura ZIP padrão (assinatura `PK\x03\x04`).
2. **Extração do Manifesto Java:**
   Lendo o índice do ZIP a partir do offset, verificou-se o manifesto:
   ```
   Manifest-Version: 1.0
   Main-Class: com.limagiran.holyrics.Holyrics
   ```
   Isso confirmou que toda a lógica de negócio do Holyrics residia em bytecode Java empacotado dentro do próprio binário executável.

---

## Etapa 3: Desacoplamento entre Terceiros e Código Proprietário

O catálogo do arquivo ZIP embutido revelou mais de 12.480 arquivos compilados (`.class`). Uma análise prévia de namespaces foi fundamental para evitar gastar recursos e tempo descompilando bibliotecas de código aberto que já possuem fontes públicos:

| Namespace | Origem | Função |
|---|---|---|
| `com.alee.*` | WebLaF | Look and Feel moderno para Java Swing |
| `javafx.*` | OpenJFX / Oracle | Componentes de UI JavaFX embebidos no Swing |
| `uk.co.caprica.vlcj.*` | VLCJ | Biblioteca de vinculação nativa com VLC Player |
| `org.sqlite.*` | Xerial SQLite-JDBC | Driver do banco de dados SQLite embutido |
| `com.sun.jna.*` | JNA | Java Native Access para chamadas diretas a DLLs |
| `com.itextpdf.*` | iText | Geração e relatórios em PDF |
| **`com.limagiran.*`** | **Proprietário Holyrics** | **Todo o software, regras de negócio e interfaces** |

Isolou-se exclusivamente o namespace `com/limagiran/*`, resultando em **1.724 arquivos** (classes e recursos).

---

## Etapa 4: Descompilação de Bytecode com CFR

Para converter os arquivos `.class` em arquivos `.java`, selecionou-se o descompilador **CFR (Class File Reader)** versão 0.152.

### Por que o CFR?
1. **Suporte a padrões modernos:** Trata com precisão construções do Java 7, 8 e superiores (como expressões lambda, method references, try-with-resources e enums avançados).
2. **Reconstrução de classes internas:** Mescla perfeitamente classes anônimas e inner classes (`Holyrics$1.class`, `MusicController$Paragraph.class`) de volta no arquivo pai.
3. **Robustez contra ofuscação:** O Holyrics não utilizou ofuscação pesada (como ProGuard/Allatori), mantendo os nomes de métodos, atributos e pacotes originais inteligíveis.
4. **Agilidade em lote:** O processamento em lote via JAR gerou **484 arquivos `.java` estruturados** em menos de 10 segundos.

---

## Etapa 5: Mapeamento de Binários Nativos com Ghidra e REA

Além do código Java, o executável embutia binários compilados (`.dll`) para prover aceleração nativa e recursos de baixo nível no Windows:
- `jnidispatch.dll`: ponte JNA para APIs do Windows.
- `sqlitejdbc.dll`: motor do SQLite compilado em C para Windows x86 e x64.

Para essas DLLs, empregou-se:
1. **7-Zip (`7zz`):** Para extrair as bibliotecas nativas de dentro do arquivo.
2. **Ghidra 12.1.4:** Framework de engenharia reversa da NSA para desassemblagem e descompilação de código de máquina.
3. **REA CLI 6.8.0:** Orquestrador com evidências criptográficas (digests SHA-256) e proveniência para inventário, análise de superfícies e verificação de P/Invoke.

---

## Etapa 6: Síntese e Documentação

O resultado de todas as etapas foi estruturado nos diretórios:
- `src/`: Fontes Java recuperados.
- `extracted_classes/`: Bytecode e assets estáticos.
- `extracted_dlls/`: Bibliotecas nativas.
- `docs/`: Manuais e guias de arquitetura.
