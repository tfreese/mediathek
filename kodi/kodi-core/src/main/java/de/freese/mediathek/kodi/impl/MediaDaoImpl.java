// Created: 13.09.2014
package de.freese.mediathek.kodi.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.freese.mediathek.kodi.api.MediaDao;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class MediaDaoImpl extends JdbcDaoSupport implements MediaDao {
    private String schema = "";

    /**
     * @see MediaDao#deleteMovieGenres(int)
     */
    @Override
    public void deleteMovieGenres(final int movieID) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(prependSchema("genre_link"));
        sql.append(" where");
        sql.append(" media_type = 'movie'");
        sql.append(" and media_id = ?");

        getJdbcTemplate().update(sql.toString(), movieID);
    }

    /**
     * @see MediaDao#deleteShowGenres(int)
     */
    @Override
    public void deleteShowGenres(final int showID) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(prependSchema("genre_link"));
        sql.append(" where");
        sql.append(" media_type = 'tvshow'");
        sql.append(" and media_id = ?");

        getJdbcTemplate().update(sql.toString(), showID);
    }

    /**
     * @see MediaDao#getGenreMovies(int)
     */
    @Override
    public List<Movie> getGenreMovies(final int genreID) {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" m.idMovie as pk");
        sql.append(", m.c00 as name");
        sql.append(", m.c08 as poster");
        sql.append(", m.c20 as fanart");
        sql.append(", m.c09 as imdb_id");
        sql.append(", m.c14 as genres");
        sql.append(", m.c07 as year");
        sql.append(", m.idSet as set_id");
        sql.append(" from ").append(prependSchema("movie m"));
        sql.append(" inner join ").append(prependSchema("genre_link gl on gl.media_id = m.idmovie"));
        sql.append(" where");
        sql.append(" gl.media_type = 'movie'");
        sql.append(" and gl.genre_id = ?");

        return getJdbcTemplate().query(sql.toString(), new MovieRowMapper(), genreID);
    }

    /**
     * @see MediaDao#getGenreShows(int)
     */
    @Override
    public List<Show> getGenreShows(final int genreID) {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" s.idShow as pk");
        sql.append(", s.c00 as name");
        sql.append(", s.c06 as banner");
        sql.append(", s.c11 as fanart");
        sql.append(", s.c12 as tvdb_id");
        sql.append(", s.c08 as genres");
        sql.append(" from ").append(prependSchema("tvshow s"));
        sql.append(" inner join ").append(prependSchema("genre_link gl on gl.media_id = s.idshow"));
        sql.append(" where");
        sql.append(" gl.media_type = 'tvshow'");
        sql.append(" and gl.genre_id = ?");

        return getJdbcTemplate().query(sql.toString(), new ShowRowMapper(), genreID);
    }

    /**
     * @see MediaDao#getGenres()
     */
    @Override
    public List<Genre> getGenres() {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" g.genre_id as pk");
        sql.append(", g.name");
        sql.append(", (");
        sql.append("select count(gl.media_id) from ").append(prependSchema("genre_link gl where gl.genre_id = g.genre_id and gl.media_type = 'movie'"));
        sql.append(") as filme_anzahl");
        sql.append(", (");
        sql.append("select count(gl.media_id) from ").append(prependSchema("genre_link gl where gl.genre_id = g.genre_id and gl.media_type = 'tvshow'"));
        sql.append(") as serien_anzahl");
        sql.append(" from ").append(prependSchema("genre g"));

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
        return getJdbcTemplate().query(sql.toString(), new GenreRowMapper());
    }

    /**
     * @see MediaDao#getMovieGenres(int)
     */
    @Override
    public List<Genre> getMovieGenres(final int movieID) {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" g.genre_id as pk");
        sql.append(", g.name");
        sql.append(", 0 as filme_anzahl");
        sql.append(", 0 as serien_anzahl");
        sql.append(" from ").append(prependSchema("genre g"));
        sql.append(" inner join ").append(prependSchema("genre_link gl on gl.genre_id = g.genre_id"));
        sql.append(" where");
        sql.append(" gl.media_type = 'movie'");
        sql.append(" and gl.media_id = ?");
        // sql.append(" order by name");

        return getJdbcTemplate().query(sql.toString(), new GenreRowMapper(), movieID);
    }

    /**
     * @see MediaDao#getMovies()
     */
    @Override
    public List<Movie> getMovies() {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" m.idMovie as pk");
        sql.append(", m.c00 as name");
        sql.append(", m.c08 as poster");
        sql.append(", m.c20 as fanart");
        sql.append(", m.c09 as imdb_id");
        sql.append(", m.c14 as genres");
        sql.append(", m.c07 as year");
        sql.append(", m.idSet as set_id");
        sql.append(" from ").append(prependSchema("movie m"));
        // sql.append("select m.idMovie as pk, m.c00 as name, m.c09 as imdb_id,");
        // sql.append(" GROUP_CONCAT(g.strgenre ORDER BY g.strgenre SEPARATOR ' / ') as genres,");
        // sql.append(" m.c08 as poster, m.c20 as fanart");
        // sql.append(" from xbmc_video75.movie m");
        // sql.append(" inner join xbmc_video75.genrelinkmovie glm on glm.idmovie = m.idmovie");
        // sql.append(" inner join xbmc_video75.genre g on g.idgenre = glm.idgenre");
        // sql.append(" group by name");

        return getJdbcTemplate().query(sql.toString(), new MovieRowMapper());
    }

    /**
     * @see MediaDao#getShowGenres(int)
     */
    @Override
    public List<Genre> getShowGenres(final int showID) {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" g.genre_id as pk");
        sql.append(", g.name");
        sql.append(", 0 as filme_anzahl");
        sql.append(", 0 as serien_anzahl");
        sql.append(" from ").append(prependSchema("genre g"));
        sql.append(" inner join ").append(prependSchema("genre_link gl on gl.genre_id = g.genre_id"));
        sql.append(" where");
        sql.append(" gl.media_type = 'tvshow'");
        sql.append(" and gl.media_id = ?");

        return getJdbcTemplate().query(sql.toString(), new GenreRowMapper(), showID);
    }

    /**
     * @see MediaDao#getShows()
     */
    @Override
    public List<Show> getShows() {
        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" s.idShow as pk");
        sql.append(", s.c00 as name");
        sql.append(", s.c06 as banner");
        sql.append(", s.c11 as fanart");
        sql.append(", s.c12 as tvdb_id");
        sql.append(", s.c08 as genres");
        sql.append(" from ").append(prependSchema("tvshow s"));
        // sql.append("select s.idshow as pk, s.c00 as name, s.c12 as tvdb_id,");
        // sql.append(" GROUP_CONCAT(g.strgenre ORDER BY g.strgenre SEPARATOR ' / ') as genres,");
        // sql.append(" s.c06 as banner, s.c11 as fanart");
        // sql.append(" from xbmc_video75.tvshow s");
        // sql.append(" inner join xbmc_video75.genrelinktvshow gls on gls.idshow = s.idshow");
        // sql.append(" inner join xbmc_video75.genre g on g.idgenre = gls.idgenre");
        // sql.append(" group by name;");

        return getJdbcTemplate().query(sql.toString(), new ShowRowMapper());
    }

    /**
     * @see MediaDao#insertMovieGenre(int, int)
     */
    @Override
    public void insertMovieGenre(final int movieID, final int genreID) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(prependSchema("genre_link"));
        sql.append(" (genre_id, media_id, media_type)");
        sql.append(" values (?, ?, 'movie')");

        getJdbcTemplate().update(sql.toString(), genreID, movieID);
    }

    /**
     * @see MediaDao#insertShowGenre(int, int)
     */
    @Override
    public void insertShowGenre(final int showID, final int genreID) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(prependSchema("genre_link"));
        sql.append(" (genre_id, media_id, media_type)");
        sql.append(" values (?, ?, 'tvshow')");

        getJdbcTemplate().update(sql.toString(), genreID, showID);
    }

    public void setSchema(final String schema) {
        this.schema = schema;
    }

    /**
     * @see MediaDao#updateMovieGenres(int)
     */
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

        List<String> genreList = getJdbcTemplate().queryForList(sql.toString(), String.class, movieID);
        StringBuilder genres = new StringBuilder();

        for (Iterator<String> iterator = genreList.iterator(); iterator.hasNext(); ) {
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

    /**
     * @see MediaDao#updateShowGenres(int)
     */
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

        List<String> genreList = getJdbcTemplate().queryForList(sql.toString(), String.class, showID);
        StringBuilder genres = new StringBuilder();

        for (Iterator<String> iterator = genreList.iterator(); iterator.hasNext(); ) {
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

    private String prependSchema(final String table) {
        return this.schema + table;
    }
}
