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
 */
public class PagedTrailerList {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailer> trailers = new ArrayList<>();

    public Vector<ContentValues> toContentValues() {
        Vector<ContentValues> contentValuesVector = new Vector<>(trailers.size());
        for(Trailer trailer: trailers){
            ContentValues trailerContentValues = trailer.toContentValues();
            trailerContentValues.put(TrailerColumns.MOVIE_ID, id);
            contentValuesVector.add(trailerContentValues);
        }
        return contentValuesVector;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<Trailer> getTrailers() {
        return trailers;
    }

    /**
     *
     * @param trailers
     * The results
     */
    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagedTrailerList that = (PagedTrailerList) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(trailers != null ? !trailers.equals(that.trailers) : that.trailers != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
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
