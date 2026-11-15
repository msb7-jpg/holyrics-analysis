import { describe, expect, test } from "bun:test";
import React from "react";
import {
  JFrame,
  JPanel,
  JSplitPane,
  JTabbedPane,
  JTab,
  JTable,
  JTree,
  WebButton,
  WebLabel,
  WebComboBox,
  HORIZONTAL_SPLIT,
  VERTICAL_SPLIT,
} from "../swing/index";

describe("Swing components", () => {
  test("components and exports are defined", () => {
    expect(JFrame).toBeDefined();
    expect(JPanel).toBeDefined();
    expect(JSplitPane).toBeDefined();
    expect(HORIZONTAL_SPLIT).toBe("horizontal");
    expect(VERTICAL_SPLIT).toBe("vertical");
    expect(JTabbedPane).toBeDefined();
    expect(JTab).toBeDefined();
    expect(JTable).toBeDefined();
    expect(JTree).toBeDefined();
    expect(WebButton).toBeDefined();
    expect(WebLabel).toBeDefined();
    expect(WebComboBox).toBeDefined();
  });
});
