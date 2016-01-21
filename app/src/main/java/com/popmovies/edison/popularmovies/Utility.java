/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.popmovies.edison.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = sharedPrefs.getString(
                context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_sort_by_popularity));
        return sortBy;
    }

    public static String getPreferredVoteCount(Context context) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String voteCount = sharedPrefs.getString(
                context.getString(R.string.pref_vote_count_key),
                context.getString(R.string.pref_1000_votes));
        return voteCount;
    }

    public static boolean isFavoritesSort(Context context) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = sharedPrefs.getString(
                context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_sort_by_popularity));
        return sortBy.equals(context.getString(R.string.pref_sort_by_favorites));
    }

}