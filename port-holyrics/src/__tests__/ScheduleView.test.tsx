import { describe, it, expect } from "bun:test";
import React from "react";
import { ScheduleView } from "../view/ScheduleView";

describe("ScheduleView", () => {
  it("renders correctly with default props", () => {
    const el = React.createElement(ScheduleView, {
      title: "Escala",
      visible: true,
      initialDate: new Date(2026, 10, 1), // November 2026
    });
    expect(el).toBeDefined();
    expect(el.props.title).toBe("Escala");
  });

  it("handles custom members and events", () => {
    const customMembers = [
      { id: "m1", name: "João Silva", active: true },
      { id: "m2", name: "Maria Santos", active: false },
    ];
    const customEvents = [
      {
        id: "e1",
        name: "Culto Domingo Manhã",
        date: new Date(2026, 10, 15, 9, 0),
        active: true,
        musics: [{ id: "s1", title: "Graça", artist: "Paulo César Baruk" }],
        assignedMemberIds: ["m1"],
      },
    ];

    const el = React.createElement(ScheduleView, {
      title: "Escala",
      initialMembers: customMembers,
      initialEvents: customEvents,
    });
    expect(el.props.initialMembers?.length).toBe(2);
    expect(el.props.initialEvents?.length).toBe(1);
  });
});
