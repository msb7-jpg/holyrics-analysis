import React, { useState } from "react";
import {
  JFrame,
  JPanel,
  JSplitPane,
  HORIZONTAL_SPLIT,
  VERTICAL_SPLIT,
  JTabbedPane,
  JTab,
  WebButton,
  WebLabel,
  WebComboBox,
} from "../swing";

export interface BibleBook {
  id: number;
  name: string;
  abbrev: string;
  testament: "OT" | "NT";
  chaptersCount: number;
}

export interface BibleVerse {
  bookId: number;
  chapter: number;
  verse: number;
  text: string;
}

export interface BibleMainProps {
  title?: string;
  onClose?: () => void;
  onGoToHolyrics?: () => void;
}

const BOOKS_DATA: BibleBook[] = [
  // Antigo Testamento (39)
  { id: 1, name: "Gênesis", abbrev: "Gn", testament: "OT", chaptersCount: 50 },
  { id: 2, name: "Êxodo", abbrev: "Êx", testament: "OT", chaptersCount: 40 },
  { id: 3, name: "Levítico", abbrev: "Lv", testament: "OT", chaptersCount: 27 },
  { id: 4, name: "Números", abbrev: "Nm", testament: "OT", chaptersCount: 36 },
  { id: 5, name: "Deuteronômio", abbrev: "Dt", testament: "OT", chaptersCount: 34 },
  { id: 6, name: "Josué", abbrev: "Js", testament: "OT", chaptersCount: 24 },
  { id: 7, name: "Juízes", abbrev: "Jz", testament: "OT", chaptersCount: 21 },
  { id: 8, name: "Rute", abbrev: "Rt", testament: "OT", chaptersCount: 4 },
  { id: 9, name: "1 Samuel", abbrev: "1Sm", testament: "OT", chaptersCount: 31 },
  { id: 10, name: "2 Samuel", abbrev: "2Sm", testament: "OT", chaptersCount: 24 },
  { id: 11, name: "1 Reis", abbrev: "1Rs", testament: "OT", chaptersCount: 22 },
  { id: 12, name: "2 Reis", abbrev: "2Rs", testament: "OT", chaptersCount: 25 },
  { id: 13, name: "1 Crônicas", abbrev: "1Cr", testament: "OT", chaptersCount: 29 },
  { id: 14, name: "2 Crônicas", abbrev: "2Cr", testament: "OT", chaptersCount: 36 },
  { id: 15, name: "Esdras", abbrev: "Ed", testament: "OT", chaptersCount: 10 },
  { id: 16, name: "Neemias", abbrev: "Ne", testament: "OT", chaptersCount: 13 },
  { id: 17, name: "Ester", abbrev: "Et", testament: "OT", chaptersCount: 10 },
  { id: 18, name: "Jó", abbrev: "Jó", testament: "OT", chaptersCount: 42 },
  { id: 19, name: "Salmos", abbrev: "Sl", testament: "OT", chaptersCount: 150 },
  { id: 20, name: "Provérbios", abbrev: "Pv", testament: "OT", chaptersCount: 31 },
  { id: 21, name: "Eclesiastes", abbrev: "Ec", testament: "OT", chaptersCount: 12 },
  { id: 22, name: "Cantares", abbrev: "Ct", testament: "OT", chaptersCount: 8 },
  { id: 23, name: "Isaías", abbrev: "Is", testament: "OT", chaptersCount: 66 },
  { id: 24, name: "Jeremias", abbrev: "Jr", testament: "OT", chaptersCount: 52 },
  { id: 25, name: "Lamentações", abbrev: "Lm", testament: "OT", chaptersCount: 5 },
  { id: 26, name: "Ezequiel", abbrev: "Ez", testament: "OT", chaptersCount: 48 },
  { id: 27, name: "Daniel", abbrev: "Dn", testament: "OT", chaptersCount: 12 },
  { id: 28, name: "Oséias", abbrev: "Os", testament: "OT", chaptersCount: 14 },
  { id: 29, name: "Joel", abbrev: "Jl", testament: "OT", chaptersCount: 3 },
  { id: 30, name: "Amós", abbrev: "Am", testament: "OT", chaptersCount: 9 },
  { id: 31, name: "Obadias", abbrev: "Ob", testament: "OT", chaptersCount: 1 },
  { id: 32, name: "Jonas", abbrev: "Jn", testament: "OT", chaptersCount: 4 },
  { id: 33, name: "Miqueias", abbrev: "Mq", testament: "OT", chaptersCount: 7 },
  { id: 34, name: "Naum", abbrev: "Na", testament: "OT", chaptersCount: 3 },
  { id: 35, name: "Habacuque", abbrev: "Hc", testament: "OT", chaptersCount: 3 },
  { id: 36, name: "Sofonias", abbrev: "Sf", testament: "OT", chaptersCount: 3 },
  { id: 37, name: "Ageu", abbrev: "Ag", testament: "OT", chaptersCount: 2 },
  { id: 38, name: "Zacarias", abbrev: "Zc", testament: "OT", chaptersCount: 14 },
  { id: 39, name: "Malaquias", abbrev: "Ml", testament: "OT", chaptersCount: 4 },

  // Novo Testamento (27)
  { id: 40, name: "Mateus", abbrev: "Mt", testament: "NT", chaptersCount: 28 },
  { id: 41, name: "Marcos", abbrev: "Mc", testament: "NT", chaptersCount: 16 },
  { id: 42, name: "Lucas", abbrev: "Lc", testament: "NT", chaptersCount: 24 },
  { id: 43, name: "João", abbrev: "Jo", testament: "NT", chaptersCount: 21 },
  { id: 44, name: "Atos", abbrev: "At", testament: "NT", chaptersCount: 28 },
  { id: 45, name: "Romanos", abbrev: "Rm", testament: "NT", chaptersCount: 16 },
  { id: 46, name: "1 Coríntios", abbrev: "1Co", testament: "NT", chaptersCount: 16 },
  { id: 47, name: "2 Coríntios", abbrev: "2Co", testament: "NT", chaptersCount: 13 },
  { id: 48, name: "Gálatas", abbrev: "Gl", testament: "NT", chaptersCount: 6 },
  { id: 49, name: "Efésios", abbrev: "Ef", testament: "NT", chaptersCount: 6 },
  { id: 50, name: "Filipenses", abbrev: "Fp", testament: "NT", chaptersCount: 4 },
  { id: 51, name: "Colossenses", abbrev: "Cl", testament: "NT", chaptersCount: 4 },
  { id: 52, name: "1 Tessalonicenses", abbrev: "1Ts", testament: "NT", chaptersCount: 5 },
  { id: 53, name: "2 Tessalonicenses", abbrev: "2Ts", testament: "NT", chaptersCount: 3 },
  { id: 54, name: "1 Timóteo", abbrev: "1Tm", testament: "NT", chaptersCount: 6 },
  { id: 55, name: "2 Timóteo", abbrev: "2Tm", testament: "NT", chaptersCount: 4 },
  { id: 56, name: "Tito", abbrev: "Tt", testament: "NT", chaptersCount: 3 },
  { id: 57, name: "Filemom", abbrev: "Fm", testament: "NT", chaptersCount: 1 },
  { id: 58, name: "Hebreus", abbrev: "Hb", testament: "NT", chaptersCount: 13 },
  { id: 59, name: "Tiago", abbrev: "Tg", testament: "NT", chaptersCount: 5 },
  { id: 60, name: "1 Pedro", abbrev: "1Pe", testament: "NT", chaptersCount: 5 },
  { id: 61, name: "2 Pedro", abbrev: "2Pe", testament: "NT", chaptersCount: 3 },
  { id: 62, name: "1 João", abbrev: "1Jo", testament: "NT", chaptersCount: 5 },
  { id: 63, name: "2 João", abbrev: "2Jo", testament: "NT", chaptersCount: 1 },
  { id: 64, name: "3 João", abbrev: "3Jo", testament: "NT", chaptersCount: 1 },
  { id: 65, name: "Judas", abbrev: "Jd", testament: "NT", chaptersCount: 1 },
  { id: 66, name: "Apocalipse", abbrev: "Ap", testament: "NT", chaptersCount: 22 },
];

// Sample verses for demonstration
const SAMPLE_VERSES: Record<string, string[]> = {
  "1-1": [
    "No princípio criou Deus os céus e a terra.",
    "E a terra era sem forma e vazia; e havia trevas sobre a face do abismo; e o Espírito de Deus se movia sobre a face das águas.",
    "E disse Deus: Haja luz; e houve luz.",
    "E viu Deus que era boa a luz; e fez Deus separação entre a luz e as trevas.",
    "E Deus chamou à luz Dia; e às trevas chamou Noite. E foi a tarde e a manhã, o dia primeiro.",
  ],
  "19-23": [
    "O Senhor é o meu pastor, nada me faltará.",
    "Deitar-me faz em verdes pastos, guia-me mansamente a águas tranqüilas.",
    "Refrigera a minha alma; guia-me pelas veredas da justiça, por amor do seu nome.",
    "Ainda que eu andasse pelo vale da sombra da morte, não temeria mal algum, porque tu estás comigo; a tua vara e o teu cajado me consolam.",
    "Preparas uma mesa perante mim na presença dos meus inimigos, unges a minha cabeça com óleo, o meu cálice transborda.",
    "Certamente que a bondade e a misericórdia me seguirão todos os dias da minha vida; e habitarei na casa do Senhor por longos dias.",
  ],
  "43-3": [
    "E havia entre os fariseus um homem, chamado Nicodemos, príncipe dos judeus.",
    "Este foi ter de noite com Jesus, e disse-lhe: Rabi, bem sabemos que és Mestre, vindo de Deus; porque ninguém pode fazer estes sinais que tu fazes, se Deus não for com ele.",
    "Jesus respondeu, e disse-lhe: Na verdade, na verdade te digo que aquele que não nascer de novo, não pode ver o reino de Deus.",
    "Disse-lhe Nicodemos: Como pode um homem nascer, sendo velho? Porventura pode tornar a entrar no ventre de sua mãe, e nascer?",
    "Jesus respondeu: Na verdade, na verdade te digo que aquele que não nascer da água e do Espírito, não pode entrar no reino de Deus.",
    "O que é nascido da carne é carne, e o que é nascido do Espírito é espírito.",
    "Não te maravilhes de te ter dito: Necessário vos é nascer de novo.",
    "O vento assopra onde quer, e ouves a sua voz, mas não sabes de onde vem, nem para onde vai; assim é todo aquele que é nascido do Espírito.",
    "Nicodemos respondeu, e disse-lhe: Como pode ser isso?",
    "Jesus respondeu, e disse-lhe: Tu és mestre de Israel, e não sabes isto?",
    "Na verdade, na verdade te digo que nós dizemos o que sabemos, e testificamos o que vimos; e não aceitais o nosso testemunho.",
    "Se vos falei de coisas terrestres, e não crestes, como crereis, se vos falar das celestiais?",
    "Ora, ninguém subiu ao céu, senão o que desceu do céu, o Filho do homem, que está no céu.",
    "E, como Moisés levantou a serpente no deserto, assim importa que o Filho do homem seja levantado;",
    "Para que todo aquele que nele crê não pereça, mas tenha a vida eterna.",
    "Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.",
  ],
};

/**
 * 1:1 Isomorphic port of `com.limagiran.holyrics.view.BibleMain`
 *
 * Emulates the desktop Swing interface for the Holyrics Bible module:
 * - Top Menus (Arquivo, Versão, Janelas, Idioma, Ajuda)
 * - Main Toolbar with Show (F5), Add Playlist, Search, Themes, Settings
 * - Left Pane: Book selection (Abbr/Names), Chapter & Verse selectors
 * - Center Pane: Tabbed verses view (Default / Aux1 / Aux2 translations)
 * - Right/Bottom Pane: Quick Projection Monitor, Playlist / Saved verses
 */
export const BibleMain: React.FC<BibleMainProps> = ({
  title = "Holyrics - Bíblia",
  onClose,
  onGoToHolyrics,
}) => {
  const [selectedBookId, setSelectedBookId] = useState<number>(1);
  const [selectedChapter, setSelectedChapter] = useState<number>(1);
  const [selectedVerse, setSelectedVerse] = useState<number>(1);
  const [displayedVerse, setDisplayedVerse] = useState<BibleVerse | null>(null);
  const [isProjecting, setIsProjecting] = useState<boolean>(false);
  const [isBlackout, setIsBlackout] = useState<boolean>(false);
  const [isClearText, setIsClearText] = useState<boolean>(false);
  const [viewModeAbbrev, setViewModeAbbrev] = useState<boolean>(true);
  const [activeTabVersion, setActiveTabVersion] = useState<string>("ARC");
  const [selectedTheme, setSelectedTheme] = useState<string>("Tema Padrão");
  const [playlist, setPlaylist] = useState<BibleVerse[]>([]);
  const [isPlaylistExpanded, setIsPlaylistExpanded] = useState<boolean>(false);

  const selectedBook: BibleBook = BOOKS_DATA.find((b) => b.id === selectedBookId) || BOOKS_DATA[0]!;

  // Helper to retrieve sample verse texts or fallback
  const getVersesForChapter = (bookId: number, chap: number): BibleVerse[] => {
    const key = `${bookId}-${chap}`;
    const raw = SAMPLE_VERSES[key];
    if (raw) {
      return raw.map((text, idx) => ({
        bookId,
        chapter: chap,
        verse: idx + 1,
        text,
      }));
    }
    // Fallback: 20 synthetic verses
    return Array.from({ length: 20 }, (_, i) => ({
      bookId,
      chapter: chap,
      verse: i + 1,
      text: `Texto bíblico ilustrativo de ${selectedBook.name} capítulo ${chap}, versículo ${i + 1}.`,
    }));
  };

  const currentVerses = getVersesForChapter(selectedBookId, selectedChapter);

  const handleShowVerse = (v?: BibleVerse) => {
    const verseToShow = v || currentVerses.find((item) => item.verse === selectedVerse) || currentVerses[0];
    if (verseToShow) {
      setDisplayedVerse(verseToShow);
      setIsProjecting(true);
      setIsBlackout(false);
      setIsClearText(false);
    }
  };

  const handleAddToPlaylist = (v?: BibleVerse) => {
    const verseToAdd = v || currentVerses.find((item) => item.verse === selectedVerse) || currentVerses[0];
    if (verseToAdd && !playlist.some((p) => p.bookId === verseToAdd.bookId && p.chapter === verseToAdd.chapter && p.verse === verseToAdd.verse)) {
      setPlaylist([...playlist, verseToAdd]);
      setIsPlaylistExpanded(true);
    }
  };

  return (
    <JFrame
      title={title}
      width="100%"
      height="100vh"
      onClose={onClose}
      style={{ display: "flex", flexDirection: "column", background: "#f0f0f0", userSelect: "none" }}
    >
      {/* Menu Bar (JMenuBar) */}
      <div
        style={{
          display: "flex",
          background: "#ece9d8",
          borderBottom: "1px solid #d0d0d0",
          fontSize: "11px",
          padding: "1px 4px",
          gap: "12px",
          color: "#222",
        }}
      >
        <span style={{ cursor: "pointer" }}>Arquivo</span>
        <span style={{ cursor: "pointer" }}>Versão</span>
        <span style={{ cursor: "pointer" }}>Janelas</span>
        <span style={{ cursor: "pointer" }}>Idioma</span>
        <span style={{ cursor: "pointer" }}>Ajuda</span>
      </div>

      {/* Top Application Toolbar (WebToolBar) */}
      <JPanel
        style={{
          display: "flex",
          alignItems: "center",
          gap: "4px",
          padding: "3px 6px",
          borderBottom: "1px solid #b0b0b0",
          background: "linear-gradient(to bottom, #ffffff, #e6e6e6)",
          flexShrink: 0,
        }}
      >
        <WebButton onClick={() => handleShowVerse()} style={{ fontWeight: "bold" }}>
          Exibir (F5)
        </WebButton>
        <WebButton onClick={() => handleAddToPlaylist()}>Adicionar à Lista (Alt+Enter)</WebButton>
        <div style={{ width: "1px", height: "18px", background: "#bbb", margin: "0 2px" }} />
        <WebButton>Pesquisar (Ctrl+F)</WebButton>
        <WebButton>Exibição Personalizada</WebButton>
        <div style={{ width: "1px", height: "18px", background: "#bbb", margin: "0 2px" }} />
        <WebButton onClick={() => setViewModeAbbrev(!viewModeAbbrev)}>
          {viewModeAbbrev ? "Nomes Completos" : "Abreviaturas"}
        </WebButton>
        <div style={{ width: "1px", height: "18px", background: "#bbb", margin: "0 2px" }} />
        <span style={{ fontSize: "11px", color: "#444" }}>Tema:</span>
        <WebComboBox
          items={[
            { value: "Tema Padrão", label: "Tema Padrão" },
            { value: "Preto Básico", label: "Preto Básico" },
            { value: "Fundo Azul", label: "Fundo Azul" },
            { value: "Gold Elegant", label: "Gold Elegant" },
          ]}
          value={selectedTheme}
          onChange={(val) => setSelectedTheme(val)}
        />
        <WebButton>Configurações (F12)</WebButton>

        <div style={{ marginLeft: "auto", display: "flex", alignItems: "center", gap: "6px" }}>
          {onGoToHolyrics && (
            <WebButton onClick={onGoToHolyrics} style={{ background: "#e8f0fe" }}>
              Ir para Músicas (Ctrl+H)
            </WebButton>
          )}
          <WebButton>Ajuda</WebButton>
        </div>
      </JPanel>

      {/* Main Content Area */}
      <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column" }}>
        <JSplitPane
          orientation={HORIZONTAL_SPLIT}
          dividerLocation={480}
          style={{ flex: 1, height: "100%" }}
          left={
            /* Left Side: Books, Chapters & Verses Navigation */
            <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", background: "#f8f8f8" }}>
              <div
                style={{
                  padding: "4px 8px",
                  background: "#e4e4e4",
                  borderBottom: "1px solid #d0d0d0",
                  fontSize: "11px",
                  fontWeight: "bold",
                  color: "#333",
                  display: "flex",
                  justifyContent: "space-between",
                }}
              >
                <span>LIVROS DA BÍBLIA</span>
                <span style={{ fontWeight: "normal", color: "#666" }}>
                  {selectedBook.name} {selectedChapter}:{selectedVerse}
                </span>
              </div>

              <JSplitPane
                orientation={HORIZONTAL_SPLIT}
                dividerLocation={260}
                style={{ flex: 1, height: "100%" }}
                left={
                  /* Books List / Grid */
                  <div style={{ height: "100%", overflowY: "auto", padding: "4px", background: "#ffffff" }}>
                    {viewModeAbbrev ? (
                      <div>
                        <div style={{ fontSize: "10px", fontWeight: "bold", color: "#777", marginBottom: "4px" }}>
                          ANTIGO TESTAMENTO
                        </div>
                        <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "2px", marginBottom: "8px" }}>
                          {BOOKS_DATA.filter((b) => b.testament === "OT").map((b) => (
                            <button
                              key={b.id}
                              onClick={() => {
                                setSelectedBookId(b.id);
                                setSelectedChapter(1);
                                setSelectedVerse(1);
                              }}
                              title={b.name}
                              style={{
                                padding: "4px 2px",
                                fontSize: "11px",
                                border: "1px solid",
                                borderColor: selectedBookId === b.id ? "#316ac5" : "#c0c0c0",
                                background: selectedBookId === b.id ? "#316ac5" : "#f5f5f5",
                                color: selectedBookId === b.id ? "#ffffff" : "#000000",
                                cursor: "pointer",
                                borderRadius: "2px",
                                textAlign: "center",
                              }}
                            >
                              {b.abbrev}
                            </button>
                          ))}
                        </div>

                        <div style={{ fontSize: "10px", fontWeight: "bold", color: "#777", marginBottom: "4px" }}>
                          NOVO TESTAMENTO
                        </div>
                        <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "2px" }}>
                          {BOOKS_DATA.filter((b) => b.testament === "NT").map((b) => (
                            <button
                              key={b.id}
                              onClick={() => {
                                setSelectedBookId(b.id);
                                setSelectedChapter(1);
                                setSelectedVerse(1);
                              }}
                              title={b.name}
                              style={{
                                padding: "4px 2px",
                                fontSize: "11px",
                                border: "1px solid",
                                borderColor: selectedBookId === b.id ? "#316ac5" : "#c0c0c0",
                                background: selectedBookId === b.id ? "#316ac5" : "#f5f5f5",
                                color: selectedBookId === b.id ? "#ffffff" : "#000000",
                                cursor: "pointer",
                                borderRadius: "2px",
                                textAlign: "center",
                              }}
                            >
                              {b.abbrev}
                            </button>
                          ))}
                        </div>
                      </div>
                    ) : (
                      /* Full Names List */
                      <div style={{ display: "flex", flexDirection: "column", gap: "1px" }}>
                        <div style={{ fontSize: "10px", fontWeight: "bold", color: "#777", padding: "2px 4px" }}>
                          ANTIGO TESTAMENTO
                        </div>
                        {BOOKS_DATA.filter((b) => b.testament === "OT").map((b) => (
                          <div
                            key={b.id}
                            onClick={() => {
                              setSelectedBookId(b.id);
                              setSelectedChapter(1);
                              setSelectedVerse(1);
                            }}
                            style={{
                              padding: "2px 6px",
                              fontSize: "11px",
                              background: selectedBookId === b.id ? "#316ac5" : "transparent",
                              color: selectedBookId === b.id ? "#ffffff" : "#000000",
                              cursor: "pointer",
                              borderRadius: "2px",
                            }}
                          >
                            {b.name}
                          </div>
                        ))}
                        <div style={{ fontSize: "10px", fontWeight: "bold", color: "#777", padding: "4px 4px 2px 4px" }}>
                          NOVO TESTAMENTO
                        </div>
                        {BOOKS_DATA.filter((b) => b.testament === "NT").map((b) => (
                          <div
                            key={b.id}
                            onClick={() => {
                              setSelectedBookId(b.id);
                              setSelectedChapter(1);
                              setSelectedVerse(1);
                            }}
                            style={{
                              padding: "2px 6px",
                              fontSize: "11px",
                              background: selectedBookId === b.id ? "#316ac5" : "transparent",
                              color: selectedBookId === b.id ? "#ffffff" : "#000000",
                              cursor: "pointer",
                              borderRadius: "2px",
                            }}
                          >
                            {b.name}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                }
                right={
                  /* Chapters and Verses selectors */
                  <JSplitPane
                    orientation={VERTICAL_SPLIT}
                    dividerLocation={200}
                    style={{ height: "100%" }}
                    left={
                      /* Chapters Grid */
                      <div style={{ height: "100%", display: "flex", flexDirection: "column", background: "#ffffff" }}>
                        <div
                          style={{
                            padding: "3px 6px",
                            background: "#e8e8e8",
                            fontSize: "11px",
                            fontWeight: "bold",
                            borderBottom: "1px solid #ddd",
                            color: "#444",
                          }}
                        >
                          Capítulo
                        </div>
                        <div
                          style={{
                            flex: 1,
                            overflowY: "auto",
                            padding: "4px",
                            display: "grid",
                            gridTemplateColumns: "repeat(5, 1fr)",
                            gap: "3px",
                            alignContent: "start",
                          }}
                        >
                          {Array.from({ length: selectedBook.chaptersCount }, (_, i) => i + 1).map((chap) => (
                            <button
                              key={chap}
                              onClick={() => {
                                setSelectedChapter(chap);
                                setSelectedVerse(1);
                              }}
                              style={{
                                padding: "4px 2px",
                                fontSize: "11px",
                                border: "1px solid",
                                borderColor: selectedChapter === chap ? "#316ac5" : "#ccc",
                                background: selectedChapter === chap ? "#316ac5" : "#fafafa",
                                color: selectedChapter === chap ? "#fff" : "#000",
                                cursor: "pointer",
                                borderRadius: "2px",
                              }}
                            >
                              {chap}
                            </button>
                          ))}
                        </div>
                      </div>
                    }
                    right={
                      /* Verses Grid */
                      <div style={{ height: "100%", display: "flex", flexDirection: "column", background: "#ffffff" }}>
                        <div
                          style={{
                            padding: "3px 6px",
                            background: "#e8e8e8",
                            fontSize: "11px",
                            fontWeight: "bold",
                            borderBottom: "1px solid #ddd",
                            color: "#444",
                          }}
                        >
                          Versículo
                        </div>
                        <div
                          style={{
                            flex: 1,
                            overflowY: "auto",
                            padding: "4px",
                            display: "grid",
                            gridTemplateColumns: "repeat(5, 1fr)",
                            gap: "3px",
                            alignContent: "start",
                          }}
                        >
                          {currentVerses.map((v) => (
                            <button
                              key={v.verse}
                              onClick={() => setSelectedVerse(v.verse)}
                              onDoubleClick={() => handleShowVerse(v)}
                              style={{
                                padding: "4px 2px",
                                fontSize: "11px",
                                border: "1px solid",
                                borderColor: selectedVerse === v.verse ? "#316ac5" : "#ccc",
                                background: selectedVerse === v.verse ? "#316ac5" : "#fafafa",
                                color: selectedVerse === v.verse ? "#fff" : "#000",
                                cursor: "pointer",
                                borderRadius: "2px",
                              }}
                            >
                              {v.verse}
                            </button>
                          ))}
                        </div>
                      </div>
                    }
                  />
                }
              />
            </JPanel>
          }
          right={
            /* Right Side: Tabbed Verses Content & Projection Monitor */
            <JSplitPane
              orientation={HORIZONTAL_SPLIT}
              dividerLocation={420}
              style={{ flex: 1, height: "100%" }}
              left={
                /* Center/Middle Verses Text List */
                <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", background: "#ffffff" }}>
                  {/* Book & Chapter Header */}
                  <div
                    style={{
                      padding: "4px 8px",
                      background: "#e8eef8",
                      borderBottom: "1px solid #c0d0e0",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                    }}
                  >
                    <span style={{ fontSize: "13px", fontWeight: "bold", color: "#1a3b6e" }}>
                      {selectedBook.name} {selectedChapter}
                    </span>
                    <div style={{ display: "flex", gap: "2px" }}>
                      <WebButton
                        onClick={() => {
                          if (selectedChapter > 1) {
                            setSelectedChapter(selectedChapter - 1);
                            setSelectedVerse(1);
                          }
                        }}
                        disabled={selectedChapter <= 1}
                        style={{ padding: "1px 6px", fontSize: "10px" }}
                      >
                        « Anterior
                      </WebButton>
                      <WebButton
                        onClick={() => {
                          if (selectedChapter < selectedBook.chaptersCount) {
                            setSelectedChapter(selectedChapter + 1);
                            setSelectedVerse(1);
                          }
                        }}
                        disabled={selectedChapter >= selectedBook.chaptersCount}
                        style={{ padding: "1px 6px", fontSize: "10px" }}
                      >
                        Próximo »
                      </WebButton>
                    </div>
                  </div>

                  {/* Versions Tabs */}
                  <JTabbedPane
                    selectedIndex={activeTabVersion === "ARC" ? 0 : activeTabVersion === "NVI" ? 1 : 2}
                    onTabChange={(idx: number) => {
                      setActiveTabVersion(idx === 0 ? "ARC" : idx === 1 ? "NVI" : "ARA");
                    }}
                    style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}
                  >
                    <JTab title="Almeida Revista e Corrigida (ARC)">
                      <div style={{ height: "100%", overflowY: "auto", padding: "6px" }}>
                        {currentVerses.map((v) => {
                          const isSelected = selectedVerse === v.verse;
                          const isDisplayed =
                            displayedVerse &&
                            displayedVerse.bookId === v.bookId &&
                            displayedVerse.chapter === v.chapter &&
                            displayedVerse.verse === v.verse;

                          return (
                            <div
                              key={v.verse}
                              onClick={() => setSelectedVerse(v.verse)}
                              onDoubleClick={() => handleShowVerse(v)}
                              style={{
                                padding: "4px 6px",
                                marginBottom: "3px",
                                borderRadius: "3px",
                                fontSize: "12px",
                                lineHeight: "1.4",
                                cursor: "pointer",
                                border: isDisplayed
                                  ? "1px solid #f57c00"
                                  : isSelected
                                  ? "1px solid #70a0d0"
                                  : "1px solid transparent",
                                background: isDisplayed
                                  ? "#fff3e0"
                                  : isSelected
                                  ? "#e6f0fa"
                                  : "transparent",
                              }}
                            >
                              <span
                                style={{
                                  fontWeight: "bold",
                                  color: isDisplayed ? "#e65100" : "#1a5fb4",
                                  marginRight: "6px",
                                }}
                              >
                                {v.verse}
                              </span>
                              <span>{v.text}</span>
                            </div>
                          );
                        })}
                      </div>
                    </JTab>
                    <JTab title="Nova Versão Internacional (NVI)">
                      <div style={{ height: "100%", overflowY: "auto", padding: "6px" }}>
                        {currentVerses.map((v) => (
                          <div
                            key={v.verse}
                            onClick={() => setSelectedVerse(v.verse)}
                            onDoubleClick={() => handleShowVerse(v)}
                            style={{
                              padding: "4px 6px",
                              marginBottom: "3px",
                              borderRadius: "3px",
                              fontSize: "12px",
                              cursor: "pointer",
                              background: selectedVerse === v.verse ? "#e6f0fa" : "transparent",
                            }}
                          >
                            <span style={{ fontWeight: "bold", color: "#1a5fb4", marginRight: "6px" }}>
                              {v.verse}
                            </span>
                            <span>{v.text} (NVI)</span>
                          </div>
                        ))}
                      </div>
                    </JTab>
                    <JTab title="Almeida Revista e Atualizada (ARA)">
                      <div style={{ height: "100%", overflowY: "auto", padding: "6px" }}>
                        {currentVerses.map((v) => (
                          <div
                            key={v.verse}
                            onClick={() => setSelectedVerse(v.verse)}
                            onDoubleClick={() => handleShowVerse(v)}
                            style={{
                              padding: "4px 6px",
                              marginBottom: "3px",
                              borderRadius: "3px",
                              fontSize: "12px",
                              cursor: "pointer",
                              background: selectedVerse === v.verse ? "#e6f0fa" : "transparent",
                            }}
                          >
                            <span style={{ fontWeight: "bold", color: "#1a5fb4", marginRight: "6px" }}>
                              {v.verse}
                            </span>
                            <span>{v.text} (ARA)</span>
                          </div>
                        ))}
                      </div>
                    </JTab>
                  </JTabbedPane>
                </JPanel>
              }
              right={
                /* Right Projection Monitor & Saved Verses Playlist */
                <JPanel style={{ height: "100%", display: "flex", flexDirection: "column", background: "#f5f5f5" }}>
                  {/* Projection Monitor Box */}
                  <div style={{ padding: "6px", borderBottom: "1px solid #d0d0d0" }}>
                    <div
                      style={{
                        fontSize: "10px",
                        fontWeight: "bold",
                        color: "#555",
                        marginBottom: "4px",
                        display: "flex",
                        justifyContent: "space-between",
                      }}
                    >
                      <span>MONITOR DE PROJEÇÃO</span>
                      <span style={{ color: isProjecting ? "#2e7d32" : "#999" }}>
                        {isProjecting ? "● NO AR" : "○ DESLIGADO"}
                      </span>
                    </div>

                    {/* Virtual screen display */}
                    <div
                      style={{
                        height: "140px",
                        background: isBlackout ? "#000000" : "#1a2536",
                        border: "2px solid #333333",
                        borderRadius: "3px",
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "center",
                        alignItems: "center",
                        padding: "10px",
                        textAlign: "center",
                        color: "#ffffff",
                        position: "relative",
                        overflow: "hidden",
                      }}
                    >
                      {!isBlackout && !isClearText && displayedVerse ? (
                        <>
                          <div
                            style={{
                              fontSize: "12px",
                              lineHeight: "1.3",
                              textShadow: "1px 1px 2px #000",
                              maxHeight: "100px",
                              overflowY: "hidden",
                            }}
                          >
                            "{displayedVerse.text}"
                          </div>
                          <div
                            style={{
                              marginTop: "8px",
                              fontSize: "10px",
                              color: "#f0c040",
                              fontWeight: "bold",
                            }}
                          >
                            {BOOKS_DATA.find((b) => b.id === displayedVerse.bookId)?.name}{" "}
                            {displayedVerse.chapter}:{displayedVerse.verse}
                          </div>
                        </>
                      ) : (
                        <div style={{ fontSize: "11px", color: "#666" }}>
                          {isBlackout ? "(Tela Preta)" : isClearText ? "(Texto Oculto)" : "(Nenhum Versículo em Projeção)"}
                        </div>
                      )}
                    </div>

                    {/* Quick projection actions */}
                    <div style={{ display: "flex", gap: "4px", marginTop: "6px" }}>
                      <WebButton
                        onClick={() => setIsBlackout(!isBlackout)}
                        style={{
                          flex: 1,
                          fontSize: "10px",
                          background: isBlackout ? "#333" : undefined,
                          color: isBlackout ? "#fff" : undefined,
                        }}
                      >
                        {isBlackout ? "Retomar" : "Tela Preta (F8)"}
                      </WebButton>
                      <WebButton
                        onClick={() => setIsClearText(!isClearText)}
                        style={{ flex: 1, fontSize: "10px" }}
                      >
                        {isClearText ? "Exibir Texto" : "Ocultar Texto (F9)"}
                      </WebButton>
                      <WebButton
                        onClick={() => {
                          setIsProjecting(false);
                          setDisplayedVerse(null);
                        }}
                        style={{ fontSize: "10px" }}
                      >
                        Fechar
                      </WebButton>
                    </div>
                  </div>

                  {/* Playlist / Saved Verses (WebCollapsiblePane) */}
                  <div style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}>
                    <div
                      onClick={() => setIsPlaylistExpanded(!isPlaylistExpanded)}
                      style={{
                        padding: "4px 8px",
                        background: "#e4e4e4",
                        borderBottom: "1px solid #d0d0d0",
                        fontSize: "11px",
                        fontWeight: "bold",
                        cursor: "pointer",
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                      }}
                    >
                      <span>★ Lista de Versículos ({playlist.length})</span>
                      <span>{isPlaylistExpanded ? "▼" : "▶"}</span>
                    </div>

                    {isPlaylistExpanded && (
                      <div style={{ flex: 1, overflowY: "auto", background: "#ffffff", padding: "4px" }}>
                        {playlist.length === 0 ? (
                          <div style={{ fontSize: "11px", color: "#888", textAlign: "center", padding: "12px 4px" }}>
                            Nenhum versículo adicionado. Pressione Alt+Enter para salvar versículos aqui.
                          </div>
                        ) : (
                          playlist.map((v, idx) => {
                            const bName = BOOKS_DATA.find((b) => b.id === v.bookId)?.name || "";
                            return (
                              <div
                                key={idx}
                                onClick={() => handleShowVerse(v)}
                                style={{
                                  padding: "4px 6px",
                                  borderBottom: "1px solid #eee",
                                  fontSize: "11px",
                                  cursor: "pointer",
                                  display: "flex",
                                  justifyContent: "space-between",
                                  alignItems: "center",
                                }}
                              >
                                <span style={{ fontWeight: "bold", color: "#1a3b6e" }}>
                                  {bName} {v.chapter}:{v.verse}
                                </span>
                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    setPlaylist(playlist.filter((_, i) => i !== idx));
                                  }}
                                  style={{
                                    border: "none",
                                    background: "transparent",
                                    color: "#a00",
                                    cursor: "pointer",
                                    fontSize: "10px",
                                  }}
                                  title="Remover"
                                >
                                  ✕
                                </button>
                              </div>
                            );
                          })
                        )}
                      </div>
                    )}
                  </div>
                </JPanel>
              }
            />
          }
        />
      </div>

      {/* Bottom Status Bar */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          padding: "2px 8px",
          background: "#ece9d8",
          borderTop: "1px solid #c0c0c0",
          fontSize: "11px",
          color: "#444",
        }}
      >
        <span>
          Bíblia: {selectedBook.name} {selectedChapter}:{selectedVerse} ({activeTabVersion})
        </span>
        <span>Holyrics 2.0 (Isomorphic Web)</span>
      </div>
    </JFrame>
  );
};

export default BibleMain;
