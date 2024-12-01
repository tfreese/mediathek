// Created: 05.04.2020
package de.freese.mediathek.services;

/**
 * @author Thomas Freese
 */
public final class Settings {
    public static final String PROPERTY_MOVIE_DB_API_KEY = "MOVIE_DB_API_KEY";
    public static final String PROPERTY_TV_DB_API_KEY = "TV_DB_API_KEY";
    /**
     * Das Boot (1981), imdb_id: tt0082096
     */
    public static final String TEST_MOVIE = "Das Boot";
    /**
     * Das Boot (1981), imdb_id: tt0082096
     */
    public static final int TEST_MOVIE_ID = 387;
    /**
     * Das Boot (1981), imdb_id: tt0082096
     */
    public static final int TEST_MOVIE_YEAR = 1981;
    /**
     * Stargate, imdb_id: tt0374455; SG-1
     */
    public static final String TEST_SHOW = "stargate";
    /**
     * Stargate, imdb_id: tt0374455; SG-1
     */
    public static final String TEST_SHOW_ID = "72449";

    public static String getMovieDbApiKey() {
        return "generateMe";

        // System.setProperty(PROPERTY_MOVIE_DB_API_KEY, apiKey);
        //
        // return apiKey;
    }

    public static String getTvDbApiKey() {
        return "generateMe";

        // System.setProperty(PROPERTY_TV_DB_API_KEY, apiKey);
        //
        // return apiKey;
    }

    private Settings() {
        super();
    }
}
