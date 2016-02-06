package com.popmovies.edison.popularmovies.data;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.activity.async.FetchMoviesTask;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.PagedReviewList;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Edison on 2/5/2016.
 */
public class PopMoviesDatabaseServices {

    private static final int UPDATE_FREQ_IN_MILLIS = 10800000; //3 hours
    private Context mContext;
    private final String LOG_TAG = PopMoviesDatabaseServices.class.getSimpleName();
    private String favoritesString;

    public PopMoviesDatabaseServices(Context context) {
        this.mContext = context;
        favoritesString = mContext.getResources().getString(R.string.pref_sort_by_favorites);
    }

    public void updateReviewsAndTrailers(PagedReviewList pagedReviewList, PagedTrailerList pagedTrailerList, String updateKey) {
        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();

        batchUpserts.addAll(getReviewsUpserts(pagedReviewList));
        batchUpserts.addAll(getTrailersUpserts(pagedTrailerList));
        String emptySortBy = "";
        batchUpserts.add(createUpdateLogUpsertOperation(emptySortBy, updateKey));

        try {
            ContentProviderResult[] results = mContext.getContentResolver().applyBatch(PopMoviesProvider.AUTHORITY, batchUpserts);
            Log.v(LOG_TAG, results.toString());
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch reviews and trailers update", e);
        }
    }

    private ArrayList<ContentProviderOperation> getTrailersUpserts(PagedTrailerList trailerList) {
        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();
        ContentProviderOperation.Builder builder = null;

        if(trailerList != null) {
            for (ContentValues contentValues : trailerList.toContentValues()) {
                String trailerId = (String) contentValues.get(TrailerColumns.TRAILER_ID);
                Cursor cursor = mContext.getContentResolver().query(PopMoviesProvider.Trailers.withTrailerId(trailerId),
                        null,
                        null,
                        null,
                        null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        builder = ContentProviderOperation.newUpdate(PopMoviesProvider.Trailers.withTrailerId(trailerId)).withValues(contentValues);
                    } else {
                        builder = ContentProviderOperation.newInsert(PopMoviesProvider.Trailers.CONTENT_URI).withValues(contentValues);
                    }
                    cursor.close();
                }
                batchUpserts.add(builder.build());
            }
        }
        return batchUpserts;
    }

    private ArrayList<ContentProviderOperation> getReviewsUpserts(PagedReviewList reviewList) {
        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();
        ContentProviderOperation.Builder builder = null;

        if(reviewList != null) {
            for (ContentValues contentValues : reviewList.toContentValues()) {
                String reviewId = (String) contentValues.get(ReviewColumns.REVIEW_ID);
                Cursor cursor = mContext.getContentResolver().query(PopMoviesProvider.Reviews.withReviewId(reviewId),
                        null,
                        null,
                        null,
                        null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        builder = ContentProviderOperation.newUpdate(PopMoviesProvider.Reviews.withReviewId(reviewId)).withValues(contentValues);
                    } else {
                        builder = ContentProviderOperation.newInsert(PopMoviesProvider.Reviews.CONTENT_URI).withValues(contentValues);
                    }
                    cursor.close();
                }
                batchUpserts.add(builder.build());
            }
        }
        return batchUpserts;
    }

    public void updateMovies(String sortBy, PagedMovieList pagedMovieList) {
        mContext.getContentResolver().delete(PopMoviesProvider.SortingAttributes.withPreferenceCategory(sortBy), null, null);

        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();

        batchUpserts.addAll(createMovieUpsertOperations(pagedMovieList));
        batchUpserts.addAll(createSortingAttributeInserts(sortBy, pagedMovieList));
        batchUpserts.add(createUpdateLogUpsertOperation(sortBy, PopMoviesDatabase.MOVIES));

        try {
            mContext.getContentResolver().applyBatch(PopMoviesProvider.AUTHORITY, batchUpserts);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch movie insert", e);
        }
    }

    private ContentProviderOperation createUpdateLogUpsertOperation(String sortBy, String itemKey) {
        ContentProviderOperation.Builder builder;
        ContentValues updateLogContentValues = new ContentValues();
        updateLogContentValues.put(UpdateLogColumns.ITEM_KEY, itemKey);
        updateLogContentValues.put(UpdateLogColumns.SORTING_ATTRIBUTE, sortBy);
        updateLogContentValues.put(UpdateLogColumns.LAST_UPDATE, Utility.dateTimeFormat.format(new Date()));

        String selection = UpdateLogColumns.SORTING_ATTRIBUTE + " =? AND " + UpdateLogColumns.ITEM_KEY + " =?";
        String[] selectionArgs = {sortBy, itemKey};
        Cursor cursor = mContext.getContentResolver().query(PopMoviesProvider.UpdateLogs.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            builder = ContentProviderOperation.newUpdate(PopMoviesProvider.UpdateLogs.CONTENT_URI).withSelection(selection, selectionArgs).withValues(updateLogContentValues);
        } else {
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.UpdateLogs.CONTENT_URI).withValues(updateLogContentValues);
        }
        cursor.close();

        return builder.build();
    }

    private ArrayList<ContentProviderOperation> createSortingAttributeInserts(String sortBy, PagedMovieList pagedMovieList) {
        ArrayList<ContentProviderOperation> batchInserts = new ArrayList<>();
        ContentProviderOperation.Builder builder;
        for (ContentValues contentValues : pagedMovieList.toSortingAttributesContentValues(sortBy)) {
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.SortingAttributes.CONTENT_URI).withValues(contentValues);
            batchInserts.add(builder.build());
        }
        return batchInserts;
    }

    private ArrayList<ContentProviderOperation> createMovieUpsertOperations(PagedMovieList pagedMovieList) {
        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();
        ContentProviderOperation.Builder builder = null;
        for (ContentValues contentValues : pagedMovieList.toMovieContentValues()) {
            long movieId = (long) contentValues.get(MovieColumns.MOVIE_ID);
            Cursor cursor = mContext.getContentResolver().query(PopMoviesProvider.Movies.withMovieId(movieId),
                    null,
                    null,
                    null,
                    null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    builder = ContentProviderOperation.newUpdate(PopMoviesProvider.Movies.withMovieId(movieId)).withValues(contentValues);
                } else {
                    builder = ContentProviderOperation.newInsert(PopMoviesProvider.Movies.CONTENT_URI).withValues(contentValues);
                }
                cursor.close();
            }
            batchUpserts.add(builder.build());
        }
        return batchUpserts;
    }

    public boolean isUpdateTime(String sortBy, String itemKey) {
        boolean updateTime = true;
        sortBy = sortBy == null ? "": sortBy;
        String selection = UpdateLogColumns.SORTING_ATTRIBUTE + " =? AND " + UpdateLogColumns.ITEM_KEY + " =?";
        String[] selectionArgs = {sortBy, itemKey};

        Cursor cursor = mContext.getContentResolver().query(PopMoviesProvider.UpdateLogs.CONTENT_URI, null, selection, selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                int indexForDate = cursor.getColumnIndex(UpdateLogColumns.LAST_UPDATE);
                try {
                    Date lastUpdate = Utility.dateTimeFormat.parse(cursor.getString(indexForDate));
                    long lastUpdateMillis = lastUpdate.getTime();
                    long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
                    updateTime = currentTimeMillis - lastUpdateMillis >= UPDATE_FREQ_IN_MILLIS;
                    Log.v(LOG_TAG, "Last Update: " + Utility.dateTimeFormat.format(lastUpdate) + " in millis: " + lastUpdateMillis);
                    Log.v(LOG_TAG, "Actual Time: " + Utility.dateTimeFormat.format(Calendar.getInstance().getTime()) + " in millis: " + currentTimeMillis);
                    Log.v(LOG_TAG, "Diff: " + (currentTimeMillis - lastUpdateMillis));
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error verifying last update time", e);
                }

            }
            cursor.close();
        }
        return updateTime;
    }

    public void insertFavorite(Movie movie){
        Cursor sequenceCursor = mContext.getContentResolver().query(PopMoviesProvider.SortingAttributes.CONTENT_URI,
                new String[]{"MAX(" + SortingAttributesColumns.POSITION + ")"},
                SortingAttributesColumns.PREFERENCE_CATEGORY + " =?",
                new String[]{favoritesString},
                null);
        sequenceCursor.moveToNext();
        int position = sequenceCursor.getInt(0);
        sequenceCursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SortingAttributesColumns.MOVIE_ID, movie.getId());
        contentValues.put(SortingAttributesColumns.PREFERENCE_CATEGORY, favoritesString);
        contentValues.put(SortingAttributesColumns.POSITION, position+1);
        mContext.getContentResolver().insert(PopMoviesProvider.SortingAttributes.CONTENT_URI,contentValues);
    }

    public void deleteFavorite(Movie movie){

        String where = SortingAttributesColumns.MOVIE_ID + " =? AND " + SortingAttributesColumns.PREFERENCE_CATEGORY + " =?";
        String[] selectionArgs = {String.valueOf(movie.getId()), favoritesString};
        mContext.getContentResolver().delete(PopMoviesProvider.SortingAttributes.CONTENT_URI,
                where,
                selectionArgs);
    }

    public boolean isFavorite(Movie movie) {
        String selection = SortingAttributesColumns.PREFERENCE_CATEGORY+ " =? AND " + SortingAttributesColumns.MOVIE_ID + " =?";
        String[] selectionArgs = {favoritesString, String.valueOf(movie.getId())};
        Cursor cursor = mContext.getContentResolver().query(PopMoviesProvider.SortingAttributes.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        cursor.moveToNext();
        boolean recordExists = cursor.getCount() > 0;
        cursor.close();
        return recordExists;
    }
}
