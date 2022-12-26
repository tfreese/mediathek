// Created: 13.09.2014
package de.freese.mediathek.kodi.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.freese.mediathek.kodi.model.Genre;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Thomas Freese
 */
public class GenreRowMapper implements RowMapper<Genre>
{
    /**
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public Genre mapRow(final ResultSet rs, final int rowNum) throws SQLException
    {
        Genre genre = new Genre();
        genre.setPk(rs.getInt("pk"));
        genre.setName(rs.getString("name"));
        genre.setAnzahlFilme(rs.getInt("filme_anzahl"));
        genre.setAnzahlSerien(rs.getInt("serien_anzahl"));

        return genre;
    }
}
