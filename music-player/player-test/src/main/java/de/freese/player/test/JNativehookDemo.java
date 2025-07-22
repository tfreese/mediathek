// Created: 22 Juli 2025
package de.freese.player.test;

import javax.swing.SwingUtilities;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href=https://github.com/kwhat/jnativehook>jnativehook</a><br>
 * com.github.kwhat:jnativehook:2.2.2<br>
 *
 * @author Thomas Freese
 */
public final class JNativehookDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(JNativehookDemo.class);

    public static void main(final String[] args) {
        /* Note:
         * JNativeHook does *NOT* operate on the event dispatching thread.
         * Because Swing components must be accessed on the event dispatching
         * thread, you *MUST* wrap access to Swing components using the
         * SwingUtilities.invokeLater() or EventQueue.invokeLater() methods.
         */
        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return;
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(final NativeKeyEvent event) {
                LOGGER.info(event.paramString());

                if (event.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                    try {
                        GlobalScreen.unregisterNativeHook();
                    }
                    catch (NativeHookException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }

                    System.exit(0);
                }
            }
        });

        SwingUtilities.invokeLater(() -> LOGGER.info("Listening for global KeyEvents..."));
    }

    private JNativehookDemo() {
        super();
    }
}
