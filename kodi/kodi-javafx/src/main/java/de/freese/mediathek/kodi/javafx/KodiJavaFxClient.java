package de.freese.mediathek.kodi.javafx;

import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.freese.mediathek.kodi.javafx.controller.GenreController;
import de.freese.mediathek.kodi.javafx.controller.MovieController;
import de.freese.mediathek.kodi.javafx.controller.TvShowController;
import de.freese.mediathek.kodi.spring.AppConfigHsqlDb;
import de.freese.mediathek.kodi.spring.AppConfigMySQL;
import de.freese.mediathek.kodi.spring.AppConfigSqLite;

/**
 * bidirectional bind: "<a href="https://community.oracle.com/thread/2462489">community.oracle.com</a>"<br>
 * <br>
 * Mit KodiJavaFxClientLauncher ausführen oder KodiJavaFxClient direkt mit folgenden Restriktionen:<br>
 * <br>
 * In Eclipse:<br>
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.</li>
 * <li>VM-Parameter: --add-modules javafx.controls</li>
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen</li>
 * </ol>
 *
 * @author Thomas Freese
 */
public class KodiJavaFxClient extends Application {
    public static final Logger LOGGER = LoggerFactory.getLogger("KODI-Client");

    public static Logger getLogger() {
        return LOGGER;
    }

    private static int getJavaVersion() {
        //        System.getProperty("java.version")
        final Runtime.Version version = Runtime.version();

        getLogger().info("JavaVersion = {}", version);

        return version.feature();
    }

    private AnnotationConfigApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        getLogger().info("init");

        final List<String> parameters = getParameters().getRaw();
        String profile = null;
        // Class<?> clazz = null;

        if (parameters == null || parameters.isEmpty()) {
            // clazz = AppConfigMySQL.class;
            profile = "sqlite";
        }
        else {
            // clazz = Class.forName(parameters.get(0));
            profile = parameters.get(0);
        }

        // this.applicationContext = new AnnotationConfigApplicationContext(clazz);
        this.applicationContext = new AnnotationConfigApplicationContext();
        this.applicationContext.getEnvironment().setActiveProfiles(profile);
        this.applicationContext.register(AppConfigMySQL.class, AppConfigHsqlDb.class, AppConfigSqLite.class);
        this.applicationContext.refresh();
        this.applicationContext.registerShutdownHook();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        getLogger().info("start");

        final ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles/messages");

        final TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab tab = new Tab(resourceBundle.getString("serien"));
        // FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/TvShowMovie-Scene.fxml"), resourceBundle);
        // fxmlLoader.setController(new TvShowController(this.applicationContext));
        // Pane pane = fxmlLoader.load();
        // tab.setContent(pane);
        //
        // fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/empty.fxml"), resourceBundle);
        // fxmlLoader.setController(new GenreController(applicationContext));
        // fxmlLoader.setRoot(new GenreScene(resourceBundle));
        // fxmlLoader.load();
        // tab.setContent(fxmlLoader.getRoot());
        // tab.setContent(pane);
        // tabPane.getTabs().add(tab);
        //
        Tab tab = new Tab(resourceBundle.getString("serien"));
        tab.setContent(new TvShowController(this.applicationContext, resourceBundle).getPane());
        tabPane.getTabs().add(tab);

        tab = new Tab(resourceBundle.getString("filme"));
        tab.setContent(new MovieController(this.applicationContext, resourceBundle).getPane());
        tabPane.getTabs().add(tab);

        tab = new Tab(resourceBundle.getString("genres"));
        tab.setContent(new GenreController(this.applicationContext, resourceBundle).getPane());
        tabPane.getTabs().add(tab);

        // Scene
        Scene scene = null;

        // Momentan kein Antialiasing wegen JavaFX-Bug.
        final int javaVersion = getJavaVersion();

        // 1_800_072 = 1.8.72
        if (Platform.isSupported(ConditionalFeature.SCENE3D) && javaVersion >= 8) {
            scene = new Scene(tabPane, 1920, 1080, true, SceneAntialiasing.BALANCED);
        }
        else {
            scene = new Scene(tabPane, 1920, 1080);
        }

        scene.getStylesheets().add("styles/styles.css");

        getLogger().info("Antialiasing: {}", scene.getAntiAliasing());

        primaryStage.setTitle("KODI-Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        getLogger().info("stop");

        this.applicationContext.close();

        Platform.exit();
    }
}
