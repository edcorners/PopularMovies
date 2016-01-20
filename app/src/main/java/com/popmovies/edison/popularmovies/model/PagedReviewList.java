package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.data.MovieColumns;
import com.popmovies.edison.popularmovies.data.ReviewColumns;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Edison on 1/14/2016.
 * @Generated("org.jsonschema2pojo")
 */
public class PagedReviewList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Review> reviews = new ArrayList<Review>();
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public Vector<ContentValues> toContentValues() {
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(reviews.size());
        for(Review review: reviews){
            ContentValues reviewContentValues = review.toContentValues();
            reviewContentValues.put(ReviewColumns.MOVIE_ID, id);
            contentValuesVector.add(reviewContentValues);
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
     * The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @param page
     * The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     *
     * @return
     * The reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     *
     * @param reviews
     * The reviews
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     *
     * @return
     * The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     * The total_results
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagedReviewList that = (PagedReviewList) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (page != null ? !page.equals(that.page) : that.page != null) return false;
        if (reviews != null ? !reviews.equals(that.reviews) : that.reviews != null) return false;
        if (totalPages != null ? !totalPages.equals(that.totalPages) : that.totalPages != null)
            return false;
        return !(totalResults != null ? !totalResults.equals(that.totalResults) : that.totalResults != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (reviews != null ? reviews.hashCode() : 0);
        result = 31 * result + (totalPages != null ? totalPages.hashCode() : 0);
        result = 31 * result + (totalResults != null ? totalResults.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PagedReviewList{" +
                "id=" + id +
                ", page=" + page +
                ", reviews=" + reviews +
                ", totalPages=" + totalPages +
                ", totalResults=" + totalResults +
                '}';
    }


}
