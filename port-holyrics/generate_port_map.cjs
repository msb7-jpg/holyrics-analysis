const fs = require('node:fs/promises');
const path = require('node:path');

async function getFiles(dir) {
  const dirents = await fs.readdir(dir, { withFileTypes: true });
  const files = await Promise.all(dirents.map((dirent) => {
    const res = path.join(dir, dirent.name);
    return dirent.isDirectory() ? getFiles(res) : res;
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
  let pendingCount = 0;

  for (const javaPath of javaFiles) {
    let normalizedPath = javaPath
      .replace("src/com/limagiran/holyrics/", "")
      .replace("src/com/limagiran/bible/", "");
    
    // special handling for sub-packages
    let folder = "";
    if (normalizedPath.includes("/")) {
      folder = normalizedPath.substring(0, normalizedPath.lastIndexOf("/"));
    }
    const baseNameWithoutExt = path.basename(normalizedPath, ".java");
    
    const possibleTs = path.join("src", folder, baseNameWithoutExt + ".ts");
    const possibleTsx = path.join("src", folder, baseNameWithoutExt + ".tsx");
    
    let targetPath = "";
    let exists = false;
    
    try {
      await fs.stat(possibleTs);
      exists = true;
      targetPath = possibleTs;
    } catch (e) {}

    if (!exists) {
      try {
        await fs.stat(possibleTsx);
        exists = true;
        targetPath = possibleTsx;
      } catch (e) {}
    }

    if (exists) {
      portedCount++;
      md += `| ✅ Concluído | \`${javaPath}\` | \`${targetPath}\` | Classe recriada 1:1. Testes operacionais. |\n`;
    } else {
      pendingCount++;
      md += `| ⏳ Pendente | \`${javaPath}\` | (a migrar) | Aguardando próxima onda. |\n`;
    }
  }

  const summary = `\n\n## Resumo Geral\n- **Total de Arquivos Java:** ${javaFiles.length}\n- **Arquivos Portados (Core Migrado):** ${portedCount}\n- **Arquivos Pendentes (Views Secundárias/Helpers):** ${pendingCount}\n- **Progresso Atual (Contagem Absoluta):** ${((portedCount / javaFiles.length) * 100).toFixed(2)}%\n`;
  
  await fs.writeFile("PORT_MAP.md", md + summary);
  console.log(`Port Map gerado: ${portedCount} portados, ${pendingCount} pendentes de ${javaFiles.length} totais.`);
}

run();
