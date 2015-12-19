package com.popmovies.edison.popularmovies.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.popmovies.edison.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edison on 12/18/2015.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    public MoviesAdapter(Context context, List<Movie> movies){
        super(context,0,movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);

        ImageView image = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(movie.getPosterUri().toString()).into(image);

        return rootView;
    }
}
