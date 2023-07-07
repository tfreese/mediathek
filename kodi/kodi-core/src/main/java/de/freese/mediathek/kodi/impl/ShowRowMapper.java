// Created: 13.09.2014
package de.freese.mediathek.kodi.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class ShowRowMapper implements RowMapper<Show> {
    @Override
    public Show mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        Show show = new Show();
        show.setPk(rs.getInt("pk"));
        show.setName(rs.getString("name"));
        show.setGenres(rs.getString("genres"));
        show.setBanner(rs.getString("banner"));
        show.setFanArt(rs.getString("fanart"));
        show.setTvDbId(rs.getString("tvdb_id"));

        return show;
    }
}
