package com.popmovies.edison.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Edison on 1/12/2016.
 */
@Database(version = PopMoviesDatabase.VERSION)
public final class PopMoviesDatabase {
    public static final int VERSION = 1;

    @Table(FavoriteMovieColumns.class) public static final String FAVORITE_MOVIE = "favorite_movie";
}
