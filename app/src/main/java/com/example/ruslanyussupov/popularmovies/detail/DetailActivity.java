package com.example.ruslanyussupov.popularmovies.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.Result;
import com.example.ruslanyussupov.popularmovies.databinding.ActivityDetailBinding;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.list.MovieGridFragment;



public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    private DetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(mBinding.toolbar);

        ActionBar actionBar = getSupportActionBar();

        Movie movie = getIntent().getParcelableExtra(MovieGridFragment.EXTRA_MOVIE);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(movie.getOriginalTitle());
        }

        DetailViewModelFactory viewModelFactory = new DetailViewModelFactory(movie);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel.class);

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
                onShare();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void onShare() {
        mViewModel.getVideosResultLiveData().observe(this, result -> {
            if (result.state == Result.State.SUCCESS) {
                if (result.data == null || result.data.isEmpty()) {
                    Toast.makeText(DetailActivity.this, getString(R.string.nothing_to_share),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share_subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, result.data.get(0).getUrl());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_subject)));
            }
        });
    }

}
