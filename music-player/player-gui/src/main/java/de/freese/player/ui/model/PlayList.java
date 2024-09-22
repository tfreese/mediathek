// Created: 22 Sept. 2024
package de.freese.player.ui.model;

/**
 * @author Thomas Freese
 */
public final class PlayList {
    private String name;
    private String whereClause;

    public String getName() {
        return name;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setWhereClause(final String whereClause) {
        this.whereClause = whereClause;
    }
}
