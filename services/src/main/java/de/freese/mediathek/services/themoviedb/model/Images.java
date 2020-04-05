/**
 * Created: 26.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Images
{
	/**
	 * backdrops
	 */
	private List<Image> backdrops = null;

	/**
	 * posters
	 */
	private List<Image> posters = null;

	/**
	 * Erstellt ein neues {@link Images} Object.
	 */
	public Images()
	{
		super();
	}

	/**
	 * @return {@link List}<Image>
	 */
	public List<Image> getBackdrops()
	{
		return this.backdrops;
	}

	/**
	 * @return {@link List}<Image>
	 */
	public List<Image> getPosters()
	{
		return this.posters;
	}

	/**
	 * @param backdrops {@link List}<Image>
	 */
	public void setBackdrops(final List<Image> backdrops)
	{
		this.backdrops = backdrops;

		Collections.sort(this.backdrops);
	}

	/**
	 * @param posters {@link List}<Image>
	 */
	public void setPosters(final List<Image> posters)
	{
		this.posters = posters;

		Collections.sort(this.posters);
	}
}
