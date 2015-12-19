package com.popmovies.edison.popularmovies.model;

/**
 * Created by Edison on 12/18/2015.
 */
public enum TMDBAPI {
    BASE_URL("https://api.themoviedb.org/3"),
    DISCOVER("discover"),
    MOVIE("movie"),
    SORT_BY("sort_by"),
    API_KEY("api_key"),
    // Image related values
    IMAGE_BASE_URL("http://image.tmdb.org/t/p/"),
    W92("w92"),
    W154("w154"),
    W185("w185"),
    W342("w342"),
    W500("w500"),
    W780("w780"),
    ORIGINAL("original");

    private String value;
    TMDBAPI(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
