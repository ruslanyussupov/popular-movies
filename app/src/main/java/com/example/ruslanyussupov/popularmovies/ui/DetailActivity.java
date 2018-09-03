package com.example.ruslanyussupov.popularmovies.ui;

import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.databinding.ActivityDetailBinding;
import com.example.ruslanyussupov.popularmovies.events.ShareEvent;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;

import org.greenrobot.eventbus.EventBus;


public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(mBinding.toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            Movie movie = getIntent().getParcelableExtra(MovieGridFragment.EXTRA_MOVIE);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(movie.getOriginalTitle());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_share:
                EventBus.getDefault().post(new ShareEvent());
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
