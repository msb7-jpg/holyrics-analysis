# 07 - Guia de Uso, Consultas e Próximos Passos

Com todo o código-fonte em Java recuperado em `src/` e as ferramentas configuradas, este guia apresenta técnicas e comandos práticos para explorar e analisar o sistema.

---

## 🔎 Técnicas de Busca Rápida no Código-Fonte

Como o código está organizado em arquivos `.java` normais, você pode pesquisar por termos, métodos e strings diretamente pelo terminal usando `grep` ou `ripgrep`:

### 1. Entender o Protocolo do Aplicativo Mobile (Holyrics Remote)
Para encontrar os endpoints e mensagens trocadas entre o computador e o smartphone via Wi-Fi:
```bash
grep -rn "AppMessage" src/
grep -rn "ServerSocket" src/
```
*Classes-chave:* `AppMessageController.java`, `com/limagiran/holyrics/webservice/`

### 2. Investigar a Formatação e Transposição de Acordes/Cifras
Para ver como o Holyrics detecta notas musicais e permite mudar o tom da música:
```bash
grep -rn "chord" src/ -i
grep -rn "transpose" src/ -i
```
*Classes-chave:* `MusicController.java`, `Paragraph.java`

### 3. Descobrir como o Banco Bíblico (`.bib`) é Lido
Para entender o formato binário dos arquivos de Bíblia:
```bash
grep -rn "\.bib" src/
grep -rn "FactoryBible" src/
```
*Classes-chave:* `com/limagiran/bible/FactoryBible.java`, `com/limagiran/bible/Bible.java`

### 4. Consultar Comandos e Queries do Banco SQLite
Para ver a criação de tabelas e consultas de músicas:
```bash
grep -rn "CREATE TABLE" src/
grep -rn "SELECT " src/com/limagiran/holyrics/dao/
```
*Classes-chave:* `com/limagiran/holyrics/util/SQLite.java`, `com/limagiran/holyrics/dao/MusicDao.java`

---

## 🛠 Recompilação e Modificação Cirúrgica

Caso deseje alterar uma classe (por exemplo, adicionar um log, alterar uma regra de negócio ou testar uma correção) e reinseri-la na aplicação:

### Passo 1: Recompilar a classe modificada
Aponte o *classpath* para o próprio `app/Holyrics.exe` (que contém todas as dependências compiladas):
```bash
javac -cp "app/Holyrics.exe" \
  src/com/limagiran/holyrics/control/MusicController.java \
  -d /tmp/patched_classes/
```

### Passo 2: Atualizar o arquivo `.exe` com a nova classe
Como o `Holyrics.exe` contém um arquivo ZIP embutido, você pode atualizar a classe diretamente usando o 7-Zip:
```bash
cd /tmp/patched_classes
7z u /home/miguelsb/workspace/holyrics-analysis/app/Holyrics.exe \
  com/limagiran/holyrics/control/MusicController.class
```
