import React, { useState, useRef, useCallback, useEffect } from "react";
import "./swing.css";

export const HORIZONTAL_SPLIT = "horizontal" as const;
export const VERTICAL_SPLIT = "vertical" as const;

export type SplitOrientation = typeof HORIZONTAL_SPLIT | typeof VERTICAL_SPLIT;

export interface JSplitPaneProps {
  orientation?: SplitOrientation;
  left?: React.ReactNode;
  right?: React.ReactNode;
  top?: React.ReactNode;
  bottom?: React.ReactNode;
  leftComponent?: React.ReactNode;
  rightComponent?: React.ReactNode;
  topComponent?: React.ReactNode;
  bottomComponent?: React.ReactNode;
  children?: [React.ReactNode, React.ReactNode] | React.ReactNode;
  dividerLocation?: number; // In px or fraction 0..1
  dividerSize?: number;
  minSize?: number;
  onDividerMove?: (newLocation: number) => void;
  className?: string;
  style?: React.CSSProperties;
}

export const JSplitPane: React.FC<JSplitPaneProps> = ({
  orientation = "horizontal",
  left,
  right,
  top,
  bottom,
  leftComponent,
  rightComponent,
  topComponent,
  bottomComponent,
  children,
  dividerLocation,
  dividerSize = 6,
  minSize = 30,
  onDividerMove,
  className = "",
  style = {},
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const isHorizontal = orientation === "horizontal";

  // Resolve panes
  let firstPane: React.ReactNode = null;
  let secondPane: React.ReactNode = null;

  if (isHorizontal) {
    firstPane = left ?? leftComponent;
    secondPane = right ?? rightComponent;
  } else {
    firstPane = top ?? topComponent;
    secondPane = bottom ?? bottomComponent;
  }

  if (firstPane === undefined && secondPane === undefined && children) {
    if (Array.isArray(children)) {
      firstPane = children[0];
      secondPane = children[1];
    } else {
      firstPane = children;
    }
  }

  // Initial position: default to 50% or dividerLocation
  const [pos, setPos] = useState<number | null>(() => {
    if (dividerLocation !== undefined) {
      return dividerLocation;
    }
    return null; // Calculated on mount
  });

  const [isDragging, setIsDragging] = useState(false);

  useEffect(() => {
    if (dividerLocation !== undefined) {
      setPos(dividerLocation);
    }
  }, [dividerLocation]);

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    setIsDragging(true);
  }, []);

  useEffect(() => {
    if (!isDragging) return;

    const handleMouseMove = (e: MouseEvent) => {
      if (!containerRef.current) return;
      const rect = containerRef.current.getBoundingClientRect();
      let nextPos = isHorizontal ? e.clientX - rect.left : e.clientY - rect.top;

      const maxLimit = (isHorizontal ? rect.width : rect.height) - minSize;
      if (nextPos < minSize) nextPos = minSize;
      if (nextPos > maxLimit) nextPos = maxLimit;

      setPos(nextPos);
      if (onDividerMove) {
        onDividerMove(nextPos);
      }
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    window.addEventListener("mousemove", handleMouseMove);
    window.addEventListener("mouseup", handleMouseUp);

    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging, isHorizontal, minSize, onDividerMove]);

  // Determine first pane style based on pos
  const firstPaneStyle: React.CSSProperties = {};
  if (pos !== null) {
    if (pos <= 1 && pos > 0) {
      // Fraction (0..1)
      const pct = `${pos * 100}%`;
      if (isHorizontal) {
        firstPaneStyle.width = pct;
        firstPaneStyle.flexShrink = 0;
      } else {
        firstPaneStyle.height = pct;
        firstPaneStyle.flexShrink = 0;
      }
    } else {
      // Pixels
      if (isHorizontal) {
        firstPaneStyle.width = `${pos}px`;
        firstPaneStyle.flexShrink = 0;
      } else {
        firstPaneStyle.height = `${pos}px`;
        firstPaneStyle.flexShrink = 0;
      }
    }
  } else {
    // 50-50 flex default
    firstPaneStyle.flex = 1;
  }

  const dividerClass = isHorizontal
    ? "swing-split-divider swing-split-divider-horizontal"
    : "swing-split-divider swing-split-divider-vertical";

  const containerOrientationClass = isHorizontal
    ? "swing-split-pane-horizontal"
    : "swing-split-pane-vertical";

  return (
    <div
      ref={containerRef}
      className={`swing-component swing-split-pane ${containerOrientationClass} ${className}`.trim()}
      style={style}
    >
      <div
        style={{
          ...firstPaneStyle,
          overflow: "auto",
        }}
      >
        {firstPane}
      </div>

      <div
        className={dividerClass}
        style={isHorizontal ? { width: dividerSize } : { height: dividerSize }}
        onMouseDown={handleMouseDown}
      >
        <div className="swing-split-divider-grip" />
      </div>

      <div
        style={{
          flex: 1,
          overflow: "auto",
        }}
      >
        {secondPane}
      </div>
    </div>
  );
};

export default JSplitPane;
