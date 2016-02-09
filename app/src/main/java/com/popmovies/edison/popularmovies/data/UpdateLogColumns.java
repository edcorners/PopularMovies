package com.popmovies.edison.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by Edison on 1/21/2016.
 * Keeps track of updates to the database
 */
public interface UpdateLogColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    String ITEM_KEY = "item_key"; // identifies a list of updatable elements e.g. "movie", "trailer-1231" where 1231 is a movie id

    @DataType(DataType.Type.TEXT)
    String SORTING_ATTRIBUTE = "sorting_attribute"; // can be popularity.desc, vote_average.desc or favorites if the item key is "movie"

    @DataType(DataType.Type.TEXT)
    String LAST_UPDATE = "last_update"; // date of the last update
}
