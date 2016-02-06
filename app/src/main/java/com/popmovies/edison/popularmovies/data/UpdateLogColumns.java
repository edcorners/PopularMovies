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
    String ITEM_KEY = "item_key";

    @DataType(DataType.Type.TEXT)
    String SORTING_ATTRIBUTE = "sorting_attribute";

    @DataType(DataType.Type.TEXT)
    String LAST_UPDATE = "last_update";
}
