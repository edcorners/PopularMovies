package com.popmovies.edison.popularmovies.activity.async;

/**
 * Created by Edison on 12/30/2015.
 */
public interface FetchTrailersTaskListener<T> {

    public void onFetchTrailersTaskComplete(T result);
}
