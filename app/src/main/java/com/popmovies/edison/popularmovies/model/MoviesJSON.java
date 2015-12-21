package com.popmovies.edison.popularmovies.model;

/**
 * Created by Edison on 12/18/2015.
 */
public enum MoviesJSON {
    PAGE("page"),
    RESULTS("results"),
    POSTER_PATH("poster_path"),
    OVERVIEW("overview"),
    RELEASE_DATE("release_date"),
    ID("id"),
    ORIGINAL_TITLE("original_title"),
    RATING("vote_average");

    private String key;

    MoviesJSON(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
