package de.freese.mediathek.kodi.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import de.freese.mediathek.kodi.api.MediaDao;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 * @since 13.09.2014
 */
public class MediaDaoImpl implements MediaDao {
    private final JdbcTemplate jdbcTemplate;

    private String schema = "";

    public MediaDaoImpl(final DataSource dataSource) {
        super();

        jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource required"));
    }

    @Override
    public void deleteMovieGenres(final int movieID) {
        final String sql = "delete from " + prependSchema("genre_link")
                + " where"
                + " media_type = 'movie'"
                + " and media_id = ?";

        getJdbcTemplate().update(sql, movieID);
    }

    @Override
    public void deleteShowGenres(final int showID) {
        final String sql = "delete from " + prependSchema("genre_link")
                + " where"
                + " media_type = 'tvshow'"
                + " and media_id = ?";

        getJdbcTemplate().update(sql, showID);
    }

    @Override
    public List<Movie> getGenreMovies(final int genreID) {
        final String sql = "select"
                + " m.idMovie as pk"
                + ", m.c00 as name"
                + ", m.c08 as poster"
                + ", m.c20 as fanart"
                + ", m.c09 as imdb_id"
                + ", m.c14 as genres"
                + ", m.c07 as year"
                + ", m.idSet as set_id"
                + " from " + prependSchema("movie m")
                + " inner join " + prependSchema("genre_link gl on gl.media_id = m.idmovie")
                + " where"
                + " gl.media_type = 'movie'"
                + " and gl.genre_id = ?";

        return getJdbcTemplate().query(sql, new MovieRowMapper(), genreID);
    }

    @Override
    public List<Show> getGenreShows(final int genreID) {
        final String sql = "select"
                + " s.idShow as pk"
                + ", s.c00 as name"
                + ", s.c06 as banner"
                + ", s.c11 as fanart"
                + ", s.c12 as tvdb_id"
                + ", s.c08 as genres"
                + " from " + prependSchema("tvshow s")
                + " inner join " + prependSchema("genre_link gl on gl.media_id = s.idshow")
                + " where"
                + " gl.media_type = 'tvshow'"
                + " and gl.genre_id = ?";

        return getJdbcTemplate().query(sql, new ShowRowMapper(), genreID);
    }

    @Override
    public List<Genre> getGenres() {
        final String sql = "select"
                + " g.genre_id as pk"
                + ", g.name"
                + ", ("
                + "select count(gl.media_id) from " + prependSchema("genre_link gl where gl.genre_id = g.genre_id and gl.media_type = 'movie'")
                + ") as filme_anzahl"
                + ", ("
                + "select count(gl.media_id) from " + prependSchema("genre_link gl where gl.genre_id = g.genre_id and gl.media_type = 'tvshow'")
                + ") as serien_anzahl"
                + " from " + prependSchema("genre g");

        // sql.append("select");
        // sql.append(" g.idgenre as pk");
        // sql.append(", g.strgenre as name");
        // sql.append(" from xbmc_video75.genre g");
        // sql.append(" order by name");
        // [mysqld]
        // group_concat_max_len = 4294967295;
        // getJdbcTemplate().execute("SET SESSION group_concat_max_len = 4294967295");
        // sql.append("select g.idgenre as pk, g.strgenre as name,");
        // sql.append("(select GROUP_CONCAT(m.c00 ORDER BY m.c00 SEPARATOR '; ')");
        // sql.append(" from xbmc_video75.genrelinkmovie glm");
        // sql.append(" inner join xbmc_video75.movie m on m.idmovie = glm.idmovie");
        // sql.append(" where glm.idgenre = g.idgenre");
        // sql.append(" group by g.strgenre");
        // sql.append(") as movies,");
        // sql.append("(select GROUP_CONCAT(s.c00 ORDER BY s.c00 SEPARATOR '; ')");
        // sql.append(" from xbmc_video75.genrelinktvshow gls");
        // sql.append(" inner join xbmc_video75.tvshow s on s.idshow = gls.idshow");
        // sql.append(" where gls.idgenre = g.idgenre");
        // sql.append(" group by g.strgenre");
        // sql.append(") as shows");
        // sql.append(" from xbmc_video75.genre g");
        return getJdbcTemplate().query(sql, new GenreRowMapper());
    }

    @Override
    public List<Genre> getMovieGenres(final int movieID) {
        final String sql = "select"
                + " g.genre_id as pk"
                + ", g.name"
                + ", 0 as filme_anzahl"
                + ", 0 as serien_anzahl"
                + " from " + prependSchema("genre g")
                + " inner join " + prependSchema("genre_link gl on gl.genre_id = g.genre_id")
                + " where"
                + " gl.media_type = 'movie'"
                + " and gl.media_id = ?";
        // sql.append(" order by name");

        return getJdbcTemplate().query(sql, new GenreRowMapper(), movieID);
    }

    @Override
    public List<Movie> getMovies() {
        final String sql = "select"
                + " m.idMovie as pk"
                + ", m.c00 as name"
                + ", m.c08 as poster"
                + ", m.c20 as fanart"
                + ", m.c09 as imdb_id"
                + ", m.c14 as genres"
                + ", m.c07 as year"
                + ", m.idSet as set_id"
                + " from " + prependSchema("movie m");
        // sql.append("select m.idMovie as pk, m.c00 as name, m.c09 as imdb_id,");
        // sql.append(" GROUP_CONCAT(g.strgenre ORDER BY g.strgenre SEPARATOR ' / ') as genres,");
        // sql.append(" m.c08 as poster, m.c20 as fanart");
        // sql.append(" from xbmc_video75.movie m");
        // sql.append(" inner join xbmc_video75.genrelinkmovie glm on glm.idmovie = m.idmovie");
        // sql.append(" inner join xbmc_video75.genre g on g.idgenre = glm.idgenre");
        // sql.append(" group by name");

        return getJdbcTemplate().query(sql, new MovieRowMapper());
    }

    @Override
    public List<Genre> getShowGenres(final int showID) {
        final String sql = "select"
                + " g.genre_id as pk"
                + ", g.name"
                + ", 0 as filme_anzahl"
                + ", 0 as serien_anzahl"
                + " from " + prependSchema("genre g")
                + " inner join " + prependSchema("genre_link gl on gl.genre_id = g.genre_id")
                + " where"
                + " gl.media_type = 'tvshow'"
                + " and gl.media_id = ?";

        return getJdbcTemplate().query(sql, new GenreRowMapper(), showID);
    }

    @Override
    public List<Show> getShows() {
        final String sql = "select"
                + " s.idShow as pk"
                + ", s.c00 as name"
                + ", s.c06 as banner"
                + ", s.c11 as fanart"
                + ", s.c12 as tvdb_id"
                + ", s.c08 as genres"
                + " from " + prependSchema("tvshow s");
        // sql.append("select s.idshow as pk, s.c00 as name, s.c12 as tvdb_id,");
        // sql.append(" GROUP_CONCAT(g.strgenre ORDER BY g.strgenre SEPARATOR ' / ') as genres,");
        // sql.append(" s.c06 as banner, s.c11 as fanart");
        // sql.append(" from xbmc_video75.tvshow s");
        // sql.append(" inner join xbmc_video75.genrelinktvshow gls on gls.idshow = s.idshow");
        // sql.append(" inner join xbmc_video75.genre g on g.idgenre = gls.idgenre");
        // sql.append(" group by name;");

        return getJdbcTemplate().query(sql, new ShowRowMapper());
    }

    @Override
    public void insertMovieGenre(final int movieID, final int genreID) {
        final String sql = "insert into " + prependSchema("genre_link")
                + " (genre_id, media_id, media_type)"
                + " values (?, ?, 'movie')";

        getJdbcTemplate().update(sql, genreID, movieID);
    }

    @Override
    public void insertShowGenre(final int showID, final int genreID) {
        final String sql = "insert into " + prependSchema("genre_link")
                + " (genre_id, media_id, media_type)"
                + " values (?, ?, 'tvshow')";

        getJdbcTemplate().update(sql, genreID, showID);
    }

    public void setSchema(final String schema) {
        this.schema = schema;
    }

    @Override
    public String updateMovieGenres(final int movieID) {
        // Mysql
        // StringBuilder sql = new StringBuilder();
        // sql.append("select GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ' / ')");
        // sql.append(" from ").append(prependSchema("genre g"));
        // sql.append(" inner join ").append(prependSchema("genre_link gl on gl.genre_id = g.genre_id"));
        // sql.append(" where gl.media_id = ?" and gl.media_type = 'movie');
        // sql.append(" group by gls.idshow");
        //
        // String genres = getJdbcTemplate().queryForObject(sql.toString(), String.class, showID);
        //
        // sql = new StringBuilder();
        // sql.append("update ").append(prependSchema("movie set"));
        // sql.append(" c14 = ?");
        // sql.append(" where idmovie = ?");
        //
        // getJdbcTemplate().update(sql.toString(), genres, showID);
        //
        // return genres;

        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" g.name");
        sql.append(" from ").append(prependSchema("genre g"));
        sql.append(" inner join ").append(prependSchema("genre_link gl on gl.genre_id = g.genre_id"));
        sql.append(" where");
        sql.append(" gl.media_type = 'movie'");
        sql.append(" and gl.media_id = ?");
        sql.append(" order by g.name");

        final List<String> genreList = getJdbcTemplate().queryForList(sql.toString(), String.class, movieID);
        final StringBuilder genres = new StringBuilder();

        for (final Iterator<String> iterator = genreList.iterator(); iterator.hasNext(); ) {
            genres.append(iterator.next());

            if (iterator.hasNext()) {
                genres.append(" / ");
            }
        }

        sql = new StringBuilder();
        sql.append("update ").append(prependSchema("movie set"));
        sql.append(" c14 = ?");
        sql.append(" where idmovie = ?");

        getJdbcTemplate().update(sql.toString(), genres.toString(), movieID);

        return genres.toString();

    }

    @Override
    public String updateShowGenres(final int showID) {
        // Mysql
        // StringBuilder sql = new StringBuilder();
        // sql.append("select GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ' / ')");
        // sql.append(" from ").append(prependSchema("genre g"));
        // sql.append(" inner join ").append(prependSchema("genre_link gl on gl.genre_id = g.genre_id"));
        // sql.append(" where gl.media_id = ?" and gl.media_type = 'tvshow');
        // sql.append(" group by gls.idshow");
        //
        // String genres = getJdbcTemplate().queryForObject(sql.toString(), String.class, showID);
        //
        // sql = new StringBuilder();
        // sql.append("update ").append(prependSchema("tvshow set"));
        // sql.append(" c08 = ?");
        // sql.append(" where idshow = ?");
        //
        // getJdbcTemplate().update(sql.toString(), genres, showID);
        //
        // return genres;

        StringBuilder sql = new StringBuilder();
        sql.append("select g.name");
        sql.append(" from ").append(prependSchema("genre g"));
        sql.append(" inner join ").append(prependSchema("genre_link gl on gl.genre_id = g.genre_id"));
        sql.append(" where");
        sql.append(" gl.media_type = 'tvshow'");
        sql.append(" and gl.media_id = ?");
        sql.append(" order by g.name");

        final List<String> genreList = getJdbcTemplate().queryForList(sql.toString(), String.class, showID);
        final StringBuilder genres = new StringBuilder();

        for (final Iterator<String> iterator = genreList.iterator(); iterator.hasNext(); ) {
            genres.append(iterator.next());

            if (iterator.hasNext()) {
                genres.append(" / ");
            }
        }

        sql = new StringBuilder();
        sql.append("update ").append(prependSchema("tvshow set"));
        sql.append(" c08 = ?");
        sql.append(" where idshow = ?");

        getJdbcTemplate().update(sql.toString(), genres.toString(), showID);

        return genres.toString();
    }

    private JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private String prependSchema(final String table) {
        return schema + table;
    }
}
