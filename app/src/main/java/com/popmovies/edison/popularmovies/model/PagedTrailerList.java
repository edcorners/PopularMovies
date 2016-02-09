package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.data.TrailerColumns;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Edison on 1/14/2016.
 * Represents a list of reviews as returned from TMDB's service
 */
public class PagedTrailerList {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("results")
    @Expose
    private List<Trailer> trailers = new ArrayList<>();

    public PagedTrailerList() {
    }

    public PagedTrailerList(long id) {
        this.id = id;
    }

    /**
     * Creates a list of content values representing the list of trailers
     * @return vector containing a ContentValues object for each trailer in this list
     */
    public Vector<ContentValues> toContentValues() {
        Vector<ContentValues> contentValuesVector = new Vector<>(trailers.size());
        for(Trailer trailer: trailers){
            ContentValues trailerContentValues = trailer.toContentValues();
            trailerContentValues.put(TrailerColumns.MOVIE_ID, id);
            contentValuesVector.add(trailerContentValues);
        }
        return contentValuesVector;
    }

    public void addTrailer(Trailer trailer) {
        trailers.add(trailer);
    }

    public void clear() {
        trailers.clear();
    }

    public Trailer getFirst() {
        return trailers.isEmpty() ? null: trailers.get(0);
    }

    public boolean isEmpty() {
        return trailers.isEmpty();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagedTrailerList that = (PagedTrailerList) o;

        if (id != that.id) return false;
        return !(trailers != null ? !trailers.equals(that.trailers) : that.trailers != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (trailers != null ? trailers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PagedTrailerList{" +
                "id=" + id +
                ", trailers=" + trailers +
                '}';
    }
}
