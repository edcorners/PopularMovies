package com.popmovies.edison.popularmovies.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.popmovies.edison.popularmovies.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Edison on 12/18/2015.
 */
public class Movie implements Parcelable {
    private long id;
    private String title;
    private Uri posterUri;
    private String overview;
    private double rating;
    private String releaseDate;

    // Constructors

    public Movie(long id, String title, Uri posterUri, String overview, double rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterUri = posterUri;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in){
        this.id = in.readLong();
        this.title = in.readString();
        this.posterUri = Uri.parse(in.readString());
        this.overview = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
    }

    public Movie(JSONObject jsonMovie) throws JSONException {
        this.id = jsonMovie.getLong(MoviesJSON.ID.getKey());
        this.title = jsonMovie.getString(MoviesJSON.ORIGINAL_TITLE.getKey());

        Uri.Builder uriBuilder = Uri.parse(TMDBAPI.IMAGE_BASE_URL.getValue()).buildUpon()
                .appendPath(TMDBAPI.W185.getValue())//TODO Parametrize
                .appendPath(jsonMovie.getString(MoviesJSON.POSTER_PATH.getKey()).substring(1));
        this.posterUri = uriBuilder.build();
        this.overview = jsonMovie.getString(MoviesJSON.OVERVIEW.getKey());
        this.rating = jsonMovie.getDouble(MoviesJSON.RATING.getKey());
        this.releaseDate = jsonMovie.getString(MoviesJSON.RELEASE_DATE.getKey());
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
        dest.writeString(posterUri.toString());
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

    // Object methods

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterUri=" + posterUri +
                ", overview='" + overview + '\'' +
                ", rating=" + rating +
                ", releaseDate='" + releaseDate + '\'' +
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
        if (posterUri != null ? !posterUri.equals(movie.posterUri) : movie.posterUri != null)
            return false;
        if (overview != null ? !overview.equals(movie.overview) : movie.overview != null)
            return false;
        return !(releaseDate != null ? !releaseDate.equals(movie.releaseDate) : movie.releaseDate != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + (posterUri != null ? posterUri.hashCode() : 0);
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        temp = Double.doubleToLongBits(rating);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
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

    public Uri getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(Uri posterUri) {
        this.posterUri = posterUri;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
