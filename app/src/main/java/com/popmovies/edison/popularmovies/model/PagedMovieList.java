package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.data.ReviewColumns;
import com.popmovies.edison.popularmovies.data.SortingAttributesColumns;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Edison on 12/30/2015.
 */
public class PagedMovieList {
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Movie> movies = new ArrayList<Movie>();

    public Vector<ContentValues> toMovieContentValues() {
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(movies.size());
        int position = 0;
        for(Movie movie: movies){
            ContentValues movieContentValues = movie.toContentValues();
            contentValuesVector.add(movieContentValues);
        }
        return contentValuesVector;
    }

    public Vector<ContentValues> toSortingAttributesContentValues(String sortPreference) {
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(movies.size());
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
