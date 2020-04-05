/**
 * Created: 24.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetails extends Movie
{
	/**
	 * 
	 */
	private List<Actor> actors = null;

	/**
	 * belongs_to_collection
	 */
	private String collection = null;

	/**
	 * production_countries
	 */
	private List<Country> countries = null;

	/**
	 * 
	 */
	private List<Crew> directors = null;

	/**
	 * genres
	 */
	private List<Genre> genres = null;

	/**
	 * imdb_id
	 */
	private String imdbID = null;

	/**
	 * spoken_languages
	 */
	private List<Language> languages = null;

	/**
	 * runtime
	 */
	private int runtime = 0;

	/**
	 * production_companies
	 */
	private List<Studio> studios = null;

	/**
	 * tagline
	 */
	private String tagline = null;

	/**
	 * vote_average
	 */
	private float voteAverage = 0.0f;

	/**
	 * vote_count
	 */
	private int voteCount = 0;

	/**
	 * Erstellt ein neues {@link MovieDetails} Object.
	 */
	public MovieDetails()
	{
		super();
	}

	/**
	 * @return {@link List}<Actor>
	 */
	public List<Actor> getActors()
	{
		return this.actors;
	}

	/**
	 * @return String
	 */
	public String getCollection()
	{
		return this.collection;
	}

	/**
	 * @return {@link List}<Country>
	 */
	public List<Country> getCountries()
	{
		return this.countries;
	}

	/**
	 * @return List<Crew>
	 */
	public List<Crew> getDirectors()
	{
		return this.directors;
	}

	/**
	 * @return {@link List}<Genre>
	 */
	public List<Genre> getGenres()
	{
		return this.genres;
	}

	/**
	 * @return String
	 */
	public String getImdbID()
	{
		return this.imdbID;
	}

	/**
	 * @return {@link List}<Language>
	 */
	public List<Language> getLanguages()
	{
		return this.languages;
	}

	/**
	 * @return int
	 */
	public int getRuntime()
	{
		return this.runtime;
	}

	/**
	 * @return {@link List}<Studio>
	 */
	public List<Studio> getStudios()
	{
		return this.studios;
	}

	/**
	 * @return String
	 */
	public String getTagline()
	{
		return this.tagline;
	}

	/**
	 * @return float
	 */
	public float getVoteAverage()
	{
		return this.voteAverage;
	}

	/**
	 * @return int
	 */
	public int getVoteCount()
	{
		return this.voteCount;
	}

	/**
	 * @param actors {@link List}<Actor>
	 */
	public void setActors(final List<Actor> actors)
	{
		this.actors = actors;

		Collections.sort(this.actors);
	}

	/**
	 * @param collection String
	 */
	@JsonSetter("belongs_to_collection")
	public void setCollection(final String collection)
	{
		// if (StringUtils.isBlank(collection) || StringUtils.equals(collection, "null"))
		// {
		// return;
		// }

		this.collection = collection;
	}

	/**
	 * @param countries {@link List}<Country>
	 */
	@JsonSetter("production_countries")
	public void setCountries(final List<Country> countries)
	{
		this.countries = countries;

		Collections.sort(this.countries);
	}

	/**
	 * @param directors List<Crew>
	 */
	public void setDirectors(final List<Crew> directors)
	{
		this.directors = directors;
	}

	/**
	 * @param genres {@link List}<Genre>
	 */
	public void setGenres(final List<Genre> genres)
	{
		this.genres = genres;

		Collections.sort(this.genres);
	}

	/**
	 * @param imdbID String
	 */
	@JsonSetter("imdb_id")
	public void setImdbID(final String imdbID)
	{
		this.imdbID = imdbID;
	}

	/**
	 * @param languages {@link List}<Language>
	 */
	@JsonSetter("spoken_languages")
	public void setLanguages(final List<Language> languages)
	{
		this.languages = languages;

		// Collections.sort(this.languages);
	}

	/**
	 * @param runtime int
	 */
	public void setRuntime(final int runtime)
	{
		this.runtime = runtime;
	}

	/**
	 * @param studios {@link List}<Studio>
	 */
	@JsonSetter("production_companies")
	public void setStudios(final List<Studio> studios)
	{
		this.studios = studios;

		// Collections.sort(this.studios);
	}

	/**
	 * @param tagline String
	 */
	public void setTagline(final String tagline)
	{
		this.tagline = tagline;
	}

	/**
	 * @param voteAverage float
	 */
	@JsonSetter("vote_average")
	public void setVoteAverage(final float voteAverage)
	{
		this.voteAverage = voteAverage;
	}

	/**
	 * @param voteCount int
	 */
	@JsonSetter("vote_count")
	public void setVoteCount(final int voteCount)
	{
		this.voteCount = voteCount;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString();
	}
}
