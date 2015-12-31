package com.popmovies.edison.popularmovies.activity.async;

/**
 * Created by Edison on 12/30/2015.
 */
public interface FetchMoviesTaskListener<T> {

    public void onTaskComplete(T result);
}
