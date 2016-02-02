package com.popmovies.edison.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.activity.fragment.MainActivityFragment;
import com.popmovies.edison.popularmovies.activity.fragment.MovieDetailFragment;
import com.popmovies.edison.popularmovies.model.Movie;
import com.squareup.okhttp.OkHttpClient;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFTAG";
    @Bind(R.id.movie_detail_container) @Nullable
    FrameLayout detailContainerFrameLayout;
    boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        if(detailContainerFrameLayout != null){
            twoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG).
                        commit();
            }
        }else{
            twoPane = false;
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if(twoPane){
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.parcelable_movie_key), movie);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else {
            Intent detailsIntent = new Intent(this, MovieDetailActivity.class);
            detailsIntent.putExtra(getString(R.string.parcelable_movie_key), movie);
            startActivity(detailsIntent);
        }
    }
}
