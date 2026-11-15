import React, { useState, useEffect } from "react";
import {
  JFrame,
  JPanel,
  WebButton,
  WebLabel,
  JTabbedPane,
} from "../swing";

export type BackgroundType =
  | "color"
  | "image"
  | "my_image"
  | "my_video"
  | "pattern"
  | "video"
  | "transparent";

export interface BackgroundItem {
  id: string;
  type: BackgroundType;
  name: string;
  url?: string;
  color?: string;
  videoUrl?: string;
  thumbnailUrl?: string;
  custom?: boolean;
}

export interface BackgroundChoiceThemeProps {
  visible?: boolean;
  initialBackground?: BackgroundItem | null;
  onSelect?: (bg: BackgroundItem) => void;
  onClose?: () => void;
}

// Built-in sample backgrounds matching Holyrics stock resources
const STOCK_IMAGES: BackgroundItem[] = [
  { id: "img_mountains", type: "image", name: "Montanhas", url: "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=640&q=80" },
  { id: "img_cross_sky", type: "image", name: "Cruz e Céu", url: "https://images.unsplash.com/photo-1507692049790-de58290a4334?auto=format&fit=crop&w=640&q=80" },
  { id: "img_clouds", type: "image", name: "Nuvens Douradas", url: "https://images.unsplash.com/photo-1513002749550-c59d786b8e6c?auto=format&fit=crop&w=640&q=80" },
  { id: "img_stars", type: "image", name: "Estrelas e Galáxia", url: "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&w=640&q=80" },
  { id: "img_forest", type: "image", name: "Floresta", url: "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=640&q=80" },
  { id: "img_worship_hands", type: "image", name: "Adoração", url: "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?auto=format&fit=crop&w=640&q=80" },
  { id: "img_ocean_sunset", type: "image", name: "Pôr do Sol no Mar", url: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=640&q=80" },
  { id: "img_abstract_blue", type: "image", name: "Azul Abstrato", url: "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=640&q=80" },
];

const STOCK_PATTERNS: BackgroundItem[] = [
  { id: "pat_dark_mesh", type: "pattern", name: "Grade Escura", url: "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='20' height='20' viewBox='0 0 20 20'><rect width='20' height='20' fill='%231a1a1a'/><circle cx='10' cy='10' r='1.5' fill='%23333333'/></svg>" },
  { id: "pat_diagonal_stripes", type: "pattern", name: "Linhas Diagonais", url: "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'><rect width='16' height='16' fill='%23222222'/><path d='M0 16L16 0M0 0L16 16' stroke='%233a3a3a' stroke-width='1.5'/></svg>" },
  { id: "pat_hexagons", type: "pattern", name: "Hexágonos", url: "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'><rect width='24' height='24' fill='%23121212'/><polygon points='12,2 22,7 22,17 12,22 2,17 2,7' fill='none' stroke='%23303030' stroke-width='1'/></svg>" },
  { id: "pat_crosses", type: "pattern", name: "Cruzes Mínimas", url: "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='30' height='30' viewBox='0 0 30 30'><rect width='30' height='30' fill='%23181818'/><path d='M15 5v20M5 12h20' stroke='%23383838' stroke-width='1.5'/></svg>" },
  { id: "pat_carbon", type: "pattern", name: "Fibra de Carbono", url: "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='10' height='10' viewBox='0 0 10 10'><rect width='10' height='10' fill='%23202020'/><rect width='5' height='5' fill='%232c2c2c'/><rect x='5' y='5' width='5' height='5' fill='%232c2c2c'/></svg>" },
];

const STOCK_ANIMATIONS: BackgroundItem[] = [
  { id: "vid_particles", type: "video", name: "Partículas de Luz", url: "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=640&q=80", videoUrl: "particles.mp4" },
  { id: "vid_bokeh_worship", type: "video", name: "Bokeh Adoração", url: "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=640&q=80", videoUrl: "bokeh.mp4" },
  { id: "vid_light_beams", type: "video", name: "Feixes de Luz", url: "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=640&q=80", videoUrl: "light_rays.mp4" },
  { id: "vid_water_waves", type: "video", name: "Ondas Fluídicas", url: "https://images.unsplash.com/photo-1518837695005-2083093ee35b?auto=format&fit=crop&w=640&q=80", videoUrl: "fluid.mp4" },
];

const DEFAULT_MY_IMAGES: BackgroundItem[] = [
  { id: "my_img_1", type: "my_image", name: "Igreja Fachada.jpg", url: "https://images.unsplash.com/photo-1548625361-195fe5787e98?auto=format&fit=crop&w=640&q=80", custom: true },
  { id: "my_img_2", type: "my_image", name: "Ceia do Senhor.png", url: "https://images.unsplash.com/photo-1544427920-c49ccfb85579?auto=format&fit=crop&w=640&q=80", custom: true },
];

const DEFAULT_MY_VIDEOS: BackgroundItem[] = [
  { id: "my_vid_1", type: "my_video", name: "Loop_Louvor_Abertura.mp4", url: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=640&q=80", videoUrl: "loop_opening.mp4", custom: true },
];

const PALETTE_COLORS = [
  "#000000", "#1a1a1a", "#333333", "#4d4d4d", "#666666", "#808080", "#999999", "#b3b3b3", "#cccccc", "#ffffff",
  "#7f0000", "#990000", "#cc0000", "#ff0000", "#ff4d4d", "#ff8080", "#ffb3b3", "#ffe6e6", "#803300", "#cc5200",
  "#ff6600", "#ff8533", "#ffa366", "#ffd1b3", "#806600", "#b38f00", "#e6b800", "#ffcc00", "#ffd633", "#ffe066",
  "#004d00", "#008000", "#00b300", "#00e600", "#33ff33", "#66ff66", "#99ff99", "#ccffcc", "#004d4d", "#008080",
  "#00cccc", "#00ffff", "#33ffff", "#66ffff", "#99ffff", "#ccffff", "#000066", "#000099", "#0000cc", "#0000ff",
  "#3333ff", "#6666ff", "#9999ff", "#ccccff", "#330066", "#4c0080", "#660099", "#800080", "#9900cc", "#b300ff",
];

export const BackgroundChoiceTheme: React.FC<BackgroundChoiceThemeProps> = ({
  visible = true,
  initialBackground,
  onSelect,
  onClose,
}) => {
  const [selectedTab, setSelectedTab] = useState<string>("my_images");
  const [selectedItem, setSelectedItem] = useState<BackgroundItem | null>(
    initialBackground || { id: "color_black", type: "color", name: "Preto", color: "#000000" }
  );

  const [myImages, setMyImages] = useState<BackgroundItem[]>(DEFAULT_MY_IMAGES);
  const [myVideos, setMyVideos] = useState<BackgroundItem[]>(DEFAULT_MY_VIDEOS);
  const [customColor, setCustomColor] = useState<string>("#1a365d");

  useEffect(() => {
    if (initialBackground) {
      setSelectedItem(initialBackground);
      if (initialBackground.type === "my_image") setSelectedTab("my_images");
      else if (initialBackground.type === "my_video") setSelectedTab("my_videos");
      else if (initialBackground.type === "image") setSelectedTab("image");
      else if (initialBackground.type === "pattern") setSelectedTab("pattern");
      else if (initialBackground.type === "video") setSelectedTab("animation");
      else if (initialBackground.type === "color") {
        setSelectedTab("color");
        if (initialBackground.color) setCustomColor(initialBackground.color);
      } else if (initialBackground.type === "transparent") setSelectedTab("transparent");
    }
  }, [initialBackground]);

  if (!visible) return null;

  const handleSelect = (item: BackgroundItem) => {
    setSelectedItem(item);
  };

  const handleDoubleClick = (item: BackgroundItem) => {
    setSelectedItem(item);
    onSelect?.(item);
    onClose?.();
  };

  const handleConfirm = () => {
    if (selectedTab === "color") {
      const colorBg: BackgroundItem = {
        id: `color_${customColor}`,
        type: "color",
        name: `Cor ${customColor}`,
        color: customColor,
      };
      onSelect?.(colorBg);
    } else if (selectedTab === "transparent") {
      const transBg: BackgroundItem = {
        id: "bg_transparent",
        type: "transparent",
        name: "Transparente",
        color: "transparent",
      };
      onSelect?.(transBg);
    } else if (selectedItem) {
      onSelect?.(selectedItem);
    }
    onClose?.();
  };

  const handleAddMyImage = () => {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "image/*";
    input.onchange = (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (file) {
        const url = URL.createObjectURL(file);
        const newItem: BackgroundItem = {
          id: `my_img_${Date.now()}`,
          type: "my_image",
          name: file.name,
          url,
          custom: true,
        };
        setMyImages((prev) => [...prev, newItem]);
        setSelectedItem(newItem);
      }
    };
    input.click();
  };

  const handleAddMyVideo = () => {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "video/mp4,video/avi,video/mkv,video/webm";
    input.onchange = (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (file) {
        const url = URL.createObjectURL(file);
        const newItem: BackgroundItem = {
          id: `my_vid_${Date.now()}`,
          type: "my_video",
          name: file.name,
          url,
          videoUrl: url,
          custom: true,
        };
        setMyVideos((prev) => [...prev, newItem]);
        setSelectedItem(newItem);
      }
    };
    input.click();
  };

  const handleRemoveItem = (id: string, type: "my_image" | "my_video", e: React.MouseEvent) => {
    e.stopPropagation();
    if (window.confirm("Deseja realmente remover este arquivo?")) {
      if (type === "my_image") {
        setMyImages((prev) => prev.filter((i) => i.id !== id));
      } else {
        setMyVideos((prev) => prev.filter((i) => i.id !== id));
      }
      if (selectedItem?.id === id) {
        setSelectedItem(null);
      }
    }
  };

  const renderThumbnailGrid = (
    items: BackgroundItem[],
    onAdd?: () => void,
    addLabel?: string,
    isCustom?: "my_image" | "my_video"
  ) => {
    return (
      <div
        style={{
          backgroundColor: "#1e1e1e",
          padding: 16,
          height: 380,
          overflowY: "auto",
          display: "flex",
          flexWrap: "wrap",
          alignContent: "flex-start",
          gap: 16,
        }}
      >
        {items.map((item) => {
          const isSelected = selectedItem?.id === item.id;
          return (
            <div
              key={item.id}
              onClick={() => handleSelect(item)}
              onDoubleClick={() => handleDoubleClick(item)}
              style={{
                width: 140,
                height: 105,
                backgroundColor: "#2a2a2a",
                border: isSelected ? "3px solid #ffcc00" : "1px solid #444",
                borderRadius: 3,
                cursor: "pointer",
                display: "flex",
                flexDirection: "column",
                overflow: "hidden",
                position: "relative",
                boxShadow: isSelected ? "0 0 8px rgba(255, 204, 0, 0.7)" : "0 2px 4px rgba(0,0,0,0.5)",
              }}
            >
              {/* Media preview */}
              <div
                style={{
                  flex: 1,
                  backgroundImage: item.url ? `url(${item.url})` : undefined,
                  backgroundColor: item.color || "#111",
                  backgroundSize: item.type === "pattern" ? "repeat" : "cover",
                  backgroundPosition: "center",
                  position: "relative",
                }}
              >
                {item.type === "video" || item.type === "my_video" ? (
                  <div
                    style={{
                      position: "absolute",
                      bottom: 4,
                      right: 4,
                      backgroundColor: "rgba(0,0,0,0.7)",
                      color: "#fff",
                      fontSize: 10,
                      padding: "1px 4px",
                      borderRadius: 2,
                    }}
                  >
                    ▶ VÍDEO
                  </div>
                ) : null}

                {/* Delete button for user uploaded items */}
                {isCustom && item.custom && (
                  <button
                    onClick={(e) => handleRemoveItem(item.id, isCustom, e)}
                    style={{
                      position: "absolute",
                      top: 2,
                      right: 2,
                      width: 18,
                      height: 18,
                      backgroundColor: "rgba(200, 0, 0, 0.8)",
                      color: "#fff",
                      border: "none",
                      borderRadius: "50%",
                      fontSize: 11,
                      lineHeight: "16px",
                      cursor: "pointer",
                      padding: 0,
                    }}
                    title="Excluir"
                  >
                    ×
                  </button>
                )}
              </div>

              {/* Title */}
              <div
                style={{
                  height: 22,
                  backgroundColor: isSelected ? "#3d3d3d" : "#222",
                  color: isSelected ? "#fff" : "#ccc",
                  fontSize: 11,
                  padding: "2px 4px",
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  textAlign: "center",
                }}
              >
                {item.name}
              </div>
            </div>
          );
        })}

        {/* Add button */}
        {onAdd && (
          <div
            onClick={onAdd}
            style={{
              width: 140,
              height: 105,
              backgroundColor: "#262626",
              border: "1px dashed #666",
              borderRadius: 3,
              cursor: "pointer",
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              gap: 8,
              color: "#aaa",
              padding: 8,
              textAlign: "center",
              userSelect: "none",
            }}
          >
            <div style={{ fontSize: 28, color: "#fff" }}>+</div>
            <div style={{ fontSize: 11 }}>{addLabel || "Adicionar"}</div>
          </div>
        )}
      </div>
    );
  };

  const renderColorTab = () => {
    return (
      <div
        style={{
          padding: 24,
          backgroundColor: "#f5f5f5",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 20,
          minHeight: 380,
        }}
      >
        <div style={{ fontWeight: "bold", fontSize: 16, color: "#222" }}>
          Seletor de Cor Sólida
        </div>

        {/* Color Palette */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(10, 28px)",
            gap: 4,
            padding: 8,
            backgroundColor: "#fff",
            border: "1px solid #ccc",
            borderRadius: 3,
            boxShadow: "inset 0 1px 3px rgba(0,0,0,0.1)",
          }}
        >
          {PALETTE_COLORS.map((c) => (
            <div
              key={c}
              onClick={() => setCustomColor(c)}
              style={{
                width: 28,
                height: 28,
                backgroundColor: c,
                border: customColor.toLowerCase() === c.toLowerCase() ? "2px solid #ffcc00" : "1px solid #999",
                cursor: "pointer",
                boxSizing: "border-box",
              }}
              title={c}
            />
          ))}
        </div>

        {/* Custom hex / color picker preview */}
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <div
            style={{
              width: 60,
              height: 40,
              backgroundColor: customColor,
              border: "2px solid #333",
              borderRadius: 2,
            }}
          />
          <div>
            <div style={{ fontSize: 12, color: "#666", marginBottom: 4 }}>Cor Selecionada (Hex):</div>
            <input
              type="text"
              value={customColor}
              onChange={(e) => setCustomColor(e.target.value)}
              style={{
                width: 100,
                height: 26,
                padding: "2px 6px",
                border: "1px solid #999",
                fontSize: 13,
                fontFamily: "monospace",
              }}
            />
          </div>
          <div>
            <input
              type="color"
              value={customColor.startsWith("#") ? customColor : "#000000"}
              onChange={(e) => setCustomColor(e.target.value)}
              style={{ width: 44, height: 32, cursor: "pointer" }}
            />
          </div>
        </div>
      </div>
    );
  };

  const renderTransparentTab = () => {
    return (
      <div
        style={{
          padding: 32,
          backgroundColor: "#f5f5f5",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: 380,
          gap: 24,
          textAlign: "center",
        }}
      >
        <div
          style={{
            maxWidth: 480,
            fontSize: 13,
            color: "#444",
            lineHeight: "1.6",
            backgroundColor: "#fff",
            padding: 16,
            border: "1px solid #ddd",
            borderRadius: 4,
          }}
        >
          <div style={{ fontWeight: "bold", fontSize: 14, color: "#111", marginBottom: 8 }}>
            Projeção com Fundo Transparente
          </div>
          O fundo transparente é ideal para sobreposições em softwares de transmissão ao vivo
          (OBS Studio, vMix, Wirecast) e mesas de corte com chave de croma ou NDI. As letras e
          elementos do tema serão exibidos normalmente sem nenhuma cor de fundo.
        </div>

        <WebButton
          onClick={() => {
            const transBg: BackgroundItem = {
              id: "bg_transparent",
              type: "transparent",
              name: "Transparente",
              color: "transparent",
            };
            onSelect?.(transBg);
            onClose?.();
          }}
          style={{
            fontSize: 16,
            fontFamily: "Georgia, serif",
            padding: "8px 24px",
            height: 38,
            backgroundColor: "#e8e8e8",
          }}
        >
          Transparente
        </WebButton>
      </div>
    );
  };

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        zIndex: 9000,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose?.();
        }
      }}
    >
      <div
        style={{
          width: 780,
          backgroundColor: "#ece9d8",
          border: "2px solid #0055ea",
          boxShadow: "0 6px 20px rgba(0,0,0,0.6)",
          display: "flex",
          flexDirection: "column",
          fontFamily: "Arial, Tahoma, sans-serif",
          userSelect: "none",
        }}
      >
        {/* Title bar (Classic Swing Dialog) */}
        <div
          style={{
            backgroundColor: "#0055ea",
            backgroundImage: "linear-gradient(to bottom, #2b7af9, #0055ea)",
            color: "#fff",
            fontWeight: "bold",
            padding: "4px 8px",
            fontSize: 13,
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <span>Plano de Fundo</span>
          <button
            onClick={onClose}
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

        {/* Tab Headers */}
        <div
          style={{
            display: "flex",
            backgroundColor: "#ece9d8",
            borderBottom: "1px solid #999",
            padding: "4px 6px 0 6px",
            gap: 2,
          }}
        >
          {[
            { id: "my_images", label: "Minhas Imagens" },
            { id: "my_videos", label: "Meus Vídeos" },
            { id: "image", label: "Imagens" },
            { id: "pattern", label: "Padrões" },
            { id: "animation", label: "Animações" },
            { id: "color", label: "Cor" },
            { id: "transparent", label: "Transparente" },
          ].map((tab) => {
            const isActive = selectedTab === tab.id;
            return (
              <div
                key={tab.id}
                onClick={() => setSelectedTab(tab.id)}
                style={{
                  padding: "5px 14px",
                  fontSize: 12,
                  fontFamily: "Arial, sans-serif",
                  fontWeight: isActive ? "bold" : "normal",
                  color: isActive ? "#000" : "#333",
                  backgroundColor: isActive ? "#fff" : "#e0ded0",
                  borderTop: isActive ? "2px solid #ff9900" : "1px solid #bbb",
                  borderLeft: "1px solid #bbb",
                  borderRight: "1px solid #bbb",
                  borderBottom: isActive ? "1px solid #fff" : "none",
                  marginBottom: isActive ? -1 : 0,
                  cursor: "pointer",
                  borderTopLeftRadius: 3,
                  borderTopRightRadius: 3,
                }}
              >
                {tab.label}
              </div>
            );
          })}
        </div>

        {/* Tab Content */}
        <div style={{ backgroundColor: "#fff", borderBottom: "1px solid #bbb" }}>
          {selectedTab === "my_images" &&
            renderThumbnailGrid(myImages, handleAddMyImage, "Clique para adicionar imagem", "my_image")}
          {selectedTab === "my_videos" &&
            renderThumbnailGrid(myVideos, handleAddMyVideo, "Clique para adicionar vídeo", "my_video")}
          {selectedTab === "image" && renderThumbnailGrid(STOCK_IMAGES)}
          {selectedTab === "pattern" && renderThumbnailGrid(STOCK_PATTERNS)}
          {selectedTab === "animation" && renderThumbnailGrid(STOCK_ANIMATIONS)}
          {selectedTab === "color" && renderColorTab()}
          {selectedTab === "transparent" && renderTransparentTab()}
        </div>

        {/* Footer */}
        <div
          style={{
            padding: "8px 16px",
            backgroundColor: "#ece9d8",
            display: "flex",
            justifyContent: "flex-end",
            gap: 10,
          }}
        >
          <WebButton
            onClick={handleConfirm}
            style={{
              minWidth: 85,
              height: 28,
              fontSize: 14,
              fontFamily: "Georgia, serif",
            }}
          >
            OK
          </WebButton>
          <WebButton
            onClick={onClose}
            style={{
              minWidth: 85,
              height: 28,
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

export default BackgroundChoiceTheme;
