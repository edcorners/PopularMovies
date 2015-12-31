package com.popmovies.edison.popularmovies.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.popmovies.edison.popularmovies.activity.MovieDetailActivity;
import com.popmovies.edison.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Edison on 12/18/2015.
 */
public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

    @Bind(R.id.movie_poster_image_view) ImageView movieImageView;

    public MoviesArrayAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }
        ButterKnife.bind(this, convertView);

        movieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent detailsIntent = new Intent(context, MovieDetailActivity.class);
                detailsIntent.putExtra(context.getString(R.string.parcelable_movie_key), movie);
                context.startActivity(detailsIntent);
            }
        });

        setMoviePoster(movie, movieImageView);

        return convertView;
    }

    private void setMoviePoster(Movie movie, ImageView movieImageView) {
        Uri posterUri = movie.getPosterUri();
        if(posterUri != null) {
            Picasso.with(getContext()).
                    load(posterUri.toString())
                    .placeholder(R.drawable.ic_no_poster)
                    .error(R.drawable.ic_no_poster)
                    .into(movieImageView);
        }else{
            Picasso.with(getContext()).
                    load(R.drawable.ic_no_poster)
                    .into(movieImageView);
        }
    }
}
