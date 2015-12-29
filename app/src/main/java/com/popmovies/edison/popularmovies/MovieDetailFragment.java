package com.popmovies.edison.popularmovies;

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

import com.popmovies.edison.popularmovies.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent caller = getActivity().getIntent();
        Movie movie = (Movie)caller.getParcelableExtra(getString(R.string.parcelable_movie_key));

        TextView movieTitle = (TextView)rootView.findViewById(R.id.details_movie_title);
        movie.setTitle(movieTitle);
        movieTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

        FrameLayout titleFrame = (FrameLayout)rootView.findViewById(R.id.details_title_frame);
        titleFrame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));


        ImageView moviePoster = (ImageView)rootView.findViewById(R.id.details_movie_poster);
        movie.setPoster(getContext(), moviePoster);

        TextView movieOverview = (TextView)rootView.findViewById(R.id.details_movie_overview);
        movie.setOverview(movieOverview);

        TextView movieRating = (TextView)rootView.findViewById(R.id.details_movie_rating);
        movie.setRating(movieRating);

        TextView movieReleaseDate = (TextView)rootView.findViewById(R.id.details_movie_year);
        movie.setReleaseDate(movieReleaseDate);

        return rootView;
    }

}
