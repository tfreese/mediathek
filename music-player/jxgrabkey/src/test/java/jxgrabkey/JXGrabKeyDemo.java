package jxgrabkey;

import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JXGrabKeyDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(JXGrabKeyDemo.class);

    private static final int MY_HOTKEY_INDEX = 1;
    private static boolean hotkeyEventReceived;

    static void main() throws Exception {
        // Load JXGrabKey lib.
        System.load(System.getProperty("user.dir") + "/music-player/jxgrabkey/build/native/JXGrabKey.so");

        // Enable Debug Output.
        JXGrabKey.setDebugOutput(LOGGER.isDebugEnabled());

        // Register some Hotkey.
        try {
            // int key = KeyEvent.VK_K, mask = KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK;
            final int key = KeyEvent.VK_F2;  // Conflicts on GNOME
            final int mask = KeyEvent.ALT_DOWN_MASK;
            // final int mask = KeyEvent.SHIFT_MASK;
            // final int mask = 0;
            JXGrabKey.getInstance().registerAwtHotkey(MY_HOTKEY_INDEX, mask, key);

            // final int key = X11KeysymDefinitions.AUDIO_RAISE_VOLUME;
            // final int mask = X11MaskDefinitions.X11_CONTROL_MASK;
            // final int mask = 0;
            // JXGrabKey.getInstance().registerX11Hotkey(MY_HOTKEY_INDEX, mask, key);
        }
        catch (HotkeyConflictException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);

            JXGrabKey.getInstance().cleanUp(); // Automatically unregisters Hotkeys and Listeners
            // Alternatively, just unregister the key causing this or leave it as it is,
            // the key may not be grabbed at all or may not respond when numlock, capslock or scrollock is on.
            return;
        }

        // Implement HotkeyListener.
        final HotkeyListener hotkeyListener = id -> {
            if (id != MY_HOTKEY_INDEX) {
                return;
            }

            LOGGER.info("HotKey received");
            hotkeyEventReceived = true;
        };

        // Add HotkeyListener.
        JXGrabKey.getInstance();
        JXGrabKey.addHotkeyListener(hotkeyListener);

        // Wait for Hotkey Event.
        while (!hotkeyEventReceived) {
            Thread.sleep(1000L);
        }

        // Shutdown JXGrabKey.
        JXGrabKey.getInstance().unregisterHotKey(MY_HOTKEY_INDEX); // Optional
        JXGrabKey.getInstance().removeHotkeyListener(hotkeyListener); // Optional
        JXGrabKey.getInstance().cleanUp();
    }
}
