package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.db.MovieContract;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.DbUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.example.ruslanyussupov.popularmovies.utils.StorageUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailContentFragment extends Fragment {

    private static final String LOG_TAG = DetailContentFragment.class.getSimpleName();
    public static final String BUNDLE_MOVIE_ID = "movie_id";
    public static final String BUNDLE_MOVIE = "movie";

    private Movie mMovie;

    @BindView(R.id.title_tv)TextView mTitleTv;
    @BindView(R.id.poster_iv)ImageView mPosterIv;
    @BindView(R.id.release_date_tv)TextView mReleaseDateTv;
    @BindView(R.id.user_rating_tv)TextView mVoteAverageTv;
    @BindView(R.id.overview_tv)TextView mOverviewTv;
    @BindView(R.id.backdrop_iv)ImageView mBackdropIv;
    @BindView(R.id.favorite_ib)ImageButton mFavouriteIb;

    public DetailContentFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail_content, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        if (savedInstanceState == null) {

            Intent intent = getActivity().getIntent();

            if (intent.hasExtra(MovieGridFragment.EXTRA_MOVIE)) {
                mMovie = intent.getParcelableExtra(MovieGridFragment.EXTRA_MOVIE);
            } else if (getArguments().containsKey(MovieGridFragment.EXTRA_MOVIE)) {
                mMovie = getArguments().getParcelable(MovieGridFragment.EXTRA_MOVIE);
            }

            if (mMovie != null) {
                addFragments();
                updateUi();
            }

        } else {

            mMovie = savedInstanceState.getParcelable(BUNDLE_MOVIE);

            if (mMovie != null) {
                updateUi();
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_MOVIE, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @OnClick(R.id.favorite_ib)
    public void favouriteChangeState() {

        if (mMovie.isFavourite()) {

            int rowsDeleted = getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{String.valueOf(mMovie.getId())});

            StorageUtils.deleteFile(mMovie.getPosterLocalPath());
            StorageUtils.deleteFile(mMovie.getBackdropLocalPath());

            mMovie.setFavourite(false);
            mFavouriteIb.setSelected(false);

            Toast.makeText(getActivity(), "Deleted from favourite: " + rowsDeleted,
                    Toast.LENGTH_SHORT).show();

        } else {

            Uri row = DbUtils.insertMovieIntoDb(getActivity(), mMovie);

            mMovie.setFavourite(true);
            mFavouriteIb.setSelected(true);

            Toast.makeText(getActivity(), "Added to favourite:\n" + row,
                    Toast.LENGTH_SHORT).show();

        }

    }

    private void addFragments() {

        VideoFragment videoFragment = new VideoFragment();
        ReviewFragment reviewFragment = new ReviewFragment();

        Bundle args = new Bundle();
        args.putInt(BUNDLE_MOVIE_ID, mMovie.getId());

        videoFragment.setArguments(args);
        reviewFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .add(R.id.videos_container, videoFragment)
                .commit();

        getFragmentManager().beginTransaction()
                .add(R.id.reviews_container, reviewFragment)
                .commit();

    }

    private void updateUi() {

        Log.d(LOG_TAG, mMovie + " = " + mMovie.isFavourite());

        if (mMovie.isFavourite()) {

            mFavouriteIb.setSelected(true);

            Bitmap poster = BitmapFactory.decodeFile(mMovie.getPosterLocalPath());
            Bitmap backdrop = BitmapFactory.decodeFile(mMovie.getBackdropLocalPath());

            if (poster == null) {
                mPosterIv.setImageResource(R.drawable.poster_placeholder);
            } else {
                mPosterIv.setImageBitmap(poster);
            }

            if (backdrop == null) {
                mBackdropIv.setImageResource(R.drawable.backdrop_placeholder);
            } else {
                mBackdropIv.setImageBitmap(backdrop);
            }

        } else {

            mFavouriteIb.setSelected(false);

            Picasso.with(getActivity())
                    .load(NetworkUtils.buildMoviePosterUrlPath(mMovie.getPosterPath()))
                    .error(R.drawable.poster_placeholder)
                    .placeholder(R.drawable.poster_error)
                    .into(mPosterIv);

            Picasso.with(getActivity())
                    .load(NetworkUtils.buildMovieBackdropUrlPath(mMovie.getBackdropPath()))
                    .error(R.drawable.backdrop_error)
                    .placeholder(R.drawable.poster_placeholder)
                    .into(mBackdropIv);

        }

        mTitleTv.setText(mMovie.getOriginalTitle());
        mReleaseDateTv.setText(mMovie.getReleaseDate());
        mVoteAverageTv.setText(String.valueOf(mMovie.getVoteAverage()));
        mOverviewTv.setText(mMovie.getOverview());

    }

}
