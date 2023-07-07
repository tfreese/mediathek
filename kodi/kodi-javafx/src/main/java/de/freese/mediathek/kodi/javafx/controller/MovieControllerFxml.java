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
//    @FXML
//    private Button buttonGenres = null;
//
//    @FXML
//    private Text genreText = null;
//
//    @FXML
//    private Label idLabel = null;
//
//    @FXML
//    private Text idText = null;
//
//    @FXML
//    private ImageView imageView = null;
//
//    @FXML
//    private TableView<Movie> tableView = null;
//
//    public MovieControllerFXML(final ApplicationContext applicationContext)
//    {
//        super(applicationContext);
//    }
//
//    @Override
//    protected Button getButtonGenres()
//    {
//        return this.buttonGenres;
//    }
//
//    @Override
//    protected List<Genre> getGenres(final Movie value)
//    {
//        return getXbmcService().getMovieGenres(value.getPk());
//    }
//
//    @Override
//    protected String getImageURL(final Movie value)
//    {
//        String url = StringUtils.substringBetween(value.getPosters(), "preview=\"", "\">");
//        url = StringUtils.replace(url, "t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500
//
//        return url;
//    }
//
//    @Override
//    protected ImageView getImageView()
//    {
//        return this.imageView;
//    }
//
//    @Override
//    protected TableView<Movie> getTableView()
//    {
//        return this.tableView;
//    }
//
//    @FXML
//    private void handleGenres(final ActionEvent event)
//    {
//        handleGenres();
//    }
//
//    @FXML
//    private void handleReload(final ActionEvent event)
//    {
//        handleReload();
//    }
//
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
//    @Override
//    protected List<Movie> load()
//    {
//        List<Movie> movies = getXbmcService().getMovies();
//
//        return movies;
//    }
//
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
//    @Override
//    protected void updateGenres(final Movie value, final int[] genreIDs)
//    {
//        String newGenres = getXbmcService().updateMovieGenres(value.getPk(), genreIDs);
//        this.genreText.setText(newGenres);
//        value.setGenres(newGenres);
//    }
//}
