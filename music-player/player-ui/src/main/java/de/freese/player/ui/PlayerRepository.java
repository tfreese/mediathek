// Created: 08 Sept. 2024
package de.freese.player.ui;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.DefaultAudioSource;
import de.freese.player.ui.model.PlayList;

/**
 * @author Thomas Freese
 */
public final class PlayerRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerRepository.class);

    public static void createDatabaseIfNotExist(final DataSource dataSource) throws Exception {
        final String tableName = "SONG";

        try (Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData metaData = connection.getMetaData();
            boolean tableExist = false;

            try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                if (tables.next()) {
                    tableExist = true;
                }
            }

            if (!tableExist) {
                LOGGER.info("Create table: {}", tableName);

                final URL url = Thread.currentThread().getContextClassLoader().getResource("music-player.sql");
                assert url != null;

                try (Stream<String> stream = Files.lines(Path.of(url.toURI()), StandardCharsets.UTF_8);
                     Statement statement = connection.createStatement()) {
                    final String createSql = stream.collect(Collectors.joining());
                    statement.execute(createSql);
                }
            }
        }
    }

    private final DataSource dataSource;

    public PlayerRepository(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    public void deleteLibraryPath(final Path path) {
        if (path == null) {
            return;
        }

        final String sql = """
                delete from config
                where
                    content = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, path.toUri().toString());
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void deleteOrphansSongs(final Set<URI> urisNew) {
        final String sqlSelect = """
                select uri from song
                """;

        final Set<URI> urisExisting = new HashSet<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlSelect)) {
            while (resultSet.next()) {
                final URI uri = URI.create(resultSet.getString("uri"));
                urisExisting.add(uri);
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }

        urisExisting.removeAll(urisNew);

        deleteSongs(urisExisting);
    }

    public void deletePlayList(final String name) {
        final String sql = """
                delete from playlist
                where
                    name = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void deleteSong(final URI uri) {
        if (uri == null) {
            return;
        }

        deleteSongs(Set.of(uri));
    }

    public void deleteSongs(final Set<URI> uris) {
        if (uris == null || uris.isEmpty()) {
            return;
        }

        final String sql = """
                delete from song
                where
                    uri = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            int n = 0;

            for (URI uri : uris) {
                preparedStatement.clearParameters();
                preparedStatement.setString(1, uri.toString());
                preparedStatement.addBatch();
                n++;

                if (n % 500 == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }

            preparedStatement.executeBatch();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public PlayList getCurrentPlayList() {
        final String sql = """
                select
                    pl.id,
                    pl.name,
                    pl.where_clause
                from playlist pl
                inner join config c on c.content = pl.name
                where
                    c.name = 'currentPlayList'
                """;

        final PlayList result;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            result = new PlayList();

            if (resultSet.next()) {
                result.setId(resultSet.getLong("id"));
                result.setName(resultSet.getString("name"));
                result.setWhereClause(resultSet.getString("where_clause"));
            }
            else {
                result.setName("Play all");
                result.setWhereClause("1 = 1");
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }

        return result;
    }

    public List<Path> getLibraryPaths() {
        final String sql = """
                select content from config
                where
                    name like 'library_path_%'
                """;

        final List<Path> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final URI uri = URI.create(resultSet.getString("content"));
                result.add(Path.of(uri));
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }

        return result;
    }

    public List<PlayList> getPlayLists() {
        final String sql = """
                select
                    id,
                    name,
                    where_clause
                from playlist
                order by
                    name asc
                """;

        final List<PlayList> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final PlayList playList = new PlayList();
                playList.setId(resultSet.getLong("id"));
                playList.setName(resultSet.getString("name"));
                playList.setWhereClause(resultSet.getString("where_clause"));

                result.add(playList);
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }

        return result;
    }

    public void getSongs(final PlayList playList, final Consumer<AudioSource> consumer) {
        String whereClause = playList.getWhereClause();

        if (whereClause == null || whereClause.isBlank()) {
            whereClause = "1 = 1";
        }

        final String sql = """
                select * from song
                where
                    %s
                order by
                    play_count desc,
                    artist asc,
                    album asc,
                    disc asc,
                    track asc
                """.formatted(whereClause);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final DefaultAudioSource audioSource = new DefaultAudioSource();
                audioSource.setUri(URI.create(resultSet.getString("uri")));
                audioSource.setBitRate(resultSet.getInt("bit_rate"));
                audioSource.setChannels(resultSet.getInt("channels"));
                audioSource.setDuration(Duration.parse(resultSet.getString("duration")));
                audioSource.setFormat(resultSet.getString("format"));
                audioSource.setSamplingRate(resultSet.getInt("sampling_rate"));
                audioSource.setArtist(resultSet.getString("artist"));
                audioSource.setAlbum(resultSet.getString("album"));
                audioSource.setTitle(resultSet.getString("title"));
                audioSource.setGenre(resultSet.getString("genre"));
                audioSource.setReleaseDate(resultSet.getString("release_date"));
                audioSource.setDisc(resultSet.getString("disc"));
                audioSource.setTrack(resultSet.getString("track"));
                audioSource.setCompilation(resultSet.getInt("is_compilation") == 1);
                audioSource.setPlayCount(resultSet.getInt("play_count"));
                audioSource.setMetaData(resultSet.getString("meta_data"));

                consumer.accept(audioSource);
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void saveCurrentPlayList(final String name) {
        if (name == null || name.isBlank()) {
            return;
        }

        final String sql = """
                merge into config
                (
                    name, content
                )
                key (name)
                values
                (
                    ?, ?
                )
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "currentPlayList");
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void saveLibraryPath(final Path path) {
        if (path == null) {
            return;
        }

        String sql = """
                select count(*) from config
                where
                    name like 'library_path_%'
                """;

        final int pathCount;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            pathCount = resultSet.getInt(1);
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }

        sql = """
                insert into config
                (name, content)
                values (?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "library_path_" + (pathCount + 1));
            preparedStatement.setString(2, path.toUri().toString());

            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void saveOrUpdatePlayList(final PlayList playList) {
        if (playList == null) {
            return;
        }

        final String sql = """
                merge into playlist
                    using DUAL
                on id = ?
                when matched then
                    update set
                        name = ?,
                        where_clause = ?
                when not matched then
                    insert (name, where_clause) values (?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, playList.getId());
            preparedStatement.setString(2, playList.getName());
            preparedStatement.setString(3, playList.getWhereClause());
            preparedStatement.setString(4, playList.getName());
            preparedStatement.setString(5, playList.getWhereClause());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    playList.setId(resultSet.getLong("id"));
                }
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void saveOrUpdateSong(final AudioSource audioSource) {
        if (audioSource == null) {
            return;
        }

        LOGGER.debug("saveOrUpdate audioSource: {}", audioSource);

        final String sql = """
                merge into song
                (
                uri, bit_rate, channels, duration,
                format, sampling_rate, artist, album,
                title, genre, release_date, disc,
                track, is_compilation, meta_data
                )
                key (uri)
                values
                (
                ?, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?
                )
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, audioSource.getUri().toString());
            preparedStatement.setInt(2, audioSource.getBitRate());
            preparedStatement.setInt(3, audioSource.getChannels());
            preparedStatement.setString(4, audioSource.getDuration().toString());
            preparedStatement.setString(5, audioSource.getFormat());
            preparedStatement.setInt(6, audioSource.getSamplingRate());
            preparedStatement.setString(7, audioSource.getArtist());
            preparedStatement.setString(8, audioSource.getAlbum());
            preparedStatement.setString(9, audioSource.getTitle());
            preparedStatement.setString(10, audioSource.getGenre());
            preparedStatement.setString(11, audioSource.getReleaseDate());
            preparedStatement.setString(12, audioSource.getDisc());
            preparedStatement.setString(13, audioSource.getTrack());
            preparedStatement.setInt(14, audioSource.isCompilation() ? 1 : 0);
            preparedStatement.setString(15, audioSource.getMetaData());

            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void updateSongPlayCount(final URI uri, final int playCount) {
        if (uri == null) {
            return;
        }

        final String sql = """
                update song
                set
                    play_count = ?
                where
                    uri = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, playCount);
            preparedStatement.setString(2, uri.toString());
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }
}
