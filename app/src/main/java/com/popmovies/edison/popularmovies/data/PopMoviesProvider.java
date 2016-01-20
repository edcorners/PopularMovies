package com.popmovies.edison.popularmovies.data;

import android.net.Uri;

import com.popmovies.edison.popularmovies.model.Trailer;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Edison on 1/12/2016.
 */
@ContentProvider(authority = PopMoviesProvider.AUTHORITY, database = PopMoviesDatabase.class)
public class PopMoviesProvider {
    public static final String AUTHORITY = "com.popmovies.edison.popularmovies.data.PopMoviesProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String MOVIES = "movies";
        String REVIEW = "reviews";
        String TRAILER = "trailers";
    }

    @TableEndpoint(table = PopMoviesDatabase.MOVIES) public static class Movies {

        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = PopMoviesDatabase.REVIEWS) public static class Reviews {

        @ContentUri(
                path = Path.REVIEW,
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColumns._ID  + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.REVIEW);

        @InexactContentUri(
                name = "REVIEW_ID",
                path = Path.REVIEW + "/#",
                type = "vnd.android.cursor.dir/review",
                whereColumn = ReviewColumns.REVIEW_ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.REVIEW, String.valueOf(id));
        }
    }

    @TableEndpoint(table = PopMoviesDatabase.TRAILERS) public static class Trailers {

        @ContentUri(
                path = Path.TRAILER,
                type = "vnd.android.cursor.dir/trailer",
                defaultSort = TrailerColumns._ID  + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.TRAILER);

        @InexactContentUri(
                name = "TRAILER_ID",
                path = Path.TRAILER + "/#",
                type = "vnd.android.cursor.dir/trailer",
                whereColumn = TrailerColumns.TRAILER_ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.TRAILER, String.valueOf(id));
        }
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

}
