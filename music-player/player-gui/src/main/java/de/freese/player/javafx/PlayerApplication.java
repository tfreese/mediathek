// Created: 18 Aug. 2024
package de.freese.player.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;

/**
 * @author Thomas Freese
 */
public final class PlayerApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerApplication.class);

    @Override
    public void init() throws Exception {
        LOGGER.info("initialize application");
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        LOGGER.info("starting application");

        // final Scene scene = new Scene(new Label("Music-Player"), 1920, 1080, true, SceneAntialiasing.BALANCED);
        final Scene scene = new Scene(new Label("Music-Player"), 1, 1, true, SceneAntialiasing.BALANCED);
        LOGGER.info("Antialiasing: {}", scene.getAntiAliasing());

        primaryStage.setTitle("Music-Player");
        primaryStage.setScene(scene);

        primaryStage.setWidth(1920D);
        primaryStage.setHeight(1080D);
        // primaryStage.setMaximized(true);

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("stopping application");

        PlayerSettings.getExecutorService().close();
        PlayerSettings.getExecutorServicePipeReader().close();

        Platform.exit();
    }
}
