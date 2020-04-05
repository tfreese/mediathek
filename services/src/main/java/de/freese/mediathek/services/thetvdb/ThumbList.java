/**
 * Created: 16.09.2014
 */

package de.freese.mediathek.services.thetvdb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "fanart")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThumbList
{
	/**
	 * 
	 */
	@XmlElement(name = "thumb")
	private List<Thumb> thumbs = new ArrayList<>();

	/**
	 * Erstellt ein neues {@link ThumbList} Object.
	 */
	public ThumbList()
	{
		super();
	}

	/**
	 * @return List<Thumb>
	 */
	public List<Thumb> getThumbs()
	{
		return this.thumbs;
	}

	/**
	 * @param thumbs List<Thumb>
	 */
	public void setThumbs(final List<Thumb> thumbs)
	{
		this.thumbs = thumbs;
	}
}
