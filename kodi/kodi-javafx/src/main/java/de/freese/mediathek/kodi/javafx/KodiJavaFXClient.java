package de.freese.mediathek.kodi.javafx;

import java.util.List;
import java.util.ResourceBundle;

import de.freese.mediathek.kodi.javafx.controller.GenreController;
import de.freese.mediathek.kodi.javafx.controller.MovieController;
import de.freese.mediathek.kodi.javafx.controller.TvShowController;
import de.freese.mediathek.kodi.spring.AppConfigHSQLDB;
import de.freese.mediathek.kodi.spring.AppConfigMySQL;
import de.freese.mediathek.kodi.spring.AppConfigSQLite;
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

/**
 * bidirectional bind: "<a href="https://community.oracle.com/thread/2462489">community.oracle.com</a>"<br>
 * <br>
 * Mit KodiJavaFXClientLauncher ausführen oder KodiJavaFXClient direkt mit foldenden Restruktionen:<br>
 * <br>
 * In Eclipse:<br>
 * <ol>
 * <li>Konstruktor muss public empty-arg sein oder nicht vorhanden sein.
 * <li>VM-Parameter: --add-modules javafx.controls
 * <li>Module-Classpath: OpenJFX die jeweils 2 Jars für javafx-base, javafx-controls und javafx-graphics hinzufügen
 * </ol>
 *
 * @author Thomas Freese
 */
public class KodiJavaFXClient extends Application
{
    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("KODI-Client");

    /**
     * @return int; Beispiel: 14.0.1 = 014.000.001 = 14000001
     */
    private static int getJavaVersion()
    {
        // String javaVersion = SystemUtils.JAVA_VERSION;
        String javaVersion = System.getProperty("java.version");
        // String javaVersionDate = System.getProperty("java.version.date");
        // String vmVersion = System.getProperty("java.vm.version");
        String[] splits = javaVersion.toLowerCase().split("[._]");

        // Major
        String versionString = String.format("%03d", Integer.parseInt(splits[0]));

        // Minor
        versionString += "." + String.format("%03d", Integer.parseInt(splits[1]));

        if (splits.length > 2)
        {
            // Micro
            versionString += "." + String.format("%03d", Integer.parseInt(splits[2]));
        }

        if ((splits.length > 3) && !splits[3].startsWith("ea"))
        {
            // Update
            try
            {
                versionString += "." + String.format("%03d", Integer.parseInt(splits[3]));
            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }

        int version = Integer.parseInt(versionString.replace(".", ""));

        getLogger().info("JavaVersion = {} = {} = {}", javaVersion, versionString, version);

        return version;
    }

    /**
     * @return {@link Logger}
     */
    public static Logger getLogger()
    {
        return LOGGER;
    }

    /**
     *
     */
    private AnnotationConfigApplicationContext applicationContext;

    /**
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception
    {
        getLogger().info("init");

        List<String> parameters = getParameters().getRaw();
        String profile = null;
        // Class<?> clazz = null;

        if ((parameters != null) && !parameters.isEmpty())
        {
            // clazz = AppConfigMySQL.class;
            profile = "sqlite";
        }
        else
        {
            // clazz = Class.forName(parameters.get(0));
            profile = parameters.get(0);
        }

        // this.applicationContext = new AnnotationConfigApplicationContext(clazz);
        this.applicationContext = new AnnotationConfigApplicationContext();
        this.applicationContext.getEnvironment().setActiveProfiles(profile);
        this.applicationContext.register(AppConfigMySQL.class, AppConfigHSQLDB.class, AppConfigSQLite.class);
        this.applicationContext.refresh();
        this.applicationContext.registerShutdownHook();
    }

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage stage) throws Exception
    {
        getLogger().info("start");

        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles/messages");

        TabPane tabPane = new TabPane();
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

        // Momentan kein Antialising wegen JavaFX-Bug.
        int javaVersion = getJavaVersion();

        if (Platform.isSupported(ConditionalFeature.SCENE3D) && (javaVersion >= 1_800_072))
        {

            scene = new Scene(tabPane, 1920, 1080, true, SceneAntialiasing.BALANCED);
        }
        else
        {
            scene = new Scene(tabPane, 1920, 1080);
        }

        scene.getStylesheets().add("styles/styles.css");

        getLogger().info("Antialising: {}", scene.getAntiAliasing());

        stage.setTitle("KODI-Client");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception
    {
        getLogger().info("stop");

        this.applicationContext.close();

        Platform.exit();
    }
}
