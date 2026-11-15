import React from "react";
import "./swing.css";

export interface ComboBoxItem {
  value: string;
  label: React.ReactNode;
}

export interface WebComboBoxProps
  extends Omit<React.SelectHTMLAttributes<HTMLSelectElement>, "value" | "onChange"> {
  items?: (string | ComboBoxItem)[];
  value?: string;
  onChange?: (value: string) => void;
  onSelectValue?: (value: string) => void;
}

export const WebComboBox = React.forwardRef<HTMLSelectElement, WebComboBoxProps>(
  (
    {
      items = [],
      value,
      onChange,
      onSelectValue,
      children,
      className = "",
      style = {},
      ...rest
    },
    ref
  ) => {
    const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
      const val = e.target.value;
      if (onChange) onChange(val);
      if (onSelectValue) onSelectValue(val);
    };

    return (
      <select
        ref={ref}
        value={value}
        onChange={handleChange}
        className={`swing-component swing-web-combobox ${className}`.trim()}
        style={style}
        {...rest}
      >
        {items.map((item, index) => {
          if (typeof item === "string") {
            return (
              <option key={`${item}-${index}`} value={item}>
                {item}
              </option>
            );
          }
          return (
            <option key={`${item.value}-${index}`} value={item.value}>
              {item.label}
            </option>
          );
        })}
        {children}
      </select>
    );
  }
);

WebComboBox.displayName = "WebComboBox";

export default WebComboBox;
