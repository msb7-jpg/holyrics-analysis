import { describe, expect, test } from "bun:test";
import React from "react";
import { MusicMain } from "../view/MusicMain";

describe("MusicMain component", () => {
  test("MusicMain component is defined and can be instantiated", () => {
    expect(MusicMain).toBeDefined();
    const element = React.createElement(MusicMain, { title: "Test Holyrics" });
    expect(element).toBeDefined();
    expect(element.props.title).toBe("Test Holyrics");
  });
});
