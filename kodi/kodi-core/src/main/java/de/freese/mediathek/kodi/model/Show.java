/**
 * Created: 13.09.2014
 */

package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Show extends AbstractModel
{
	/**
	 * 
	 */
	private String banner = null;

	/**
	 * 
	 */
	private String fanart = null;
	/**
	 * 
	 */
	private String genres = null;

	/**
	 * 
	 */
	private String tvdbID = null;

	/**
	 * Erstellt ein neues {@link Show} Object.
	 */
	public Show()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getBanner()
	{
		return this.banner;
	}

	/**
	 * @return String
	 */
	public String getFanart()
	{
		return this.fanart;
	}

	/**
	 * @return String
	 */
	public String getGenres()
	{
		return this.genres;
	}

	/**
	 * @return String
	 */
	public String getTvdbID()
	{
		return this.tvdbID;
	}

	/**
	 * @param banner String
	 */
	public void setBanner(final String banner)
	{
		this.banner = banner;
	}

	/**
	 * @param fanart String
	 */
	public void setFanart(final String fanart)
	{
		this.fanart = fanart;
	}

	/**
	 * @param genres String
	 */
	public void setGenres(final String genres)
	{
		this.genres = genres;
	}

	/**
	 * @param tvdbID String
	 */
	public void setTvdbID(final String tvdbID)
	{
		this.tvdbID = tvdbID;
	}
}
