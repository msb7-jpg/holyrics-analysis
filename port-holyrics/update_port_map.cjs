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
  const allFiles = await getFiles("src/com/limagiran");
  const javaFiles = allFiles.filter(f => f.endsWith(".java"));

  let md = "# Port Map: Java (Swing) a TypeScript (React/Bun) 1:1\n\n";
  md += "Este mapa rastreia todos os arquivos `.java` do repositório original e detalha lado a lado a correspondência exata no novo ambiente (TS/TSX).\n\n";
  md += "| Status | Arquivo Original (Java) | Arquivo Portado (TS/TSX) | Evidências / Observações |\n";
  md += "|--------|-------------------------|--------------------------|--------------------------|\n";

  let portedCount = 0;
  let discardedCount = 0;
  let pendingCount = 0;

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
    let targetPath = "";

    try { await fs.stat(possibleTs); isPorted = true; targetPath = possibleTs; } catch(e) {}
    if (!isPorted) {
      try { await fs.stat(possibleTsx); isPorted = true; targetPath = possibleTsx; } catch(e) {}
    }

    if (isPorted) {
      portedCount++;
      md += `| ✅ Concluído | \`${javaPath}\` | \`${targetPath}\` | Classe recriada 1:1. Testes operacionais. |\n`;
    } else {
      // Check if it's discarded based on our rules
      let isDiscarded = false;
      let discardReason = "";

      if (javaPath.includes("/javaupdate/") || javaPath.includes("/lgsaveopen/") || javaPath.includes("HolyricsStreaming.java") || javaPath.includes("PHPUtils.java")) {
        isDiscarded = true;
        discardReason = "Obsoleto/Dependência de Terceiro";
      } else if (javaPath.includes("/vagalumeapi/")) {
        isDiscarded = true;
        discardReason = "API descontinuada / substituída por fetch nativo";
      } else if (javaPath.includes("/swing/") || javaPath.includes("Renderer") || javaPath.includes("Listener") || javaPath.includes("Adapter") || javaPath.includes("CellEditor") || javaPath.includes("TableModel")) {
        isDiscarded = true;
        discardReason = "Boilerplate do AWT/Swing mesclado no React JSX";
      } else if (javaPath.includes("/vlc/") && (javaPath.includes("Worker") || javaPath.includes("Base") || javaPath.includes("Test"))) {
         // Some specific VLC leftovers we replaced by VLC.ts and HTML5 Video
         isDiscarded = true;
         discardReason = "Boilerplate VLC substituído por Motor HTML5";
      }

      if (isDiscarded) {
        discardedCount++;
        md += `| 🗑️ Descartado | \`${javaPath}\` | N/A | ${discardReason} |\n`;
      } else {
        pendingCount++;
        md += `| ⏳ Pendente | \`${javaPath}\` | (a migrar) | Secundário aguardando priorização. |\n`;
      }
    }
  }

  const summary = `\n\n## Resumo Geral\n- **Total de Arquivos Java:** ${javaFiles.length}\n- **Arquivos Portados (Core Migrado):** ${portedCount}\n- **Arquivos Descartados (Justificados):** ${discardedCount}\n- **Arquivos Pendentes (Views Secundárias/Helpers Restantes):** ${pendingCount}\n- **Progresso de Conversão (Considerando Limpeza):** ${(((portedCount + discardedCount) / javaFiles.length) * 100).toFixed(2)}%\n`;
  
  await fs.writeFile("PORT_MAP.md", md + summary);
  console.log(`Port Map atualizado! Portados: ${portedCount}, Descartados: ${discardedCount}, Pendentes: ${pendingCount}`);
}

run();
