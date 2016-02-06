package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabase;
import com.popmovies.edison.popularmovies.data.ReviewColumns;
import com.popmovies.edison.popularmovies.data.TrailerColumns;

import java.net.URL;

/**
 * Created by Edison on 1/14/2016.
 */
public class Trailer {

    private static final String YOUTUBE_VIDEO_BASE_URI = "http://www.youtube.com/watch?v=";

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("site")
    @Expose
    private String site;

    public Trailer(Cursor cursor) {
        id = cursor.getString(TrailerColumnProjection.TRAILER_ID.ordinal());
        key = cursor.getString(TrailerColumnProjection.KEY.ordinal());
        name = cursor.getString(TrailerColumnProjection.NAME.ordinal());
        site = cursor.getString(TrailerColumnProjection.SITE.ordinal());
    }

    public enum TrailerColumnProjection{
        _ID(PopMoviesDatabase.TRAILERS+"."+ TrailerColumns._ID),
        MOVIE_ID(PopMoviesDatabase.TRAILERS+"."+TrailerColumns.MOVIE_ID),
        TRAILER_ID(PopMoviesDatabase.TRAILERS+"."+TrailerColumns.TRAILER_ID),
        KEY(PopMoviesDatabase.TRAILERS+"."+TrailerColumns.KEY),
        NAME(PopMoviesDatabase.TRAILERS+"."+TrailerColumns.NAME),
        SITE(PopMoviesDatabase.TRAILERS+"."+TrailerColumns.SITE);

        private String columnName;

        TrailerColumnProjection(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public static String[] getProjection(){
            return new String[]{_ID.columnName,MOVIE_ID.columnName,TRAILER_ID.columnName,KEY.columnName, NAME.columnName, SITE.columnName};
        }
    }

    public TextView setName(TextView trailerTitleTextView) {
        trailerTitleTextView.setText(name);
        return trailerTitleTextView;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(TrailerColumns.KEY, key);
        cv.put(TrailerColumns.NAME, name);
        cv.put(TrailerColumns.SITE, site);
        cv.put(TrailerColumns.TRAILER_ID, id);
        return cv;
    }

    public Uri getVideoUri() {
        return Uri.parse(YOUTUBE_VIDEO_BASE_URI + key);
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The site
     */
    public String getSite() {
        return site;
    }

    /**
     * @param site The site
     */
    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trailer trailer = (Trailer) o;

        if (!id.equals(trailer.id)) return false;
        if (key != null ? !key.equals(trailer.key) : trailer.key != null) return false;
        if (name != null ? !name.equals(trailer.name) : trailer.name != null) return false;
        return !(site != null ? !site.equals(trailer.site) : trailer.site != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (site != null ? site.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}
