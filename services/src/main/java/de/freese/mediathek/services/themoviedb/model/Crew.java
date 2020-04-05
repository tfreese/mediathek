/**
 * Created: 26.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crew
{
	/**
	 * job
	 */
	private String job = null;

	/**
	 * name
	 */
	private String name = null;

	/**
	 * profile_path
	 */
	private String profile = null;

	/**
	 * Erstellt ein neues {@link Crew} Object.
	 */
	public Crew()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getJob()
	{
		return this.job;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return String
	 */
	public String getProfile()
	{
		return this.profile;
	}

	/**
	 * @param job String
	 */
	public void setJob(final String job)
	{
		this.job = job;
	}

	/**
	 * @param name String
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @param profile String
	 */
	@JsonSetter("profile_path")
	public void setProfile(final String profile)
	{
		this.profile = profile;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Crew [name=");
		builder.append(this.name);
		builder.append(", job=");
		builder.append(this.job);
		builder.append(", profile=");
		builder.append(this.profile);
		builder.append("]");

		return builder.toString();
	}
}
