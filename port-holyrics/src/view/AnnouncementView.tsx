import React, { useState, useEffect } from "react";
import { JPanel, JTabbedPane, WebButton, WebLabel } from "../swing";

export interface AnnouncementItem {
  id: string;
  title: string;
  text: string;
  duration?: number;
  image?: string;
  theme?: string;
}

export interface AnnouncementViewProps {
  visible?: boolean;
  onClose?: () => void;
  callback?: () => void;
  initialAnnouncements?: AnnouncementItem[];
}

export const AnnouncementView: React.FC<AnnouncementViewProps> = ({
  visible = true,
  onClose,
  callback,
  initialAnnouncements = [
    {
      id: "1",
      title: "Aviso 1",
      text: "Bem-vindos ao culto deste domingo!\nParticipe das nossas programações.",
      duration: 5,
    },
  ],
}) => {
  const [announcements, setAnnouncements] =
    useState<AnnouncementItem[]>(initialAnnouncements);
  const [selectedIndex, setSelectedIndex] = useState<number>(0);

  if (!visible) return null;

  const handleClose = () => {
    if (callback) callback();
    if (onClose) onClose();
  };

  const handleCreate = () => {
    const newId = String(Date.now());
    const newAn: AnnouncementItem = {
      id: newId,
      title: `Aviso ${announcements.length + 1}`,
      text: "",
      duration: 5,
    };
    const nextList = [...announcements, newAn];
    setAnnouncements(nextList);
    setSelectedIndex(nextList.length - 1);
  };

  const handleRemove = (index: number) => {
    const nextList = announcements.filter((_, i) => i !== index);
    setAnnouncements(nextList);
    if (selectedIndex >= nextList.length) {
      setSelectedIndex(Math.max(0, nextList.length - 1));
    }
  };

  const handleMove = (from: number, to: number) => {
    if (to < 0 || to >= announcements.length) return;
    const nextList = [...announcements];
    const item = nextList.splice(from, 1)[0];
    if (item) {
      nextList.splice(to, 0, item);
      setAnnouncements(nextList);
      setSelectedIndex(to);
    }
  };

  const handleTextChange = (text: string) => {
    const current = announcements[selectedIndex];
    if (!current) return;
    const nextList = [...announcements];
    nextList[selectedIndex] = { ...current, text };
    setAnnouncements(nextList);
  };

  const handleTitleChange = (title: string) => {
    const current = announcements[selectedIndex];
    if (!current) return;
    const nextList = [...announcements];
    nextList[selectedIndex] = { ...current, title };
    setAnnouncements(nextList);
  };

  const handleDurationChange = (duration: number) => {
    const current = announcements[selectedIndex];
    if (!current) return;
    const nextList = [...announcements];
    nextList[selectedIndex] = { ...current, duration };
    setAnnouncements(nextList);
  };

  const currentAnnouncement = announcements[selectedIndex];

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
          width: 1050,
          minHeight: 520,
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
        {/* Title Bar simulating Swing JDialog */}
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
            <span style={{ fontSize: "14px" }}>📢</span>
            <span>Avisos</span>
          </div>
          <button
            onClick={handleClose}
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

        {/* Header Toolbar */}
        <div
          style={{
            padding: "6px 10px",
            borderBottom: "1px solid #bbb",
            backgroundColor: "#e8e8e8",
            display: "flex",
            alignItems: "center",
            gap: "12px",
          }}
        >
          <WebButton
            onClick={handleCreate}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "6px",
              padding: "4px 12px",
              fontFamily: "Georgia, serif",
              fontSize: "14px",
              fontWeight: "bold",
            }}
          >
            <span style={{ color: "#228822", fontSize: "16px" }}>➕</span>
            Criar
          </WebButton>

          {announcements.length >= 100 && (
            <span
              style={{
                fontFamily: "Georgia, serif",
                fontSize: "12px",
                color: "#990000",
                fontWeight: "bold",
              }}
            >
              Limite de memória alcançado
            </span>
          )}
        </div>

        {/* Main Content Area: Left Tab list / Editor */}
        <div
          style={{
            flex: 1,
            display: "flex",
            overflow: "hidden",
            padding: "8px",
            gap: "8px",
          }}
        >
          {/* Left Vertical Tab / Navigation list */}
          <div
            style={{
              width: 260,
              backgroundColor: "#ffffff",
              border: "1px solid #999",
              display: "flex",
              flexDirection: "column",
              overflowY: "auto",
            }}
          >
            <div
              style={{
                backgroundColor: "#e0e0e0",
                padding: "4px 8px",
                fontWeight: "bold",
                borderBottom: "1px solid #ccc",
              }}
            >
              Lista de Avisos ({announcements.length})
            </div>
            {announcements.map((an, idx) => {
              const isSelected = idx === selectedIndex;
              return (
                <div
                  key={an.id}
                  onClick={() => setSelectedIndex(idx)}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    padding: "6px 8px",
                    borderBottom: "1px solid #eee",
                    backgroundColor: isSelected ? "#cce2ff" : "transparent",
                    cursor: "pointer",
                    borderLeft: isSelected ? "4px solid #3366cc" : "4px solid transparent",
                  }}
                >
                  <span
                    style={{
                      fontWeight: isSelected ? "bold" : "normal",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                      maxWidth: "140px",
                    }}
                  >
                    {an.title || "(Sem título)"}
                  </span>
                  <div
                    style={{ display: "flex", gap: "2px" }}
                    onClick={(e) => e.stopPropagation()}
                  >
                    <button
                      disabled={idx === 0}
                      onClick={() => handleMove(idx, idx - 1)}
                      title="Mover para cima"
                      style={{
                        padding: "0 4px",
                        fontSize: "10px",
                        cursor: idx === 0 ? "default" : "pointer",
                      }}
                    >
                      ▲
                    </button>
                    <button
                      disabled={idx === announcements.length - 1}
                      onClick={() => handleMove(idx, idx + 1)}
                      title="Mover para baixo"
                      style={{
                        padding: "0 4px",
                        fontSize: "10px",
                        cursor:
                          idx === announcements.length - 1
                            ? "default"
                            : "pointer",
                      }}
                    >
                      ▼
                    </button>
                    <button
                      onClick={() => handleRemove(idx)}
                      title="Excluir aviso"
                      style={{
                        padding: "0 4px",
                        fontSize: "10px",
                        color: "#cc0000",
                        cursor: "pointer",
                      }}
                    >
                      ✕
                    </button>
                  </div>
                </div>
              );
            })}
            {announcements.length === 0 && (
              <div style={{ padding: "16px", color: "#888", textAlign: "center" }}>
                Nenhum aviso criado. Clique em "Criar".
              </div>
            )}
          </div>

          {/* Right Announcement Editor */}
          <div
            style={{
              flex: 1,
              backgroundColor: "#ffffff",
              border: "1px solid #999",
              display: "flex",
              flexDirection: "column",
              padding: "10px",
              gap: "8px",
            }}
          >
            {currentAnnouncement ? (
              <>
                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                  <WebLabel style={{ fontWeight: "bold", width: 60 }}>
                    Título:
                  </WebLabel>
                  <input
                    type="text"
                    value={currentAnnouncement.title}
                    onChange={(e) => handleTitleChange(e.target.value)}
                    style={{
                      flex: 1,
                      padding: "4px 6px",
                      border: "1px solid #7f9db9",
                      fontSize: "13px",
                    }}
                  />
                  <WebLabel style={{ fontWeight: "bold" }}>
                    Tempo (seg):
                  </WebLabel>
                  <input
                    type="number"
                    min={1}
                    max={120}
                    value={currentAnnouncement.duration || 5}
                    onChange={(e) =>
                      handleDurationChange(parseInt(e.target.value, 10) || 5)
                    }
                    style={{
                      width: 50,
                      padding: "4px",
                      border: "1px solid #7f9db9",
                    }}
                  />
                </div>

                <WebLabel style={{ fontWeight: "bold" }}>
                  Conteúdo do Aviso:
                </WebLabel>
                <textarea
                  value={currentAnnouncement.text}
                  onChange={(e) => handleTextChange(e.target.value)}
                  style={{
                    flex: 1,
                    resize: "none",
                    fontFamily: "Georgia, serif",
                    fontSize: "14px",
                    padding: "8px",
                    border: "1px solid #7f9db9",
                  }}
                  placeholder="Digite o texto do aviso aqui..."
                />

                {/* Preview miniature simulating Holyrics slide */}
                <div
                  style={{
                    height: 120,
                    backgroundColor: "#111",
                    color: "#fff",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    textAlign: "center",
                    padding: "12px",
                    border: "1px solid #444",
                    fontSize: "14px",
                    whiteSpace: "pre-wrap",
                    overflow: "hidden",
                  }}
                >
                  {currentAnnouncement.text || "(Aviso sem texto)"}
                </div>
              </>
            ) : (
              <div
                style={{
                  flex: 1,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  color: "#888",
                }}
              >
                Selecione ou crie um aviso.
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div
          style={{
            padding: "8px 12px",
            backgroundColor: "#e8e8e8",
            borderTop: "1px solid #bbb",
            display: "flex",
            justifyContent: "flex-end",
            gap: "8px",
          }}
        >
          <WebButton
            onClick={handleClose}
            style={{ minWidth: 80, padding: "4px 12px" }}
          >
            Fechar
          </WebButton>
        </div>
      </div>
    </div>
  );
};

export default AnnouncementView;
