import React, { useState } from "react";
import "./swing.css";

export interface TreeNode<T = any> {
  id: string;
  label: React.ReactNode;
  icon?: React.ReactNode;
  children?: TreeNode<T>[];
  data?: T;
}

export interface JTreeProps<T = any> {
  data: TreeNode<T>[];
  selectedId?: string;
  defaultSelectedId?: string;
  defaultExpandedIds?: string[];
  onSelectNode?: (node: TreeNode<T>) => void;
  className?: string;
  style?: React.CSSProperties;
}

interface TreeItemProps<T> {
  node: TreeNode<T>;
  level: number;
  selectedId?: string;
  expandedIds: Set<string>;
  onToggle: (id: string) => void;
  onSelect: (node: TreeNode<T>) => void;
}

function TreeItem<T>({
  node,
  level,
  selectedId,
  expandedIds,
  onToggle,
  onSelect,
}: TreeItemProps<T>) {
  const hasChildren = Boolean(node.children && node.children.length > 0);
  const isExpanded = expandedIds.has(node.id);
  const isSelected = selectedId === node.id;

  return (
    <div className="swing-tree-node">
      <div
        className={`swing-tree-row ${isSelected ? "swing-tree-row-selected" : ""}`}
        style={{ paddingLeft: `${level * 16 + 2}px` }}
        onClick={() => onSelect(node)}
      >
        <span
          className="swing-tree-toggle"
          onClick={(e) => {
            e.stopPropagation();
            if (hasChildren) {
              onToggle(node.id);
            }
          }}
        >
          {hasChildren ? (isExpanded ? "▼" : "►") : " "}
        </span>

        {node.icon ? (
          <span className="swing-tree-icon">{node.icon}</span>
        ) : (
          <span className="swing-tree-icon">
            {hasChildren ? (isExpanded ? "📂" : "📁") : "📄"}
          </span>
        )}

        <span className="swing-tree-label">{node.label}</span>
      </div>

      {hasChildren && isExpanded && (
        <div>
          {node.children!.map((child) => (
            <TreeItem
              key={child.id}
              node={child}
              level={level + 1}
              selectedId={selectedId}
              expandedIds={expandedIds}
              onToggle={onToggle}
              onSelect={onSelect}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export function JTree<T = any>({
  data,
  selectedId,
  defaultSelectedId,
  defaultExpandedIds = [],
  onSelectNode,
  className = "",
  style = {},
}: JTreeProps<T>) {
  const [internalSelected, setInternalSelected] = useState<string | undefined>(
    defaultSelectedId
  );
  const [expandedIds, setExpandedIds] = useState<Set<string>>(
    () => new Set(defaultExpandedIds)
  );

  const activeSelectedId = selectedId !== undefined ? selectedId : internalSelected;

  const handleToggle = (id: string) => {
    setExpandedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const handleSelect = (node: TreeNode<T>) => {
    if (selectedId === undefined) {
      setInternalSelected(node.id);
    }
    if (onSelectNode) {
      onSelectNode(node);
    }
  };

  return (
    <div
      className={`swing-component swing-tree ${className}`.trim()}
      style={style}
    >
      {data.map((node) => (
        <TreeItem
          key={node.id}
          node={node}
          level={0}
          selectedId={activeSelectedId}
          expandedIds={expandedIds}
          onToggle={handleToggle}
          onSelect={handleSelect}
        />
      ))}
    </div>
  );
}

export default JTree;
