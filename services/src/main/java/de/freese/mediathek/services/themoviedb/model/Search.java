/**
 * Created: 24.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search implements Iterable<Movie>
{
	/**
	 * results
	 */
	private List<Movie> results = null;

	/**
	 * Erstellt ein neues {@link Search} Object.
	 */
	public Search()
	{
		super();
	}

	/**
	 * @return {@link List}<Movie>
	 */
	public List<Movie> getResults()
	{
		return this.results;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Movie> iterator()
	{
		if (getResults() != null)
		{
			return getResults().iterator();
		}

		return Collections.emptyIterator();
	}

	/**
	 * @param results {@link List}<Movie>
	 */
	public void setResults(final List<Movie> results)
	{
		this.results = results;
	}
}
