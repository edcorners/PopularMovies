package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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


    public TextView setAuthor(TextView reviewAuthorTextView) {
        reviewAuthorTextView.setText(author);
        return reviewAuthorTextView;
    }

    public TextView setContent(TextView reviewContentTextView) {
        reviewContentTextView.setText(content);
        return reviewContentTextView;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ReviewColumns.AUTHOR, author);
        cv.put(ReviewColumns.CONTENT, content);
        cv.put(ReviewColumns.REVIEW_ID, id);
        cv.put(ReviewColumns.URL, url);
        return cv;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
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
