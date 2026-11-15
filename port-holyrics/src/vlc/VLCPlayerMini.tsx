import React, { useRef, useState, useEffect } from 'react';

interface VLCPlayerMiniProps {
    src?: string;
    onVisible?: (visible: boolean) => void;
}

export const VLCPlayerMini: React.FC<VLCPlayerMiniProps> = ({ src, onVisible }) => {
    const videoRef = useRef<HTMLVideoElement>(null);
    const [isPlaying, setIsPlaying] = useState(false);
    const [volume, setVolume] = useState(1);
    const [isMuted, setIsMuted] = useState(false);
    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(0);

    useEffect(() => {
        if (onVisible) {
            onVisible(true);
        }
        return () => {
            if (onVisible) onVisible(false);
        };
    }, [onVisible]);

    const togglePlay = () => {
        if (videoRef.current) {
            if (isPlaying) {
                videoRef.current.pause();
            } else {
                videoRef.current.play();
            }
            setIsPlaying(!isPlaying);
        }
    };

    const stop = () => {
        if (videoRef.current) {
            videoRef.current.pause();
            videoRef.current.currentTime = 0;
            setIsPlaying(false);
        }
    };

    const handleTimeUpdate = () => {
        if (videoRef.current) {
            setCurrentTime(videoRef.current.currentTime);
        }
    };

    const handleLoadedMetadata = () => {
        if (videoRef.current) {
            setDuration(videoRef.current.duration);
        }
    };

    const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const val = parseFloat(e.target.value);
        setVolume(val);
        if (videoRef.current) {
            videoRef.current.volume = val;
        }
    };

    const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
        const val = parseFloat(e.target.value);
        if (videoRef.current) {
            videoRef.current.currentTime = val;
            setCurrentTime(val);
        }
    };

    const toggleMute = () => {
        if (videoRef.current) {
            videoRef.current.muted = !isMuted;
            setIsMuted(!isMuted);
        }
    };

    return (
        <div className="vlc-player-mini" style={{ display: 'flex', flexDirection: 'column', width: '100%', border: '1px solid #ccc', padding: '10px' }}>
            <video
                ref={videoRef}
                src={src}
                onTimeUpdate={handleTimeUpdate}
                onLoadedMetadata={handleLoadedMetadata}
                style={{ width: '100%', height: 'auto', backgroundColor: '#000' }}
            />
            <div className="controls" style={{ display: 'flex', alignItems: 'center', marginTop: '10px', gap: '10px' }}>
                <button onClick={togglePlay}>{isPlaying ? 'Pause' : 'Play'}</button>
                <button onClick={stop}>Stop</button>
                <input
                    type="range"
                    min={0}
                    max={duration || 0}
                    value={currentTime}
                    onChange={handleSeek}
                    style={{ flex: 1 }}
                />
                <button onClick={toggleMute}>{isMuted ? 'Unmute' : 'Mute'}</button>
                <input
                    type="range"
                    min={0}
                    max={1}
                    step={0.01}
                    value={volume}
                    onChange={handleVolumeChange}
                    style={{ width: '80px' }}
                />
            </div>
        </div>
    );
};
