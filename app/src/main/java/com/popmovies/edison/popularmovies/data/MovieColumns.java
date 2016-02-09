package com.popmovies.edison.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by Edison on 1/12/2016.
 * Defines the columns for a movie
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    @Unique
    String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String TITLE = "title";

    @DataType(DataType.Type.TEXT)
    String POSTER_PATH = "poster_path";

    @DataType(DataType.Type.TEXT)
    String OVERVIEW = "overview";

    @DataType(DataType.Type.REAL)
    String RATING = "vote_average";

    @DataType(DataType.Type.TEXT)
    String RELEASE_DATE = "release_date";
}
