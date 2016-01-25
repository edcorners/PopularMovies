package com.popmovies.edison.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Edison on 1/21/2016.
 */
public interface SortingAttributesColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    String PREFERENCE_CATEGORY = "preference_category";

    @DataType(DataType.Type.INTEGER)
    String POSITION = "position";
}
