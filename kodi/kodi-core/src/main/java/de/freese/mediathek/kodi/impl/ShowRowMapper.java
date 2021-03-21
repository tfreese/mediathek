// Created: 13.09.2014
package de.freese.mediathek.kodi.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class ShowRowMapper implements RowMapper<Show>
{
    /**
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public Show mapRow(final ResultSet rs, final int rowNum) throws SQLException
    {
        Show show = new Show();
        show.setPK(rs.getInt("pk"));
        show.setName(rs.getString("name"));
        show.setGenres(rs.getString("genres"));
        show.setBanner(rs.getString("banner"));
        show.setFanart(rs.getString("fanart"));
        show.setTvdbID(rs.getString("tvdb_id"));

        return show;
    }
}
