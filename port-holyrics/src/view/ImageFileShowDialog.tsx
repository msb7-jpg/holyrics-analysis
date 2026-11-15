import React, { useState, useRef } from "react";
import { JPanel, WebButton, WebLabel } from "../swing";

export interface ImageFileShowDialogProps {
  visible?: boolean;
  onClose?: () => void;
  onShow?: (
    files: { name: string; url: string }[],
    isAuto: boolean,
    repeat: boolean,
    duration: number
  ) => void;
}

export const ImageFileShowDialog: React.FC<ImageFileShowDialogProps> = ({
  visible = true,
  onClose,
  onShow,
}) => {
  const [selectedFiles, setSelectedFiles] = useState<
    { name: string; url: string }[]
  >([]);
  const [activeFileIndex, setActiveFileIndex] = useState<number>(0);
  const [isAutomatic, setIsAutomatic] = useState<boolean>(false);
  const [repeat, setRepeat] = useState<boolean>(false);
  const [duration, setDuration] = useState<number>(5);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  if (!visible) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const filesArray = Array.from(e.target.files).map((f) => ({
        name: f.name,
        url: URL.createObjectURL(f),
      }));
      setSelectedFiles((prev) => [...prev, ...filesArray]);
    }
  };

  const handleRemoveFile = (index: number) => {
    setSelectedFiles((prev) => prev.filter((_, idx) => idx !== index));
    if (activeFileIndex >= selectedFiles.length - 1) {
      setActiveFileIndex(Math.max(0, selectedFiles.length - 2));
    }
  };

  const handleShow = () => {
    if (onShow) {
      onShow(selectedFiles, isAutomatic, repeat, duration);
    }
    if (onClose) {
      onClose();
    }
  };

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
          width: 650,
          minHeight: 550,
          maxHeight: "90vh",
          backgroundColor: "#f0f0f0",
          border: "2px solid #666",
          boxShadow: "3px 3px 10px rgba(0,0,0,0.5)",
          display: "flex",
          flexDirection: "column",
          fontFamily: "Tahoma, Georgia, sans-serif",
          fontSize: "12px",
        }}
      >
        {/* Title Bar */}
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
            <span>🖼️</span>
            <span>Apresentação de Imagens</span>
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

        {/* Center Panel */}
        <div
          style={{
            flex: 1,
            display: "flex",
            flexDirection: "column",
            padding: "10px",
            gap: "8px",
            overflow: "hidden",
          }}
        >
          {/* Header Label */}
          <div
            style={{
              textAlign: "center",
              fontFamily: "Georgia, serif",
              fontSize: "18px",
              color: "#222",
              margin: "4px 0",
            }}
          >
            Apresentação de Imagens
          </div>

          {/* Emulated JFileChooser */}
          <div
            style={{
              flex: 1,
              backgroundColor: "#fff",
              border: "1px solid #7f9db9",
              display: "flex",
              flexDirection: "column",
              overflow: "hidden",
            }}
          >
            {/* Toolbar for FileChooser */}
            <div
              style={{
                backgroundColor: "#f3f3f3",
                borderBottom: "1px solid #ddd",
                padding: "4px 8px",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                <span style={{ fontWeight: "bold" }}>Arquivos Selecionados:</span>
                <span style={{ color: "#666" }}>
                  ({selectedFiles.length} imagens)
                </span>
              </div>
              <div style={{ display: "flex", gap: "6px" }}>
                <input
                  type="file"
                  multiple
                  accept="image/png,image/jpeg,image/gif,image/bmp"
                  ref={fileInputRef}
                  style={{ display: "none" }}
                  onChange={handleFileChange}
                />
                <WebButton
                  onClick={() => fileInputRef.current?.click()}
                  style={{ padding: "2px 8px" }}
                >
                  📁 Selecionar Imagens...
                </WebButton>
                {selectedFiles.length > 0 && (
                  <WebButton
                    onClick={() => setSelectedFiles([])}
                    style={{ padding: "2px 8px" }}
                  >
                    Limpar
                  </WebButton>
                )}
              </div>
            </div>

            {/* List & Preview Split */}
            <div style={{ flex: 1, display: "flex", overflow: "hidden" }}>
              {/* File List */}
              <div
                style={{
                  width: "50%",
                  borderRight: "1px solid #ddd",
                  overflowY: "auto",
                  backgroundColor: "#ffffff",
                }}
              >
                {selectedFiles.length === 0 ? (
                  <div
                    style={{
                      padding: "24px",
                      textAlign: "center",
                      color: "#999",
                    }}
                  >
                    Nenhuma imagem selecionada.
                    <br />
                    Clique em "Selecionar Imagens" acima.
                  </div>
                ) : (
                  selectedFiles.map((file, idx) => (
                    <div
                      key={idx}
                      onClick={() => setActiveFileIndex(idx)}
                      style={{
                        padding: "4px 8px",
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        cursor: "pointer",
                        backgroundColor:
                          idx === activeFileIndex ? "#cce2ff" : "transparent",
                        borderBottom: "1px solid #f0f0f0",
                      }}
                    >
                      <span
                        style={{
                          textOverflow: "ellipsis",
                          overflow: "hidden",
                          whiteSpace: "nowrap",
                          maxWidth: "180px",
                        }}
                      >
                        {file.name}
                      </span>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleRemoveFile(idx);
                        }}
                        style={{
                          border: "none",
                          background: "none",
                          color: "#cc0000",
                          cursor: "pointer",
                          fontSize: "11px",
                        }}
                      >
                        ✕
                      </button>
                    </div>
                  ))
                )}
              </div>

              {/* Preview Box */}
              <div
                style={{
                  width: "50%",
                  backgroundColor: "#222",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  overflow: "hidden",
                  padding: "8px",
                }}
              >
                {selectedFiles[activeFileIndex] ? (
                  <img
                    src={selectedFiles[activeFileIndex].url}
                    alt={selectedFiles[activeFileIndex].name}
                    style={{
                      maxWidth: "100%",
                      maxHeight: "100%",
                      objectFit: "contain",
                    }}
                  />
                ) : (
                  <span style={{ color: "#777" }}>Prévia da Imagem</span>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Separator */}
        <div style={{ height: "1px", backgroundColor: "#ccc", margin: "0 10px" }} />

        {/* Settings & Show Area */}
        <div
          style={{
            padding: "8px 12px",
            display: "flex",
            flexDirection: "column",
            gap: "8px",
            alignItems: "center",
          }}
        >
          {/* Automatic settings panel */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "16px",
              flexWrap: "wrap",
            }}
          >
            <label
              style={{
                display: "flex",
                alignItems: "center",
                gap: "4px",
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

            <label
              style={{
                display: "flex",
                alignItems: "center",
                gap: "4px",
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

            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "4px",
                fontFamily: "Georgia, serif",
                fontSize: "14px",
                opacity: isAutomatic ? 1 : 0.5,
              }}
            >
              <input
                type="number"
                min={1}
                max={60}
                disabled={!isAutomatic}
                value={duration}
                onChange={(e) =>
                  setDuration(parseInt(e.target.value, 10) || 5)
                }
                style={{ width: "45px", padding: "2px 4px" }}
              />
              <span>segundos</span>
            </div>
          </div>

          <div
            style={{
              width: "100%",
              height: "1px",
              backgroundColor: "#ccc",
              margin: "2px 0",
            }}
          />

          {/* Show button */}
          <WebButton
            disabled={selectedFiles.length === 0}
            onClick={handleShow}
            style={{
              padding: "6px 20px",
              fontFamily: "Georgia, serif",
              fontSize: "14px",
              fontWeight: "bold",
              display: "flex",
              alignItems: "center",
              gap: "6px",
            }}
          >
            <span style={{ fontSize: "16px" }}>▶</span>
            Exibir
          </WebButton>
        </div>
      </div>
    </div>
  );
};

export default ImageFileShowDialog;
