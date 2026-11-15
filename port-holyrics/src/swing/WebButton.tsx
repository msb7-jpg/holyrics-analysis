import React from "react";
import "./swing.css";

export interface WebButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  icon?: React.ReactNode;
  dense?: boolean;
}

export const WebButton = React.forwardRef<HTMLButtonElement, WebButtonProps>(
  ({ children, icon, dense = true, className = "", style = {}, ...rest }, ref) => {
    return (
      <button
        ref={ref}
        type="button"
        className={`swing-component swing-web-button ${className}`.trim()}
        style={{
          padding: dense ? "1px 6px" : "2px 8px",
          ...style,
        }}
        {...rest}
      >
        {icon && <span style={{ display: "inline-flex", alignItems: "center" }}>{icon}</span>}
        {children}
      </button>
    );
  }
);

WebButton.displayName = "WebButton";

export default WebButton;
