import React, { useState } from "react";
import {
  JFrame,
  JPanel,
  JSplitPane,
  HORIZONTAL_SPLIT,
  JTabbedPane,
} from "../swing";

export interface MusicItem {
  id: string;
  title: string;
  artist: string;
  key?: string;
  lyrics?: string;
}

export interface SlideSection {
  title: string;
  content: string;
}

export interface MusicMainProps {
  title?: string;
  onClose?: () => void;
  initialMusics?: MusicItem[];
}

export const MusicMain: React.FC<MusicMainProps> = ({
  title = "Holyrics",
  onClose,
  initialMusics = [
    { id: "1", title: "Porque Ele Vive", artist: "Harpa Cristã", key: "G", lyrics: "Deus enviou Seu Filho amado\nPra perdoar, pra me salvar\n\nPorque Ele vive, posso crer no amanhã\nPorque Ele vive, temor não há" },
    { id: "2", title: "Grandioso És Tu", artist: "Harpa Cristã", key: "A", lyrics: "Senhor meu Deus, quando eu maravilhado\nFico a pensar nas obras de Tuas mãos\n\nEntão a minh'alma canta a Ti, Senhor\nGrandioso és Tu! Grandioso és Tu!" },
    { id: "3", title: "Em Espírito, Em Verdade", artist: "Ministério Koinonya", key: "D", lyrics: "Em espírito, em verdade\nTe adoramos, Te adoramos\n\nRei dos reis e Senhor\nTe entregamos nosso viver" },
  ],
}) => {
  const [musics] = useState<MusicItem[]>(initialMusics);
  const [selectedMusicId, setSelectedMusicId] = useState<string | null>(musics[0]?.id ?? null);
  const [searchTerm, setSearchTerm] = useState("");
  const [activeSlideIndex, setActiveSlideIndex] = useState<number | null>(0);

  const selectedMusic = musics.find((m) => m.id === selectedMusicId) || null;

  const slides: SlideSection[] = selectedMusic?.lyrics
    ? selectedMusic.lyrics
        .split(/\n\s*\n/)
        .filter((part) => part.trim().length > 0)
        .map((part, index) => {
          const lines = part.trim().split("\n");
          const firstLine = lines[0] || "";
          let sectionTitle = `Slide ${index + 1}`;
          if (firstLine.toLowerCase().includes("refrão") || firstLine.toLowerCase().includes("coro")) {
            sectionTitle = `Refrão`;
          }
          return {
            title: sectionTitle,
            content: part.trim(),
          };
        })
    : [];

  const filteredMusics = musics.filter(
    (m) =>
      m.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      m.artist.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <JFrame
      title={title}
      width="100%"
      height="100vh"
      onClose={onClose}
      style={{ display: "flex", flexDirection: "column", background: "#f0f0f0", userSelect: "none" }}
    >
      {/* 1. Top Menu Bar */}
      <div style={{ display: "flex", background: "#f0f0f0", borderBottom: "1px solid #d4d0c8", padding: "2px 4px", fontSize: "11px", gap: "8px", color: "#000" }}>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Arquivo</div>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Novo</div>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Editar</div>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Música</div>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Tema</div>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Anúncios</div>
        <div style={{ padding: "2px 6px", cursor: "pointer" }}>Escala...</div>
      </div>

      {/* Main Container */}
      <div style={{ flex: 1, minHeight: 0, display: "flex" }}>
        <JSplitPane
          orientation={HORIZONTAL_SPLIT}
          dividerLocation={310} // jSplitPaneMusicsAndMain: Left Sidebar (310) vs Rest
          style={{ width: "100%", height: "100%" }}
          left={
            /* Left Sidebar: jPanelSplitLeft */
            <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", background: "#f0f0f0" }}>
              {/* jPanelSplitLeftHeader */}
              <div style={{ padding: "4px", display: "flex", gap: "4px", borderBottom: "1px solid #a0a0a0", background: "#ece9d8" }}>
                <button style={{ flex: 1, fontSize: "11px", padding: "4px", cursor: "pointer" }}>⚡ Quick Presentation</button>
                <button style={{ flex: 1, fontSize: "11px", padding: "4px", cursor: "pointer" }}>📖 Bible</button>
              </div>
              
              {/* jPanelMusicFilter / tabbedMedia */}
              <JTabbedPane
                style={{ flex: 1, height: "100%", fontSize: "11px" }}
                tabs={[
                  {
                    title: "Músicas",
                    content: (
                      <JPanel style={{ display: "flex", flexDirection: "column", height: "100%", padding: "4px", background: "#ece9d8" }}>
                        {/* MusicSearchPanel */}
                        <div style={{ display: "flex", flexDirection: "column", gap: "4px", marginBottom: "4px" }}>
                          <input
                            type="text"
                            placeholder="Pesquisar..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{ flex: 1, fontSize: "11px", padding: "4px", border: "1px solid #7a7a7a" }}
                          />
                          <div style={{ display: "flex", gap: "4px", fontSize: "10px", flexWrap: "wrap" }}>
                            <label><input type="checkbox" /> Title</label>
                            <label><input type="checkbox" /> Lyrics</label>
                            <label><input type="checkbox" /> Artist</label>
                            <label><input type="checkbox" /> New Music</label>
                            <label><input type="checkbox" /> Edit</label>
                            <label><input type="checkbox" /> Delete</label>
                          </div>
                        </div>
                        {/* Music List */}
                        <div style={{ flex: 1, overflowY: "auto", border: "1px solid #a0a0a0", background: "#fff" }}>
                          {filteredMusics.map((m) => (
                            <div
                              key={m.id}
                              onClick={() => {
                                setSelectedMusicId(m.id);
                                setActiveSlideIndex(0);
                              }}
                              style={{
                                padding: "4px",
                                fontSize: "11px",
                                cursor: "pointer",
                                borderBottom: "1px solid #f0f0f0",
                                background: m.id === selectedMusicId ? "#0078d7" : "transparent",
                                color: m.id === selectedMusicId ? "#fff" : "#000",
                              }}
                            >
                              {m.title} <span style={{ color: m.id === selectedMusicId ? "#ccc" : "#666", fontSize: "10px" }}>- {m.artist}</span>
                            </div>
                          ))}
                        </div>
                      </JPanel>
                    )
                  },
                  { title: "Programação", content: <div style={{ background: "#ece9d8", height: "100%" }} /> },
                  { title: "Histórico", content: <div style={{ background: "#ece9d8", height: "100%" }} /> },
                ]}
              />
            </JPanel>
          }
          right={
            /* jPanelSplitRight */
            <JSplitPane
              orientation={HORIZONTAL_SPLIT}
              dividerLocation={458} // jSplitPaneMainAndRightColumn: Center vs Right Sidebar
              style={{ width: "100%", height: "100%" }}
              left={
                /* Center Area: jPanelMusic -> jPanelMusicMiddle */
                <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", background: "#ece9d8" }}>
                  {/* WebToolBar (jToolBarMusic) inside Center Area */}
                  <div style={{ 
                    height: "28px", 
                    background: "#ece9d8", 
                    borderBottom: "1px solid #a0a0a0", 
                    display: "flex", 
                    alignItems: "center", 
                    padding: "0 8px", 
                    gap: "6px" 
                  }}>
                    <button style={{ background: "transparent", border: "1px solid #a0a0a0", fontSize: "11px", cursor: "pointer", padding: "2px 6px" }}>▶ Play</button>
                    <button style={{ background: "transparent", border: "1px solid #a0a0a0", fontSize: "11px", cursor: "pointer", padding: "2px 6px" }}>⏸ Pause</button>
                    <div style={{ width: "1px", height: "16px", background: "#a0a0a0", margin: "0 4px" }} />
                    <button style={{ background: "transparent", border: "1px solid #a0a0a0", fontSize: "11px", cursor: "pointer", padding: "2px 6px" }}>Playlist Add</button>
                    <button style={{ background: "transparent", border: "1px solid #a0a0a0", fontSize: "11px", cursor: "pointer", padding: "2px 6px" }}>History Add</button>
                    <button style={{ background: "transparent", border: "1px solid #a0a0a0", fontSize: "11px", cursor: "pointer", padding: "2px 6px" }}>Edit</button>
                    <button style={{ background: "transparent", border: "1px solid #a0a0a0", fontSize: "11px", cursor: "pointer", padding: "2px 6px" }}>Delete</button>
                  </div>
                  
                  <div style={{ flex: 1, minHeight: 0 }}>
                    <JSplitPane
                      orientation={HORIZONTAL_SPLIT}
                      dividerLocation={150} // jSplitPaneMusicAndSlide: Lyrics vs Monitor
                      style={{ width: "100%", height: "100%" }}
                      left={
                        /* jPanelMusicLyrics */
                        <JPanel style={{ height: "100%", background: "#fff", borderRight: "1px solid #a0a0a0", overflowY: "auto" }}>
                          <div style={{ background: "#f0f0f0", padding: "4px", fontSize: "11px", fontWeight: "bold", borderBottom: "1px solid #a0a0a0" }}>
                            🎵 {selectedMusic?.title || "Lyrics"}
                          </div>
                          {slides.map((slide, idx) => (
                            <div
                              key={idx}
                              onClick={() => setActiveSlideIndex(idx)}
                              style={{
                                padding: "4px",
                                borderBottom: "1px solid #f0f0f0",
                                background: activeSlideIndex === idx ? "#e3f0ff" : "#fff",
                                color: "#000",
                                cursor: "pointer",
                              }}
                            >
                              <div style={{ color: "#0066cc", fontWeight: "bold", fontSize: "10px", marginBottom: "2px" }}>{slide.title}</div>
                              <div style={{ fontSize: "11px", whiteSpace: "pre-wrap", lineHeight: "1.2" }}>{slide.content}</div>
                            </div>
                          ))}
                        </JPanel>
                      }
                      right={
                        /* jPanelSlideView */
                        <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", padding: "6px", background: "#ece9d8" }}>
                          {/* Monitor Area */}
                          <div style={{ 
                            flex: 1, 
                            background: "#000000", 
                            border: "1px solid #333", 
                            display: "flex", 
                            alignItems: "center", 
                            justifyContent: "center", 
                            position: "relative" 
                          }}>
                            <div style={{ color: "#fff", textAlign: "center", fontSize: "24px", whiteSpace: "pre-wrap", width: "80%", textShadow: "1px 1px 3px #000" }}>
                              {slides[activeSlideIndex || 0]?.content}
                            </div>
                            
                            {/* VLC.PLAYER_CONTROL in a collapsible pane on center/right */}
                            <div style={{ 
                              position: "absolute", 
                              bottom: 0, 
                              left: 0, 
                              right: 0, 
                              height: "28px", 
                              background: "rgba(0,0,0,0.8)", 
                              borderTop: "1px solid #444", 
                              display: "flex", 
                              alignItems: "center", 
                              padding: "0 8px", 
                              gap: "8px", 
                              color: "#ccc", 
                              fontSize: "11px" 
                            }}>
                              <span style={{ fontWeight: "bold", color: "#fff" }}>VLC</span>
                              <span style={{ cursor: "pointer" }}>▶ Play</span>
                              <span style={{ cursor: "pointer" }}>⏸ Pause</span>
                              <span style={{ cursor: "pointer" }}>⏹ Stop</span>
                              <div style={{ flex: 1 }} />
                              <span>00:00</span>
                              <span>Vol: ||||||||||||</span>
                            </div>
                          </div>
                          
                          {/* Presentation Controller Footer Toolbar */}
                          <div style={{ 
                            marginTop: "6px", 
                            height: "28px", 
                            background: "#ece9d8", 
                            border: "1px solid #a0a0a0", 
                            display: "flex", 
                            alignItems: "center", 
                            padding: "0 4px", 
                            gap: "4px" 
                          }}>
                            <button style={{ fontSize: "11px", padding: "2px 6px", cursor: "pointer" }}>⏮ Previous</button>
                            <button style={{ fontSize: "11px", padding: "2px 6px", cursor: "pointer" }}>⏭ Next</button>
                            <div style={{ width: "1px", height: "16px", background: "#a0a0a0", margin: "0 4px" }} />
                            <button style={{ fontSize: "11px", padding: "2px 6px", cursor: "pointer" }}>Text Only</button>
                            <button style={{ fontSize: "11px", padding: "2px 6px", cursor: "pointer" }}>Image</button>
                          </div>
                        </JPanel>
                      }
                    />
                  </div>
                </JPanel>
              }
              right={
                /* Far Right: jPanelRight */
                <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", background: "#ece9d8", borderLeft: "1px solid #a0a0a0" }}>
                  {/* jPanelPlayList */}
                  <div style={{ flex: 1, display: "flex", flexDirection: "column", borderBottom: "1px solid #a0a0a0" }}>
                    <div style={{ padding: "4px", background: "#f0f0f0", borderBottom: "1px solid #a0a0a0", fontSize: "11px" }}>
                      <select style={{ width: "100%", padding: "2px", fontSize: "11px" }}>
                        <option>Worship/Event</option>
                        <option>Culto Principal</option>
                      </select>
                    </div>
                    {/* jPanelPlayListCard */}
                    <div style={{ flex: 1, background: "#fff", margin: "4px", border: "1px solid #a0a0a0", padding: "4px", fontSize: "11px", overflowY: "auto" }}>
                      <div style={{ color: "#666", fontStyle: "italic", textAlign: "center", marginTop: "10px" }}>Playlist is empty</div>
                    </div>
                  </div>
                  
                  {/* jPanelTheme */}
                  <div style={{ flex: 1, display: "flex", flexDirection: "column", padding: "4px" }}>
                    <div style={{ fontSize: "11px", fontWeight: "bold", padding: "2px 0", textAlign: "center", borderBottom: "1px solid #ccc", marginBottom: "4px" }}>
                      Themes
                    </div>
                    <div style={{ flex: 1, background: "#fff", border: "1px solid #a0a0a0", overflowY: "auto", display: "grid", gridTemplateColumns: "1fr 1fr", gap: "4px", padding: "4px" }}>
                      {[1, 2, 3, 4, 5, 6].map((i) => (
                        <div key={i} style={{ height: "48px", background: "#333", color: "#fff", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "10px", border: "1px solid #000", cursor: "pointer" }}>
                          Theme {i}
                        </div>
                      ))}
                    </div>
                  </div>
                </JPanel>
              }
            />
          }
        />
      </div>
    </JFrame>
  );
};

export default MusicMain;
