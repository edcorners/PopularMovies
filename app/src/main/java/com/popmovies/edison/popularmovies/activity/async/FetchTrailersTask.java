package com.popmovies.edison.popularmovies.activity.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.model.Trailer;
import com.popmovies.edison.popularmovies.service.TMDBService;

import java.io.IOException;
import java.util.List;

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
    private Context context;

    public FetchTrailersTask(Context context, FetchTrailersTaskListener<PagedTrailerList> listener){
        this.context = context;
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
        TMDBService service = retrofit.create(TMDBService.class);

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
