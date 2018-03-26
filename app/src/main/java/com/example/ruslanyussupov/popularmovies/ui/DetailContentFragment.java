package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.ruslanyussupov.popularmovies.events.AddFavouriteEvent;
import com.example.ruslanyussupov.popularmovies.events.RemoveFavouriteEvent;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.DbUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick(R.id.favorite_ib)
    public void favouriteChangeState() {

        if (mIsFavourite) {

            DbUtils.deleteMovieFromFavourite(getActivity(), mMovie);

            mFavouriteIb.setSelected(false);
            mIsFavourite = false;

            EventBus.getDefault().post(new RemoveFavouriteEvent(mMovie));

            Toast.makeText(getActivity(), getString(R.string.removed_from_favourite),
                    Toast.LENGTH_SHORT).show();

        } else {

            DbUtils.addMovieToFavourite(getActivity(), mMovie);

            mFavouriteIb.setSelected(true);
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

            mFavouriteIb.setSelected(true);

            Bitmap poster = BitmapFactory.decodeFile(mPosterLocalPath);
            Bitmap backdrop = BitmapFactory.decodeFile(mBackdropLocalPath);

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
