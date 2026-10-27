# 02 - Guia de Instalação e Configuração do Ambiente

Este guia descreve os comandos e procedimentos exatos para reproduzir a configuração do ambiente completo de engenharia reversa e descompilação em qualquer máquina Linux (Ubuntu/Debian) ou WSL2.

---

## 📋 Pré-requisitos Básicos

- Sistema Operacional: Linux x86_64 (testado em Ubuntu 24.04 LTS / WSL2).
- Espaço em disco recomendado: Pelo menos 5 GB livres (para JDK, Ghidra e artefatos).
- Conexão com a Internet para download dos binários e pacotes.

---

## 1. Java Runtime & Development Kit (JDK 21)

O Ghidra 12.x e o CFR requerem um JDK 21 ou superior de 64 bits.

```bash
# Instalação via repositório da distribuição (caso ainda não possua)
sudo apt update
sudo apt install -y openjdk-21-jdk curl unzip tar

# Verificação
java -version
javac -version
```

A saída esperada deve confirmar o OpenJDK 21:
```
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-24.04.4-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-24.04.4-Ubuntu, mixed mode, sharing)
```

---

## 2. Instalação do 7-Zip Standalone (`7zz`)

Como os utilitários padrão `unzip` ou `tar` não manipulam offsets em executáveis PE32, instalamos a versão estática oficial do 7-Zip em `~/.local/bin/`:

```bash
mkdir -p /tmp/7z_setup ~/.local/bin
curl -sL https://www.7-zip.org/a/7z2301-linux-x64.tar.xz -o /tmp/7z_setup/7z.tar.xz
tar -xf /tmp/7z_setup/7z.tar.xz -C /tmp/7z_setup
cp /tmp/7z_setup/7zz ~/.local/bin/7zz
ln -sf ~/.local/bin/7zz ~/.local/bin/7z
ln -sf ~/.local/bin/7zz ~/.local/bin/7za
chmod +x ~/.local/bin/7zz
rm -rf /tmp/7z_setup

# Testar
7z --help | head -n 3
```

---

## 3. Instalação do `innoextract` (Desempacotador de Instaladores Windows)

Para descompactar instaladores criados pelo Inno Setup sem precisar do Windows:

```bash
mkdir -p /tmp/inno_setup ~/.local/bin
curl -sL https://github.com/dscharrer/innoextract/releases/download/1.9/innoextract-1.9-linux.tar.xz -o /tmp/inno_setup/inno.tar.xz
tar -xf /tmp/inno_setup/inno.tar.xz -C /tmp/inno_setup
cp /tmp/inno_setup/innoextract-1.9-linux/bin/amd64/innoextract ~/.local/bin/innoextract
chmod +x ~/.local/bin/innoextract
rm -rf /tmp/inno_setup

# Testar
innoextract -v | head -n 2
```

---

## 4. Instalação dos Descompiladores Java (CFR e Vineflower)

### 4.1 CFR Java Decompiler (0.152)
O CFR é rápido e excelente para descompilação geral:
```bash
mkdir -p ~/.local/bin
curl -sL "https://repo1.maven.org/maven2/org/benf/cfr/0.152/cfr-0.152.jar" -o ~/.local/bin/cfr.jar

cat << 'EOF' > ~/.local/bin/cfr
#!/usr/bin/env bash
java -jar "$HOME/.local/bin/cfr.jar" "$@"
EOF
chmod +x ~/.local/bin/cfr
```

### 4.2 Vineflower (1.10.1 - Especialista em Try-With-Resources e AST)
O Vineflower é o fork moderno e mantido do Fernflower, ideal para resolver métodos com múltiplos recursos `AutoCloseable` aninhados onde o CFR reporta `ConfusedCFRException`:
```bash
curl -sL "https://repo1.maven.org/maven2/org/vineflower/vineflower/1.10.1/vineflower-1.10.1.jar" -o ~/.local/bin/vineflower.jar

cat << 'EOF' > ~/.local/bin/vineflower
#!/usr/bin/env bash
java -jar "$HOME/.local/bin/vineflower.jar" "$@"
EOF
chmod +x ~/.local/bin/vineflower

# Testar
vineflower --help | head -n 2
```

---

## 5. Instalação do REA (Reverse Engineer Anything) CLI

O REA orquestra ferramentas de análise estática e nativa através de CLI e MCP (Model Context Protocol).

```bash
# Instalação global do pacote
npm install -g rea-agents

# Verificação de versão
rea --version
# Saída esperada: 6.8.0 ou superior
```

### Configuração de Integrações MCP e Skills:
Para habilitar o servidor MCP do REA nos clientes suportados (Gemini CLI e Antigravity):

```bash
# Configura os clientes e sincroniza a skill ~/.agents/skills/reverse-engineer-anything
rea setup --client antigravity --client gemini_cli --yes
```

---

## 6. Instalação e Configuração do Ghidra 12.1.4

O Ghidra atua como backend de desassemblagem e descompilação de código nativo para o REA.

```bash
mkdir -p ~/tools && cd ~/tools
curl -sL -o ghidra_12.1.4.zip "https://github.com/NationalSecurityAgency/ghidra/releases/download/Ghidra_12.1.4_build/ghidra_12.1.4_PUBLIC_20260921.zip"
unzip -q ghidra_12.1.4.zip
rm ghidra_12.1.4.zip
```

### Persistência de Variáveis de Ambiente:
Adicione a variável `GHIDRA_INSTALL_DIR` ao seu perfil de shell (`~/.bashrc` e/ou `~/.zshrc`):

```bash
echo 'export GHIDRA_INSTALL_DIR="$HOME/tools/ghidra_12.1.4_PUBLIC"' >> ~/.bashrc
[ -f ~/.zshrc ] && echo 'export GHIDRA_INSTALL_DIR="$HOME/tools/ghidra_12.1.4_PUBLIC"' >> ~/.zshrc
export GHIDRA_INSTALL_DIR="$HOME/tools/ghidra_12.1.4_PUBLIC"
```

---

## 7. Verificação de Saúde Geral do Ambiente

Execute o diagnóstico do REA para confirmar que todas as peças se comunicam perfeitamente:

```bash
rea doctor --client antigravity --client gemini_cli
```

### Checklist de Sucesso:
- `node: ok: true`
- `registration:antigravity: ok: true`
- `registration:gemini_cli: ok: true`
- `ghidra: ok: true` (versão 12.1.4 reconhecida com analyzeHeadless e descompilador ELF x86_64)
- `skill: state: aligned`
- `healthy: true`
