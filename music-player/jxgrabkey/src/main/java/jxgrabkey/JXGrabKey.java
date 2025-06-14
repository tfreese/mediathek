/*  Copyright 2008  Edwin Stang (edwinstang@gmail.com),
 *
 *  This file is part of JXGrabKey.
 *
 *  JXGrabKey is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JXGrabKey is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JXGrabKey.  If not, see <http://www.gnu.org/licenses/>.
 */

package jxgrabkey;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the API access.
 * All public methods are synchronized, hence thread-safe.
 *
 * @author subes
 */
public final class JXGrabKey {
    private static final List<HotkeyListener> LISTENERS = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(JXGrabKey.class);
    private static final long SLEEP_WHILE_LISTEN_EXITS = 100L;

    private static JXGrabKey instance;
    private static Thread thread;

    /**
     * Either gives debug messages to a HotkeyListenerDebugEnabled if registered,
     * or prints to console otherwise.
     * Does only print if debug is enabled.
     *
     * This method is both used by the C++ and Java code, so it should not be synchronized.
     * Don't use this method from externally.
     */
    public static void debugCallback(final String debugmessage) {
        if (LOGGER.isDebugEnabled()) {
            String message = debugmessage.strip();

            if (message.charAt(message.length() - 1) != '\n') {
                message += "\n";
            }
            else {
                while (message.endsWith("\n\n")) {
                    message = message.substring(0, message.length() - 1);
                }
            }

            // boolean found = false;
            //
            // for (HotkeyListener listener : LISTENERS) {
            //     if (listener instanceof HotkeyListenerDebugEnabled hotkeyListenerDebugEnabled) {
            //         hotkeyListenerDebugEnabled.debugCallback(message);
            //         found = true;
            //     }
            // }
            //
            // if (!found) {
            LOGGER.debug(message);
            // }
        }
    }

    /**
     * Notifies HotkeyListeners about a received KeyEvent.
     *
     * This method is used by the C++ code.
     * Do not use this method from externally.
     */
    public static void fireKeyEvent(final int id) {
        for (HotkeyListener listener : LISTENERS) {
            listener.onHotkey(id);
        }
    }

    /**
     * Retrieves the singleton. Initializes it, if not yet done.
     */
    public static synchronized JXGrabKey getInstance() {
        if (instance == null) {
            instance = new JXGrabKey();
        }

        return instance;
    }

    /**
     * Enables/Disables printing of debug messages.
     */
    public static void setDebugOutput(final boolean enabled) {
        setDebug(enabled);
    }

    private static native void setDebug(boolean debug);

    /**
     * This constructor starts a separate Thread for the main listen loop.
     */
    private JXGrabKey() {
        super();

        thread = new Thread(() -> {
            LOGGER.debug("listening...");
            listen();
            // debugCallback("-- listen()");
        });

        thread.start();
    }

    /**
     * Adds a HotkeyListener.
     */
    public void addHotkeyListener(final HotkeyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }

        JXGrabKey.LISTENERS.add(listener);
    }

    /**
     * Unregisters all hotkeys, removes all HotkeyListeners,
     * stops the main listen loop and deinitializes the singleton.
     */
    public void cleanUp() {
        clean();

        if (thread.isAlive()) {
            while (thread.isAlive()) {
                try {
                    Thread.sleep(SLEEP_WHILE_LISTEN_EXITS);
                }
                catch (InterruptedException ex) {
                    // debugCallback("cleanUp() - InterruptedException: " + ex.getMessage());
                    LOGGER.error(ex.getMessage(), ex);

                    // Restore interrupted state.
                    Thread.currentThread().interrupt();
                }
            }

            instance = null; // Next time getInstance is called, reinitialize JXGrabKey.
        }

        if (!LISTENERS.isEmpty()) {
            LISTENERS.clear();
        }
    }

    /**
     * Converts an AWT hotkey into a X11 hotkey and registers it.
     */
    public void registerAwtHotkey(final int id, final int awtMask, final int awtKey) throws HotkeyConflictException {
        LOGGER.debug("registerAwtHotkey({}, 0x{}, 0x{})", id, Integer.toHexString(awtMask), Integer.toHexString(awtKey));

        final int x11Mask = X11MaskDefinitions.awtMaskToX11Mask(awtMask);
        final int x11Keysym = X11KeysymDefinitions.awtKeyToX11Keysym(awtKey);

        LOGGER.debug("registerAwtHotkey:  converted AWT mask '{}' (0x{}) to X11 mask (0x{})",
                InputEvent.getModifiersExText(awtMask),
                Integer.toHexString(awtMask),
                Integer.toHexString(x11Mask));

        LOGGER.debug("registerAwtHotkey:  converted AWT key '{}' (0x{}) to X11 keysym (0x{})",
                KeyEvent.getKeyText(awtKey),
                Integer.toHexString(awtKey),
                Integer.toHexString(x11Keysym));

        registerHotkey(id, x11Mask, x11Keysym);
    }

    /**
     * Registers a X11 hotkey.
     */
    public void registerX11Hotkey(final int id, final int x11Mask, final int x11Keysym) throws HotkeyConflictException {
        registerHotkey(id, x11Mask, x11Keysym);
    }

    /**
     * Removes a HotkeyListener.
     */
    public void removeHotkeyListener(final HotkeyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }

        JXGrabKey.LISTENERS.remove(listener);
    }

    /**
     * This method unregisters a hotkey.
     * If the hotkey is not yet registered, nothing will happen.
     */
    public native void unregisterHotKey(int id);

    private native void clean();

    private native void listen();

    private native void registerHotkey(int id, int mask, int key) throws HotkeyConflictException;
}
