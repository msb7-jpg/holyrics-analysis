import React from "react";
import "./swing.css";

export interface WebLabelProps
  extends React.HTMLAttributes<HTMLSpanElement> {
  icon?: React.ReactNode;
  text?: React.ReactNode;
  htmlFor?: string;
}

export const WebLabel = React.forwardRef<HTMLSpanElement, WebLabelProps>(
  ({ children, icon, text, className = "", style = {}, ...rest }, ref) => {
    return (
      <span
        ref={ref}
        className={`swing-component swing-web-label ${className}`.trim()}
        style={style}
        {...rest}
      >
        {icon && <span style={{ display: "inline-flex", alignItems: "center" }}>{icon}</span>}
        {text ?? children}
      </span>
    );
  }
);

WebLabel.displayName = "WebLabel";

export default WebLabel;
