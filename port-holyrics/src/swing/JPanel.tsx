import React from "react";
import "./swing.css";

export interface JPanelProps extends React.HTMLAttributes<HTMLDivElement> {
  layout?: "flow" | "border" | "grid" | "vertical" | "horizontal" | "none";
  gap?: number | string;
  border?: "none" | "etched" | "lowered" | "raised" | "line";
  title?: string;
  padding?: number | string;
}

export const JPanel = React.forwardRef<HTMLDivElement, JPanelProps>(
  (
    {
      children,
      layout = "none",
      gap,
      border = "none",
      title,
      padding = title ? undefined : 0,
      className = "",
      style = {},
      ...rest
    },
    ref
  ) => {
    const layoutStyles: React.CSSProperties = {};

    if (layout === "flow" || layout === "horizontal") {
      layoutStyles.display = "flex";
      layoutStyles.flexDirection = "row";
      layoutStyles.alignItems = "center";
      if (gap !== undefined) layoutStyles.gap = gap;
    } else if (layout === "vertical") {
      layoutStyles.display = "flex";
      layoutStyles.flexDirection = "column";
      if (gap !== undefined) layoutStyles.gap = gap;
    } else if (layout === "grid") {
      layoutStyles.display = "grid";
      if (gap !== undefined) layoutStyles.gap = gap;
    } else if (layout === "border") {
      layoutStyles.display = "flex";
      layoutStyles.flexDirection = "column";
    }

    const borderClass =
      title
        ? "swing-panel-titled"
        : border === "etched"
        ? "swing-panel-border-etched"
        : border === "lowered"
        ? "swing-panel-border-lowered"
        : border === "raised"
        ? "swing-panel-border-raised"
        : border === "line"
        ? "swing-panel-border-line"
        : "";

    return (
      <div
        ref={ref}
        className={`swing-component swing-panel ${borderClass} ${className}`.trim()}
        style={{
          ...layoutStyles,
          padding,
          ...style,
        }}
        {...rest}
      >
        {title && <span className="swing-panel-title-label">{title}</span>}
        {children}
      </div>
    );
  }
);

JPanel.displayName = "JPanel";

export default JPanel;
