package com.popmovies.edison.popularmovies.activity.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.service.TMDBService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 12/30/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private FetchMoviesTaskListener<List<Movie>> listener;
    private Context context;

    public FetchMoviesTask(Context context, FetchMoviesTaskListener<List<Movie>> listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        String sortBy = params[0];
        String voteCountGte = "0";
        List<Movie> movies = null;

        PagedMovieList pagedMovieList = new PagedMovieList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBService service = retrofit.create(TMDBService.class);

        if(sortBy.equalsIgnoreCase(context.getString(R.string.pref_sort_by_rating))){
            voteCountGte = params[1];
        }
        Call<PagedMovieList> moviesCall = service.getMoviesSortedBy(sortBy,voteCountGte, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedMovieList> response = moviesCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            pagedMovieList = response.body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return pagedMovieList.getResults();
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        listener.onTaskComplete(movies);
    }

}