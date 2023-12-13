// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Genre extends AbstractModel {
    private int anzahlFilme;
    private int anzahlSerien;

    public int getAnzahlFilme() {
        return this.anzahlFilme;
    }

    public int getAnzahlSerien() {
        return this.anzahlSerien;
    }

    public void setAnzahlFilme(final int anzahlFilme) {
        this.anzahlFilme = anzahlFilme;
    }

    public void setAnzahlSerien(final int anzahlSerien) {
        this.anzahlSerien = anzahlSerien;
    }
}
