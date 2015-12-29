package com.popmovies.edison.popularmovies.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.popmovies.edison.popularmovies.MovieDetail;
import com.popmovies.edison.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Edison on 12/18/2015.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    private static class ViewHolder {
        ImageView movieImageView;
    }

    public MoviesAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }
        ImageView movieImageView = (ImageView) convertView.findViewById(R.id.movie_poster_image_view);
        movieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent detailsIntent = new Intent(context, MovieDetail.class);
                detailsIntent.putExtra(context.getString(R.string.parcelable_movie_key), movie);
                context.startActivity(detailsIntent);
            }
        });

        Picasso.with(getContext()).load(movie.getPosterUri().toString()).into(movieImageView);

        return convertView;
    }
}
