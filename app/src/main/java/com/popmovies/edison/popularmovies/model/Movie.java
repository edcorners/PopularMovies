package com.popmovies.edison.popularmovies.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Edison on 12/18/2015.
 */
public class Movie implements Parcelable {

    public static final String NO_POSTER_W185 = "https://assets.tmdb.org/assets/7f29bd8b3370c71dd379b0e8b570887c/images/no-poster-w185-v2.png";
    private final String LOG_TAG = Movie.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
    private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private static final String RATING_SCALE = "/10";

    private long id;
    private String title;
    private Uri posterUri;
    private String overview;
    private double rating;
    private Date releaseDate;

    // Constructors

    public Movie(long id, String title, Uri posterUri, String overview, double rating, String releaseDate){
        this.id = id;
        this.title = title;
        this.posterUri = posterUri;
        this.overview = overview;
        this.rating = rating;
        try {
            this.releaseDate = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
            this.releaseDate = null;
        }
    }

    private Movie(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.posterUri = Uri.parse(in.readString());
        this.overview = in.readString();
        this.rating = in.readDouble();
        try {
            this.releaseDate = dateFormat.parse(in.readString());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
            this.releaseDate = null;
        }
    }

    public Movie(JSONObject jsonMovie) throws JSONException{
        this.id = jsonMovie.getLong(MoviesJSON.ID.getKey());
        this.title = jsonMovie.getString(MoviesJSON.ORIGINAL_TITLE.getKey());
        String posterPath = jsonMovie.getString(MoviesJSON.POSTER_PATH.getKey());
        Uri.Builder uriBuilder = null;
        if(!posterPath.equalsIgnoreCase("null")){
            uriBuilder = Uri.parse(TMDBAPI.IMAGE_BASE_URL.getValue()).buildUpon()
                    .appendPath(TMDBAPI.W185.getValue())
                    .appendPath(posterPath.substring(1)); //trim escape char
        }else{
            uriBuilder = Uri.parse(NO_POSTER_W185).buildUpon();
        }
        this.posterUri = uriBuilder.build();
        this.overview = jsonMovie.getString(MoviesJSON.OVERVIEW.getKey());
        this.rating = jsonMovie.getDouble(MoviesJSON.RATING.getKey());
        String date = jsonMovie.getString(MoviesJSON.RELEASE_DATE.getKey());
        Log.v(LOG_TAG, "date to parse "+date);
        try {
            this.releaseDate = dateFormat.parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
            this.releaseDate = null;
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
        dest.writeString(posterUri.toString());
        dest.writeString(overview);
        dest.writeDouble(rating);
        String date = releaseDate == null ? "" : dateFormat.format(releaseDate);
        dest.writeString(date);
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

    public TextView setTitle(TextView movieTitle) {
        movieTitle.setText(this.title);
        return movieTitle;
    }

    public ImageView setPoster(Context context, ImageView poster) {
        Picasso.with(context).load(posterUri.toString()).into(poster);
        return poster;
    }

    public TextView setOverview(TextView movieOverview) {
        movieOverview.setText(this.overview);
        return movieOverview;
    }

    public TextView setRating(TextView movieRating) {
        movieRating.setText(this.rating + RATING_SCALE);
        return movieRating;
    }

    public TextView setReleaseDate(TextView movieReleaseDate) {
        String date = this.releaseDate == null ? "-" : yearFormat.format(this.releaseDate);
        movieReleaseDate.setText(date);
        return movieReleaseDate;
    }

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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

}
