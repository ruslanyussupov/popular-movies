package com.example.ruslanyussupov.popularmovies.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.title_tv)TextView mTitleTv;
    @BindView(R.id.poster_iv)ImageView mPosterIv;
    @BindView(R.id.release_date_tv)TextView mReleaseDateTv;
    @BindView(R.id.user_rating_tv)TextView mVoteAverageTv;
    @BindView(R.id.overview_tv)TextView mOverviewTv;
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.EXTRA_MOVIE)) {

            Movie movie = intent.getParcelableExtra(MainActivity.EXTRA_MOVIE);

            Picasso.with(this)
                    .load(NetworkUtils.buildMoviePosterUrlPath(movie.getPosterPath()))
                    .error(R.drawable.poster_placeholder)
                    .placeholder(R.drawable.poster_error)
                    .into(mPosterIv);

            mTitleTv.setText(movie.getOriginalTitle());
            mReleaseDateTv.setText(movie.getReleaseDate());
            mVoteAverageTv.setText(String.valueOf(movie.getVoteAverage()));
            mOverviewTv.setText(movie.getOverview());

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
