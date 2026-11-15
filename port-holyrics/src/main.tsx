import React from 'react';
import { createRoot } from 'react-dom/client';
import { MusicMain } from './view/MusicMain';
import './swing/swing.css'; // Carrega o look and feel base (se existir)

const rootElement = document.getElementById('root');
if (rootElement) {
  const root = createRoot(rootElement);
  root.render(
    <React.StrictMode>
      <MusicMain />
    </React.StrictMode>
  );
}
