import React, { useState } from "react";
import "./swing.css";

export interface TabItem {
  id?: string;
  title: React.ReactNode;
  icon?: React.ReactNode;
  content: React.ReactNode;
  disabled?: boolean;
}

export interface JTabbedPaneProps {
  tabs?: TabItem[];
  selectedIndex?: number;
  defaultSelectedIndex?: number;
  onTabChange?: (index: number) => void;
  children?: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
}

export interface JTabProps {
  title: React.ReactNode;
  icon?: React.ReactNode;
  disabled?: boolean;
  children: React.ReactNode;
}

export const JTab: React.FC<JTabProps> = ({ children }) => {
  return <>{children}</>;
};

export const JTabbedPane: React.FC<JTabbedPaneProps> = ({
  tabs,
  selectedIndex,
  defaultSelectedIndex = 0,
  onTabChange,
  children,
  className = "",
  style = {},
}) => {
  // Convert children to tabs if tabs prop isn't provided
  const resolvedTabs: TabItem[] = React.useMemo(() => {
    if (tabs && tabs.length > 0) {
      return tabs;
    }

    const items: TabItem[] = [];
    React.Children.forEach(children, (child) => {
      if (React.isValidElement<JTabProps>(child)) {
        items.push({
          title: child.props.title,
          icon: child.props.icon,
          disabled: child.props.disabled,
          content: child.props.children,
        });
      }
    });
    return items;
  }, [tabs, children]);

  const [internalIndex, setInternalIndex] = useState(defaultSelectedIndex);
  const activeIdx = selectedIndex !== undefined ? selectedIndex : internalIndex;

  const handleSelect = (idx: number, disabled?: boolean) => {
    if (disabled) return;
    if (selectedIndex === undefined) {
      setInternalIndex(idx);
    }
    if (onTabChange) {
      onTabChange(idx);
    }
  };

  const currentTab = resolvedTabs[activeIdx];

  return (
    <div
      className={`swing-component swing-tabbed-pane ${className}`.trim()}
      style={style}
    >
      <div className="swing-tab-bar">
        {resolvedTabs.map((tab, idx) => {
          const isActive = idx === activeIdx;
          return (
            <button
              key={tab.id ?? idx}
              type="button"
              className={`swing-tab ${isActive ? "swing-tab-active" : ""}`}
              disabled={tab.disabled}
              onClick={() => handleSelect(idx, tab.disabled)}
              style={tab.disabled ? { opacity: 0.5, cursor: "not-allowed" } : undefined}
            >
              {tab.icon && <span>{tab.icon}</span>}
              <span>{tab.title}</span>
            </button>
          );
        })}
      </div>
      <div className="swing-tab-content">
        {currentTab ? currentTab.content : null}
      </div>
    </div>
  );
};

export default JTabbedPane;
