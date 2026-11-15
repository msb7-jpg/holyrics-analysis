import React, { useState } from 'react';
import { WebButton } from '../swing/WebButton';
import { WebLabel } from '../swing/WebLabel';
import { JPanel } from '../swing/JPanel';
import ClockSettingsDialog from './ClockSettingsDialog';

export interface SettingsViewProps {
  visible: boolean;
  onClose: () => void;
  onSave?: (settings: SettingsData) => void;
}

export interface SettingsData {
  accessCodePresentation: string;
  accessCodeBible: string;
  accessCodeUpdate: string;
  accessCodeMedia: string;
  enableRemoteControl: boolean;
  enableStream: boolean;
  displayOnAllMonitors: boolean;
  fadeInOut: boolean;
  fadeInOutDuration: number;
  displayWallpaper: boolean;
  wallpaperImageBase64?: string;
  displayClock: boolean;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  visible,
  onClose,
  onSave,
}) => {
  const [accessCodePresentation, setAccessCodePresentation] = useState('');
  const [accessCodeBible, setAccessCodeBible] = useState('');
  const [accessCodeUpdate, setAccessCodeUpdate] = useState('');
  const [accessCodeMedia, setAccessCodeMedia] = useState('');
  const [enableRemoteControl, setEnableRemoteControl] = useState(false);
  const [enableStream, setEnableStream] = useState(false);
  const [displayOnAllMonitors, setDisplayOnAllMonitors] = useState(false);
  const [fadeInOut, setFadeInOut] = useState(false);
  const [fadeInOutDuration, setFadeInOutDuration] = useState(500);
  const [displayWallpaper, setDisplayWallpaper] = useState(false);
  const [wallpaperImageBase64, setWallpaperImageBase64] = useState<string | undefined>();
  const [displayClock, setDisplayClock] = useState(false);
  const [clockDialogOpen, setClockDialogOpen] = useState(false);

  if (!visible) return null;

  const handleWallpaperChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (uploadEvent) => {
        setWallpaperImageBase64(uploadEvent.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleOk = () => {
    if (onSave) {
      onSave({
        accessCodePresentation,
        accessCodeBible,
        accessCodeUpdate,
        accessCodeMedia,
        enableRemoteControl,
        enableStream,
        displayOnAllMonitors,
        fadeInOut,
        fadeInOutDuration,
        displayWallpaper,
        wallpaperImageBase64,
        displayClock,
      });
    }
    onClose();
  };

  return (
    <>
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
          zIndex: 9998,
        }}
      >
        <div
          style={{
            width: 520,
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
            <span>Configurações / Settings</span>
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

          {/* Body Content */}
          <JPanel
            style={{
              padding: '12px 16px',
              display: 'flex',
              flexDirection: 'column',
              gap: 8,
              maxHeight: '80vh',
              overflowY: 'auto',
            }}
          >
            {/* Access Code: Presentation */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <WebLabel style={{ fontSize: 12 }}>Senha para transmitir letra/aviso por celular:</WebLabel>
              <input
                type="text"
                placeholder="(opcional)"
                maxLength={8}
                value={accessCodePresentation}
                onChange={(e) => setAccessCodePresentation(e.target.value)}
                style={{
                  width: 90,
                  textAlign: 'center',
                  fontSize: 12,
                  border: '1px solid #7F9DB9',
                  padding: '2px 4px',
                }}
              />
            </div>

            {/* Access Code: Bible */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <WebLabel style={{ fontSize: 12 }}>Senha para controlar a bíblia por celular:</WebLabel>
              <input
                type="text"
                placeholder="(opcional)"
                maxLength={8}
                value={accessCodeBible}
                onChange={(e) => setAccessCodeBible(e.target.value)}
                style={{
                  width: 90,
                  textAlign: 'center',
                  fontSize: 12,
                  border: '1px solid #7F9DB9',
                  padding: '2px 4px',
                }}
              />
            </div>

            {/* Access Code: Update */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <WebLabel style={{ fontSize: 12 }}>Senha para atualizar arquivos pelo celular:</WebLabel>
              <input
                type="text"
                placeholder="(opcional)"
                maxLength={8}
                value={accessCodeUpdate}
                onChange={(e) => setAccessCodeUpdate(e.target.value)}
                style={{
                  width: 90,
                  textAlign: 'center',
                  fontSize: 12,
                  border: '1px solid #7F9DB9',
                  padding: '2px 4px',
                }}
              />
            </div>

            {/* Access Code: Media */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <WebLabel style={{ fontSize: 12 }}>Senha para exibir mídias:</WebLabel>
              <input
                type="text"
                placeholder="(opcional)"
                maxLength={8}
                value={accessCodeMedia}
                onChange={(e) => setAccessCodeMedia(e.target.value)}
                style={{
                  width: 90,
                  textAlign: 'center',
                  fontSize: 12,
                  border: '1px solid #7F9DB9',
                  padding: '2px 4px',
                }}
              />
            </div>

            {/* Enable App Remote Control */}
            <label style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer', marginTop: 4 }}>
              <input
                type="checkbox"
                checked={enableRemoteControl}
                onChange={(e) => setEnableRemoteControl(e.target.checked)}
              />
              <span>Ativar controle remoto pelo aplicativo</span>
            </label>

            {/* Enable Stream to App */}
            <label style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={enableStream}
                onChange={(e) => setEnableStream(e.target.checked)}
              />
              <span>Ativar transmissão de slides para o aplicativo</span>
            </label>

            {/* Separator */}
            <div style={{ borderTop: '1px solid #999', borderBottom: '1px solid #FFF', margin: '4px 0' }} />

            {/* Auxiliary monitors */}
            <label style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={displayOnAllMonitors}
                onChange={(e) => setDisplayOnAllMonitors(e.target.checked)}
              />
              <span>Exibir em todos os monitores auxiliares</span>
            </label>

            {/* Fade in/out */}
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={fadeInOut}
                  onChange={(e) => setFadeInOut(e.target.checked)}
                />
                <span>Fade in/out</span>
              </label>
              <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                <input
                  type="number"
                  min={200}
                  max={1200}
                  step={100}
                  disabled={!fadeInOut}
                  value={fadeInOutDuration}
                  onChange={(e) => setFadeInOutDuration(parseInt(e.target.value, 10) || 500)}
                  style={{
                    width: 60,
                    height: 20,
                    fontSize: 12,
                    border: '1px solid #7F9DB9',
                    padding: '1px 3px',
                    opacity: fadeInOut ? 1 : 0.5,
                  }}
                />
                <span>ms</span>
              </div>
            </div>

            {/* Separator */}
            <div style={{ borderTop: '1px solid #999', borderBottom: '1px solid #FFF', margin: '4px 0' }} />

            {/* Display Wallpaper */}
            <label style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={displayWallpaper}
                onChange={(e) => setDisplayWallpaper(e.target.checked)}
              />
              <span>Exibir papel de parede</span>
            </label>

            {/* Wallpaper selection button / thumbnail */}
            <div style={{ display: 'flex', justifyContent: 'center' }}>
              <label
                style={{
                  width: 180,
                  height: 100,
                  border: '1px solid #7F9DB9',
                  backgroundColor: '#FFF',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer',
                  overflow: 'hidden',
                  position: 'relative',
                }}
              >
                {wallpaperImageBase64 ? (
                  <img
                    src={wallpaperImageBase64}
                    alt="Papel de Parede"
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                ) : (
                  <span style={{ fontSize: 11, color: '#666', textAlign: 'center', padding: 4 }}>
                    Clique para alterar
                  </span>
                )}
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleWallpaperChange}
                  style={{ display: 'none' }}
                />
              </label>
            </div>

            {/* Display Clock */}
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8, marginTop: 4 }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={displayClock}
                  onChange={(e) => setDisplayClock(e.target.checked)}
                />
                <span>Exibir relógio</span>
              </label>
              <button
                type="button"
                onClick={() => setClockDialogOpen(true)}
                title="Configurações do Relógio"
                style={{
                  width: 24,
                  height: 22,
                  fontSize: 12,
                  cursor: 'pointer',
                  backgroundColor: '#ECE9D8',
                  border: '1px outset #FFF',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                ⚙
              </button>
            </div>

            {/* Separator */}
            <div style={{ borderTop: '1px solid #999', borderBottom: '1px solid #FFF', margin: '4px 0' }} />

            {/* OK Button */}
            <div style={{ display: 'flex', justifyContent: 'center' }}>
              <WebButton
                onClick={handleOk}
                style={{
                  width: 80,
                  height: 30,
                  fontFamily: 'Georgia, serif',
                  fontSize: 14,
                }}
              >
                OK
              </WebButton>
            </div>
          </JPanel>
        </div>
      </div>

      {/* Clock Settings Dialog Modal */}
      <ClockSettingsDialog
        visible={clockDialogOpen}
        onClose={() => setClockDialogOpen(false)}
      />
    </>
  );
};

export default SettingsView;
