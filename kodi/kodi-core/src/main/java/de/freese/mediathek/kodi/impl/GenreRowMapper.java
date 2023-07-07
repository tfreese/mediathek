// Created: 13.09.2014
package de.freese.mediathek.kodi.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.freese.mediathek.kodi.model.Genre;

/**
 * @author Thomas Freese
 */
public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setPk(rs.getInt("pk"));
        genre.setName(rs.getString("name"));
        genre.setAnzahlFilme(rs.getInt("filme_anzahl"));
        genre.setAnzahlSerien(rs.getInt("serien_anzahl"));

        return genre;
    }
}
