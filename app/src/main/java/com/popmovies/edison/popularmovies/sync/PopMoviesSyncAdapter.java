package com.popmovies.edison.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.data.MovieColumns;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.data.UpdateLogColumns;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.webservice.TMDBWebService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 2/2/2016.
 */
public class PopMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    public final String LOG_TAG = PopMoviesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PopMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    public PopMoviesSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String sortBy = Utility.getPreferredSortOrder(getContext());
        String voteCountGte = "0";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBWebService service = retrofit.create(TMDBWebService.class);

        if (sortBy.equalsIgnoreCase(getContext().getString(R.string.pref_sort_by_rating))) {
            voteCountGte = Utility.getPreferredVoteCount(getContext());
        }
        Call<PagedMovieList> moviesCall = service.getMoviesSortedBy(sortBy, voteCountGte, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedMovieList> response = moviesCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            PagedMovieList pagedMovieList = response.body();

            updateDatabase(sortBy, pagedMovieList);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        Log.d(LOG_TAG, "Sync Complete. ");
    }

    private void updateDatabase(String sortBy, PagedMovieList pagedMovieList) {
        mContentResolver.delete(PopMoviesProvider.SortingAttributes.withPreferenceCategory(sortBy), null, null);

        ArrayList<ContentProviderOperation> batchUpserts = new ArrayList<>();

        batchUpserts.addAll(createMovieUpsertOperations(pagedMovieList));
        batchUpserts.addAll(createSortingAttributeInserts(sortBy, pagedMovieList));

        try {
            mContentResolver.applyBatch(PopMoviesProvider.AUTHORITY, batchUpserts);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
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
            Cursor cursor = mContentResolver.query(PopMoviesProvider.Movies.CONTENT_URI,
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

    private ContentProviderOperation.Builder createUpsertOperation(Uri uri, ContentValues contentValues) {
        ContentProviderOperation.Builder builder = null;
        Cursor cursor = mContentResolver.query(uri,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                builder = ContentProviderOperation.newUpdate(uri).withValues(contentValues);
            } else {
                builder = ContentProviderOperation.newInsert(uri).withValues(contentValues);
            }
            cursor.close();
        }
        return builder;
    }

    /**
     * Helper method to have the sync adapter sync immediately (Taken from sunshine)
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.(Taken from sunshine)
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution (Taken from sunshine)
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * (Taken from sunshine)
     *
     * @param newAccount
     * @param context
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        PopMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context); //Not necessary
    }

    /**
     * (Taken from sunshine)
     * @param context
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
