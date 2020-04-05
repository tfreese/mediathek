/**
 * Created: 10.11.2014
 */

package de.freese.mediathek.services.thetvdb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Actors")
@XmlAccessorType(XmlAccessType.FIELD)
class Actors
{
	/**
	 *
	 */
	@XmlElement(name = "Actor")
	private List<Actor> actors = null;

	/**
	 * Erstellt ein neues {@link Actors} Object.
	 */
	Actors()
	{
		super();
	}

	/**
	 * @return {@link List}<Actor>
	 */
	List<Actor> getActors()
	{
		return this.actors;
	}
}
