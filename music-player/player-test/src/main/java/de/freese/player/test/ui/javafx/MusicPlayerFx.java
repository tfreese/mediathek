// Created: 14 Juli 2024
package de.freese.player.test.ui.javafx;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author Thomas Freese
 */
public final class MusicPlayerFx extends Application {
    public static final Logger LOGGER = LoggerFactory.getLogger(MusicPlayerFx.class);

    static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // Redirect Java-Util-Logger to Slf4J.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        LOGGER.info("init");
    }

    @Override
    public void start(final Stage stage) throws Exception {
        LOGGER.info("start");

        final Media media = new Media(Path.of("samples/sample.m4a").toUri().toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        // final MediaView mediaView = new MediaView(mediaPlayer);
        // final Group root = new Group(mediaView);
        //
        // final Scene scene = new Scene(root, 1920, 1080, true, SceneAntialiasing.BALANCED);
        //
        // stage.setTitle("MusicPlayer");
        // stage.setScene(scene);
        // stage.show();

        mediaPlayer.play();
    }
}
