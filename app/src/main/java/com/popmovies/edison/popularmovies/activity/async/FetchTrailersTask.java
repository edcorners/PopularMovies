package com.popmovies.edison.popularmovies.activity.async;

import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.webservice.TMDBWebService;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 1/14/2016.
 */
public class FetchTrailersTask extends AsyncTask<String, Void, PagedTrailerList> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private FetchTrailersTaskListener<PagedTrailerList> listener;

    public FetchTrailersTask(FetchTrailersTaskListener<PagedTrailerList> listener){
        this.listener = listener;
    }

    @Override
    protected PagedTrailerList doInBackground(String... params) {
        int movieId = Integer.valueOf(params[0]);

        PagedTrailerList pagedTrailerList = new PagedTrailerList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBWebService service = retrofit.create(TMDBWebService.class);

        Call<PagedTrailerList> videosCall = service.getVideos(movieId, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedTrailerList> response = videosCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            pagedTrailerList = response.body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return pagedTrailerList;
    }

    @Override
    protected void onPostExecute(PagedTrailerList trailers) {
        super.onPostExecute(trailers);
        listener.onFetchTrailersTaskComplete(trailers);
    }
}
