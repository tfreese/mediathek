// Created: 08 Sept. 2024
package de.freese.player;

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
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.exception.PlayerException;
import de.freese.player.input.AudioSource;
import de.freese.player.input.DefaultAudioSource;

/**
 * @author Thomas Freese
 */
public final class MusicPlayerRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPlayerRepository.class);

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

    public MusicPlayerRepository(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    public void deleteLibraryPath(final Path path) {
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

    public void deleteSong(final URI uri) {
        final String sql = """
                delete from song
                where uri = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uri.toString());

            preparedStatement.execute();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public List<Path> getLibraryPaths() {
        final String sql = """
                select content from config
                where
                    name like 'library_path_%'
                """;

        final List<Path> uris = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final URI uri = URI.create(resultSet.getString("content"));
                uris.add(Path.of(uri));
            }
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }

        return uris;
    }

    public void loadSongs(final Consumer<AudioSource> consumer) {
        final String sql = """
                select * from song
                order by play_count desc, artist asc
                """;

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

    public void saveLibraryPath(final Path path) {
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

            preparedStatement.execute();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void saveOrUpdateSong(final AudioSource audioSource) {
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

            preparedStatement.execute();
        }
        catch (SQLException ex) {
            throw new PlayerException(ex);
        }
    }

    public void updateSong(final URI uri, final int playCount) {
        // TODO
    }
}
