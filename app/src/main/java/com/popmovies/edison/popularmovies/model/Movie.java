package com.popmovies.edison.popularmovies.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.data.MovieColumns;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Edison on 12/18/2015.
 */
public class Movie implements Parcelable {

    private final String LOG_TAG = Movie.class.getSimpleName();
    private static final String RATING_SCALE = "/10";

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("vote_average")
    @Expose
    private double rating;

    @SerializedName("release_date")
    @Expose
    private String releaseDateString;

    // Constructors

    public Movie(long id, String title, String posterPath, String overview, double rating, String releaseDate){
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.rating = rating;
        this.releaseDateString = releaseDate;
    }

    private Movie(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.rating = in.readDouble();
        this.releaseDateString = in.readString();
    }

    public Movie(Cursor cursor){
        int index = cursor.getColumnIndex(MovieColumns.MOVIE_ID);
        this.id = cursor.getLong(index);
        index = cursor.getColumnIndex(MovieColumns.TITLE);
        this.title = cursor.getString(index);
        index = cursor.getColumnIndex(MovieColumns.OVERVIEW);
        this.overview = cursor.getString(index);
        index = cursor.getColumnIndex(MovieColumns.POSTER_PATH);
        this.posterPath = cursor.getString(index);
        index = cursor.getColumnIndex(MovieColumns.RATING);
        this.rating = cursor.getDouble(index);
        index = cursor.getColumnIndex(MovieColumns.RELEASE_DATE);
        this.releaseDateString = cursor.getString(index);
    }

    public enum MovieColumnProjection{
        _ID(PopMoviesDatabase.MOVIES+"."+MovieColumns._ID),
        MOVIE_ID(PopMoviesDatabase.MOVIES+"."+MovieColumns.MOVIE_ID),
        TITLE(PopMoviesDatabase.MOVIES+"."+MovieColumns.TITLE),
        OVERVIEW(PopMoviesDatabase.MOVIES+"."+MovieColumns.OVERVIEW),
        POSTER_PATH(PopMoviesDatabase.MOVIES+"."+MovieColumns.POSTER_PATH),
        RATING(PopMoviesDatabase.MOVIES+"."+MovieColumns.RATING),
        RELEASE_DATE(PopMoviesDatabase.MOVIES+"."+MovieColumns.RELEASE_DATE);

        private String columnName;

        MovieColumnProjection(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public static String[] getProjection(){
            return new String[]{_ID.columnName,MOVIE_ID.columnName,TITLE.columnName,OVERVIEW.columnName, POSTER_PATH.columnName, RATING.columnName, RELEASE_DATE.columnName};
        }
    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeString(releaseDateString);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

    // Business methods

    public Uri getPosterUri() {
        Uri.Builder uriBuilder = null;
        if(posterPath != null){
            uriBuilder = Uri.parse(TMDBAPI.IMAGE_BASE_URL.getValue()).buildUpon()
                    .appendPath(TMDBAPI.W185.getValue())
                    .appendPath(posterPath.substring(1)); //trim escape char
        }
        return uriBuilder != null ? uriBuilder.build() : null;
    }

    public Date getReleaseDate() {
        Date parsedDate = null;
        try {
            parsedDate = Utility.dateFormat.parse(releaseDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public TextView setTitle(TextView movieTitle) {
        movieTitle.setText(this.title);
        return movieTitle;
    }

    public ImageView setPoster(Context context, ImageView poster) {
        Picasso.with(context)
                .load(getPosterUri())
                .placeholder(R.drawable.ic_popcorn_noposter)
                .error(R.drawable.ic_popcorn_noposter)
                .into(poster);
        return poster;
    }

    public TextView setOverview(TextView movieOverview) {
        movieOverview.setText(this.overview);
        return movieOverview;
    }

    public TextView setRating(Context context, TextView movieRating) {
        movieRating.setText(Utility.formatRating(context, this.rating));
        return movieRating;
    }

    public TextView setReleaseDate(TextView movieReleaseDate) {
        String date = this.releaseDateString == null ? "-" : Utility.yearFormat.format(getReleaseDate());
        movieReleaseDate.setText(date);
        return movieReleaseDate;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.MOVIE_ID, id);
        cv.put(MovieColumns.OVERVIEW, overview);
        cv.put(MovieColumns.POSTER_PATH, posterPath);
        cv.put(MovieColumns.RATING, rating);
        cv.put(MovieColumns.TITLE, title);
        cv.put(MovieColumns.RELEASE_DATE, releaseDateString);
        return cv;
    }

    // Object methods


    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", overview='" + overview + '\'' +
                ", rating=" + rating +
                ", releaseDateString='" + releaseDateString + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (id != movie.id) return false;
        if (Double.compare(movie.rating, rating) != 0) return false;
        if (!title.equals(movie.title)) return false;
        if (posterPath != null ? !posterPath.equals(movie.posterPath) : movie.posterPath != null)
            return false;
        if (overview != null ? !overview.equals(movie.overview) : movie.overview != null)
            return false;
        return !(releaseDateString != null ? !releaseDateString.equals(movie.releaseDateString) : movie.releaseDateString != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (posterPath != null ? posterPath.hashCode() : 0);
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        temp = Double.doubleToLongBits(rating);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (releaseDateString != null ? releaseDateString.hashCode() : 0);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
