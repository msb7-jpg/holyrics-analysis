import React, { useState, useEffect } from "react";
import { JPanel, WebButton, WebComboBox } from "../swing";

export interface FontEditThemeDefaultListener {
  setFontName: (name: string | null) => void;
  setBold: (bold: boolean | null) => void;
  setItalic: (italic: boolean | null) => void;
  setColor: (color: string | null) => void;
}

export interface FontEditThemeDefaultDialogProps {
  visible?: boolean;
  fontName?: string | null;
  bold?: boolean | null;
  italic?: boolean | null;
  color?: string | null;
  listener?: FontEditThemeDefaultListener;
  onClose?: () => void;
  onApply?: (values: {
    fontName: string | null;
    bold: boolean | null;
    italic: boolean | null;
    color: string | null;
  }) => void;
  position?: { x: number; y: number };
}

const AVAILABLE_FONTS = [
  "Arial",
  "Arial Black",
  "Calibri",
  "Cambria",
  "Comic Sans MS",
  "Consolas",
  "Courier New",
  "Georgia",
  "Impact",
  "Lucida Console",
  "Lucida Sans Unicode",
  "Segoe UI",
  "Tahoma",
  "Times New Roman",
  "Trebuchet MS",
  "Verdana",
];

const PRESET_COLORS = [
  "#ffffff", "#000000", "#ff0000", "#00ff00", "#0000ff",
  "#ffff00", "#00ffff", "#ff00ff", "#ffa500", "#808080",
  "#c0c0c0", "#800000", "#808000", "#008000", "#800080",
  "#008080", "#000080", "#ffd700", "#f0e68c", "#e6e6fa",
];

export const FontEditThemeDefaultDialog: React.FC<FontEditThemeDefaultDialogProps> = ({
  visible = true,
  fontName: initialFontName = null,
  bold: initialBold = null,
  italic: initialItalic = null,
  color: initialColor = null,
  listener,
  onClose,
  onApply,
  position,
}) => {
  const [useDefaultFont, setUseDefaultFont] = useState<boolean>(initialFontName === null);
  const [selectedFont, setSelectedFont] = useState<string>(initialFontName || "Arial");

  const [useDefaultBold, setUseDefaultBold] = useState<boolean>(initialBold === null);
  const [isBold, setIsBold] = useState<boolean>(initialBold ?? false);

  const [useDefaultItalic, setUseDefaultItalic] = useState<boolean>(initialItalic === null);
  const [isItalic, setIsItalic] = useState<boolean>(initialItalic ?? false);

  const [useDefaultColor, setUseDefaultColor] = useState<boolean>(initialColor === null);
  const [selectedColor, setSelectedColor] = useState<string>(initialColor || "#ffffff");

  const [showColorPicker, setShowColorPicker] = useState<boolean>(false);

  useEffect(() => {
    setUseDefaultFont(initialFontName === null);
    if (initialFontName) setSelectedFont(initialFontName);

    setUseDefaultBold(initialBold === null);
    if (initialBold !== null) setIsBold(initialBold);

    setUseDefaultItalic(initialItalic === null);
    if (initialItalic !== null) setIsItalic(initialItalic);

    setUseDefaultColor(initialColor === null);
    if (initialColor) setSelectedColor(initialColor);
  }, [initialFontName, initialBold, initialItalic, initialColor]);

  if (!visible) return null;

  const handleFontCheckboxChange = (checked: boolean) => {
    setUseDefaultFont(checked);
    const val = checked ? null : selectedFont;
    listener?.setFontName(val);
  };

  const handleFontSelect = (font: string) => {
    setSelectedFont(font);
    if (!useDefaultFont) {
      listener?.setFontName(font);
    }
  };

  const handleBoldCheckboxChange = (checked: boolean) => {
    setUseDefaultBold(checked);
    const val = checked ? null : isBold;
    listener?.setBold(val);
  };

  const handleBoldToggle = () => {
    const next = !isBold;
    setIsBold(next);
    if (!useDefaultBold) {
      listener?.setBold(next);
    }
  };

  const handleItalicCheckboxChange = (checked: boolean) => {
    setUseDefaultItalic(checked);
    const val = checked ? null : isItalic;
    listener?.setItalic(val);
  };

  const handleItalicToggle = () => {
    const next = !isItalic;
    setIsItalic(next);
    if (!useDefaultItalic) {
      listener?.setItalic(next);
    }
  };

  const handleColorCheckboxChange = (checked: boolean) => {
    setUseDefaultColor(checked);
    const val = checked ? null : selectedColor;
    listener?.setColor(val);
  };

  const handleColorSelect = (c: string) => {
    setSelectedColor(c);
    setShowColorPicker(false);
    if (!useDefaultColor) {
      listener?.setColor(c);
    }
  };

  const handleOk = () => {
    const result = {
      fontName: useDefaultFont ? null : selectedFont,
      bold: useDefaultBold ? null : isBold,
      italic: useDefaultItalic ? null : isItalic,
      color: useDefaultColor ? null : selectedColor,
    };
    onApply?.(result);
    onClose?.();
  };

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.35)",
        zIndex: 10000,
        display: "flex",
        alignItems: position ? "flex-start" : "center",
        justifyContent: position ? "flex-start" : "center",
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose?.();
        }
      }}
    >
      <div
        style={{
          position: position ? "absolute" : "relative",
          left: position ? Math.max(10, position.x) : undefined,
          top: position ? Math.max(10, position.y) : undefined,
          backgroundColor: "#f5f5f5",
          border: "1px solid #707070",
          boxShadow: "0 4px 16px rgba(0, 0, 0, 0.4)",
          borderRadius: 3,
          minWidth: 420,
          padding: "10px 16px 12px 16px",
          fontFamily: "Arial, Tahoma, sans-serif",
          color: "#222",
          userSelect: "none",
        }}
      >
        {/* Title */}
        <div
          style={{
            textAlign: "center",
            fontWeight: "bold",
            fontSize: 18,
            marginBottom: 12,
            fontFamily: "Tahoma, Arial, sans-serif",
            color: "#111",
          }}
        >
          Fonte
        </div>

        {/* Form Grid */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "80px 140px 1fr",
            rowGap: 10,
            columnGap: 8,
            alignItems: "center",
            fontSize: 14,
          }}
        >
          {/* Row 1: Font Name */}
          <div style={{ textAlign: "right", fontSize: 15, fontWeight: "normal" }}>
            Fonte:
          </div>
          <div>
            <label style={{ display: "flex", alignItems: "center", gap: 5, fontSize: 12, cursor: "pointer" }}>
              <input
                type="checkbox"
                checked={useDefaultFont}
                onChange={(e) => handleFontCheckboxChange(e.target.checked)}
              />
              Padrão do tema
            </label>
          </div>
          <div>
            <WebComboBox
              value={selectedFont}
              onChange={(val: any) => handleFontSelect(typeof val === "string" ? val : val?.target?.value ?? String(val))}
              disabled={useDefaultFont}
              style={{
                width: 170,
                height: 25,
                fontFamily: selectedFont,
                fontSize: 13,
              }}
            >
              {AVAILABLE_FONTS.map((font) => (
                <option key={font} value={font} style={{ fontFamily: font }}>
                  {font}
                </option>
              ))}
            </WebComboBox>
          </div>

          {/* Row 2: Bold */}
          <div style={{ textAlign: "right", fontSize: 15, fontWeight: "normal" }}>
            Negrito:
          </div>
          <div>
            <label style={{ display: "flex", alignItems: "center", gap: 5, fontSize: 12, cursor: "pointer" }}>
              <input
                type="checkbox"
                checked={useDefaultBold}
                onChange={(e) => handleBoldCheckboxChange(e.target.checked)}
              />
              Padrão do tema
            </label>
          </div>
          <div>
            <WebButton
              disabled={useDefaultBold}
              onClick={handleBoldToggle}
              style={{
                width: 42,
                height: 25,
                fontWeight: "bold",
                fontFamily: "Georgia, serif",
                backgroundColor: isBold && !useDefaultBold ? "#cce0f0" : undefined,
                border: isBold && !useDefaultBold ? "1px solid #3399ff" : undefined,
              }}
            >
              N
            </WebButton>
          </div>

          {/* Row 3: Italic */}
          <div style={{ textAlign: "right", fontSize: 15, fontWeight: "normal" }}>
            Itálico:
          </div>
          <div>
            <label style={{ display: "flex", alignItems: "center", gap: 5, fontSize: 12, cursor: "pointer" }}>
              <input
                type="checkbox"
                checked={useDefaultItalic}
                onChange={(e) => handleItalicCheckboxChange(e.target.checked)}
              />
              Padrão do tema
            </label>
          </div>
          <div>
            <WebButton
              disabled={useDefaultItalic}
              onClick={handleItalicToggle}
              style={{
                width: 42,
                height: 25,
                fontStyle: "italic",
                fontFamily: "Georgia, serif",
                backgroundColor: isItalic && !useDefaultItalic ? "#cce0f0" : undefined,
                border: isItalic && !useDefaultItalic ? "1px solid #3399ff" : undefined,
              }}
            >
              I
            </WebButton>
          </div>

          {/* Row 4: Color */}
          <div style={{ textAlign: "right", fontSize: 15, fontWeight: "normal" }}>
            Cor:
          </div>
          <div>
            <label style={{ display: "flex", alignItems: "center", gap: 5, fontSize: 12, cursor: "pointer" }}>
              <input
                type="checkbox"
                checked={useDefaultColor}
                onChange={(e) => handleColorCheckboxChange(e.target.checked)}
              />
              Padrão do tema
            </label>
          </div>
          <div style={{ position: "relative" }}>
            <button
              type="button"
              disabled={useDefaultColor}
              onClick={() => setShowColorPicker(!showColorPicker)}
              style={{
                width: 42,
                height: 25,
                border: "1px solid #999",
                borderRadius: 2,
                backgroundColor: "#fff",
                cursor: useDefaultColor ? "default" : "pointer",
                padding: 3,
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                opacity: useDefaultColor ? 0.5 : 1,
              }}
              title="Escolher cor"
            >
              <span
                style={{
                  width: "100%",
                  height: "100%",
                  backgroundColor: selectedColor,
                  border: "1px solid #000",
                  display: "block",
                }}
              />
            </button>

            {/* Color dropdown popover */}
            {showColorPicker && !useDefaultColor && (
              <div
                style={{
                  position: "absolute",
                  left: 0,
                  top: 30,
                  backgroundColor: "#fff",
                  border: "1px solid #666",
                  boxShadow: "0 2px 8px rgba(0,0,0,0.3)",
                  padding: 8,
                  zIndex: 10001,
                  display: "grid",
                  gridTemplateColumns: "repeat(5, 20px)",
                  gap: 4,
                }}
              >
                {PRESET_COLORS.map((c) => (
                  <div
                    key={c}
                    onClick={() => handleColorSelect(c)}
                    style={{
                      width: 20,
                      height: 20,
                      backgroundColor: c,
                      border: selectedColor.toLowerCase() === c.toLowerCase() ? "2px solid #0066cc" : "1px solid #ccc",
                      cursor: "pointer",
                    }}
                    title={c}
                  />
                ))}
                <div style={{ gridColumn: "1 / -1", marginTop: 4 }}>
                  <input
                    type="color"
                    value={selectedColor}
                    onChange={(e) => handleColorSelect(e.target.value)}
                    style={{ width: "100%", height: 22, cursor: "pointer", padding: 0 }}
                  />
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            marginTop: 18,
            gap: 12,
          }}
        >
          <WebButton
            onClick={handleOk}
            style={{
              minWidth: 80,
              height: 30,
              fontSize: 14,
              fontFamily: "Georgia, serif",
            }}
          >
            OK
          </WebButton>
          <WebButton
            onClick={onClose}
            style={{
              minWidth: 80,
              height: 30,
              fontSize: 14,
              fontFamily: "Georgia, serif",
            }}
          >
            Cancelar
          </WebButton>
        </div>
      </div>
    </div>
  );
};

export default FontEditThemeDefaultDialog;
