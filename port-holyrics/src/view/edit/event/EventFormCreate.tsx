import React, { useState, useEffect } from "react";
import { WebButton } from "../../../swing/WebButton";
import { Event } from "../../../model/Event";
import { EventController } from "../../../control/EventController";
import "../../../swing/swing.css";

export interface EventFormCreateProps {
  visible?: boolean;
  event?: Event;
  onClose?: () => void;
  onSave?: (createdEvent: Event) => void;
  onUpdate?: () => void;
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.edit.event.EventFormCreate
 * Modal dialog for creating a new Event.
 */
export const EventFormCreate: React.FC<EventFormCreateProps> = ({
  visible = true,
  event: initialEvent,
  onClose,
  onSave,
  onUpdate,
}) => {
  const [name, setName] = useState<string>("");
  const [dateStr, setDateStr] = useState<string>("");
  const [timeStr, setTimeStr] = useState<string>("19:00");
  const [desc, setDesc] = useState<string>("");

  useEffect(() => {
    const baseDate = initialEvent ? initialEvent.getDate() : new Date();
    const yyyy = baseDate.getFullYear();
    const mm = String(baseDate.getMonth() + 1).padStart(2, "0");
    const dd = String(baseDate.getDate()).padStart(2, "0");
    setDateStr(`${yyyy}-${mm}-${dd}`);

    const hh = String(baseDate.getHours()).padStart(2, "0");
    const min = String(baseDate.getMinutes()).padStart(2, "0");
    setTimeStr(`${hh}:${min}`);

    if (initialEvent) {
      setName(initialEvent.getName() || "");
      setDesc(initialEvent.getDesc() || "");
    } else {
      setName("");
      setDesc("");
    }
  }, [initialEvent, visible]);

  if (!visible) {
    return null;
  }

  const handleCreate = () => {
    try {
      const parts = dateStr.split("-");
      const y = Number(parts[0]) || new Date().getFullYear();
      const m = (Number(parts[1]) || 1) - 1;
      const d = Number(parts[2]) || new Date().getDate();

      const timeParts = timeStr.split(":");
      const hours = Number(timeParts[0]) || 0;
      const minutes = Number(timeParts[1]) || 0;

      const targetDate = new Date(y, m, d, hours, minutes, 0, 0);

      const targetEvent = initialEvent ? initialEvent : new Event(targetDate);
      targetEvent.setName(name.trim());
      targetEvent.setDate(targetDate);
      targetEvent.setDesc(desc.trim());

      EventController.add(targetEvent);

      if (onSave) {
        onSave(targetEvent);
      }
      if (onUpdate) {
        onUpdate();
      }
      if (onClose) {
        onClose();
      }
    } catch (err) {
      console.error(err);
      alert("Erro ao criar evento. Tente novamente.");
    }
  };

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
          width: 500,
          minHeight: 450,
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
              onClick={onClose}
              title="Close"
              aria-label="Close"
            >
              ×
            </button>
          </div>
        </div>

        {/* Form body (jPanelForm - GridBagLayout) */}
        <div
          style={{
            flex: 1,
            display: "flex",
            flexDirection: "column",
            padding: 8,
            gap: 10,
          }}
        >
          {/* Row 0: jTextFieldName */}
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
                padding: "6px 8px",
                border: "1px solid #7f9db9",
              }}
              autoFocus
            />
          </div>

          {/* Row 1: jPanelDateTime (Date picker + Time spinner) */}
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: 12,
              padding: "4px 0",
            }}
          >
            <input
              type="date"
              value={dateStr}
              onChange={(e) => setDateStr(e.target.value)}
              style={{
                fontFamily: "Georgia, serif",
                fontSize: 14,
                padding: "4px 8px",
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
                padding: "4px 8px",
                border: "1px solid #7f9db9",
                textAlign: "center",
              }}
            />
          </div>

          {/* Row 2: jScrollPaneDesc / jTextAreaDesc */}
          <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
            <textarea
              maxLength={2048}
              placeholder="Descrição"
              value={desc}
              onChange={(e) => setDesc(e.target.value)}
              rows={8}
              style={{
                width: "100%",
                flex: 1,
                boxSizing: "border-box",
                fontFamily: "Monospace, monospace",
                fontSize: 14,
                padding: "6px 8px",
                border: "1px solid #7f9db9",
                resize: "none",
              }}
            />
          </div>

          {/* Row 3: jPanelButtons (FlowLayout: Exit + Save) */}
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: 20,
              padding: "6px 0",
            }}
          >
            <WebButton
              onClick={onClose}
              title="Fechar"
              style={{
                width: 44,
                height: 36,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: 18,
                cursor: "pointer",
              }}
            >
              ✕
            </WebButton>
            <WebButton
              onClick={handleCreate}
              title="Salvar"
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
              💾
            </WebButton>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EventFormCreate;
