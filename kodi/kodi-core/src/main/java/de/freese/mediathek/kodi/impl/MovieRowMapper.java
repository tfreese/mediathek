// Created: 13.09.2014
package de.freese.mediathek.kodi.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import de.freese.mediathek.kodi.model.Movie;

/**
 * @author Thomas Freese
 */
public class MovieRowMapper implements RowMapper<Movie>
{
    /**
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public Movie mapRow(final ResultSet rs, final int rowNum) throws SQLException
    {
        Movie movie = new Movie();
        movie.setPK(rs.getInt("pk"));
        movie.setName(rs.getString("name"));
        movie.setGenres(rs.getString("genres"));
        movie.setPosters(rs.getString("poster"));
        movie.setFanarts(rs.getString("fanart"));
        movie.setImdbID(rs.getString("imdb_id"));
        movie.setYear(rs.getInt("year"));
        movie.setSetID(rs.getInt("set_id"));

        return movie;
    }
}
