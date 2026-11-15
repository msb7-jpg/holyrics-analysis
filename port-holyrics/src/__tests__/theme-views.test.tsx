import { describe, it, expect } from "bun:test";
import React from "react";
import { renderToString } from "react-dom/server";
import {
  ThemeView,
  FontEditThemeDefaultDialog,
  BackgroundChoiceTheme,
} from "../view";

describe("Theme Views Component Tests", () => {
  it("renders ThemeView without crashing", () => {
    const html = renderToString(
      <ThemeView
        visible={true}
        theme={{
          name: "Test Theme",
          screenAspect: "16:9",
          fontSize: 48,
        }}
      />
    );
    expect(html).toContain("Tema: Test Theme");
    expect(html).toContain("Plano de Fundo");
    expect(html).toContain("Tipografia Principal");
    expect(html).toContain("Salvar");
    expect(html).toContain("16:9");
  });

  it("renders FontEditThemeDefaultDialog without crashing", () => {
    const html = renderToString(
      <FontEditThemeDefaultDialog
        visible={true}
        fontName="Verdana"
        bold={true}
        italic={false}
        color="#ffcc00"
      />
    );
    expect(html).toContain("Fonte");
    expect(html).toContain("Negrito");
    expect(html).toContain("Itálico");
    expect(html).toContain("Cor");
    expect(html).toContain("OK");
  });

  it("renders BackgroundChoiceTheme without crashing", () => {
    const html = renderToString(
      <BackgroundChoiceTheme
        visible={true}
        initialBackground={{
          id: "test_bg",
          type: "image",
          name: "Minha Imagem",
        }}
      />
    );
    expect(html).toContain("Plano de Fundo");
    expect(html).toContain("Minhas Imagens");
    expect(html).toContain("Meus Vídeos");
    expect(html).toContain("Padrões");
    expect(html).toContain("Animações");
    expect(html).toContain("Transparente");
    expect(html).toContain("OK");
    expect(html).toContain("Cancelar");
  });
});
