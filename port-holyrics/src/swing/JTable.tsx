import React, { useState } from "react";
import "./swing.css";

export interface Column<T = any> {
  key: string;
  header: React.ReactNode;
  width?: number | string;
  align?: "left" | "center" | "right";
  render?: (value: any, row: T, index: number) => React.ReactNode;
}

export interface JTableProps<T = any> {
  columns: Column<T>[];
  data: T[];
  selectedRowIndex?: number;
  defaultSelectedRowIndex?: number;
  onSelectRow?: (rowIndex: number, row: T) => void;
  onDoubleClickRow?: (rowIndex: number, row: T) => void;
  rowKey?: (row: T, index: number) => string | number;
  className?: string;
  style?: React.CSSProperties;
  showHeader?: boolean;
}

export function JTable<T = any>({
  columns,
  data,
  selectedRowIndex,
  defaultSelectedRowIndex = -1,
  onSelectRow,
  onDoubleClickRow,
  rowKey,
  className = "",
  style = {},
  showHeader = true,
}: JTableProps<T>) {
  const [internalSelected, setInternalSelected] = useState<number>(defaultSelectedRowIndex);

  const activeSelected =
    selectedRowIndex !== undefined ? selectedRowIndex : internalSelected;

  const handleRowClick = (rowIndex: number, row: T) => {
    if (selectedRowIndex === undefined) {
      setInternalSelected(rowIndex);
    }
    if (onSelectRow) {
      onSelectRow(rowIndex, row);
    }
  };

  return (
    <div
      className={`swing-component swing-table-container ${className}`.trim()}
      style={style}
    >
      <table className="swing-table">
        {showHeader && (
          <thead>
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className="swing-table-header-cell"
                  style={{
                    width: col.width,
                    textAlign: col.align || "left",
                  }}
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
        )}
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td
                colSpan={columns.length}
                style={{ textAlign: "center", padding: "8px", color: "#888888" }}
              >
                (Sem dados)
              </td>
            </tr>
          ) : (
            data.map((row, index) => {
              const isSelected = index === activeSelected;
              const key = rowKey
                ? rowKey(row, index)
                : (row as any)?.id ?? index;

              return (
                <tr
                  key={key}
                  className={`swing-table-row ${isSelected ? "swing-table-row-selected" : ""}`}
                  onClick={() => handleRowClick(index, row)}
                  onDoubleClick={() => onDoubleClickRow && onDoubleClickRow(index, row)}
                >
                  {columns.map((col) => {
                    const rawVal = (row as any)?.[col.key];
                    const content = col.render
                      ? col.render(rawVal, row, index)
                      : String(rawVal ?? "");
                    return (
                      <td
                        key={col.key}
                        className="swing-table-cell"
                        style={{ textAlign: col.align || "left" }}
                      >
                        {content}
                      </td>
                    );
                  })}
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>
  );
}

export default JTable;
