import React from "react";
import "./swing.css";

export interface JFrameProps {
  title?: string;
  icon?: React.ReactNode;
  width?: number | string;
  height?: number | string;
  menuBar?: React.ReactNode;
  onClose?: () => void;
  onMinimize?: () => void;
  onMaximize?: () => void;
  closable?: boolean;
  maximizable?: boolean;
  minimizable?: boolean;
  children?: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
}

export const JFrame: React.FC<JFrameProps> = ({
  title = "Application Window",
  icon,
  width = "100%",
  height = "100%",
  menuBar,
  onClose,
  onMinimize,
  onMaximize,
  closable = true,
  maximizable = true,
  minimizable = true,
  children,
  className = "",
  style = {},
}) => {
  return (
    <div
      className={`swing-component swing-frame ${className}`}
      style={{
        width,
        height,
        ...style,
      }}
    >
      <div className="swing-frame-titlebar">
        <div className="swing-frame-title-group">
          {icon && <span className="swing-frame-icon">{icon}</span>}
          <span>{title}</span>
        </div>
        <div className="swing-frame-buttons">
          {minimizable && (
            <button
              type="button"
              className="swing-frame-btn"
              onClick={onMinimize}
              title="Minimize"
              aria-label="Minimize"
            >
              _
            </button>
          )}
          {maximizable && (
            <button
              type="button"
              className="swing-frame-btn"
              onClick={onMaximize}
              title="Maximize"
              aria-label="Maximize"
            >
              □
            </button>
          )}
          {closable && (
            <button
              type="button"
              className="swing-frame-btn swing-frame-btn-close"
              onClick={onClose}
              title="Close"
              aria-label="Close"
            >
              ×
            </button>
          )}
        </div>
      </div>
      {menuBar && <div className="swing-frame-menubar">{menuBar}</div>}
      <div className="swing-frame-content">{children}</div>
    </div>
  );
};

export default JFrame;
