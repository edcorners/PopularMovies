package com.popmovies.edison.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Edison on 1/12/2016.
 */
@ContentProvider(authority = PopMoviesProvider.AUTHORITY, database = PopMoviesDatabase.class)
public class PopMoviesProvider {
    public static final String AUTHORITY = "com.popmovies.edison.popularmovies.data.PopMoviesProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FAVORITE_MOVIE = "favorite_movie";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = PopMoviesDatabase.FAVORITE_MOVIE) public static class FavoriteMovies {

        @ContentUri(
                path = Path.FAVORITE_MOVIE,
                type = "vnd.android.cursor.dir/list",
                defaultSort = FavoriteMovieColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITE_MOVIE);;
    }
}
