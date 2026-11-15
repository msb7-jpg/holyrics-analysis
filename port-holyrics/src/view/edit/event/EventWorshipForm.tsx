import React, { useState, useEffect } from "react";
import { JPanel } from "../../../swing/JPanel";
import { WebLabel } from "../../../swing/WebLabel";
import { WebButton } from "../../../swing/WebButton";
import { JTabbedPane, JTab } from "../../../swing/JTabbedPane";
import { Event } from "../../../model/Event";
import { Schedule } from "../../../model/Schedule";
import { ScheduleController } from "../../../control/ScheduleController";
import "../../../swing/swing.css";

export interface EventWorshipFormProps {
  visible?: boolean;
  event?: Event;
  onClose?: () => void;
}

function formatEventDateTime(d: Date): string {
  const day = String(d.getDate()).padStart(2, "0");
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const year = d.getFullYear();
  const hours = String(d.getHours()).padStart(2, "0");
  const minutes = String(d.getMinutes()).padStart(2, "0");
  return `${day}/${month}/${year} ${hours}:${minutes}`;
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.edit.event.EventWorshipForm
 * Modal dialog displaying event schedule and details for a worship event.
 */
export const EventWorshipForm: React.FC<EventWorshipFormProps> = ({
  visible = true,
  event,
  onClose,
}) => {
  const [schedule, setSchedule] = useState<Schedule | null>(null);
  const [notes, setNotes] = useState<string>("");
  const [tabIndex, setTabIndex] = useState<number>(0);

  useEffect(() => {
    if (event) {
      const s = ScheduleController.get(event);
      setSchedule(s);
      setNotes(s ? s.getNotes() : "");
    }
  }, [event]);

  if (!visible || !event) {
    return null;
  }

  const handleNotesChange = (val: string) => {
    setNotes(val);
    if (schedule) {
      schedule.setNotes(val, true);
    }
  };

  const musics = schedule ? schedule.getMusics() : [];
  const medias = schedule ? schedule.getMedias() : [];

  return (
    <div
      className="swing-modal-overlay"
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.4)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 10000,
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget && onClose) {
          onClose();
        }
      }}
    >
      <div
        className="swing-component swing-frame"
        style={{
          width: 750,
          minHeight: 500,
          display: "flex",
          flexDirection: "column",
          backgroundColor: "var(--swing-bg, #ece9d8)",
          border: "2px solid #555",
          boxShadow: "3px 3px 8px rgba(0,0,0,0.4)",
        }}
      >
        {/* Title bar */}
        <div className="swing-frame-titlebar">
          <div className="swing-frame-title-group">
            <span
              style={{
                display: "inline-block",
                width: 14,
                height: 14,
                backgroundColor: "#2266aa",
                borderRadius: 2,
                marginRight: 6,
              }}
            />
            <span>Culto / Evento</span>
          </div>
          <div className="swing-frame-buttons">
            <button
              type="button"
              className="swing-frame-btn swing-frame-btn-close"
              onClick={onClose}
              title="Close"
              aria-label="Close"
            >
              ×
            </button>
          </div>
        </div>

        {/* Content */}
        <div
          style={{
            flex: 1,
            display: "flex",
            flexDirection: "column",
            padding: 8,
            gap: 8,
            overflow: "hidden",
          }}
        >
          {/* Header Panel (jPanelForm - GridBagLayout) */}
          <JPanel
            border="none"
            padding={4}
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              gap: 4,
            }}
          >
            {/* jLabelName */}
            <WebLabel
              style={{
                fontFamily: "'Trebuchet MS', sans-serif",
                fontSize: 20,
                fontWeight: "bold",
                color: "#111",
                textAlign: "center",
              }}
            >
              {event.getName() || "Culto"}
            </WebLabel>

            {/* jLabelDateTime */}
            <WebLabel
              style={{
                fontFamily: "'Trebuchet MS', sans-serif",
                fontSize: 18,
                color: "#333",
                textAlign: "center",
              }}
            >
              {formatEventDateTime(event.getDate())}
            </WebLabel>
          </JPanel>

          {/* Schedule Grid Panel (jPanelSchedule - BorderLayout Center) */}
          <JPanel
            border="etched"
            style={{
              flex: 1,
              display: "flex",
              flexDirection: "column",
              overflow: "hidden",
              backgroundColor: "#ffffff",
            }}
          >
            <JTabbedPane
              selectedIndex={tabIndex}
              onTabChange={(i) => setTabIndex(i)}
              style={{ flex: 1, display: "flex", flexDirection: "column" }}
            >
              <JTab title={`Músicas (${musics.length})`}>
                <div style={{ flex: 1, overflowY: "auto", padding: 6 }}>
                  {musics.length === 0 ? (
                    <div style={{ padding: 12, color: "#888", textAlign: "center", fontSize: 13 }}>
                      Nenhuma música vinculada a este culto.
                    </div>
                  ) : (
                    <ul style={{ listStyle: "none", margin: 0, padding: 0 }}>
                      {musics.map((m, idx) => (
                        <li
                          key={idx}
                          style={{
                            padding: "6px 10px",
                            borderBottom: "1px solid #e8e8e8",
                            fontSize: 13,
                            display: "flex",
                            justifyContent: "space-between",
                          }}
                        >
                          <span>{idx + 1}. {m.getTitleAndArtistToShow ? m.getTitleAndArtistToShow() : m.toString()}</span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </JTab>

              <JTab title={`Mídias (${medias.length})`}>
                <div style={{ flex: 1, overflowY: "auto", padding: 6 }}>
                  {medias.length === 0 ? (
                    <div style={{ padding: 12, color: "#888", textAlign: "center", fontSize: 13 }}>
                      Nenhuma mídia vinculada.
                    </div>
                  ) : (
                    <ul style={{ listStyle: "none", margin: 0, padding: 0 }}>
                      {medias.map((med, idx) => (
                        <li
                          key={idx}
                          style={{
                            padding: "6px 10px",
                            borderBottom: "1px solid #e8e8e8",
                            fontSize: 13,
                          }}
                        >
                          {idx + 1}. {med.toString ? med.toString() : String(med)}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </JTab>

              <JTab title="Anotações">
                <div style={{ flex: 1, display: "flex", flexDirection: "column", padding: 6 }}>
                  <textarea
                    placeholder="Anotações do culto..."
                    value={notes}
                    onChange={(e) => handleNotesChange(e.target.value)}
                    style={{
                      flex: 1,
                      width: "100%",
                      boxSizing: "border-box",
                      fontFamily: "Monospace, monospace",
                      fontSize: 13,
                      padding: 8,
                      border: "1px solid #7f9db9",
                      resize: "none",
                    }}
                  />
                </div>
              </JTab>
            </JTabbedPane>
          </JPanel>

          {/* Close button at bottom */}
          <div style={{ display: "flex", justifyContent: "flex-end", padding: "4px 0" }}>
            <WebButton onClick={onClose} style={{ width: 80, height: 26 }}>
              Fechar
            </WebButton>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EventWorshipForm;
