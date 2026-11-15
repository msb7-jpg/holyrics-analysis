import { VLCPlayerMini } from './VLCPlayerMini';

export class VLC {
    public static readonly IS_32BITS: boolean = false; // Mocking false for web/modern environments
    public static readonly CHECK_PATH: boolean = true;
    public static PLAYER_CONTROL: typeof VLCPlayerMini | null = null;
    public static ENABLED: boolean = true;
    
    private static backgroundPresentationView: HTMLVideoElement | null = null;

    public static getBackgroundPresentationView(): HTMLVideoElement | null {
        if (VLC.ENABLED && VLC.CHECK_PATH) {
            if (!VLC.backgroundPresentationView) {
                VLC.backgroundPresentationView = document.createElement('video');
                VLC.backgroundPresentationView.autoplay = true;
                VLC.backgroundPresentationView.muted = true;
                VLC.backgroundPresentationView.loop = true;
                // vlcj Emulation: 
                // In Java this used: mp.setPlaySubItems(true); mp.mute(true); mp.setRepeat(true);
            }
            return VLC.backgroundPresentationView;
        }
        return null;
    }
}
