// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Show extends AbstractModel {
    private String banner;

    private String fanArt;

    private String genres;

    private String tvDbId;

    public String getBanner() {
        return this.banner;
    }

    public String getFanArt() {
        return this.fanArt;
    }

    public String getGenres() {
        return this.genres;
    }

    public String getTvDbId() {
        return this.tvDbId;
    }

    public void setBanner(final String banner) {
        this.banner = banner;
    }

    public void setFanArt(final String fanArt) {
        this.fanArt = fanArt;
    }

    public void setGenres(final String genres) {
        this.genres = genres;
    }

    public void setTvDbId(final String tvDbId) {
        this.tvDbId = tvDbId;
    }
}
