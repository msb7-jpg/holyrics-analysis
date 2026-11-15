const fsSync = require('node:fs');
const fs = require('node:fs/promises');
const path = require('node:path');

async function getFiles(dir) {
  const dirents = await fs.readdir(dir, { withFileTypes: true });
  const files = await Promise.all(dirents.map((dirent) => {
    const res = path.join(dir, dirent.name);
    return dirent.isDirectory() ? getFiles(res) : [res];
  }));
  return Array.prototype.concat(...files);
}

async function run() {
  const javaDir = fsSync.existsSync("src/com/limagiran")
    ? "src/com/limagiran"
    : "../holyrics-source/legacy-java/com/limagiran";
  const allFiles = await getFiles(javaDir);
  const javaFiles = allFiles.filter(f => f.endsWith(".java"));

  const ported = new Set();
  const pending = [];

  for (const javaPath of javaFiles) {
    let normalizedPath = javaPath
      .replace("src/com/limagiran/holyrics/", "")
      .replace("src/com/limagiran/bible/", "");
    
    let folder = "";
    if (normalizedPath.includes("/")) {
      folder = normalizedPath.substring(0, normalizedPath.lastIndexOf("/"));
    }
    const baseName = path.basename(normalizedPath, ".java");
    
    const possibleTs = path.join("src", folder, baseName + ".ts");
    const possibleTsx = path.join("src", folder, baseName + ".tsx");
    
    let isPorted = false;
    try { await fs.stat(possibleTs); isPorted = true; } catch(e) {}
    if (!isPorted) {
      try { await fs.stat(possibleTsx); isPorted = true; } catch(e) {}
    }
    
    if (isPorted) {
      ported.add(javaPath);
    } else {
      pending.push(javaPath);
    }
  }

  const categories = {
    THIRD_PARTY_OBSOLETE: [],
    SWING_BOILERPLATE_MERGED: [],
    CORE_BIBLE: [],
    CORE_HITEM: [],
    VIEWS_DIALOGS: [],
    UTILS: [],
    OTHER: []
  };

  for (const f of pending) {
    if (f.includes("/javaupdate/") || f.includes("/lgsaveopen/") || f.includes("HolyricsStreaming.java") || f.includes("PHPUtils.java")) {
      categories.THIRD_PARTY_OBSOLETE.push(f);
    } else if (f.includes("/vagalumeapi/")) {
      categories.THIRD_PARTY_OBSOLETE.push(f); 
    } else if (f.includes("/swing/") || f.includes("Renderer") || f.includes("Listener") || f.includes("Adapter") || f.includes("CellEditor") || f.includes("TableModel")) {
      categories.SWING_BOILERPLATE_MERGED.push(f);
    } else if (f.includes("/bible/")) {
      categories.CORE_BIBLE.push(f);
    } else if (f.includes("/hitem/")) {
      categories.CORE_HITEM.push(f);
    } else if (f.includes("/view/") || f.includes("/edit/") || f.includes("/report/")) {
      categories.VIEWS_DIALOGS.push(f);
    } else if (f.includes("/util/") || f.includes("/i18n/")) {
      categories.UTILS.push(f);
    } else {
      categories.OTHER.push(f);
    }
  }

  let md = "# Auditoria Analítica de Arquivos Pendentes (Justificativas)\n\n";
  md += "Dos arquivos Java originais, analisamos o que falta ser portado e justificamos a exclusão ou fusão do que era 'lixo' de arquitetura legada.\n\n";

  md += "## 1. Arquitetura Swing Descartada / Mesclada no React (Justificada) 🗑️\n";
  md += "> **Justificativa:** O Java Swing exige dezenas de arquivos dedicados para tratar cliques (`MouseAdapter`), renderização de células de tabelas (`ListCellRenderer`), e listeners de teclado/texto. No React, isso não existe como arquivo isolado: usamos funções aninhadas no próprio componente (`onClick`, `onChange`, `.map()`). Portanto, estes arquivos **não terão** um `.ts` isolado, pois sua lógica já foi ou será absorvida pelos componentes React nativos.\n\n";
  for (const f of categories.SWING_BOILERPLATE_MERGED) md += `- \`${f}\`\n`;

  md += "\n## 2. Bibliotecas de Terceiros e APIs Obsoletas (Descartadas) 🚫\n";
  md += "> **Justificativa:** Códigos dedicados a atualização do executável Java (`javaupdate`), serialização nativa de Objetos Java (`lgsaveopen`) ou scripts antigos em PHP não fazem sentido no ecossistema web/TS. Atualizações serão feitas pelo Electron/Tauri; Serialização será feita via `JSON.parse()`. A API do Vagalume, se ainda viva, será consumida nativamente com um simples `fetch()` web (sem necessidade de DTOs nativos gigantes).\n\n";
  for (const f of categories.THIRD_PARTY_OBSOLETE) md += `- \`${f}\`\n`;

  md += "\n## 3. O Que REALMENTE Fica Faltando para 100% de Funcionalidade 🚀\n";
  md += "Estes são os pacotes vitais que precisam de agentes para serem convertidos em TypeScript/React:\n\n";

  md += "### A. Motor da Bíblia (Módulo Isolado) [" + categories.CORE_BIBLE.length + " arquivos]\n";
  md += "> O sistema de Bíblia tem seu próprio ecossistema de parse de arquivos XML e banco local.\n";
  for (const f of categories.CORE_BIBLE) md += `- \`${f}\`\n`;

  md += "\n### B. Histórico e Itens de Programação (HItem) [" + categories.CORE_HITEM.length + " arquivos]\n";
  md += "> Estruturas de dados e parsers que formatam as listas roláveis de Músicas, Avisos e Eventos na tela principal.\n";
  for (const f of categories.CORE_HITEM) md += `- \`${f}\`\n`;

  md += "\n### C. Views Secundárias, Edição e Relatórios [" + categories.VIEWS_DIALOGS.length + " arquivos]\n";
  md += "> Modais de edição de músicas (`EditWorship`), telas menores de configurações e relatórios para PDF/Impressão.\n";
  for (const f of categories.VIEWS_DIALOGS) md += `- \`${f}\`\n`;

  md += "\n### D. Utilitários VITAIS (Regras de Negócio) [" + categories.UTILS.length + " arquivos]\n";
  md += "> Funções auxiliares vitais (`StringUtils`, `KeyboardUtils`, `Security`) e o sistema de internacionalização (`i18n`).\n";
  for (const f of categories.UTILS) md += `- \`${f}\`\n`;

  md += "\n### E. Outros [" + categories.OTHER.length + " arquivos]\n";
  for (const f of categories.OTHER) md += `- \`${f}\`\n`;

  await fs.writeFile("DISCARD_JUSTIFICATION.md", md);
  console.log(`Auditoria gerada. Descartados/Mesclados: ${categories.SWING_BOILERPLATE_MERGED.length + categories.THIRD_PARTY_OBSOLETE.length}. Restam Reais: ${categories.CORE_BIBLE.length + categories.CORE_HITEM.length + categories.VIEWS_DIALOGS.length + categories.UTILS.length + categories.OTHER.length}`);
}

run();
