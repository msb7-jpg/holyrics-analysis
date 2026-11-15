import React, { useState, useEffect, useCallback } from "react";
import { JPanel } from "../../swing/JPanel";
import { WebButton } from "../../swing/WebButton";
import { WebLabel } from "../../swing/WebLabel";
import { WebComboBox } from "../../swing/WebComboBox";
import { Worship, WorshipDay, WorshipWeek } from "../../model/Worship";
import { WorshipController } from "../../control/WorshipController";
import "../../swing/swing.css";

export interface EditWorshipProps {
  onClose?: () => void;
}

interface WorshipEditModalProps {
  worship: Worship;
  onClose: () => void;
  onSave: () => void;
}

/**
 * Modal to edit an existing Worship item.
 * Isomorphic TypeScript port of EditWorship.WorshipEditView
 */
const WorshipEditModal: React.FC<WorshipEditModalProps> = ({
  worship,
  onClose,
  onSave,
}) => {
  const [hour, setHour] = useState<number>(worship.hour);
  const [minute, setMinute] = useState<number>(worship.minute);
  const [description, setDescription] = useState<string>(worship.desc);
  const [keepSchedules, setKeepSchedules] = useState<boolean>(true);

  const handleSave = () => {
    try {
      const updated = new Worship(
        worship.day,
        worship.week,
        Number(hour),
        Number(minute),
        description.trim(),
        worship.id
      );

      if (keepSchedules) {
        WorshipController.setTimeAndDesc(worship, updated);
        WorshipController.save();
      } else {
        const confirmed = window.confirm(
          "Deseja realmente alterar o culto? As programações associadas serão desvinculadas."
        );
        if (confirmed) {
          WorshipController.remove(worship);
          WorshipController.add(updated);
          WorshipController.save();
        } else {
          return;
        }
      }

      onSave();
      onClose();
    } catch (err) {
      console.error(err);
      alert("Erro ao salvar culto. Tente novamente.");
    }
  };

  return (
    <div
      className="swing-modal-overlay"
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.4)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 10000,
      }}
    >
      <div
        className="swing-component swing-frame"
        style={{
          width: 520,
          backgroundColor: "var(--swing-bg, #ece9d8)",
          border: "2px solid #555",
          boxShadow: "3px 3px 8px rgba(0,0,0,0.4)",
        }}
      >
        <div className="swing-frame-titlebar">
          <span>Editar Culto</span>
          <button
            type="button"
            className="swing-frame-btn swing-frame-btn-close"
            onClick={onClose}
          >
            ×
          </button>
        </div>

        <div style={{ padding: 12 }}>
          <JPanel
            border="etched"
            padding={10}
            style={{
              display: "flex",
              flexDirection: "column",
              gap: 10,
              backgroundColor: "#f5f5f5",
            }}
          >
            {/* Day and Week (disabled) */}
            <div style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 12 }}>
              <WebLabel style={{ fontFamily: "Georgia, serif", fontSize: 12 }}>
                Dia:
              </WebLabel>
              <WebComboBox
                disabled
                items={WorshipWeek.values().map((w) => ({
                  value: w.key,
                  label: w.label,
                }))}
                value={worship.week.key}
                style={{ fontFamily: "Georgia, serif", fontSize: 12 }}
              />
              <WebComboBox
                disabled
                items={WorshipDay.values().map((d) => ({
                  value: d.key,
                  label: d.label,
                }))}
                value={worship.day.key}
                style={{ fontFamily: "Georgia, serif", fontSize: 12 }}
              />
            </div>

            {/* Time and Description */}
            <div style={{ display: "flex", alignItems: "center", gap: 6, fontSize: 12 }}>
              <WebLabel style={{ fontFamily: "Georgia, serif", fontSize: 12 }}>
                Horário:
              </WebLabel>
              <input
                type="number"
                min={0}
                max={23}
                value={hour}
                onChange={(e) => setHour(Math.max(0, Math.min(23, Number(e.target.value))))}
                style={{
                  width: 50,
                  fontFamily: "Georgia, serif",
                  fontSize: 12,
                  textAlign: "center",
                  border: "1px solid #7f9db9",
                  padding: "2px 4px",
                }}
              />
              <span style={{ fontFamily: "Georgia, serif", fontSize: 14, fontWeight: "bold" }}>
                :
              </span>
              <input
                type="number"
                min={0}
                max={55}
                step={5}
                value={minute}
                onChange={(e) => setMinute(Math.max(0, Math.min(55, Number(e.target.value))))}
                style={{
                  width: 50,
                  fontFamily: "Georgia, serif",
                  fontSize: 12,
                  textAlign: "center",
                  border: "1px solid #7f9db9",
                  padding: "2px 4px",
                }}
              />
              <input
                type="text"
                maxLength={20}
                placeholder="Descrição"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                style={{
                  flex: 1,
                  fontFamily: "Georgia, serif",
                  fontSize: 12,
                  textAlign: "center",
                  border: "1px solid #7f9db9",
                  padding: "2px 6px",
                }}
              />
            </div>

            {/* Keep schedules checkbox */}
            <label
              style={{
                display: "flex",
                alignItems: "center",
                gap: 6,
                fontSize: 12,
                cursor: "pointer",
                marginTop: 4,
              }}
            >
              <input
                type="checkbox"
                checked={keepSchedules}
                onChange={(e) => setKeepSchedules(e.target.checked)}
              />
              <span>Manter programações associadas a este culto</span>
            </label>
          </JPanel>

          {/* Action buttons */}
          <div
            style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: 8,
              marginTop: 12,
            }}
          >
            <WebButton onClick={handleSave} style={{ width: 80, height: 26 }}>
              OK
            </WebButton>
            <WebButton onClick={onClose} style={{ width: 80, height: 26 }}>
              Cancelar
            </WebButton>
          </div>
        </div>
      </div>
    </div>
  );
};

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.edit.EditWorship
 */
export const EditWorship: React.FC<EditWorshipProps> = () => {
  const [worshipList, setWorshipList] = useState<Worship[]>([]);
  const [selectedWorship, setSelectedWorship] = useState<Worship | null>(null);
  const [editingWorship, setEditingWorship] = useState<Worship | null>(null);

  // Form states for creating a new Worship
  const [selectedWeekKey, setSelectedWeekKey] = useState<string>("ALL");
  const [selectedDayKey, setSelectedDayKey] = useState<string>("SUN");
  const [hour, setHour] = useState<number>(19);
  const [minute, setMinute] = useState<number>(0);
  const [description, setDescription] = useState<string>("");

  const refreshList = useCallback(() => {
    setWorshipList([...WorshipController.getWorships()]);
  }, []);

  useEffect(() => {
    refreshList();
  }, [refreshList]);

  const handleAddWorship = () => {
    try {
      const day = WorshipDay.valueOf(selectedDayKey);
      const week = WorshipWeek.valueOf(selectedWeekKey);
      const w = new Worship(day, week, Number(hour), Number(minute), description.trim());

      const success = WorshipController.add(w);
      if (!success) {
        alert("Culto já cadastrado!");
        return;
      }
      refreshList();
      clearFields();
    } catch (err) {
      console.error(err);
      alert("Erro ao adicionar culto. Tente novamente.");
    }
  };

  const clearFields = () => {
    setSelectedDayKey("SUN");
    setSelectedWeekKey("ALL");
    setHour(19);
    setMinute(0);
    setDescription("");
  };

  const handleDoubleClick = (w: Worship) => {
    if (w.isActive()) {
      setEditingWorship(w);
    } else {
      if (window.confirm("Ativar Culto / Evento?")) {
        w.setActive(true);
        WorshipController.save();
        refreshList();
      }
    }
  };

  const handleRemove = (w: Worship) => {
    if (window.confirm(`Deseja realmente remover o culto "${w.toString()}"?`)) {
      WorshipController.remove(w);
      if (selectedWorship?.id === w.id) {
        setSelectedWorship(null);
      }
      refreshList();
    }
  };

  const handleToggleActive = (w: Worship) => {
    w.setActive(!w.isActive());
    WorshipController.save();
    refreshList();
  };

  return (
    <JPanel
      border="etched"
      style={{
        display: "flex",
        flexDirection: "column",
        width: "100%",
        height: "100%",
        minHeight: 392,
        backgroundColor: "transparent",
        boxSizing: "border-box",
        padding: 4,
        gap: 6,
      }}
    >
      {/* Top Settings Form Panel (jPanelWorshipSettings) */}
      <JPanel
        border="etched"
        padding={6}
        style={{
          display: "flex",
          flexWrap: "wrap",
          alignItems: "center",
          gap: 8,
          backgroundColor: "#f5f5f5",
        }}
      >
        {/* Day selection */}
        <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
          <WebLabel style={{ fontFamily: "Georgia, serif", fontSize: 12 }}>
            Dia:
          </WebLabel>
          <WebComboBox
            items={WorshipWeek.values().map((w) => ({
              value: w.key,
              label: w.label,
            }))}
            value={selectedWeekKey}
            onChange={(val) => setSelectedWeekKey(val)}
            style={{ fontFamily: "Georgia, serif", fontSize: 12 }}
          />
          <WebComboBox
            items={WorshipDay.values().map((d) => ({
              value: d.key,
              label: d.label,
            }))}
            value={selectedDayKey}
            onChange={(val) => setSelectedDayKey(val)}
            style={{ fontFamily: "Georgia, serif", fontSize: 12 }}
          />
        </div>

        {/* Time selection */}
        <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
          <WebLabel style={{ fontFamily: "Georgia, serif", fontSize: 12 }}>
            Horário:
          </WebLabel>
          <input
            type="number"
            min={0}
            max={23}
            value={hour}
            onChange={(e) => setHour(Math.max(0, Math.min(23, Number(e.target.value))))}
            style={{
              width: 48,
              fontFamily: "Georgia, serif",
              fontSize: 12,
              textAlign: "center",
              border: "1px solid #7f9db9",
              padding: "2px 4px",
            }}
          />
          <span style={{ fontFamily: "Georgia, serif", fontSize: 14, fontWeight: "bold" }}>
            :
          </span>
          <input
            type="number"
            min={0}
            max={55}
            step={5}
            value={minute}
            onChange={(e) => setMinute(Math.max(0, Math.min(55, Number(e.target.value))))}
            style={{
              width: 48,
              fontFamily: "Georgia, serif",
              fontSize: 12,
              textAlign: "center",
              border: "1px solid #7f9db9",
              padding: "2px 4px",
            }}
          />
        </div>

        {/* Description field */}
        <input
          type="text"
          maxLength={24}
          placeholder="Descrição"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              handleAddWorship();
            }
          }}
          style={{
            minWidth: 160,
            fontFamily: "Georgia, serif",
            fontSize: 12,
            textAlign: "center",
            border: "1px solid #7f9db9",
            padding: "3px 6px",
          }}
        />

        {/* Add button */}
        <WebButton
          onClick={handleAddWorship}
          style={{
            fontFamily: "Georgia, serif",
            fontSize: 11,
            height: 24,
            padding: "2px 10px",
          }}
        >
          Adicionar
        </WebButton>
      </JPanel>

      {/* Worships List (jScrollPaneWorship + jListWorship) */}
      <div
        style={{
          flex: 1,
          border: "1px solid #9c9c9c",
          backgroundColor: "#ffffff",
          overflowY: "auto",
          minHeight: 260,
        }}
        title="Dois cliques para editar, selecione para opções"
      >
        {worshipList.length === 0 ? (
          <div
            style={{
              padding: 20,
              textAlign: "center",
              color: "#888",
              fontFamily: "Georgia, serif",
              fontSize: 14,
            }}
          >
            Nenhum culto cadastrado.
          </div>
        ) : (
          worshipList.map((w) => {
            const isSelected = selectedWorship?.id === w.id;
            return (
              <div
                key={w.id}
                onClick={() => setSelectedWorship(w)}
                onDoubleClick={() => handleDoubleClick(w)}
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  height: 40,
                  padding: "0 12px",
                  cursor: "pointer",
                  backgroundColor: isSelected
                    ? "var(--swing-selection-bg, #316ac5)"
                    : "transparent",
                  color: isSelected
                    ? "var(--swing-selection-fg, #ffffff)"
                    : w.isActive()
                    ? "#000000"
                    : "#888888",
                  textDecoration: !w.isActive() ? "line-through" : "none",
                  borderBottom: "1px solid #e0e0e0",
                  fontFamily: "Georgia, serif",
                  fontWeight: "bold",
                  fontSize: 16,
                  userSelect: "none",
                }}
              >
                <span>{w.toString()}</span>

                {/* Operations buttons on the right */}
                <div style={{ display: "flex", gap: 6 }}>
                  <WebButton
                    dense
                    onClick={(e) => {
                      e.stopPropagation();
                      handleToggleActive(w);
                    }}
                    style={{
                      fontSize: 11,
                      height: 22,
                      padding: "1px 6px",
                      color: "#000",
                    }}
                  >
                    {w.isActive() ? "Desativar" : "Ativar"}
                  </WebButton>
                  <WebButton
                    dense
                    onClick={(e) => {
                      e.stopPropagation();
                      setEditingWorship(w);
                    }}
                    style={{
                      fontSize: 11,
                      height: 22,
                      padding: "1px 6px",
                      color: "#000",
                    }}
                  >
                    Editar
                  </WebButton>
                  <WebButton
                    dense
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRemove(w);
                    }}
                    style={{
                      fontSize: 11,
                      height: 22,
                      padding: "1px 6px",
                      color: "#b00",
                    }}
                  >
                    Excluir
                  </WebButton>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Modal for editing a worship item */}
      {editingWorship && (
        <WorshipEditModal
          worship={editingWorship}
          onClose={() => setEditingWorship(null)}
          onSave={() => {
            refreshList();
          }}
        />
      )}
    </JPanel>
  );
};

export default EditWorship;
