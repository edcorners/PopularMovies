package com.popmovies.edison.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by Edison on 1/14/2016.
 * Defines the columns for a review
 */
public interface ReviewColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";
    @DataType(DataType.Type.TEXT)
    String REVIEW_ID = "review_id";
    @DataType(DataType.Type.INTEGER)
    @References(column = MovieColumns.MOVIE_ID, table = PopMoviesDatabase.MOVIES)
    String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.TEXT)
    String AUTHOR = "author";
    @DataType(DataType.Type.TEXT)
    String CONTENT = "content";
    @DataType(DataType.Type.TEXT)
    String URL = "url";
}
