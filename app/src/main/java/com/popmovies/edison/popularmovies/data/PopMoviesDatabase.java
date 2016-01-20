package com.popmovies.edison.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Edison on 1/12/2016.
 */
@Database(version = PopMoviesDatabase.VERSION)
public final class PopMoviesDatabase {
    public static final int VERSION = 1;

    @Table(MovieColumns.class) public static final String MOVIES = "movies";
    @Table(TrailerColumns.class) public static final String TRAILERS = "trailers";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
}
