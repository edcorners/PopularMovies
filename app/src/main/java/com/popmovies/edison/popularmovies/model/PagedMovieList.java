package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.data.SortingAttributesColumns;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Edison on 12/30/2015.
 * Represents a list of movies as returned from TMDB's service
 */
public class PagedMovieList {
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Movie> movies = new ArrayList<>();

    /**
     * Creates a list of content values with this list's movies
     * @return vector containing this movies as content values
     */
    public Vector<ContentValues> toMovieContentValues() {
        Vector<ContentValues> contentValuesVector = new Vector<>(movies.size());
        for(Movie movie: movies){
            ContentValues movieContentValues = movie.toContentValues();
            contentValuesVector.add(movieContentValues);
        }
        return contentValuesVector;
    }

    /**
     * Creates a list of content values ready to insert as SortingAttributes records
     * @param sortPreference the sorting preference to be inserted
     * @return vector containing a list of SortingAttributes records
     */
    public Vector<ContentValues> toSortingAttributesContentValues(String sortPreference) {
        Vector<ContentValues> contentValuesVector = new Vector<>(movies.size());
        int position = 0;
        for(Movie movie: movies){
            ContentValues movieContentValues = new ContentValues();
            movieContentValues.put(SortingAttributesColumns.POSITION, position);
            movieContentValues.put(SortingAttributesColumns.MOVIE_ID, movie.getId());
            movieContentValues.put(SortingAttributesColumns.PREFERENCE_CATEGORY, sortPreference);
            contentValuesVector.add(movieContentValues);
            position++;
        }
        return contentValuesVector;
    }

    public Integer getPage() {
        return page;
    }


    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "PagedMovieList{" +
                "page=" + page +
                ", movies=" + movies +
                '}';
    }
}
