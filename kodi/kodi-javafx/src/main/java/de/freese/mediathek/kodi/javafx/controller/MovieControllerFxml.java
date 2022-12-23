package de.freese.mediathek.kodi.javafx.controller;
//package de.freese.mediadb.kodi.javafx.controller;
//
//import java.net.URL;
//import java.util.List;
//import java.util.ResourceBundle;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.context.ApplicationContext;
//import de.freese.mediadb.kodi.model.Genre;
//import de.freese.mediadb.kodi.model.Movie;
//import javafx.beans.binding.Bindings;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableView;
//import javafx.scene.image.ImageView;
//import javafx.scene.text.Text;
//
///**
// * @author Thomas Freese
// */
//public class MovieControllerFXML extends AbstractMediaController<Movie>
//{
//    /**
//     *
//     */
//    @FXML
//    private Button buttonGenres = null;
//
//    /**
//     *
//     */
//    @FXML
//    private Text genreText = null;
//
//    /**
//     *
//     */
//    @FXML
//    private Label idLabel = null;
//
//    /**
//     *
//     */
//    @FXML
//    private Text idText = null;
//
//    /**
//     *
//     */
//    @FXML
//    private ImageView imageView = null;
//
//    /**
//     *
//     */
//    @FXML
//    private TableView<Movie> tableView = null;
//
//    /**
//     * Erstellt ein neues {@link MovieControllerFXML} Object.
//     *
//     * @param applicationContext {@link ApplicationContext}
//     */
//    public MovieControllerFXML(final ApplicationContext applicationContext)
//    {
//        super(applicationContext);
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#getButtonGenres()
//     */
//    @Override
//    protected Button getButtonGenres()
//    {
//        return this.buttonGenres;
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#getGenres(de.freese.mediadb.kodi.model.Model)
//     */
//    @Override
//    protected List<Genre> getGenres(final Movie value)
//    {
//        return getXbmcService().getMovieGenres(value.getPK());
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#getImageURL(de.freese.mediadb.kodi.model.Model)
//     */
//    @Override
//    protected String getImageURL(final Movie value)
//    {
//        String url = StringUtils.substringBetween(value.getPosters(), "preview=\"", "\">");
//        url = StringUtils.replace(url, "t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500
//
//        return url;
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#getImageView()
//     */
//    @Override
//    protected ImageView getImageView()
//    {
//        return this.imageView;
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#getTableView()
//     */
//    @Override
//    protected TableView<Movie> getTableView()
//    {
//        return this.tableView;
//    }
//
//    /**
//     * @param event {@link ActionEvent}
//     */
//    @FXML
//    private void handleGenres(final ActionEvent event)
//    {
//        handleGenres();
//    }
//
//    /**
//     * @param event {@link ActionEvent}
//     */
//    @FXML
//    private void handleReload(final ActionEvent event)
//    {
//        handleReload();
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#initialize(java.net.URL, java.util.ResourceBundle)
//     */
//    @Override
//    public void initialize(final URL url, final ResourceBundle rb)
//    {
//        super.initialize(url, rb);
//
//        this.idLabel.setText(rb.getString("imdb_id"));
//
//        // this.genreText.textProperty().bind(Bindings.selectString(getTableView().getSelectionModel().selectedItemProperty(), "genres"));
//        this.idText.textProperty().bind(Bindings.selectString(getTableView().getSelectionModel().selectedItemProperty(), "imdbID"));
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#load()
//     */
//    @Override
//    protected List<Movie> load()
//    {
//        List<Movie> movies = getXbmcService().getMovies();
//
//        return movies;
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#updateDetails(de.freese.mediadb.kodi.model.Model)
//     */
//    @Override
//    protected void updateDetails(final Movie value)
//    {
//        super.updateDetails(value);
//
//        this.genreText.setText(null);
//
//        if (value != null)
//        {
//            this.genreText.setText(value.getGenres());
//        }
//    }
//
//    /**
//     * @see de.freese.mediadb.kodi.javafx.controller.AbstractMediaController#updateGenres(de.freese.mediadb.kodi.model.Model, int[])
//     */
//    @Override
//    protected void updateGenres(final Movie value, final int[] genreIDs)
//    {
//        String newGenres = getXbmcService().updateMovieGenres(value.getPK(), genreIDs);
//        this.genreText.setText(newGenres);
//        value.setGenres(newGenres);
//    }
//}
