package com.popmovies.edison.popularmovies.data;

import android.net.Uri;

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
        String SORTING_ATTRIBUTES = "sorting_attributes";
        String UPDATE_LOGS = "update_logs";
    }

    @TableEndpoint(table = PopMoviesDatabase.MOVIES) public static class Movies {

        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withMovieId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }

        @InexactContentUri(
                name = "MOVIES_WITH_SORT",
                path = Path.MOVIES +"/"+ Path.SORTING_ATTRIBUTES + "/*",
                type = "vnd.android.cursor.dir/movie",
                whereColumn = PopMoviesDatabase.SORTING_ATTRIBUTES+"."+SortingAttributesColumns.PREFERENCE_CATEGORY,
                pathSegment = 2,
                join = "INNER JOIN "+PopMoviesDatabase.SORTING_ATTRIBUTES+" ON "+
                        PopMoviesDatabase.MOVIES+"."+MovieColumns.MOVIE_ID + " = " +
                        PopMoviesDatabase.SORTING_ATTRIBUTES+"."+SortingAttributesColumns.MOVIE_ID,
                defaultSort = PopMoviesDatabase.SORTING_ATTRIBUTES+"."+SortingAttributesColumns.POSITION + " ASC"
        )
        public static Uri withSortingAttribute(String sort){
            return buildUri(Path.MOVIES, Path.SORTING_ATTRIBUTES, sort);
        }

    }

    @TableEndpoint(table = PopMoviesDatabase.REVIEWS) public static class Reviews {

        @ContentUri(
                path = Path.REVIEW,
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColumns._ID  + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.REVIEW);

        @InexactContentUri(
                name = "REVIEW_BY_MOVIE_ID",
                path = Path.MOVIES + "/#/"+Path.REVIEW,
                type = "vnd.android.cursor.dir/review",
                whereColumn = ReviewColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withMovieId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id), Path.REVIEW);
        }
    }

    @TableEndpoint(table = PopMoviesDatabase.TRAILERS) public static class Trailers {

        @ContentUri(
                path = Path.TRAILER,
                type = "vnd.android.cursor.dir/trailer",
                defaultSort = TrailerColumns._ID  + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.TRAILER);

        @InexactContentUri(
                name = "TRAILER_BY_MOVIE_ID",
                path = Path.MOVIES + "/#/"+Path.TRAILER,
                type = "vnd.android.cursor.dir/trailer",
                whereColumn = TrailerColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withMovieId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id), Path.TRAILER);
        }

    }

    @TableEndpoint(table = PopMoviesDatabase.UPDATE_LOG) public static class UpdateLogs {

        @ContentUri(
                path = Path.UPDATE_LOGS,
                type = "vnd.android.cursor.dir/update_log")
        public static final Uri CONTENT_URI = buildUri(Path.UPDATE_LOGS);

        @InexactContentUri(
                name = "BY_SORTING_ATTRIBUTE",
                path = Path.UPDATE_LOGS + "/" +Path.SORTING_ATTRIBUTES + "/*",
                type = "vnd.android.cursor.item/update_log",
                whereColumn = UpdateLogColumns.SORTING_ATTRIBUTE,
                pathSegment = 2
        )
        public static Uri withSortingAttribute(String sort){
            return buildUri(Path.UPDATE_LOGS, Path.SORTING_ATTRIBUTES, sort);
        }

    }

    @TableEndpoint(table = PopMoviesDatabase.SORTING_ATTRIBUTES) public static class SortingAttributes {

        @ContentUri(
                path = Path.SORTING_ATTRIBUTES,
                type = "vnd.android.cursor.dir/sorting_attributes")
        public static final Uri CONTENT_URI = buildUri(Path.SORTING_ATTRIBUTES);

        @InexactContentUri(
                name = "BY_PREFERENCE_CATEGORY",
                path = Path.SORTING_ATTRIBUTES + "/*",
                type = "vnd.android.cursor.dir/sorting_preference",
                whereColumn = SortingAttributesColumns.PREFERENCE_CATEGORY,
                pathSegment = 1
        )
        public static Uri withPreferenceCategory(String category){
            return buildUri(Path.SORTING_ATTRIBUTES, category);
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
