import React, { useState, useEffect } from "react";
import { JPanel } from "../../../swing/JPanel";
import { WebButton } from "../../../swing/WebButton";
import { JTabbedPane, JTab } from "../../../swing/JTabbedPane";
import { Event } from "../../../model/Event";
import { Schedule } from "../../../model/Schedule";
import { EventController } from "../../../control/EventController";
import { ScheduleController } from "../../../control/ScheduleController";
import "../../../swing/swing.css";

export interface EventFormEditProps {
  visible?: boolean;
  event?: Event;
  onClose?: () => void;
  onSave?: (savedEvent: Event) => void;
  onUpdate?: () => void;
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.edit.event.EventFormEdit
 * Modal dialog for editing an existing Event and its associated Schedule.
 */
export const EventFormEdit: React.FC<EventFormEditProps> = ({
  visible = true,
  event,
  onClose,
  onSave,
  onUpdate,
}) => {
  const [name, setName] = useState<string>("");
  const [dateStr, setDateStr] = useState<string>("");
  const [timeStr, setTimeStr] = useState<string>("19:00");
  const [desc, setDesc] = useState<string>("");
  const [schedule, setSchedule] = useState<Schedule | null>(null);
  const [notes, setNotes] = useState<string>("");
  const [tabIndex, setTabIndex] = useState<number>(0);

  useEffect(() => {
    if (event) {
      setName(event.getName() || "");
      setDesc(event.getDesc() || "");

      const d = event.getDate();
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, "0");
      const dd = String(d.getDate()).padStart(2, "0");
      setDateStr(`${yyyy}-${mm}-${dd}`);

      const hh = String(d.getHours()).padStart(2, "0");
      const min = String(d.getMinutes()).padStart(2, "0");
      setTimeStr(`${hh}:${min}`);

      const s = ScheduleController.get(event);
      setSchedule(s);
      setNotes(s ? s.getNotes() : "");
    }
  }, [event, visible]);

  if (!visible || !event) {
    return null;
  }

  const handleSaveAndExit = () => {
    try {
      const parts = dateStr.split("-");
      const y = Number(parts[0]) || new Date().getFullYear();
      const m = (Number(parts[1]) || 1) - 1;
      const d = Number(parts[2]) || new Date().getDate();

      const timeParts = timeStr.split(":");
      const hours = Number(timeParts[0]) || 0;
      const minutes = Number(timeParts[1]) || 0;

      const targetDate = new Date(y, m, d, hours, minutes, 0, 0);

      event.setName(name.trim());
      event.setDate(targetDate);
      event.setDesc(desc.trim());

      if (schedule) {
        schedule.setNotes(notes, true);
      }

      EventController.save();

      if (onSave) {
        onSave(event);
      }
      if (onUpdate) {
        onUpdate();
      }
      if (onClose) {
        onClose();
      }
    } catch (err) {
      console.error(err);
      alert("Erro ao salvar evento. Tente novamente.");
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
          minHeight: 600,
          maxHeight: "92vh",
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
            <span>Evento</span>
          </div>
          <div className="swing-frame-buttons">
            <button
              type="button"
              className="swing-frame-btn swing-frame-btn-close"
              onClick={handleSaveAndExit}
              title="Close"
              aria-label="Close"
            >
              ×
            </button>
          </div>
        </div>

        {/* Form Container */}
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
          {/* Top Form (jPanelForm - BorderLayout "First") */}
          <JPanel
            border="none"
            padding={4}
            style={{
              display: "flex",
              flexDirection: "column",
              gap: 6,
            }}
          >
            {/* jTextFieldName */}
            <div>
              <input
                type="text"
                maxLength={25}
                placeholder="Nome"
                value={name}
                onChange={(e) => setName(e.target.value)}
                style={{
                  width: "100%",
                  boxSizing: "border-box",
                  fontFamily: "Monospace, monospace",
                  fontSize: 14,
                  textAlign: "center",
                  padding: "5px 8px",
                  border: "1px solid #7f9db9",
                }}
              />
            </div>

            {/* jPanelDateTime */}
            <div
              style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                gap: 12,
              }}
            >
              <input
                type="date"
                value={dateStr}
                onChange={(e) => setDateStr(e.target.value)}
                style={{
                  fontFamily: "Georgia, serif",
                  fontSize: 14,
                  padding: "3px 8px",
                  border: "1px solid #7f9db9",
                  textAlign: "center",
                }}
              />
              <input
                type="time"
                value={timeStr}
                onChange={(e) => setTimeStr(e.target.value)}
                style={{
                  fontFamily: "Georgia, serif",
                  fontSize: 14,
                  padding: "3px 8px",
                  border: "1px solid #7f9db9",
                  textAlign: "center",
                }}
              />
            </div>

            {/* jScrollPaneDesc / jTextAreaDesc */}
            <div>
              <textarea
                maxLength={2048}
                placeholder="Descrição"
                value={desc}
                onChange={(e) => setDesc(e.target.value)}
                rows={4}
                style={{
                  width: "100%",
                  boxSizing: "border-box",
                  fontFamily: "Monospace, monospace",
                  fontSize: 14,
                  padding: "5px 8px",
                  border: "1px solid #7f9db9",
                  resize: "none",
                }}
              />
            </div>
          </JPanel>

          {/* Schedule Section (jPanelSchedule - BorderLayout "Center") */}
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
                      Nenhuma música na programação deste evento.
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
                    placeholder="Anotações do evento..."
                    value={notes}
                    onChange={(e) => setNotes(e.target.value)}
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

          {/* Bottom Buttons (jPanelButtons - BorderLayout "Last") */}
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: 15,
              padding: "4px 0",
            }}
          >
            <WebButton
              onClick={handleSaveAndExit}
              title="OK"
              style={{
                width: 44,
                height: 36,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: 18,
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              ✓
            </WebButton>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EventFormEdit;
