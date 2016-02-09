package com.popmovies.edison.popularmovies.activity.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabase;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabaseServices;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.webservice.TMDBAPI;
import com.popmovies.edison.popularmovies.webservice.TMDBWebService;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 12/30/2015.
 * Movies are fetched from TMDB only if it's update time. (See PopMoviesDatabaseServices isUpdateTime method)
 * NOTE: Sync adapter was implemented instead but didn't work because of the "40 transactions every 10 seconds" restriction of the API.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Context context;

    /**
     * Default constructor
     * @param context application context
     */
    public FetchMoviesTask(Context context) {
        this.context = context;
    }

    /**
     * doInBackground implementation
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.v(LOG_TAG, "FetchMoviesTask started");
        String sortBy = Utility.getPreferredSortOrder(context);
        String voteCountGte = "0";
        PopMoviesDatabaseServices databaseServices = new PopMoviesDatabaseServices(context);

        if (databaseServices.isUpdateTime(sortBy, PopMoviesDatabase.MOVIES)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDBAPI.BASE_URL.getValue())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TMDBWebService service = retrofit.create(TMDBWebService.class);

            if (sortBy.equalsIgnoreCase(context.getString(R.string.pref_sort_by_rating))) {// Movies sorted by rating don't make much sense without a vote count
                voteCountGte = Utility.getPreferredVoteCount(context);
            }
            Call<PagedMovieList> moviesCall = service.getMoviesSortedBy(sortBy, voteCountGte, BuildConfig.TMDB_API_KEY);

            try {
                Response<PagedMovieList> response = moviesCall.execute();
                Log.v(LOG_TAG, response.raw().toString());
                PagedMovieList pagedMovieList = response.body();

                databaseServices.updateMovies(sortBy, pagedMovieList);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            }
        }
        return null;
    }
}