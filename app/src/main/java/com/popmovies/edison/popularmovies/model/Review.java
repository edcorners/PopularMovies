package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.data.MovieColumns;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabase;
import com.popmovies.edison.popularmovies.data.ReviewColumns;

/**
 * Created by Edison on 1/14/2016.
 */
public class Review {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("url")
    @Expose
    private String url;

    // Constructors

    public Review(Cursor cursor) {
        id = cursor.getString(ReviewColumnProjection.REVIEW_ID.ordinal());
        author = cursor.getString(ReviewColumnProjection.AUTHOR.ordinal());
        content = cursor.getString(ReviewColumnProjection.CONTENT.ordinal());
        url = cursor.getString(ReviewColumnProjection.URL.ordinal());
    }

    /**
     * This enumeration defines the projection used when loading a review from a cursor
     */
    public enum ReviewColumnProjection{
        _ID(PopMoviesDatabase.REVIEWS+"."+ ReviewColumns._ID),
        MOVIE_ID(PopMoviesDatabase.REVIEWS+"."+ReviewColumns.MOVIE_ID),
        REVIEW_ID(PopMoviesDatabase.REVIEWS+"."+ReviewColumns.REVIEW_ID),
        AUTHOR(PopMoviesDatabase.REVIEWS+"."+ReviewColumns.AUTHOR),
        CONTENT(PopMoviesDatabase.REVIEWS+"."+ReviewColumns.CONTENT),
        URL(PopMoviesDatabase.REVIEWS+"."+ReviewColumns.URL);

        private String columnName;

        ReviewColumnProjection(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public static String[] getProjection(){
            return new String[]{_ID.columnName,MOVIE_ID.columnName,REVIEW_ID.columnName,AUTHOR.columnName, CONTENT.columnName, URL.columnName};
        }
    }

    /**
     * Sets this review's author to a TextView
     * @param reviewAuthorTextView TextView to set this review's author
     * @return a TextView displaying this review's author
     */
    public TextView setAuthor(TextView reviewAuthorTextView) {
        reviewAuthorTextView.setText(author);
        return reviewAuthorTextView;
    }

    /**
     * Sets this review's content to a TextView
     * @param reviewContentTextView TextView to set this review's content
     * @return a TextView displaying this review's content
     */
    public TextView setContent(TextView reviewContentTextView) {
        reviewContentTextView.setText(content);
        return reviewContentTextView;
    }

    /**
     * Creates a ContentValues representation of this review
     * @return the attributes of this review as a ContentValues object
     */
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ReviewColumns.AUTHOR, author);
        cv.put(ReviewColumns.CONTENT, content);
        cv.put(ReviewColumns.REVIEW_ID, id);
        cv.put(ReviewColumns.URL, url);
        return cv;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (id != null ? !id.equals(review.id) : review.id != null) return false;
        if (author != null ? !author.equals(review.author) : review.author != null) return false;
        if (content != null ? !content.equals(review.content) : review.content != null)
            return false;
        return !(url != null ? !url.equals(review.url) : review.url != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

}
