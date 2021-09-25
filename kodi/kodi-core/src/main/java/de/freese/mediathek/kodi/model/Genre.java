// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Genre extends AbstractModel
{
    /**
     *
     */
    private int anzahlFilme;
    /**
     *
     */
    private int anzahlSerien;

    /**
     * @return int
     */
    public int getAnzahlFilme()
    {
        return this.anzahlFilme;
    }

    /**
     * @return int
     */
    public int getAnzahlSerien()
    {
        return this.anzahlSerien;
    }

    /**
     * @param anzahlFilme int
     */
    public void setAnzahlFilme(final int anzahlFilme)
    {
        this.anzahlFilme = anzahlFilme;
    }

    /**
     * @param anzahlSerien int
     */
    public void setAnzahlSerien(final int anzahlSerien)
    {
        this.anzahlSerien = anzahlSerien;
    }
}
