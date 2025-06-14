package jxgrabkey;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author subes
 */
class JXGrabKeyTest {

    @AfterAll
    static void afterAll() {
        JXGrabKey.getInstance().cleanUp();
    }

    @BeforeAll
    static void beforeAll() {
        try {
            // Files.writeString(Path.of(System.getProperty("java.io.tmpdir"), "test.txt"), System.getProperty("user.dir"));

            // System.load("/usr/include/X11/Xlib.h");
            System.load(System.getProperty("user.dir") + "/build/native/JXGrabKey.so");
        }
        catch (Throwable _) {
            // Fallback
            System.loadLibrary("JXGrabKey");
        }

        JXGrabKey.setDebugOutput(true);
    }

    @Test
        //@Disabled("symbol lookup error: JXGrabKey.so: undefined symbol: XOpenDisplay")
    void testRegisterAwtHotkey() throws HotkeyConflictException {
        final int id = 0;
        final int awtMask = KeyEvent.ALT_DOWN_MASK;
        final int awtKey = KeyEvent.VK_F;

        // final LinkageError error = assertThrows(LinkageError.class, () -> JXGrabKey.getInstance().registerAwtHotkey(id, awtMask, awtKey));
        // assertTrue(error.getMessage().contains("undefined symbol: XOpenDisplay"));

        JXGrabKey.getInstance().registerAwtHotkey(id, awtMask, awtKey);
        JXGrabKey.getInstance().unregisterHotKey(id);

        assertTrue(true);
    }
}
