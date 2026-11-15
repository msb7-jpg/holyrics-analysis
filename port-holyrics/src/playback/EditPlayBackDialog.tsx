import React, { useState, useEffect } from 'react';

// Emulating the presentation window logic
class PresentationManager {
    private presentationWindow: Window | null = null;
    private videoElement: HTMLVideoElement | null = null;

    openPresentationWindow() {
        if (!this.presentationWindow || this.presentationWindow.closed) {
            this.presentationWindow = window.open('', '_blank', 'width=800,height=600');
            if (this.presentationWindow) {
                this.presentationWindow.document.write(`
                    <html>
                    <head><title>Holyrics Presentation</title></head>
                    <body style="margin:0; background:black; display:flex; align-items:center; justify-content:center; overflow:hidden;">
                        <video id="bg-video" autoplay muted loop style="width:100%; height:100%; object-fit:cover; transition: opacity 1s;"></video>
                    </body>
                    </html>
                `);
                
                // Keep a reference to the injected video element for API control
                this.videoElement = this.presentationWindow.document.getElementById('bg-video') as HTMLVideoElement;
            }
        }
    }

    playMedia(src?: string) {
        if (this.presentationWindow && !this.presentationWindow.closed && this.videoElement) {
            if (src) {
                this.videoElement.src = src;
            }
            this.videoElement.style.opacity = '1';
            this.videoElement.play().catch(() => {});
        }
    }

    pause() {
        if (this.presentationWindow && !this.presentationWindow.closed && this.videoElement) {
            this.videoElement.pause();
        }
    }

    stop() {
        if (this.presentationWindow && !this.presentationWindow.closed && this.videoElement) {
            this.videoElement.pause();
            this.videoElement.currentTime = 0;
            this.videoElement.removeAttribute('src');
            this.videoElement.load();
        }
    }

    blackout() {
        if (this.presentationWindow && !this.presentationWindow.closed && this.videoElement) {
            this.videoElement.style.opacity = '0';
        }
    }

    fade() {
        if (this.presentationWindow && !this.presentationWindow.closed && this.videoElement) {
            // Toggle fade
            const isFaded = this.videoElement.style.opacity === '0';
            this.videoElement.style.opacity = isFaded ? '1' : '0';
        }
    }

    closePresentationWindow() {
        if (this.presentationWindow) {
            this.presentationWindow.close();
            this.presentationWindow = null;
            this.videoElement = null;
        }
    }
}

export const presentationManager = new PresentationManager();

interface EditPlayBackDialogProps {
    onClose?: () => void;
    musicId?: string;
}

export const EditPlayBackDialog: React.FC<EditPlayBackDialogProps> = ({ onClose, musicId }) => {
    const [mediaSrc, setMediaSrc] = useState<string>('');
    const [slides, setSlides] = useState<any[]>([]);
    
    useEffect(() => {
        // Load some dummy slide mappings
        setSlides([{ id: 1, text: 'Slide 1', start: 0, duration: 5 }]);
    }, []);

    const handlePlayPresentation = () => {
        presentationManager.openPresentationWindow();
        if (mediaSrc) {
            presentationManager.playMedia(mediaSrc);
        }
    };

    return (
        <div className="dialog-overlay" style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <div className="dialog-content" style={{ background: '#fff', padding: '20px', width: '600px', borderRadius: '4px' }}>
                <h2>Edit PlayBack - Music {musicId}</h2>
                <div style={{ marginBottom: '15px' }}>
                    <label>Media Source URL:</label>
                    <input 
                        type="text" 
                        value={mediaSrc} 
                        onChange={e => setMediaSrc(e.target.value)} 
                        style={{ width: '100%', padding: '5px' }} 
                        placeholder="http://example.com/video.mp4" 
                    />
                </div>
                
                <div style={{ border: '1px solid #ccc', height: '200px', overflowY: 'auto', marginBottom: '15px' }}>
                    {slides.map(slide => (
                        <div key={slide.id} style={{ padding: '5px', borderBottom: '1px solid #eee' }}>
                            {slide.text} (Start: {slide.start}s, Duration: {slide.duration}s)
                        </div>
                    ))}
                </div>

                <div className="actions" style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={handlePlayPresentation}>Project to Window</button>
                    <button onClick={() => presentationManager.pause()}>Pause</button>
                    <button onClick={() => presentationManager.stop()}>Stop</button>
                    <button onClick={() => presentationManager.blackout()}>Blackout</button>
                    <button onClick={() => presentationManager.fade()}>Fade</button>
                    <button onClick={onClose} style={{ marginLeft: 'auto' }}>Close</button>
                </div>
            </div>
        </div>
    );
};
