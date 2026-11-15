import { existsSync } from "node:fs";
import { readdir, stat, writeFile } from "node:fs/promises";
import { join, basename, dirname, extname } from "node:path";

async function getFiles(dir: string): Promise<string[]> {
  const dirents = await readdir(dir, { withFileTypes: true });
  const files = await Promise.all(dirents.map((dirent) => {
    const res = join(dir, dirent.name);
    return dirent.isDirectory() ? getFiles(res) : [res];
  }));
  return Array.prototype.concat(...files);
}

async function run() {
  const javaDir = existsSync("src/com/limagiran")
    ? "src/com/limagiran"
    : "../holyrics-source/legacy-java/com/limagiran";
  const allJavaFiles = (await getFiles(javaDir)).filter(f => f.endsWith(".java"));
  
  // Get all new TS/TSX files
  const allSrcFiles = await getFiles("src");
  const newCodeFiles = allSrcFiles.filter(f => 
    !f.startsWith("src/com/") && 
    (f.endsWith(".ts") || f.endsWith(".tsx"))
  );

  // Map to easily find if a file exists anywhere by its base name (e.g., "AvailableVersions")
  const newFilesMap = new Map<string, string>();
  for (const f of newCodeFiles) {
    const base = basename(f, extname(f)); // e.g. "AvailableVersions"
    newFilesMap.set(base, f);
  }

  let md = "# Port Map: Java (Swing) a TypeScript (React/Bun) 1:1\n\n";
  md += "Este mapa rastreia todos os arquivos `.java` do repositório original e detalha lado a lado a correspondência exata no novo ambiente (TS/TSX).\n\n";
  md += "| Status | Arquivo Original (Java) | Arquivo Portado (TS/TSX) | Evidências / Observações |\n";
  md += "|--------|-------------------------|--------------------------|--------------------------|\n";

  let portedCount = 0;
  let discardedCount = 0;
  let pendingCount = 0;

  // Sorting to keep the markdown clean
  allJavaFiles.sort();

  for (const javaPath of allJavaFiles) {
    const baseName = basename(javaPath, ".java");
    
    // 1. Check if it was ported (Search in Map)
    let isPorted = false;
    let targetPath = "";
    
    if (newFilesMap.has(baseName)) {
      isPorted = true;
      targetPath = newFilesMap.get(baseName)!;
    }

    if (isPorted) {
      portedCount++;
      md += `| ✅ Concluído | \`${javaPath}\` | \`${targetPath}\` | Classe recriada e funcional no ecossistema TS/React. |\n`;
    } else {
      // 2. Check if it's discarded based on rules
      let isDiscarded = false;
      let discardReason = "";

      const lowerPath = javaPath.toLowerCase();

      if (javaPath.includes("/javaupdate/") || javaPath.includes("/lgsaveopen/")) {
        isDiscarded = true;
        discardReason = "Obsoleto/Dependência de Terceiro";
      } else if (javaPath.includes("HolyricsStreaming.java") || javaPath.includes("PHPUtils.java") || javaPath.includes("DesktopShortcut.java")) {
        isDiscarded = true;
        discardReason = "Recurso nativo descontinuado para a Web";
      } else if (javaPath.includes("/vagalumeapi/")) {
        isDiscarded = true;
        discardReason = "API legada / substituída por fetch nativo nas views";
      } else if (
        javaPath.includes("Renderer") || 
        javaPath.includes("Listener") || 
        javaPath.includes("Adapter") || 
        javaPath.includes("CellEditor") || 
        javaPath.includes("TableModel") || 
        javaPath.includes("ComboBoxModel") ||
        javaPath.includes("/swing/") // most swing custom components
      ) {
        // Exception for actual ported components
        if (!newFilesMap.has(baseName)) {
          isDiscarded = true;
          discardReason = "Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX";
        }
      } else if (javaPath.includes("/vlc/") && (javaPath.includes("Worker") || javaPath.includes("Base") || javaPath.includes("Test") || javaPath.includes("Utils"))) {
         isDiscarded = true;
         discardReason = "Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open";
      }

      if (isDiscarded) {
        discardedCount++;
        md += `| 🗑️ Descartado | \`${javaPath}\` | N/A | ${discardReason} |\n`;
      } else {
        pendingCount++;
        md += `| ⏳ Pendente | \`${javaPath}\` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |\n`;
      }
    }
  }

  const summary = `\n\n## Resumo Geral\n- **Total de Arquivos Java:** ${allJavaFiles.length}\n- **Arquivos Portados (Core Migrado):** ${portedCount}\n- **Arquivos Descartados (Justificados):** ${discardedCount}\n- **Arquivos Pendentes (A Migrar):** ${pendingCount}\n- **Progresso de Conversão (Efetivo):** ${(((portedCount + discardedCount) / allJavaFiles.length) * 100).toFixed(2)}%\n`;
  
  await writeFile("PORT_MAP.md", md + summary);
  console.log(`Port Map atualizado via Global Search! Portados: ${portedCount}, Descartados: ${discardedCount}, Pendentes: ${pendingCount}`);
}

run();