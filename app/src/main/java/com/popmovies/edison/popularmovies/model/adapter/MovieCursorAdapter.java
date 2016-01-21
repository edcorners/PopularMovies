package com.popmovies.edison.popularmovies.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.activity.MovieDetailActivity;
import com.popmovies.edison.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Edison on 1/20/2016.
 */
public class MovieCursorAdapter extends CursorAdapter {

    @Bind(R.id.movie_poster_image_view)
    ImageView movieImageView;

    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ButterKnife.bind(this, view);
        final Movie movie = new Movie(cursor);
        movieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent(context, MovieDetailActivity.class);
                detailsIntent.putExtra(context.getString(R.string.parcelable_movie_key), movie);
                context.startActivity(detailsIntent);
            }
        });
        movie.setPoster(context, movieImageView);
    }
}
