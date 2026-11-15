import React, { useState, useRef, useEffect, useMemo } from "react";
import {
  JFrame,
  JPanel,
  WebButton,
  WebLabel,
} from "../swing";

export interface ScheduledMember {
  id: string;
  name: string;
  email?: string;
  phone?: string;
  group?: string;
  roles?: string[];
  active: boolean;
}

export interface ScheduledMusic {
  id: string;
  title: string;
  artist: string;
}

export interface ScheduleEvent {
  id: string;
  name: string;
  date: Date;
  time?: string;
  active: boolean;
  musics: ScheduledMusic[];
  notes?: string;
  assignedMemberIds: string[];
}

export interface ScheduleViewProps {
  title?: string;
  visible?: boolean;
  onClose?: () => void;
  initialDate?: Date;
  initialMembers?: ScheduledMember[];
  initialEvents?: ScheduleEvent[];
}

const DEFAULT_MEMBERS: ScheduledMember[] = [
  { id: "1", name: "Ana Souza", group: "Louvor", roles: ["Vocal"], email: "ana@igreja.org", phone: "(11) 98765-4321", active: true },
  { id: "2", name: "Carlos Eduardo", group: "Louvor", roles: ["Violão", "Guitarra"], email: "carlos@igreja.org", phone: "(11) 98765-4322", active: true },
  { id: "3", name: "Daniel Rocha", group: "Mídia", roles: ["Projeção"], email: "daniel@igreja.org", phone: "(11) 98765-4323", active: true },
  { id: "4", name: "Fernanda Lima", group: "Louvor", roles: ["Teclado"], email: "fernanda@igreja.org", phone: "(11) 98765-4324", active: true },
  { id: "5", name: "Gabriel Alves", group: "Som", roles: ["Mesa de Som"], email: "gabriel@igreja.org", phone: "(11) 98765-4325", active: true },
  { id: "6", name: "Juliana Mendes", group: "Louvor", roles: ["Bateria"], email: "juliana@igreja.org", phone: "(11) 98765-4326", active: false },
  { id: "7", name: "Lucas Ferreira", group: "Louvor", roles: ["Baixo"], email: "lucas@igreja.org", phone: "(11) 98765-4327", active: true },
  { id: "8", name: "Mariana Costa", group: "Louvor", roles: ["Vocal"], email: "mariana@igreja.org", phone: "(11) 98765-4328", active: true },
  { id: "9", name: "Rafael Silveira", group: "Mídia", roles: ["Transmissão"], email: "rafael@igreja.org", phone: "(11) 98765-4329", active: true },
  { id: "10", name: "Vitória Ramos", group: "Mídia", roles: ["Câmera"], email: "vitoria@igreja.org", phone: "(11) 98765-4330", active: false },
];

function createSampleEvents(year: number, month: number): ScheduleEvent[] {
  // Generate sample Sundays and Midweek worship events for the month
  const events: ScheduleEvent[] = [];
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  for (let day = 1; day <= daysInMonth; day++) {
    const d = new Date(year, month, day);
    const dayOfWeek = d.getDay(); // 0 = Sunday, 3 = Wednesday, 6 = Saturday

    if (dayOfWeek === 0) {
      // Sunday Morning
      events.push({
        id: `event-${year}-${month}-${day}-morning`,
        name: "Culto da Manhã",
        date: new Date(year, month, day, 9, 30),
        time: "09:30",
        active: true,
        musics: [
          { id: "m1", title: "Porque Ele Vive", artist: "Harpa Cristã" },
          { id: "m2", title: "Grandioso És Tu", artist: "Harpa Cristã" },
        ],
        notes: "Santa Ceia do Senhor",
        assignedMemberIds: ["1", "2", "3", "5"],
      });

      // Sunday Evening
      events.push({
        id: `event-${year}-${month}-${day}-evening`,
        name: "Culto da Noite",
        date: new Date(year, month, day, 19, 0),
        time: "19:00",
        active: true,
        musics: [
          { id: "m3", title: "Em Espírito, Em Verdade", artist: "Koinonya" },
          { id: "m4", title: "Ao Único", artist: "Aline Barros" },
          { id: "m5", title: "Vim Para Adorar-te", artist: "Adoração & Adoradores" },
        ],
        notes: "Culto de Celebração e Família",
        assignedMemberIds: ["1", "2", "4", "5", "7", "9"],
      });
    } else if (dayOfWeek === 3) {
      // Wednesday Service
      events.push({
        id: `event-${year}-${month}-${day}-wed`,
        name: "Culto de Oração e Estudo",
        date: new Date(year, month, day, 19, 30),
        time: "19:30",
        active: true,
        musics: [
          { id: "m6", title: "Quão Grande É o Meu Deus", artist: "Soraya Moraes" },
        ],
        notes: "Estudo Bíblico em Romanos",
        assignedMemberIds: ["2", "3"],
      });
    } else if (dayOfWeek === 6 && day <= 14) {
      // Youth / Special event
      events.push({
        id: `event-${year}-${month}-${day}-youth`,
        name: "Culto de Jovens",
        date: new Date(year, month, day, 19, 30),
        time: "19:30",
        active: false,
        musics: [
          { id: "m7", title: "Ousado Amor", artist: "Isaías Saad" },
          { id: "m8", title: "Ruja o Leão", artist: "Talita Catanzaro" },
        ],
        notes: "Reunião de Jovens e Adolescentes",
        assignedMemberIds: ["1", "2", "6", "7", "10"],
      });
    }
  }

  return events;
}

/**
 * 1:1 Isomorphic port of `com.limagiran.holyrics.view.ScheduleView`
 *
 * Implements the Swing Escala (Schedule) dialog with:
 * - Month navigation (Previous / Next) with formatted Georgia 24px header
 * - Checkboxes to filter: "Exibir apenas ativos (Culto / Evento)" and "Exibir apenas ativos (Membro)"
 * - Export to Excel button
 * - Split synchronized dual-table view:
 *   - Left: JTable jTableFirstColumn (Members list)
 *   - Right: JTable jTableSchedule (Events as columns, checkboxes for member attendance)
 * - Swing-styled tooltips on event headers and member cells
 * - Alternate row backgrounds (#ffffff and #f2f5f9)
 */
export const ScheduleView: React.FC<ScheduleViewProps> = ({
  title = "Escala",
  visible = true,
  onClose,
  initialDate = new Date(),
  initialMembers = DEFAULT_MEMBERS,
  initialEvents,
}) => {
  const [currentDate, setCurrentDate] = useState<Date>(
    new Date(initialDate.getFullYear(), initialDate.getMonth(), 1)
  );
  const [displayOnlyEnabledWorship, setDisplayOnlyEnabledWorship] = useState<boolean>(true);
  const [displayOnlyEnabledMember, setDisplayOnlyEnabledMember] = useState<boolean>(true);

  const [members] = useState<ScheduledMember[]>(initialMembers);
  const [events, setEvents] = useState<ScheduleEvent[]>(() => {
    return initialEvents || createSampleEvents(currentDate.getFullYear(), currentDate.getMonth());
  });

  // Tooltip state
  const [tooltipText, setTooltipText] = useState<string | null>(null);
  const [tooltipPos, setTooltipPos] = useState<{ x: number; y: number } | null>(null);

  // Synchronized scroll refs
  const firstColScrollRef = useRef<HTMLDivElement>(null);
  const scheduleScrollRef = useRef<HTMLDivElement>(null);
  const isSyncingScroll = useRef<boolean>(false);

  // Update events when month changes if using generated sample events
  useEffect(() => {
    if (!initialEvents) {
      setEvents(createSampleEvents(currentDate.getFullYear(), currentDate.getMonth()));
    }
  }, [currentDate, initialEvents]);

  // Handle ESC key to close
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape" && onClose) {
        onClose();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [onClose]);

  // Month navigation
  const addMonth = (delta: number) => {
    setCurrentDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + delta, 1));
  };

  // Month label formatted as "Month/Year" e.g. "novembro/2026"
  const monthName = currentDate.toLocaleString("pt-BR", { month: "long" });
  const formattedMonthLabel = `${monthName.charAt(0).toUpperCase() + monthName.slice(1)}/${currentDate.getFullYear()}`;

  // Filtered members and events
  const filteredMembers = useMemo(() => {
    return members.filter((m) => (!displayOnlyEnabledMember ? true : m.active));
  }, [members, displayOnlyEnabledMember]);

  const filteredEvents = useMemo(() => {
    return events.filter((e) => {
      const isSameMonth =
        e.date.getFullYear() === currentDate.getFullYear() &&
        e.date.getMonth() === currentDate.getMonth();
      const matchEnabled = !displayOnlyEnabledWorship ? true : e.active;
      return isSameMonth && matchEnabled;
    });
  }, [events, currentDate, displayOnlyEnabledWorship]);

  // Synchronize vertical scroll between member column and schedule table
  const handleScrollFirstCol = () => {
    if (isSyncingScroll.current) return;
    if (firstColScrollRef.current && scheduleScrollRef.current) {
      isSyncingScroll.current = true;
      scheduleScrollRef.current.scrollTop = firstColScrollRef.current.scrollTop;
      requestAnimationFrame(() => {
        isSyncingScroll.current = false;
      });
    }
  };

  const handleScrollSchedule = () => {
    if (isSyncingScroll.current) return;
    if (firstColScrollRef.current && scheduleScrollRef.current) {
      isSyncingScroll.current = true;
      firstColScrollRef.current.scrollTop = scheduleScrollRef.current.scrollTop;
      requestAnimationFrame(() => {
        isSyncingScroll.current = false;
      });
    }
  };

  // Toggle member assigned in event
  const toggleMemberInEvent = (eventId: string, memberId: string) => {
    setEvents((prev) =>
      prev.map((ev) => {
        if (ev.id !== eventId) return ev;
        const exists = ev.assignedMemberIds.includes(memberId);
        const newIds = exists
          ? ev.assignedMemberIds.filter((id) => id !== memberId)
          : [...ev.assignedMemberIds, memberId];
        return { ...ev, assignedMemberIds: newIds };
      })
    );
  };

  // Export to Excel / CSV
  const handleSaveExcel = () => {
    const lines: string[] = [];
    lines.push(`Escala - ${formattedMonthLabel}`);
    lines.push("");

    // Header line
    const header = [
      "Membro",
      ...filteredEvents.map(
        (e) =>
          `"${e.date.toLocaleDateString("pt-BR")} (${e.date.toLocaleDateString("pt-BR", { weekday: "short" })}) - ${e.name} ${e.time || ""}"`
      ),
    ];
    lines.push(header.join(";"));

    // Member lines
    filteredMembers.forEach((m) => {
      const row = [
        `"${m.name}"`,
        ...filteredEvents.map((e) => (e.assignedMemberIds.includes(m.id) ? "SIM" : "")),
      ];
      lines.push(row.join(";"));
    });

    lines.push("");
    lines.push("Eventos e Músicas:");
    filteredEvents.forEach((e) => {
      const musicList = e.musics.map((m) => `${m.title} (${m.artist})`).join(" | ");
      lines.push(`"${e.name} (${e.date.toLocaleDateString("pt-BR")})";"${musicList}";"${e.notes || ""}"`);
    });

    const blob = new Blob(["\uFEFF" + lines.join("\r\n")], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `Escala_${currentDate.getFullYear()}_${currentDate.getMonth() + 1}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  };

  if (!visible) {
    return null;
  }

  const ROW_HEIGHT = 25;
  const HEADER_HEIGHT = 48;

  return (
    <JFrame
      title={title}
      width="100%"
      height="100vh"
      onClose={onClose}
      style={{
        display: "flex",
        flexDirection: "column",
        background: "#f0f0f0",
        userSelect: "none",
        minWidth: "800px",
        minHeight: "550px",
      }}
    >
      {/* jPanelMain */}
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          flex: 1,
          padding: "4px",
          minHeight: 0,
          background: "#ece9d8",
          fontFamily: "Tahoma, Arial, sans-serif",
        }}
      >
        {/* jPanelHeader (WebPanel) */}
        <JPanel
          style={{
            display: "flex",
            flexDirection: "column",
            background: "linear-gradient(to bottom, #ffffff, #e6e6e6)",
            border: "1px solid #b0b0b0",
            borderRadius: "3px",
            marginBottom: "5px",
            boxShadow: "0 1px 3px rgba(0,0,0,0.1)",
          }}
        >
          {/* jPanelButtons */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              padding: "5px 15px",
              gap: "15px",
              position: "relative",
            }}
          >
            {/* filler4 & filler3 space representation */}
            <div style={{ width: "88px", flexShrink: 0 }} />
            <div style={{ width: "48px", flexShrink: 0 }} />

            {/* jButtonPrevious */}
            <WebButton
              onClick={() => addMonth(-1)}
              style={{
                width: "32px",
                height: "28px",
                padding: "0",
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                fontWeight: "bold",
                fontSize: "14px",
                cursor: "pointer",
              }}
              title="Mês Anterior"
            >
              ◀
            </WebButton>

            {/* jLabelMonth */}
            <WebLabel
              style={{
                fontFamily: "Georgia, serif",
                fontSize: "24px",
                textAlign: "center",
                width: "250px",
                height: "40px",
                lineHeight: "40px",
                display: "inline-block",
                color: "#222222",
                fontWeight: "normal",
              }}
            >
              {formattedMonthLabel}
            </WebLabel>

            {/* jButtonNext */}
            <WebButton
              onClick={() => addMonth(1)}
              style={{
                width: "32px",
                height: "28px",
                padding: "0",
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                fontWeight: "bold",
                fontSize: "14px",
                cursor: "pointer",
              }}
              title="Próximo Mês"
            >
              ▶
            </WebButton>

            {/* filler1 */}
            <div style={{ width: "48px", flexShrink: 0 }} />

            {/* jButtonSaveExcel */}
            <WebButton
              onClick={handleSaveExcel}
              style={{
                display: "inline-flex",
                alignItems: "center",
                gap: "6px",
                height: "28px",
                padding: "0 10px",
                fontSize: "12px",
                background: "linear-gradient(to bottom, #f9f9f9, #e2e2e2)",
                border: "1px solid #707070",
                borderRadius: "2px",
                cursor: "pointer",
              }}
            >
              <span style={{ color: "#1e7e34", fontWeight: "bold" }}>📊</span>
              <span>Salvar no Excel</span>
            </WebButton>
          </div>

          {/* jPanelSettings */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "30px",
              paddingBottom: "5px",
            }}
          >
            {/* jCheckBoxDisplayOnlyEnabledWorship */}
            <label
              style={{
                display: "flex",
                alignItems: "center",
                gap: "6px",
                fontFamily: "Georgia, serif",
                fontSize: "14px",
                color: "#222",
                cursor: "pointer",
              }}
            >
              <input
                type="checkbox"
                checked={displayOnlyEnabledWorship}
                onChange={(e) => setDisplayOnlyEnabledWorship(e.target.checked)}
                style={{ cursor: "pointer" }}
              />
              <span>Exibir apenas ativos (Culto / Evento)</span>
            </label>

            {/* jCheckBoxDisplayOnlyEnabledMember */}
            <label
              style={{
                display: "flex",
                alignItems: "center",
                gap: "6px",
                fontFamily: "Georgia, serif",
                fontSize: "14px",
                color: "#222",
                cursor: "pointer",
              }}
            >
              <input
                type="checkbox"
                checked={displayOnlyEnabledMember}
                onChange={(e) => setDisplayOnlyEnabledMember(e.target.checked)}
                style={{ cursor: "pointer" }}
              />
              <span>Exibir apenas ativos (Membro)</span>
            </label>
          </div>
        </JPanel>

        {/* jPanelGeneral: Left column (Members) + Center (Schedule Matrix) */}
        <div
          style={{
            display: "flex",
            flex: 1,
            minHeight: 0,
            background: "#ffffff",
            border: "1px solid #999999",
            position: "relative",
          }}
        >
          {/* jScrollPaneFirstColumn: Fixed width member names */}
          <div
            style={{
              width: "250px",
              flexShrink: 0,
              display: "flex",
              flexDirection: "column",
              borderRight: "2px solid #888888",
              background: "#ffffff",
            }}
          >
            {/* Table Header for Members column */}
            <div
              style={{
                height: `${HEADER_HEIGHT}px`,
                background: "linear-gradient(to bottom, #f2f2f2, #dcdcdc)",
                borderBottom: "1px solid #a0a0a0",
                display: "flex",
                alignItems: "center",
                padding: "0 8px",
                fontWeight: "bold",
                fontFamily: "Georgia, serif",
                fontSize: "13px",
                color: "#333",
                boxSizing: "border-box",
              }}
            >
              Membro ({filteredMembers.length})
            </div>

            {/* Scrollable list of members */}
            <div
              ref={firstColScrollRef}
              onScroll={handleScrollFirstCol}
              style={{
                flex: 1,
                overflowY: "scroll",
                overflowX: "hidden",
                scrollbarWidth: "none", // Hide scrollbar to sync with main table
              }}
            >
              {filteredMembers.map((member, index) => {
                const bg = index % 2 === 0 ? "#ffffff" : "#f2f5f9";
                return (
                  <div
                    key={member.id}
                    onMouseEnter={(e) => {
                      const rolesStr = member.roles ? member.roles.join(", ") : "";
                      const infoHtml = `<b>${member.name}</b><br/>${member.group || ""}${rolesStr ? " - " + rolesStr : ""}<br/>${member.phone || ""}<br/>${member.email || ""}`;
                      setTooltipText(infoHtml);
                      const rect = e.currentTarget.getBoundingClientRect();
                      setTooltipPos({ x: rect.right + 5, y: rect.top });
                    }}
                    onMouseLeave={() => {
                      setTooltipText(null);
                      setTooltipPos(null);
                    }}
                    style={{
                      height: `${ROW_HEIGHT}px`,
                      lineHeight: `${ROW_HEIGHT}px`,
                      padding: "0 8px",
                      background: bg,
                      borderBottom: "1px solid #e0e0e0",
                      fontFamily: "Georgia, serif",
                      fontSize: "13px",
                      color: member.active ? "#111111" : "#888888",
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      cursor: "default",
                      boxSizing: "border-box",
                    }}
                  >
                    {member.name}
                  </div>
                );
              })}
            </div>
          </div>

          {/* jScrollPaneSchedule: Horizontal and Vertical scroll for events matrix */}
          <div
            ref={scheduleScrollRef}
            onScroll={handleScrollSchedule}
            style={{
              flex: 1,
              overflow: "auto",
              background: "#ffffff",
            }}
          >
            <div style={{ display: "inline-block", minWidth: "100%" }}>
              {/* Header row containing all events */}
              <div
                style={{
                  display: "flex",
                  height: `${HEADER_HEIGHT}px`,
                  background: "linear-gradient(to bottom, #f2f2f2, #dcdcdc)",
                  borderBottom: "1px solid #a0a0a0",
                  boxSizing: "border-box",
                }}
              >
                {filteredEvents.length === 0 ? (
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      padding: "0 16px",
                      fontFamily: "Georgia, serif",
                      fontSize: "12px",
                      color: "#666",
                    }}
                  >
                    Nenhum culto ou evento cadastrado neste mês.
                  </div>
                ) : (
                  filteredEvents.map((event) => {
                    const dayNum = String(event.date.getDate()).padStart(2, "0");
                    const weekday = event.date.toLocaleDateString("pt-BR", { weekday: "short" });

                    return (
                      <div
                        key={event.id}
                        onMouseEnter={(e) => {
                          const musics = event.musics || [];
                          let tip = `<b>${event.name}</b><br/><br/>`;
                          if (musics.length > 0) {
                            tip += `<b>Músicas:</b><br/>`;
                            tip += musics
                              .map((m) => `• <b>${m.title}</b> (${m.artist})`)
                              .join("<br/>");
                          } else {
                            tip += `Nenhuma música adicionada`;
                          }
                          if (event.notes) {
                            tip += `<br/><br/><i>Anotações: ${event.notes}</i>`;
                          }
                          setTooltipText(tip);
                          const rect = e.currentTarget.getBoundingClientRect();
                          setTooltipPos({ x: rect.left, y: rect.bottom + 5 });
                        }}
                        onMouseLeave={() => {
                          setTooltipText(null);
                          setTooltipPos(null);
                        }}
                        style={{
                          width: "120px",
                          minWidth: "120px",
                          borderRight: "1px solid #c0c0c0",
                          display: "flex",
                          flexDirection: "column",
                          alignItems: "center",
                          justifyContent: "center",
                          padding: "2px 4px",
                          fontFamily: "Georgia, serif",
                          fontSize: "11px",
                          textAlign: "center",
                          cursor: "pointer",
                          userSelect: "none",
                          boxSizing: "border-box",
                        }}
                      >
                        <div style={{ fontWeight: "bold", color: "#222" }}>
                          {dayNum} ({weekday})
                        </div>
                        <div
                          style={{
                            fontSize: "10px",
                            color: "#555",
                            whiteSpace: "nowrap",
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            maxWidth: "110px",
                          }}
                        >
                          {event.time ? `${event.time} - ` : ""}
                          {event.name}
                        </div>
                      </div>
                    );
                  })
                )}
              </div>

              {/* Rows matching each member */}
              {filteredMembers.map((member, memberIndex) => {
                const bg = memberIndex % 2 === 0 ? "#ffffff" : "#f2f5f9";
                return (
                  <div
                    key={member.id}
                    style={{
                      display: "flex",
                      height: `${ROW_HEIGHT}px`,
                      background: bg,
                      borderBottom: "1px solid #e0e0e0",
                      boxSizing: "border-box",
                    }}
                  >
                    {filteredEvents.map((event) => {
                      const isAssigned = event.assignedMemberIds.includes(member.id);
                      return (
                        <div
                          key={event.id}
                          onClick={() => toggleMemberInEvent(event.id, member.id)}
                          style={{
                            width: "120px",
                            minWidth: "120px",
                            borderRight: "1px solid #e0e0e0",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            cursor: "pointer",
                            boxSizing: "border-box",
                          }}
                        >
                          <input
                            type="checkbox"
                            checked={isAssigned}
                            onChange={() => toggleMemberInEvent(event.id, member.id)}
                            style={{ cursor: "pointer" }}
                          />
                        </div>
                      );
                    })}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Floating ToolTip emulating Swing JLabelToolTip */}
          {tooltipText && tooltipPos && (
            <div
              style={{
                position: "fixed",
                left: `${tooltipPos.x}px`,
                top: `${tooltipPos.y}px`,
                background: "#ffffdc",
                border: "1px solid #000000",
                padding: "6px 10px",
                fontFamily: "Georgia, serif",
                fontSize: "13px",
                color: "#000000",
                zIndex: 99999,
                boxShadow: "2px 2px 5px rgba(0,0,0,0.25)",
                pointerEvents: "none",
                maxWidth: "350px",
                whiteSpace: "normal",
                lineHeight: "1.4",
              }}
              dangerouslySetInnerHTML={{ __html: tooltipText }}
            />
          )}
        </div>
      </div>
    </JFrame>
  );
};

export default ScheduleView;
