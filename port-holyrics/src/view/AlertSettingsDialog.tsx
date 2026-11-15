import React, { useState, useEffect, useRef } from 'react';
import { WebButton } from '../swing/WebButton';
import { WebLabel } from '../swing/WebLabel';
import { WebComboBox } from '../swing/WebComboBox';
import { JPanel } from '../swing/JPanel';
import AlertSettings from '../model/AlertSettings';
import { Color } from '../util/awt';

export interface AlertSettingsDialogProps {
  visible: boolean;
  alertSettings?: AlertSettings;
  onClose: () => void;
  onSave?: (settings: AlertSettings) => void;
}

const COMMON_FONTS = [
  'Arial',
  'Arial Black',
  'Comic Sans MS',
  'Courier New',
  'Georgia',
  'Impact',
  'Lucida Console',
  'Lucida Sans Unicode',
  'Palatino Linotype',
  'Segoe UI',
  'Tahoma',
  'Times New Roman',
  'Trebuchet MS',
  'Verdana',
];

const LINE_HEIGHT_OPTIONS = Array.from({ length: 11 }, (_, i) => `${i + 5}%`);

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

export const AlertSettingsDialog: React.FC<AlertSettingsDialogProps> = ({
  visible,
  alertSettings: initialSettings,
  onClose,
  onSave,
}) => {
  const [settings] = useState<AlertSettings>(() => initialSettings || new AlertSettings());

  const [fontColor, setFontColor] = useState<string>(() => colorToHex(settings.getForeground(), '#FFFFFF'));
  const [backgroundColor, setBackgroundColor] = useState<string>(() => colorToHex(settings.getBackground(), '#000000'));
  const [hasBackground, setHasBackground] = useState<boolean>(() => settings.isBackground());
  const [lineHeight, setLineHeight] = useState<string>(() => `${settings.getLineHeight()}%`);
  const [isOverlap, setIsOverlap] = useState<boolean>(() => settings.isOverlap());
  const [locationIndex, setLocationIndex] = useState<number>(() => (settings.isTop() ? 0 : 1));
  const [velocity, setVelocity] = useState<number>(() => settings.getVelocity() || 2.0);
  const [frameSkip, setFrameSkip] = useState<number>(() => settings.getFrameSkip() || 15);
  const [fontName, setFontName] = useState<string>(() => settings.getFontName() || 'Arial');
  const [isBold, setIsBold] = useState<boolean>(() => settings.getFont().isBold());

  // Marquee scroll offset for preview
  const [scrollX, setScrollX] = useState<number>(0);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!visible) return;
    let animationFrameId: number;
    let lastTime = performance.now();

    const loop = (currentTime: number) => {
      const dt = (currentTime - lastTime) / 1000;
      lastTime = currentTime;

      setScrollX((prev) => {
        const next = prev - (velocity * 60) * dt;
        if (next < -600) return 400;
        return next;
      });

      animationFrameId = requestAnimationFrame(loop);
    };

    animationFrameId = requestAnimationFrame(loop);
    return () => cancelAnimationFrame(animationFrameId);
  }, [visible, velocity]);

  if (!visible) return null;

  const handleOk = () => {
    settings.setForeground(hexToColor(fontColor));
    settings.setBackground(hexToColor(backgroundColor));
    settings.setBackground(hasBackground);
    settings.setLineHeight(lineHeight);
    settings.setOverlap(isOverlap);
    settings.setTop(locationIndex === 0);
    settings.setVelocity(velocity);
    settings.setFrameSkip(frameSkip);
    settings.setFontName(fontName);
    settings.setBold(isBold);

    if (typeof settings.save === 'function') {
      settings.save();
    }
    if (onSave) {
      onSave(settings);
    }
    onClose();
  };

  const handleHelpOverlap = () => {
    alert(
      'Permite que o aviso seja exibido por cima da letra ou mídia sem interromper a projeção atual.'
    );
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
          width: 480,
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
          <span>Aviso</span>
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
              items={LINE_HEIGHT_OPTIONS}
              value={lineHeight}
              onChange={(val) => setLineHeight(val)}
              style={{ width: 85, fontFamily: 'Georgia, serif', fontSize: 12 }}
            />
          </div>

          {/* Overlap */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span
              onClick={handleHelpOverlap}
              style={{
                fontFamily: 'Georgia, serif',
                fontSize: 12,
                cursor: 'pointer',
                color: '#003399',
                textDecoration: 'underline',
              }}
              title="Clique para obter ajuda"
            >
              <b>?</b> Sobrepor
            </span>
            <input
              type="checkbox"
              checked={isOverlap}
              onChange={(e) => setIsOverlap(e.target.checked)}
              style={{ cursor: 'pointer' }}
            />
          </div>

          {/* Location */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Localização</WebLabel>
            <WebComboBox
              items={['Superior', 'Inferior']}
              value={locationIndex === 0 ? 'Superior' : 'Inferior'}
              onChange={(val) => setLocationIndex(val === 'Superior' ? 0 : 1)}
              style={{ width: 110, fontFamily: 'Georgia, serif', fontSize: 12 }}
            />
          </div>

          {/* Velocity */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Velocidade</WebLabel>
            <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
              <input
                type="number"
                min={0.2}
                max={5.0}
                step={0.2}
                value={velocity}
                onChange={(e) => setVelocity(parseFloat(e.target.value) || 1.0)}
                style={{
                  width: 60,
                  height: 20,
                  fontFamily: 'Georgia, serif',
                  fontSize: 12,
                  border: '1px solid #7F9DB9',
                  padding: '1px 3px',
                }}
              />
            </div>
          </div>

          {/* Frame Skip */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <WebLabel style={{ fontFamily: 'Georgia, serif', fontSize: 12 }}>Pular quadros</WebLabel>
            <input
              type="number"
              min={0}
              max={60}
              step={5}
              value={frameSkip}
              onChange={(e) => setFrameSkip(parseInt(e.target.value, 10) || 0)}
              style={{
                width: 60,
                height: 20,
                fontFamily: 'Georgia, serif',
                fontSize: 12,
                border: '1px solid #7F9DB9',
                padding: '1px 3px',
              }}
            />
          </div>

          {/* Font Selection & Bold Toggle */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 4 }}>
            <WebComboBox
              items={COMMON_FONTS}
              value={fontName}
              onChange={(val) => setFontName(val)}
              style={{ width: 220, fontFamily: 'Georgia, serif', fontSize: 12 }}
            />
            <button
              type="button"
              onClick={() => setIsBold(!isBold)}
              style={{
                fontFamily: 'Georgia, serif',
                fontWeight: 'bold',
                fontSize: 12,
                padding: '2px 10px',
                backgroundColor: isBold ? '#B0C4DE' : '#ECE9D8',
                border: isBold ? '1px inset #666' : '1px outset #FFF',
                cursor: 'pointer',
              }}
            >
              Negrito
            </button>
          </div>

          {/* Animated Preview Banner */}
          <div
            ref={containerRef}
            style={{
              height: 70,
              backgroundColor: hasBackground ? backgroundColor : '#E0E0E0',
              border: '1px inset #999',
              position: 'relative',
              overflow: 'hidden',
              display: 'flex',
              alignItems: 'center',
              margin: '6px 0',
            }}
          >
            <div
              style={{
                position: 'absolute',
                transform: `translateX(${scrollX}px)`,
                whiteSpace: 'nowrap',
                color: fontColor,
                fontFamily: fontName,
                fontWeight: isBold ? 'bold' : 'normal',
                fontSize: 32,
                userSelect: 'none',
              }}
            >
              Holyrics Holyrics Holyrics Holyrics Holyrics Holyrics Holyrics Holyrics
            </div>
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

export default AlertSettingsDialog;
