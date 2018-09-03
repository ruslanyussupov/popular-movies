package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.databinding.FragmentDetailContentBinding;
import com.example.ruslanyussupov.popularmovies.db.MovieContract;
import com.example.ruslanyussupov.popularmovies.events.AddFavouriteEvent;
import com.example.ruslanyussupov.popularmovies.events.RemoveFavouriteEvent;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.DbUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

public class DetailContentFragment extends Fragment {

    private static final String LOG_TAG = DetailContentFragment.class.getSimpleName();
    private static final String BUNDLE_MOVIE = "movie";
    private static final String BUNDLE_POSTER_LOCAL_PATH = "posterLocalPath";
    private static final String BUNDLE_BACKDROP_LOCAL_PATH = "backdropLocalPath";
    private static final String BUNDLE_IS_FAVOURITE = "isFavourite";

    private Movie mMovie;
    private boolean mIsFavourite;
    private String mPosterLocalPath;
    private String mBackdropLocalPath;
    private FragmentDetailContentBinding mBinding;

    public DetailContentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_content, container, false);

        return mBinding.getRoot();

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
                checkIsFavourite();
                addFragments();
                updateUi();
            }

        } else {

            mMovie = savedInstanceState.getParcelable(BUNDLE_MOVIE);
            mPosterLocalPath = savedInstanceState.getString(BUNDLE_POSTER_LOCAL_PATH);
            mBackdropLocalPath = savedInstanceState.getString(BUNDLE_BACKDROP_LOCAL_PATH);
            mIsFavourite = savedInstanceState.getBoolean(BUNDLE_IS_FAVOURITE);

            if (mMovie != null) {
                checkIsFavourite();
                updateUi();
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_MOVIE, mMovie);
        outState.putString(BUNDLE_POSTER_LOCAL_PATH, mPosterLocalPath);
        outState.putString(BUNDLE_BACKDROP_LOCAL_PATH, mBackdropLocalPath);
        outState.putBoolean(BUNDLE_IS_FAVOURITE, mIsFavourite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    public void favouriteChangeState(View view) {

        if (mIsFavourite) {

            DbUtils.deleteMovieFromFavourite(getActivity(), mMovie);

            mBinding.favoriteIb.setSelected(false);
            mIsFavourite = false;

            EventBus.getDefault().post(new RemoveFavouriteEvent(mMovie));

            Toast.makeText(getActivity(), getString(R.string.removed_from_favourite),
                    Toast.LENGTH_SHORT).show();

        } else {

            DbUtils.addMovieToFavourite(getActivity(), mMovie);

            mBinding.favoriteIb.setSelected(true);
            mIsFavourite = true;

            EventBus.getDefault().post(new AddFavouriteEvent(mMovie));

            Toast.makeText(getActivity(), getString(R.string.added_to_favourite),
                    Toast.LENGTH_SHORT).show();

        }

    }

    // Create detail fragment with args
    public static DetailContentFragment create(Movie movie) {
        DetailContentFragment detailContentFragment = new DetailContentFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_MOVIE, movie);
        detailContentFragment.setArguments(args);
        return detailContentFragment;
    }

    // Add trailers and reviews fragments
    private void addFragments() {

        getFragmentManager().beginTransaction()
                .add(R.id.videos_container, VideoFragment.create(mMovie.getId()))
                .commit();

        getFragmentManager().beginTransaction()
                .add(R.id.reviews_container, ReviewFragment.create(mMovie.getId()))
                .commit();

    }

    private void updateUi() {

        if (mIsFavourite) {

            mBinding.favoriteIb.setSelected(true);

            Bitmap poster = BitmapFactory.decodeFile(mPosterLocalPath);
            Bitmap backdrop = BitmapFactory.decodeFile(mBackdropLocalPath);

            if (poster == null) {
                mBinding.posterIv.setImageResource(R.drawable.poster_placeholder);
            } else {
                mBinding.posterIv.setImageBitmap(poster);
            }

            if (backdrop == null) {
                mBinding.backdropIv.setImageResource(R.drawable.backdrop_placeholder);
            } else {
                mBinding.backdropIv.setImageBitmap(backdrop);
            }

        } else {

            mBinding.favoriteIb.setSelected(false);

            Picasso.get()
                    .load(NetworkUtils.buildMoviePosterUrlPath(mMovie.getPosterPath()))
                    .error(R.drawable.poster_placeholder)
                    .placeholder(R.drawable.poster_error)
                    .into(mBinding.posterIv);

            Picasso.get()
                    .load(NetworkUtils.buildMovieBackdropUrlPath(mMovie.getBackdropPath()))
                    .error(R.drawable.backdrop_error)
                    .placeholder(R.drawable.poster_placeholder)
                    .into(mBinding.backdropIv);

        }

        mBinding.titleTv.setText(mMovie.getOriginalTitle());
        mBinding.releaseDateTv.setText(mMovie.getReleaseDate());
        mBinding.userRatingTv.setText(String.valueOf(mMovie.getVoteAverage()));
        mBinding.overviewTv.setText(mMovie.getOverview());

    }

    private void checkIsFavourite() {

        Cursor movieCursor = DbUtils.getMovieFromDb(getActivity(), mMovie.getId());
        mIsFavourite = movieCursor != null && movieCursor.getCount() != 0;

        if (mIsFavourite) {

            movieCursor.moveToFirst();

            mPosterLocalPath = movieCursor.getString(
                    movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_POSTER_LOCAL_PATH));
            mBackdropLocalPath = movieCursor.getString(
                    movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_BACKDROP_LOCAL_PATH));

            movieCursor.close();
        }

    }

}
