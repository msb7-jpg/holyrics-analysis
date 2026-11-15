import React, { useState, useEffect, useMemo } from "react";
import { JPanel, WebButton, WebLabel, WebComboBox } from "../swing";

export interface ThemeOption {
  id: string;
  name: string;
  backgroundColor: string;
  textColor: string;
  fontFamily: string;
}

const DEFAULT_THEMES: ThemeOption[] = [
  {
    id: "default",
    name: "Tema Padrão",
    backgroundColor: "#000000",
    textColor: "#ffffff",
    fontFamily: "Arial, sans-serif",
  },
  {
    id: "blue_gradient",
    name: "Azul Noturno",
    backgroundColor: "#0d1b2a",
    textColor: "#e0e1dd",
    fontFamily: "Georgia, serif",
  },
  {
    id: "light_clean",
    name: "Branco Limpo",
    backgroundColor: "#ffffff",
    textColor: "#111111",
    fontFamily: "Arial, sans-serif",
  },
  {
    id: "purple_worship",
    name: "Adoração Púrpura",
    backgroundColor: "#2b1055",
    textColor: "#ffd700",
    fontFamily: "Georgia, serif",
  },
];

export interface QuickPresentationDialogProps {
  visible?: boolean;
  onClose?: () => void;
  onShow?: (
    slides: string[],
    theme: ThemeOption,
    isAuto: boolean,
    repeat: boolean,
    seconds: number
  ) => void;
  initialText?: string;
}

export const QuickPresentationDialog: React.FC<
  QuickPresentationDialogProps
> = ({
  visible = true,
  onClose,
  onShow,
  initialText = "Digite aqui o texto da apresentação rápida.\n\nUse duas quebras de linha para separar os slides.",
}) => {
  const [text, setText] = useState<string>(initialText);
  const [selectedThemeId, setSelectedThemeId] = useState<string>("default");
  const [isAutomatic, setIsAutomatic] = useState<boolean>(false);
  const [repeat, setRepeat] = useState<boolean>(false);
  const [seconds, setSeconds] = useState<number>(5);

  const fallbackTheme: ThemeOption = DEFAULT_THEMES[0] as ThemeOption;
  const selectedTheme: ThemeOption = useMemo(
    () =>
      DEFAULT_THEMES.find((t) => t.id === selectedThemeId) || fallbackTheme,
    [selectedThemeId, fallbackTheme]
  );

  // Split text by \n\n to form individual slides
  const slides = useMemo(() => {
    const trimmed = text.trim();
    if (!trimmed) return [""];
    return text.split(/\n\n+/).filter((s) => s.trim().length > 0);
  }, [text]);

  const handleShow = () => {
    if (onShow) {
      onShow(slides, selectedTheme, isAutomatic, repeat, seconds);
    }
    if (onClose) {
      onClose();
    }
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        if (onClose) onClose();
      } else if (e.key === "Enter" && e.shiftKey) {
        e.preventDefault();
        handleShow();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [slides, selectedTheme, isAutomatic, repeat, seconds, onClose]);

  if (!visible) return null;

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0,0,0,0.4)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 9999,
      }}
    >
      <div
        style={{
          width: 860,
          minHeight: 560,
          maxHeight: "92vh",
          backgroundColor: "#f0f0f0",
          border: "2px solid #666",
          boxShadow: "3px 3px 10px rgba(0,0,0,0.5)",
          display: "flex",
          flexDirection: "column",
          fontFamily: "Tahoma, Georgia, sans-serif",
          fontSize: "12px",
        }}
      >
        {/* Title Bar simulating JDialog */}
        <div
          style={{
            backgroundColor: "#2c5b9c",
            color: "#ffffff",
            padding: "4px 8px",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            fontWeight: "bold",
            fontSize: "12px",
            userSelect: "none",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
            <span>⚡</span>
            <span>Apresentação Rápida</span>
          </div>
          <button
            onClick={onClose}
            style={{
              backgroundColor: "#c75050",
              border: "1px solid #990000",
              color: "#fff",
              cursor: "pointer",
              fontWeight: "bold",
              fontSize: "11px",
              padding: "1px 6px",
              lineHeight: "1",
            }}
          >
            ✕
          </button>
        </div>

        {/* Content Body: Left Input/Settings + Right MultiSlidePreview */}
        <div style={{ flex: 1, display: "flex", overflow: "hidden" }}>
          {/* Left Side: Text input & Controls (West) */}
          <div
            style={{
              width: 380,
              borderRight: "1px solid #bbb",
              display: "flex",
              flexDirection: "column",
              padding: "8px",
              gap: "8px",
              backgroundColor: "#f5f5f5",
            }}
          >
            <div style={{ fontWeight: "bold", color: "#333" }}>
              Texto da Apresentação:
            </div>
            {/* JTextArea inside JScrollPane */}
            <textarea
              value={text}
              maxLength={2048}
              onChange={(e) => setText(e.target.value)}
              style={{
                flex: 1,
                minHeight: 180,
                resize: "none",
                fontFamily: "Georgia, serif",
                fontSize: "14px",
                padding: "8px",
                border: "1px solid #7f9db9",
                backgroundColor: "#fff",
              }}
              placeholder="Digite o texto aqui... Separe os slides com duas quebras de linha (Enter Enter)."
            />

            {/* Separator */}
            <div style={{ height: "1px", backgroundColor: "#ccc" }} />

            {/* Left Footer Settings */}
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                gap: "8px",
                padding: "4px",
              }}
            >
              {/* Theme selector */}
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <WebLabel style={{ fontWeight: "bold", width: 50 }}>
                  Tema:
                </WebLabel>
                <select
                  value={selectedThemeId}
                  onChange={(e) => setSelectedThemeId(e.target.value)}
                  style={{
                    flex: 1,
                    height: 28,
                    border: "1px solid #7f9db9",
                    padding: "2px 4px",
                    fontFamily: "Tahoma, sans-serif",
                    fontSize: "12px",
                  }}
                >
                  {DEFAULT_THEMES.map((theme) => (
                    <option key={theme.id} value={theme.id}>
                      {theme.name}
                    </option>
                  ))}
                </select>
              </div>

              {/* Automatic slide show checkbox */}
              <div>
                <label
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "6px",
                    fontFamily: "Georgia, serif",
                    fontSize: "14px",
                    cursor: "pointer",
                  }}
                >
                  <input
                    type="checkbox"
                    checked={isAutomatic}
                    onChange={(e) => setIsAutomatic(e.target.checked)}
                  />
                  Apresentação automática de slides
                </label>
              </div>

              {/* Repeat checkbox (indented by 25px) */}
              <div style={{ paddingLeft: "25px" }}>
                <label
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "6px",
                    fontFamily: "Georgia, serif",
                    fontSize: "14px",
                    opacity: isAutomatic ? 1 : 0.5,
                    cursor: isAutomatic ? "pointer" : "default",
                  }}
                >
                  <input
                    type="checkbox"
                    disabled={!isAutomatic}
                    checked={repeat}
                    onChange={(e) => setRepeat(e.target.checked)}
                  />
                  Repetir
                </label>
              </div>

              {/* Seconds spinner (indented by 25px) */}
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "6px",
                  paddingLeft: "25px",
                  fontFamily: "Georgia, serif",
                  fontSize: "14px",
                  opacity: isAutomatic ? 1 : 0.5,
                }}
              >
                <input
                  type="number"
                  min={2}
                  max={60}
                  disabled={!isAutomatic}
                  value={seconds}
                  onChange={(e) =>
                    setSeconds(parseInt(e.target.value, 10) || 2)
                  }
                  style={{
                    width: "48px",
                    padding: "2px 4px",
                    border: "1px solid #7f9db9",
                  }}
                />
                <span>segundos</span>
              </div>

              {/* Separator */}
              <div style={{ height: "1px", backgroundColor: "#ccc" }} />

              {/* Show button */}
              <WebButton
                onClick={handleShow}
                style={{
                  padding: "6px 16px",
                  fontFamily: "Georgia, serif",
                  fontSize: "14px",
                  fontWeight: "bold",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "6px",
                }}
              >
                <span>▶</span>
                <span>Exibir (Shift + Enter)</span>
              </WebButton>
            </div>
          </div>

          {/* Right Side: MultiSlidePreview */}
          <div
            style={{
              flex: 1,
              backgroundColor: "#2e2e2e",
              display: "flex",
              flexDirection: "column",
              padding: "10px",
              overflowY: "auto",
              gap: "12px",
            }}
          >
            <div
              style={{
                color: "#ccc",
                fontSize: "11px",
                fontWeight: "bold",
                textTransform: "uppercase",
                letterSpacing: "0.5px",
              }}
            >
              Pré-visualização dos Slides ({slides.length}{" "}
              {slides.length === 1 ? "slide" : "slides"})
            </div>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))",
                gap: "12px",
              }}
            >
              {slides.map((slideText, index) => (
                <div
                  key={index}
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "4px",
                  }}
                >
                  <div
                    style={{
                      aspectRatio: "16 / 9",
                      backgroundColor: selectedTheme.backgroundColor,
                      color: selectedTheme.textColor,
                      fontFamily: selectedTheme.fontFamily,
                      border: "2px solid #555",
                      boxShadow: "0 2px 6px rgba(0,0,0,0.4)",
                      padding: "8px",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      textAlign: "center",
                      fontSize: "11px",
                      lineHeight: "1.3",
                      whiteSpace: "pre-wrap",
                      overflow: "hidden",
                      userSelect: "none",
                    }}
                  >
                    {slideText || "(Slide vazio)"}
                  </div>
                  <div
                    style={{
                      fontSize: "10px",
                      color: "#aaa",
                      textAlign: "center",
                    }}
                  >
                    Slide {index + 1}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default QuickPresentationDialog;
