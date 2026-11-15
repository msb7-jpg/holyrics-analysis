import React, { useState, useEffect } from "react";
import { JPanel } from "../../swing/JPanel";
import { WebButton } from "../../swing/WebButton";
import { JTabbedPane, JTab } from "../../swing/JTabbedPane";
import EditWorship from "./EditWorship";
import "../../swing/swing.css";

export interface EditDialogProps {
  visible?: boolean;
  panel?: number;
  onClose?: () => void;
  callback?: () => void;
  title?: string;
  children?: React.ReactNode;
}

export const PANEL_DEFAULT = 0;
export const PANEL_PROFILE = 0;
export const PANEL_WORSHIP = 1;
export const PANEL_EVENT = 2;
export const PANEL_MEMBER = 3;

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.edit.EditDialog
 * Classic Java Swing modal dialog wrapping Edit panels.
 */
export const EditDialog: React.FC<EditDialogProps> = ({
  visible = true,
  panel = PANEL_DEFAULT,
  onClose,
  callback,
  title = "Holyrics",
  children,
}) => {
  const [selectedIndex, setSelectedIndex] = useState<number>(panel);

  useEffect(() => {
    setSelectedIndex(panel);
  }, [panel]);

  if (!visible) {
    return null;
  }

  const handleClose = () => {
    if (callback) {
      callback();
    }
    if (onClose) {
      onClose();
    }
  };

  const handleOk = () => {
    handleClose();
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
        zIndex: 9999,
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          handleClose();
        }
      }}
    >
      <div
        className="swing-component swing-frame"
        style={{
          width: 820,
          minHeight: 520,
          maxHeight: "90vh",
          display: "flex",
          flexDirection: "column",
          backgroundColor: "var(--swing-bg, #ece9d8)",
          border: "2px solid #555",
          boxShadow: "4px 4px 10px rgba(0,0,0,0.5)",
        }}
      >
        {/* Title Bar */}
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
            <span style={{ fontWeight: "bold" }}>{title}</span>
          </div>
          <div className="swing-frame-buttons">
            <button
              type="button"
              className="swing-frame-btn swing-frame-btn-close"
              onClick={handleClose}
              title="Close"
              aria-label="Close"
            >
              ×
            </button>
          </div>
        </div>

        {/* Content Area (BorderLayout Center) */}
        <div
          style={{
            flex: 1,
            display: "flex",
            flexDirection: "column",
            overflow: "auto",
            padding: 4,
          }}
        >
          {children ? (
            children
          ) : (
            <JTabbedPane
              selectedIndex={selectedIndex}
              onTabChange={(idx) => setSelectedIndex(idx)}
              style={{ flex: 1, display: "flex", flexDirection: "column" }}
            >
              <JTab title="Perfil">
                <JPanel
                  layout="vertical"
                  padding={12}
                  style={{ minHeight: 380, justifyContent: "center", alignItems: "center" }}
                >
                  <span style={{ fontSize: 13, color: "#666" }}>Configurações de Perfil</span>
                </JPanel>
              </JTab>
              <JTab title="Culto">
                <EditWorship />
              </JTab>
              <JTab title="Evento">
                <JPanel
                  layout="vertical"
                  padding={12}
                  style={{ minHeight: 380, justifyContent: "center", alignItems: "center" }}
                >
                  <span style={{ fontSize: 13, color: "#666" }}>Configurações de Evento</span>
                </JPanel>
              </JTab>
              <JTab title="Membro">
                <JPanel
                  layout="vertical"
                  padding={12}
                  style={{ minHeight: 380, justifyContent: "center", alignItems: "center" }}
                >
                  <span style={{ fontSize: 13, color: "#666" }}>Configurações de Membro</span>
                </JPanel>
              </JTab>
            </JTabbedPane>
          )}
        </div>

        {/* Footer with OK button (BorderLayout Last) */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            padding: "8px 0 10px 0",
            borderTop: "1px solid #d0d0d0",
            backgroundColor: "var(--swing-bg, #ece9d8)",
          }}
        >
          <WebButton
            onClick={handleOk}
            style={{
              fontFamily: "Georgia, serif",
              fontSize: 16,
              width: 100,
              height: 35,
              fontWeight: "normal",
              cursor: "pointer",
            }}
          >
            OK
          </WebButton>
        </div>
      </div>
    </div>
  );
};

export default EditDialog;
