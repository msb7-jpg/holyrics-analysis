import { describe, it, expect } from "bun:test";
import React from "react";
import { renderToString } from "react-dom/server";
import { BibleMain } from "../view/BibleMain";

describe("BibleMain View Component", () => {
  it("renders correctly without errors", () => {
    const html = renderToString(<BibleMain />);
    expect(html).toContain("Holyrics - Bíblia");
    expect(html).toContain("LIVROS DA BÍBLIA");
    expect(html).toContain("Gênesis");
    expect(html).toContain("Almeida Revista e Corrigida (ARC)");
    expect(html).toContain("MONITOR DE PROJEÇÃO");
    expect(html).toContain("Lista de Versículos");
  });
});
