package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.model.Movie;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    @Bind(R.id.details_movie_title) TextView movieTitle;
    @Bind(R.id.details_title_frame) FrameLayout titleFrame;
    @Bind(R.id.details_movie_poster) ImageView moviePoster;
    @Bind(R.id.details_movie_overview) TextView movieOverview;
    @Bind(R.id.details_movie_rating) TextView movieRating;
    @Bind(R.id.details_movie_year) TextView movieReleaseDate;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent caller = getActivity().getIntent();
        Movie movie = (Movie)caller.getParcelableExtra(getString(R.string.parcelable_movie_key));

        ButterKnife.bind(this,rootView);

        movie.setTitle(movieTitle);
        movieTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        titleFrame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        movie.setPoster(getContext(), moviePoster);
        movie.setOverview(movieOverview);
        movie.setRating(movieRating);
        movie.setReleaseDate(movieReleaseDate);

        return rootView;
    }

}
