package com.popmovies.edison.popularmovies.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.activity.fragment.MovieDetailFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Edison on 1/15/2016.
 */
public class ReviewsArrayAdapter extends ArrayAdapter<Review> {

    private final String LOG_TAG = ReviewsArrayAdapter.class.getSimpleName();

    @Bind(R.id.review_content_text_view)
    TextView reviewContentTextView;
    @Bind(R.id.review_author_text_view)
    TextView reviewAuthorTextView;
    @Bind(R.id.review_entry_linear_layout )
    LinearLayout entryLinearLayout;

    public ReviewsArrayAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
            Log.v(LOG_TAG, " Review Count:"+getCount());
        }
        ButterKnife.bind(this, convertView);
        review.setContent(reviewContentTextView);
        review.setAuthor(reviewAuthorTextView);

        return convertView;
    }
}
