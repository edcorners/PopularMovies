package com.popmovies.edison.popularmovies.activity.async;

/**
 * Created by Edison on 12/30/2015.
 */
public interface FetchReviewsTaskListener<T> {

    void onFetchReviewsTaskComplete(T result);
}
