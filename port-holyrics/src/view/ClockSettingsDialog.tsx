import React, { useState, useEffect } from 'react';
import { WebButton } from '../swing/WebButton';
import { WebLabel } from '../swing/WebLabel';
import { WebComboBox } from '../swing/WebComboBox';
import { JPanel } from '../swing/JPanel';
import ClockSettings, { Color } from '../model/ClockSettings';
import ClockSettingsUtils from '../util/ClockSettingsUtils';

export interface ClockSettingsDialogProps {
  visible: boolean;
  clockSettings?: ClockSettings;
  callback?: () => void;
  onClose: () => void;
}

function colorToHex(color?: Color | null, fallback = '#FFFFFF'): string {
  if (!color || typeof color.getRed !== 'function') return fallback;
  const r = color.getRed().toString(16).padStart(2, '0');
  const g = color.getGreen().toString(16).padStart(2, '0');
  const b = color.getBlue().toString(16).padStart(2, '0');
  return `#${r}${g}${b}`;
}

function hexToColor(hex: string): Color {
  const cleanHex = hex.replace('#', '');
  const r = parseInt(cleanHex.substring(0, 2), 16) || 0;
  const g = parseInt(cleanHex.substring(2, 4), 16) || 0;
  const b = parseInt(cleanHex.substring(4, 6), 16) || 0;
  return new Color(r, g, b);
}

export const ClockSettingsDialog: React.FC<ClockSettingsDialogProps> = ({
  visible,
  clockSettings: initialSettings,
  callback,
  onClose,
}) => {
  const [settings] = useState<ClockSettings>(() => initialSettings || new ClockSettings());
  const [fontColor, setFontColor] = useState<string>(() => colorToHex(settings.getForeground(), '#FF0000'));
  const [backgroundColor, setBackgroundColor] = useState<string>(() => colorToHex(settings.getBackground(), '#000000'));
  const [hasBackground, setHasBackground] = useState<boolean>(() => settings.isBackground());
  const [lineHeight, setLineHeight] = useState<string>(() => `${settings.getLineHeight()}%`);
  const [position, setPosition] = useState<number>(() => settings.getPosition());
  const [currentTime, setCurrentTime] = useState<string>(() => ClockSettingsUtils.getTime());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(ClockSettingsUtils.getTime());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  if (!visible) return null;

  const arrows = ['↖', '↑', '↗', '←', '■', '→', '↙', '↓', '↘'];
  const lineHeightOptions = ['6%', '8%', '10%', '12%', '15%', '20%', '25%', '30%', '35%', '40%'];

  const handleOk = () => {
    settings.setForeground(hexToColor(fontColor));
    settings.setBackground(hexToColor(backgroundColor));
    settings.setBackground(hasBackground);
    settings.setLineHeight(lineHeight);
    settings.setPosition(position);
    if (typeof settings.save === 'function') {
      settings.save();
    }
    if (callback) {
      callback();
    }
    onClose();
  };

  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.4)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 9999,
      }}
    >
      <div
        style={{
          width: 320,
          backgroundColor: '#ECE9D8',
          border: '2px solid #0055EA',
          borderRadius: '4px 4px 0 0',
          boxShadow: '2px 2px 8px rgba(0,0,0,0.5)',
          fontFamily: 'Georgia, Tahoma, Arial, sans-serif',
          fontSize: 12,
        }}
      >
        {/* Title Bar */}
        <div
          style={{
            background: 'linear-gradient(to right, #0A246A, #A6CAF0)',
            color: '#FFFFFF',
            fontWeight: 'bold',
            padding: '3px 6px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            userSelect: 'none',
            fontFamily: 'Tahoma, Arial, sans-serif',
          }}
        >
          <span>Relógio</span>
          <button
            onClick={onClose}
            style={{
              width: 18,
              height: 18,
              lineHeight: '14px',
              fontSize: 11,
              fontWeight: 'bold',
              color: '#000',
              backgroundColor: '#ECE9D8',
              border: '1px outset #FFFFFF',
              cursor: 'pointer',
              padding: 0,
            }}
          >
            ×
          </button>
        </div>

        {/* Content Area */}
        <JPanel
          style={{
            padding: 12,
            display: 'flex',
            flexDirection: 'column',
            gap: 8,
          }}
        >
          {/* Font Color */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Cor da fonte</WebLabel>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <input
                type="color"
                value={fontColor}
                onChange={(e) => setFontColor(e.target.value)}
                style={{
                  width: 38,
                  height: 22,
                  padding: 1,
                  border: '1px solid #7F9DB9',
                  cursor: 'pointer',
                  backgroundColor: '#ECE9D8',
                }}
              />
            </div>
          </div>

          {/* Background Color & Checkbox */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Cor do fundo</WebLabel>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <input
                type="color"
                value={backgroundColor}
                onChange={(e) => setBackgroundColor(e.target.value)}
                style={{
                  width: 38,
                  height: 22,
                  padding: 1,
                  border: '1px solid #7F9DB9',
                  cursor: 'pointer',
                  backgroundColor: '#ECE9D8',
                }}
              />
              <input
                type="checkbox"
                checked={hasBackground}
                onChange={(e) => setHasBackground(e.target.checked)}
                title="Exibir fundo"
                style={{ cursor: 'pointer' }}
              />
            </div>
          </div>

          {/* Line Height */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Altura da linha</WebLabel>
            <WebComboBox
              items={lineHeightOptions}
              value={lineHeight}
              onChange={(val) => setLineHeight(val)}
              style={{ width: 75, fontFamily: 'Georgia, serif', fontSize: 12 }}
            />
          </div>

          {/* Location Grid (3x3) */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Posição</WebLabel>
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(3, 26px)',
                gridTemplateRows: 'repeat(3, 26px)',
                gap: 1,
                backgroundColor: '#999',
                border: '1px solid #7F9DB9',
                padding: 1,
              }}
            >
              {arrows.map((arr, idx) => {
                const isSelected = position === idx;
                return (
                  <button
                    key={idx}
                    type="button"
                    onClick={() => setPosition(idx)}
                    style={{
                      fontFamily: 'Arial, sans-serif',
                      fontSize: 14,
                      padding: 0,
                      backgroundColor: isSelected ? '#B0C4DE' : '#F0F0F0',
                      border: isSelected ? '1px inset #666' : '1px outset #FFF',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: isSelected ? 'bold' : 'normal',
                    }}
                  >
                    {arr}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Preview Box */}
          <div
            style={{
              height: 50,
              margin: '6px 0',
              border: '1px inset #999',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              backgroundColor: hasBackground ? backgroundColor : 'transparent',
              color: fontColor,
              fontFamily: 'Tahoma, Arial, sans-serif',
              fontSize: 28,
              overflow: 'hidden',
              userSelect: 'none',
            }}
          >
            {currentTime}
          </div>

          {/* Separator */}
          <div style={{ borderTop: '1px solid #999', borderBottom: '1px solid #FFF', margin: '4px 0' }} />

          {/* OK / Cancel Buttons */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: 10 }}>
            <WebButton onClick={handleOk} style={{ width: 75, fontFamily: 'Georgia, serif', fontSize: 12 }}>
              OK
            </WebButton>
            <WebButton onClick={onClose} style={{ width: 75, fontFamily: 'Georgia, serif', fontSize: 12 }}>
              Cancelar
            </WebButton>
          </div>
        </JPanel>
      </div>
    </div>
  );
};

export default ClockSettingsDialog;
