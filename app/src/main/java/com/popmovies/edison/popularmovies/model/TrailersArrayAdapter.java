package com.popmovies.edison.popularmovies.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.activity.MovieDetailActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Edison on 12/18/2015.
 */
public class TrailersArrayAdapter extends ArrayAdapter<Trailer> {

    @Bind(R.id.trailer_title_text_view)
    TextView trailerTitleTextView;

    public TrailersArrayAdapter(Context context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, parent, false);
        }
        ButterKnife.bind(this, convertView);
        trailer.setName(trailerTitleTextView);
        trailerTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent openVideoIntent = new Intent(Intent.ACTION_VIEW, trailer.getVideoUri());
                context.startActivity(openVideoIntent);
            }
        });


        return convertView;
    }
}
