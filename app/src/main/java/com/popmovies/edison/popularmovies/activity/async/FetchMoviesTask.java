package com.popmovies.edison.popularmovies.activity.async;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.data.MovieColumns;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.data.UpdateLogColumns;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.service.TMDBService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 12/30/2015.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

    public static final int UPDATE_FREQ_IN_MILLIS = 120000; // 2 minutes 10800000; 3 hours
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private FetchMoviesTaskListener<Void> listener;
    private Context context;
    boolean updated = false;

    public FetchMoviesTask(Context context, FetchMoviesTaskListener<Void> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String sortBy = Utility.getPreferredSortOrder(context);
        String voteCountGte = "0";
        List<Movie> movies = null;

        if (isUpdateTime()) {
            PagedMovieList pagedMovieList = new PagedMovieList();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDBAPI.BASE_URL.getValue())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TMDBService service = retrofit.create(TMDBService.class);

            if (sortBy.equalsIgnoreCase(context.getString(R.string.pref_sort_by_rating))) {
                voteCountGte = Utility.getPreferredVoteCount(context);
            }
            Call<PagedMovieList> moviesCall = service.getMoviesSortedBy(sortBy, voteCountGte, BuildConfig.TMDB_API_KEY);

            try {
                Response<PagedMovieList> response = moviesCall.execute();
                Log.v(LOG_TAG, response.raw().toString());
                pagedMovieList = response.body();

                updateDatabase(sortBy, pagedMovieList);
                this.updated = true;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            }
        }
        return null;
    }

    private void updateDatabase(String sortBy, PagedMovieList pagedMovieList) {
        int deletedSortingAttributes = context.getContentResolver().delete(PopMoviesProvider.SortingAttributes.withPreferenceCategory(sortBy), null, null);

        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();

        ContentProviderOperation.Builder builder;
        batchUpserts.addAll(createMovieUpsertOperations(pagedMovieList));
        batchUpserts.addAll(createSortingAttributeInserts(sortBy, pagedMovieList));
        batchUpserts.add(createUpdateLogUpsertOperation(sortBy));

        try {
            context.getContentResolver().applyBatch(PopMoviesProvider.AUTHORITY, batchUpserts);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    private ContentProviderOperation createUpdateLogUpsertOperation(String sortBy) {
        ContentProviderOperation.Builder builder;Uri upsertUpdateLogUri = PopMoviesProvider.UpdateLogs.withSortingAttribute(sortBy);
        ContentValues updateLogContentValues = new ContentValues();
        updateLogContentValues.put(UpdateLogColumns.SORTING_ATTRIBUTE, sortBy);
        updateLogContentValues.put(UpdateLogColumns.LAST_UPDATE, Utility.dateTimeFormat.format(new Date()));
        builder = createUpsertOperation(upsertUpdateLogUri,updateLogContentValues);
        return builder.build();
    }

    private ArrayList<ContentProviderOperation> createSortingAttributeInserts(String sortBy, PagedMovieList pagedMovieList) {
        ArrayList<ContentProviderOperation> batchInserts = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation.Builder builder;
        for (ContentValues contentValues : pagedMovieList.toSortingAttributesContentValues(sortBy)) {
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.SortingAttributes.CONTENT_URI).withValues(contentValues);
            batchInserts.add(builder.build());
        }
        return batchInserts;
    }

    private ArrayList<ContentProviderOperation> createMovieUpsertOperations(PagedMovieList pagedMovieList) {
        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation.Builder builder;
        for (ContentValues contentValues : pagedMovieList.toMovieContentValues()) {
            long movieId = (long) contentValues.get(MovieColumns.MOVIE_ID);
            Uri upsertMovieUri = PopMoviesProvider.Movies.withMovieId(movieId);
            builder = createUpsertOperation(upsertMovieUri,contentValues);
            batchUpserts.add(builder.build());
        }
        return batchUpserts;
    }

    private ContentProviderOperation.Builder createUpsertOperation(Uri uri, ContentValues contentValues){
        ContentProviderOperation.Builder builder;
        Cursor cursor = context.getContentResolver().query(uri,
                null,
                null,
                null,
                null);
        if(cursor.getCount() > 0){
            builder = ContentProviderOperation.newUpdate(uri).withValues(contentValues);
        }else {
            builder = ContentProviderOperation.newInsert(uri).withValues(contentValues);
        }
        cursor.close();
        return builder;
    }

    private boolean isUpdateTime() {
        boolean updateTime = true;

        String preferredSort = Utility.getPreferredSortOrder(context);
        Cursor cursor = context.getContentResolver().query(PopMoviesProvider.UpdateLogs.withSortingAttribute(preferredSort), null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            int indexForDate = cursor.getColumnIndex(UpdateLogColumns.LAST_UPDATE);
            Date lastUpdate = null;
            try {
                lastUpdate = Utility.dateTimeFormat.parse(cursor.getString(indexForDate));
                long lastUpdateMillis = lastUpdate.getTime();
                long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
                updateTime = currentTimeMillis - lastUpdateMillis >= UPDATE_FREQ_IN_MILLIS;
                Log.v(LOG_TAG, "Last Update: "+ Utility.dateTimeFormat.format(lastUpdate) + " in millis: "+lastUpdateMillis);
                Log.v(LOG_TAG, "Actual Time: "+ Utility.dateTimeFormat.format(Calendar.getInstance().getTime()) + " in millis: "+currentTimeMillis);
                Log.v(LOG_TAG, "Diff: "+ (currentTimeMillis - lastUpdateMillis));
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error verifying last update time", e);
            }

        }
        cursor.close();
        return updateTime;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(updated)
            listener.onTaskComplete(null);
    }

}