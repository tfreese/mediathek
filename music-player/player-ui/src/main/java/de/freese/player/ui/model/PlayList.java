// Created: 22 Sept. 2024
package de.freese.player.ui.model;

/**
 * @author Thomas Freese
 */
public final class PlayList {
    private long id;
    private String name;
    private String whereClause;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setWhereClause(final String whereClause) {
        this.whereClause = whereClause;
    }
}
