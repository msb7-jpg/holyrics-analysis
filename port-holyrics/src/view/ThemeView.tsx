import React, { useState, useRef, useEffect, useMemo } from "react";
import {
  JFrame,
  JPanel,
  WebButton,
  WebLabel,
  WebComboBox,
  JSplitPane,
} from "../swing";
import { BackgroundChoiceTheme, type BackgroundItem } from "./BackgroundChoiceTheme";
import { FontEditThemeDefaultDialog } from "./FontEditThemeDefaultDialog";

export type ScreenAspect = "16:9" | "4:3" | "16:10" | "21:9";

export interface FontOverride {
  fontName: string | null;
  bold: boolean | null;
  italic: boolean | null;
  color: string | null;
}

export interface ThemeModel {
  id?: string;
  name: string;
  screenAspect: ScreenAspect;
  background: BackgroundItem;
  opacityPercent: number; // 0 - 100
  videoRate: number; // 0.25 - 4.0
  fontFamily: string;
  fontSize: number;
  bold: boolean;
  italic: boolean;
  allCaps: boolean;
  textColor: string;
  textAlign: "left" | "center" | "right" | "justify";
  verticalAlign: "top" | "center" | "bottom";
  lineSpacing: number; // 1.0 - 2.5
  strokeEnabled: boolean;
  strokeColor: string;
  strokeWidth: number;
  shadowEnabled: boolean;
  shadowColor: string;
  shadowDistance: number;
  shadowBlur: number;
  marginTop: number;
  marginBottom: number;
  marginLeft: number;
  marginRight: number;
  headerFont?: FontOverride;
  chorusFont?: FontOverride;
  verseFont?: FontOverride;
  footerFont?: FontOverride;
}

export interface ThemeViewProps {
  theme?: Partial<ThemeModel>;
  isNew?: boolean;
  visible?: boolean;
  onSave?: (theme: ThemeModel) => void;
  onClose?: () => void;
}

const DEFAULT_THEME: ThemeModel = {
  name: "Padrão",
  screenAspect: "16:9",
  background: {
    id: "img_mountains",
    type: "image",
    name: "Montanhas",
    url: "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=640&q=80",
  },
  opacityPercent: 100,
  videoRate: 1.0,
  fontFamily: "Arial",
  fontSize: 44,
  bold: true,
  italic: false,
  allCaps: false,
  textColor: "#ffffff",
  textAlign: "center",
  verticalAlign: "center",
  lineSpacing: 1.25,
  strokeEnabled: true,
  strokeColor: "#000000",
  strokeWidth: 2,
  shadowEnabled: true,
  shadowColor: "rgba(0,0,0,0.85)",
  shadowDistance: 3,
  shadowBlur: 5,
  marginTop: 40,
  marginBottom: 40,
  marginLeft: 50,
  marginRight: 50,
};

const SAMPLE_TEXT =
  "Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.\n\nJoão 3:16";

const FONT_FAMILIES = [
  "Arial",
  "Arial Black",
  "Calibri",
  "Cambria",
  "Comic Sans MS",
  "Courier New",
  "Georgia",
  "Impact",
  "Lucida Sans Unicode",
  "Segoe UI",
  "Tahoma",
  "Times New Roman",
  "Trebuchet MS",
  "Verdana",
];

export const ThemeView: React.FC<ThemeViewProps> = ({
  theme: initialTheme,
  isNew = false,
  visible = true,
  onSave,
  onClose,
}) => {
  const [theme, setTheme] = useState<ThemeModel>({
    ...DEFAULT_THEME,
    ...initialTheme,
  });

  const [hasChanges, setHasChanges] = useState<boolean>(false);
  const [paintMargin, setPaintMargin] = useState<boolean>(false);
  const [showBgDialog, setShowBgDialog] = useState<boolean>(false);

  // Sub-dialogs for header/chorus/verse/footer font override
  const [activeFontOverrideTarget, setActiveFontOverrideTarget] = useState<
    "header" | "chorus" | "verse" | "footer" | null
  >(null);

  useEffect(() => {
    if (initialTheme) {
      setTheme({ ...DEFAULT_THEME, ...initialTheme });
      setHasChanges(false);
    }
  }, [initialTheme]);

  const updateTheme = <K extends keyof ThemeModel>(key: K, value: ThemeModel[K]) => {
    setTheme((prev) => ({ ...prev, [key]: value }));
    setHasChanges(true);
  };

  const handleSave = () => {
    onSave?.(theme);
    setHasChanges(false);
    onClose?.();
  };

  const handleClose = () => {
    if (hasChanges) {
      const confirmSave = window.confirm("Deseja salvar as alterações feitas no tema?");
      if (confirmSave) {
        handleSave();
        return;
      }
    }
    onClose?.();
  };

  // Aspect ratio proportions for preview calculation
  const aspectMultiplier = useMemo(() => {
    switch (theme.screenAspect) {
      case "16:9":
        return 9 / 16;
      case "4:3":
        return 3 / 4;
      case "16:10":
        return 10 / 16;
      case "21:9":
        return 9 / 21;
      default:
        return 9 / 16;
    }
  }, [theme.screenAspect]);

  if (!visible) return null;

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.6)",
        zIndex: 8000,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <div
        style={{
          width: "95vw",
          height: "92vh",
          maxWidth: 1360,
          maxHeight: 900,
          backgroundColor: "#ece9d8",
          border: "2px solid #0055ea",
          boxShadow: "0 8px 30px rgba(0,0,0,0.7)",
          display: "flex",
          flexDirection: "column",
          fontFamily: "Arial, Tahoma, sans-serif",
          overflow: "hidden",
        }}
      >
        {/* Window Title Bar */}
        <div
          style={{
            height: 28,
            backgroundColor: "#0055ea",
            backgroundImage: "linear-gradient(to bottom, #2b7af9, #0055ea)",
            color: "#fff",
            fontWeight: "bold",
            padding: "0 8px",
            fontSize: 13,
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            userSelect: "none",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
            <span style={{ fontSize: 14 }}>🎨</span>
            <span>{isNew ? "Novo Tema" : `Tema: ${theme.name}`}</span>
          </div>
          <button
            onClick={handleClose}
            style={{
              background: "transparent",
              border: "none",
              color: "#fff",
              fontWeight: "bold",
              fontSize: 14,
              cursor: "pointer",
            }}
          >
            ✕
          </button>
        </div>

        {/* Main Content: Split into Left Settings & Right Preview */}
        <div style={{ flex: 1, display: "flex", overflow: "hidden" }}>
          {/* Left Settings Panel */}
          <div
            style={{
              width: 440,
              minWidth: 440,
              backgroundColor: "#f5f5f5",
              borderRight: "2px solid #b8b8b8",
              display: "flex",
              flexDirection: "column",
              overflowY: "auto",
              padding: "10px 14px",
              gap: 12,
              fontSize: 12,
            }}
          >
            {/* Theme Name & Aspect Ratio */}
            <fieldset
              style={{
                border: "1px solid #c0c0c0",
                padding: "8px 12px",
                borderRadius: 2,
                backgroundColor: "#fff",
              }}
            >
              <legend style={{ fontWeight: "bold", color: "#333", padding: "0 4px" }}>
                Identificação & Formato
              </legend>
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <label style={{ width: 80, fontWeight: "bold" }}>Nome:</label>
                  <input
                    type="text"
                    value={theme.name}
                    onChange={(e) => updateTheme("name", e.target.value)}
                    style={{
                      flex: 1,
                      height: 24,
                      padding: "2px 6px",
                      border: "1px solid #999",
                      fontSize: 12,
                    }}
                  />
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <label style={{ width: 80, fontWeight: "bold" }}>Formato:</label>
                  <WebComboBox
                    value={theme.screenAspect}
                    onChange={(val: any) => {
                      const aspect = typeof val === "string" ? val : val?.target?.value ?? String(val);
                      updateTheme("screenAspect", aspect as ScreenAspect);
                    }}
                    style={{ flex: 1, height: 24 }}
                  >
                    <option value="16:9">16:9 (Widescreen)</option>
                    <option value="4:3">4:3 (Padrão)</option>
                    <option value="16:10">16:10</option>
                    <option value="21:9">21:9 (Ultrawide)</option>
                  </WebComboBox>
                </div>
              </div>
            </fieldset>

            {/* Background Settings */}
            <fieldset
              style={{
                border: "1px solid #c0c0c0",
                padding: "8px 12px",
                borderRadius: 2,
                backgroundColor: "#fff",
              }}
            >
              <legend style={{ fontWeight: "bold", color: "#333", padding: "0 4px" }}>
                Plano de Fundo
              </legend>
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                  {/* Thumbnail */}
                  <div
                    style={{
                      width: 90,
                      height: 52,
                      backgroundColor: theme.background.color || "#000",
                      backgroundImage: theme.background.url ? `url(${theme.background.url})` : undefined,
                      backgroundSize: theme.background.type === "pattern" ? "repeat" : "cover",
                      backgroundPosition: "center",
                      border: "1px solid #666",
                      borderRadius: 2,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#fff",
                      fontSize: 10,
                      textShadow: "0 1px 2px #000",
                    }}
                  >
                    {theme.background.type === "transparent" ? "Transparente" : ""}
                  </div>
                  <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 4 }}>
                    <div style={{ fontWeight: "bold", fontSize: 11, color: "#444" }}>
                      {theme.background.name}
                    </div>
                    <WebButton
                      onClick={() => setShowBgDialog(true)}
                      style={{ height: 26, fontSize: 11 }}
                    >
                      Alterar Plano de Fundo...
                    </WebButton>
                  </div>
                </div>

                {/* Opacity slider */}
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <label style={{ width: 80 }}>Opacidade:</label>
                  <input
                    type="range"
                    min="0"
                    max="100"
                    value={theme.opacityPercent}
                    onChange={(e) => updateTheme("opacityPercent", Number(e.target.value))}
                    style={{ flex: 1 }}
                  />
                  <span style={{ width: 36, textAlign: "right" }}>{theme.opacityPercent}%</span>
                </div>

                {/* Video speed (if video) */}
                {(theme.background.type === "video" || theme.background.type === "my_video") && (
                  <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <label style={{ width: 80 }}>Velocidade:</label>
                    <input
                      type="range"
                      min="0.25"
                      max="2.0"
                      step="0.05"
                      value={theme.videoRate}
                      onChange={(e) => updateTheme("videoRate", Number(e.target.value))}
                      style={{ flex: 1 }}
                    />
                    <span style={{ width: 36, textAlign: "right" }}>{theme.videoRate.toFixed(2)}x</span>
                  </div>
                )}
              </div>
            </fieldset>

            {/* Typography / Font Settings */}
            <fieldset
              style={{
                border: "1px solid #c0c0c0",
                padding: "8px 12px",
                borderRadius: 2,
                backgroundColor: "#fff",
              }}
            >
              <legend style={{ fontWeight: "bold", color: "#333", padding: "0 4px" }}>
                Tipografia Principal
              </legend>
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {/* Font family & Size */}
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <WebComboBox
                    value={theme.fontFamily}
                    onChange={(val: any) => {
                      const font = typeof val === "string" ? val : val?.target?.value ?? String(val);
                      updateTheme("fontFamily", font);
                    }}
                    style={{ flex: 1, height: 24, fontFamily: theme.fontFamily }}
                  >
                    {FONT_FAMILIES.map((f) => (
                      <option key={f} value={f} style={{ fontFamily: f }}>
                        {f}
                      </option>
                    ))}
                  </WebComboBox>
                  <input
                    type="number"
                    min="14"
                    max="120"
                    value={theme.fontSize}
                    onChange={(e) => updateTheme("fontSize", Number(e.target.value))}
                    style={{ width: 54, height: 24, textAlign: "center", border: "1px solid #999" }}
                    title="Tamanho da fonte"
                  />
                  <input
                    type="color"
                    value={theme.textColor}
                    onChange={(e) => updateTheme("textColor", e.target.value)}
                    style={{ width: 30, height: 26, padding: 0, cursor: "pointer" }}
                    title="Cor do texto"
                  />
                </div>

                {/* Styles: Bold, Italic, Caps & Alignments */}
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                  <div style={{ display: "flex", gap: 4 }}>
                    <WebButton
                      onClick={() => updateTheme("bold", !theme.bold)}
                      style={{
                        width: 28,
                        height: 24,
                        fontWeight: "bold",
                        backgroundColor: theme.bold ? "#cce0f0" : undefined,
                        border: theme.bold ? "1px solid #3399ff" : undefined,
                      }}
                    >
                      B
                    </WebButton>
                    <WebButton
                      onClick={() => updateTheme("italic", !theme.italic)}
                      style={{
                        width: 28,
                        height: 24,
                        fontStyle: "italic",
                        backgroundColor: theme.italic ? "#cce0f0" : undefined,
                        border: theme.italic ? "1px solid #3399ff" : undefined,
                      }}
                    >
                      I
                    </WebButton>
                    <WebButton
                      onClick={() => updateTheme("allCaps", !theme.allCaps)}
                      style={{
                        width: 32,
                        height: 24,
                        fontSize: 10,
                        backgroundColor: theme.allCaps ? "#cce0f0" : undefined,
                        border: theme.allCaps ? "1px solid #3399ff" : undefined,
                      }}
                    >
                      AA
                    </WebButton>
                  </div>

                  {/* Horizontal Alignment */}
                  <div style={{ display: "flex", gap: 2 }}>
                    {(["left", "center", "right", "justify"] as const).map((align) => (
                      <WebButton
                        key={align}
                        onClick={() => updateTheme("textAlign", align)}
                        style={{
                          width: 26,
                          height: 24,
                          fontSize: 10,
                          backgroundColor: theme.textAlign === align ? "#cce0f0" : undefined,
                          border: theme.textAlign === align ? "1px solid #3399ff" : undefined,
                        }}
                      >
                        {align === "left" ? "⯇" : align === "center" ? "⯁" : align === "right" ? "⯈" : "≣"}
                      </WebButton>
                    ))}
                  </div>

                  {/* Vertical Alignment */}
                  <div style={{ display: "flex", gap: 2 }}>
                    {(["top", "center", "bottom"] as const).map((vAlign) => (
                      <WebButton
                        key={vAlign}
                        onClick={() => updateTheme("verticalAlign", vAlign)}
                        style={{
                          width: 26,
                          height: 24,
                          fontSize: 10,
                          backgroundColor: theme.verticalAlign === vAlign ? "#cce0f0" : undefined,
                          border: theme.verticalAlign === vAlign ? "1px solid #3399ff" : undefined,
                        }}
                      >
                        {vAlign === "top" ? "▲" : vAlign === "center" ? "◆" : "▼"}
                      </WebButton>
                    ))}
                  </div>
                </div>

                {/* Line spacing */}
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <label style={{ width: 90 }}>Espaçamento:</label>
                  <input
                    type="range"
                    min="1.0"
                    max="2.5"
                    step="0.05"
                    value={theme.lineSpacing}
                    onChange={(e) => updateTheme("lineSpacing", Number(e.target.value))}
                    style={{ flex: 1 }}
                  />
                  <span style={{ width: 36, textAlign: "right" }}>{theme.lineSpacing.toFixed(2)}</span>
                </div>
              </div>
            </fieldset>

            {/* Stroke & Shadow */}
            <fieldset
              style={{
                border: "1px solid #c0c0c0",
                padding: "8px 12px",
                borderRadius: 2,
                backgroundColor: "#fff",
              }}
            >
              <legend style={{ fontWeight: "bold", color: "#333", padding: "0 4px" }}>
                Contorno & Sombra
              </legend>
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {/* Stroke */}
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <label style={{ display: "flex", alignItems: "center", gap: 4, width: 85, cursor: "pointer" }}>
                    <input
                      type="checkbox"
                      checked={theme.strokeEnabled}
                      onChange={(e) => updateTheme("strokeEnabled", e.target.checked)}
                    />
                    Contorno:
                  </label>
                  <input
                    type="color"
                    disabled={!theme.strokeEnabled}
                    value={theme.strokeColor}
                    onChange={(e) => updateTheme("strokeColor", e.target.value)}
                    style={{ width: 26, height: 22, padding: 0, cursor: "pointer" }}
                  />
                  <input
                    type="range"
                    min="1"
                    max="8"
                    disabled={!theme.strokeEnabled}
                    value={theme.strokeWidth}
                    onChange={(e) => updateTheme("strokeWidth", Number(e.target.value))}
                    style={{ flex: 1 }}
                  />
                  <span style={{ width: 28, textAlign: "right" }}>{theme.strokeWidth}px</span>
                </div>

                {/* Shadow */}
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <label style={{ display: "flex", alignItems: "center", gap: 4, width: 85, cursor: "pointer" }}>
                    <input
                      type="checkbox"
                      checked={theme.shadowEnabled}
                      onChange={(e) => updateTheme("shadowEnabled", e.target.checked)}
                    />
                    Sombra:
                  </label>
                  <input
                    type="range"
                    min="1"
                    max="15"
                    disabled={!theme.shadowEnabled}
                    value={theme.shadowBlur}
                    onChange={(e) => updateTheme("shadowBlur", Number(e.target.value))}
                    style={{ flex: 1 }}
                  />
                  <span style={{ width: 28, textAlign: "right" }}>{theme.shadowBlur}px</span>
                </div>
              </div>
            </fieldset>

            {/* Margins */}
            <fieldset
              style={{
                border: "1px solid #c0c0c0",
                padding: "8px 12px",
                borderRadius: 2,
                backgroundColor: "#fff",
              }}
            >
              <legend style={{ fontWeight: "bold", color: "#333", padding: "0 4px" }}>
                Margens (Pixels)
              </legend>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
                <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <label style={{ width: 50 }}>Sup:</label>
                  <input
                    type="number"
                    min="0"
                    max="300"
                    value={theme.marginTop}
                    onChange={(e) => updateTheme("marginTop", Number(e.target.value))}
                    style={{ width: 55, height: 22, border: "1px solid #999", textAlign: "center" }}
                  />
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <label style={{ width: 50 }}>Inf:</label>
                  <input
                    type="number"
                    min="0"
                    max="300"
                    value={theme.marginBottom}
                    onChange={(e) => updateTheme("marginBottom", Number(e.target.value))}
                    style={{ width: 55, height: 22, border: "1px solid #999", textAlign: "center" }}
                  />
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <label style={{ width: 50 }}>Esq:</label>
                  <input
                    type="number"
                    min="0"
                    max="300"
                    value={theme.marginLeft}
                    onChange={(e) => updateTheme("marginLeft", Number(e.target.value))}
                    style={{ width: 55, height: 22, border: "1px solid #999", textAlign: "center" }}
                  />
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <label style={{ width: 50 }}>Dir:</label>
                  <input
                    type="number"
                    min="0"
                    max="300"
                    value={theme.marginRight}
                    onChange={(e) => updateTheme("marginRight", Number(e.target.value))}
                    style={{ width: 55, height: 22, border: "1px solid #999", textAlign: "center" }}
                  />
                </div>
              </div>

              <div style={{ marginTop: 8, paddingTop: 6, borderTop: "1px solid #eee" }}>
                <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer", fontSize: 11 }}>
                  <input
                    type="checkbox"
                    checked={paintMargin}
                    onChange={(e) => setPaintMargin(e.target.checked)}
                  />
                  Exibir margens no preview
                </label>
              </div>
            </fieldset>

            {/* Overrides for Header, Chorus, Footer */}
            <fieldset
              style={{
                border: "1px solid #c0c0c0",
                padding: "8px 12px",
                borderRadius: 2,
                backgroundColor: "#fff",
              }}
            >
              <legend style={{ fontWeight: "bold", color: "#333", padding: "0 4px" }}>
                Sobrescrita de Fontes
              </legend>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6 }}>
                <WebButton
                  onClick={() => setActiveFontOverrideTarget("header")}
                  style={{ height: 24, fontSize: 11 }}
                >
                  Título / Cabeçalho...
                </WebButton>
                <WebButton
                  onClick={() => setActiveFontOverrideTarget("chorus")}
                  style={{ height: 24, fontSize: 11 }}
                >
                  Refrão (Coro)...
                </WebButton>
                <WebButton
                  onClick={() => setActiveFontOverrideTarget("verse")}
                  style={{ height: 24, fontSize: 11 }}
                >
                  Versículos...
                </WebButton>
                <WebButton
                  onClick={() => setActiveFontOverrideTarget("footer")}
                  style={{ height: 24, fontSize: 11 }}
                >
                  Rodapé / Autor...
                </WebButton>
              </div>
            </fieldset>
          </div>

          {/* Right Preview Panel */}
          <div
            style={{
              flex: 1,
              backgroundColor: "#2e2e2e",
              padding: 20,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              position: "relative",
              overflow: "hidden",
            }}
          >
            {/* Screen container keeping aspect ratio */}
            <div
              style={{
                width: "92%",
                maxWidth: 820,
                aspectRatio: theme.screenAspect.replace(":", " / "),
                backgroundColor: "#000",
                border: "2px solid #1e1e1e",
                boxShadow: "0 10px 25px rgba(0,0,0,0.8)",
                position: "relative",
                overflow: "hidden",
                display: "flex",
                flexDirection: "column",
              }}
            >
              {/* Slide Background Layer */}
              <div
                style={{
                  position: "absolute",
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  backgroundColor:
                    theme.background.type === "transparent"
                      ? "transparent"
                      : theme.background.color || "#000",
                  backgroundImage:
                    theme.background.type === "transparent"
                      ? "radial-gradient(#444 1px, transparent 1px)"
                      : theme.background.url
                      ? `url(${theme.background.url})`
                      : undefined,
                  backgroundSize:
                    theme.background.type === "transparent"
                      ? "12px 12px"
                      : theme.background.type === "pattern"
                      ? "repeat"
                      : "cover",
                  backgroundPosition: "center",
                  opacity: theme.opacityPercent / 100,
                  zIndex: 1,
                }}
              />

              {/* Margin guides if paintMargin is enabled */}
              {paintMargin && (
                <div
                  style={{
                    position: "absolute",
                    top: `${theme.marginTop / 4}px`,
                    bottom: `${theme.marginBottom / 4}px`,
                    left: `${theme.marginLeft / 4}px`,
                    right: `${theme.marginRight / 4}px`,
                    border: "1px dashed rgba(255, 0, 0, 0.7)",
                    pointerEvents: "none",
                    zIndex: 5,
                  }}
                />
              )}

              {/* Text Presentation Layer */}
              <div
                style={{
                  position: "relative",
                  zIndex: 2,
                  flex: 1,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent:
                    theme.verticalAlign === "top"
                      ? "flex-start"
                      : theme.verticalAlign === "bottom"
                      ? "flex-end"
                      : "center",
                  paddingTop: `${theme.marginTop / 4}px`,
                  paddingBottom: `${theme.marginBottom / 4}px`,
                  paddingLeft: `${theme.marginLeft / 4}px`,
                  paddingRight: `${theme.marginRight / 4}px`,
                  boxSizing: "border-box",
                }}
              >
                <div
                  style={{
                    fontFamily: theme.fontFamily,
                    fontSize: `${theme.fontSize * 0.45}px`,
                    fontWeight: theme.bold ? "bold" : "normal",
                    fontStyle: theme.italic ? "italic" : "normal",
                    textTransform: theme.allCaps ? "uppercase" : "none",
                    color: theme.textColor,
                    textAlign: theme.textAlign,
                    lineHeight: theme.lineSpacing,
                    whiteSpace: "pre-wrap",
                    textShadow: theme.shadowEnabled
                      ? `${theme.shadowDistance}px ${theme.shadowDistance}px ${theme.shadowBlur}px ${theme.shadowColor}`
                      : undefined,
                    WebkitTextStroke: theme.strokeEnabled
                      ? `${theme.strokeWidth * 0.5}px ${theme.strokeColor}`
                      : undefined,
                  }}
                >
                  {SAMPLE_TEXT}
                </div>
              </div>

              {/* Watermark / Ratio Indicator */}
              <div
                style={{
                  position: "absolute",
                  bottom: 6,
                  right: 8,
                  backgroundColor: "rgba(0,0,0,0.6)",
                  color: "#aaa",
                  fontSize: 10,
                  padding: "2px 6px",
                  borderRadius: 2,
                  zIndex: 6,
                  userSelect: "none",
                }}
              >
                {theme.screenAspect}
              </div>
            </div>
          </div>
        </div>

        {/* Footer with Actions */}
        <div
          style={{
            height: 42,
            backgroundColor: "#ece9d8",
            borderTop: "1px solid #999",
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-end",
            padding: "0 16px",
            gap: 12,
          }}
        >
          <WebButton
            onClick={handleSave}
            style={{
              minWidth: 90,
              height: 28,
              fontSize: 13,
              fontFamily: "Georgia, serif",
              fontWeight: "bold",
            }}
          >
            Salvar
          </WebButton>
          <WebButton
            onClick={handleClose}
            style={{
              minWidth: 90,
              height: 28,
              fontSize: 13,
              fontFamily: "Georgia, serif",
            }}
          >
            Cancelar
          </WebButton>
        </div>
      </div>

      {/* Background Choice Modal */}
      {showBgDialog && (
        <BackgroundChoiceTheme
          visible={showBgDialog}
          initialBackground={theme.background}
          onSelect={(newBg) => {
            updateTheme("background", newBg);
          }}
          onClose={() => setShowBgDialog(false)}
        />
      )}

      {/* Font Override Modal */}
      {activeFontOverrideTarget && (
        <FontEditThemeDefaultDialog
          visible={!!activeFontOverrideTarget}
          fontName={
            activeFontOverrideTarget === "header"
              ? theme.headerFont?.fontName
              : activeFontOverrideTarget === "chorus"
              ? theme.chorusFont?.fontName
              : activeFontOverrideTarget === "verse"
              ? theme.verseFont?.fontName
              : theme.footerFont?.fontName
          }
          bold={
            activeFontOverrideTarget === "header"
              ? theme.headerFont?.bold
              : activeFontOverrideTarget === "chorus"
              ? theme.chorusFont?.bold
              : activeFontOverrideTarget === "verse"
              ? theme.verseFont?.bold
              : theme.footerFont?.bold
          }
          italic={
            activeFontOverrideTarget === "header"
              ? theme.headerFont?.italic
              : activeFontOverrideTarget === "chorus"
              ? theme.chorusFont?.italic
              : activeFontOverrideTarget === "verse"
              ? theme.verseFont?.italic
              : theme.footerFont?.italic
          }
          color={
            activeFontOverrideTarget === "header"
              ? theme.headerFont?.color
              : activeFontOverrideTarget === "chorus"
              ? theme.chorusFont?.color
              : activeFontOverrideTarget === "verse"
              ? theme.verseFont?.color
              : theme.footerFont?.color
          }
          onApply={(override) => {
            if (activeFontOverrideTarget === "header") updateTheme("headerFont", override);
            else if (activeFontOverrideTarget === "chorus") updateTheme("chorusFont", override);
            else if (activeFontOverrideTarget === "verse") updateTheme("verseFont", override);
            else if (activeFontOverrideTarget === "footer") updateTheme("footerFont", override);
          }}
          onClose={() => setActiveFontOverrideTarget(null)}
        />
      )}
    </div>
  );
};

export default ThemeView;
