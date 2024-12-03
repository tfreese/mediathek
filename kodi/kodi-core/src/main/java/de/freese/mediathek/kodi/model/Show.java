// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class Show extends AbstractModel {
    private String banner;
    private String fanArt;
    private String genres;
    private String tvDbId;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Show show)) {
            return false;
        }
        
        if (!super.equals(o)) {
            return false;
        }

        return Objects.equals(banner, show.banner) && Objects.equals(fanArt, show.fanArt) && Objects.equals(genres, show.genres)
                && Objects.equals(tvDbId, show.tvDbId);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), banner, fanArt, genres, tvDbId);
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
