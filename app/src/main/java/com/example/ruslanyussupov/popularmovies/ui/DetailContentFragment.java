package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailContentFragment extends Fragment {

    private Movie mMovie;

    @BindView(R.id.title_tv)TextView mTitleTv;
    @BindView(R.id.poster_iv)ImageView mPosterIv;
    @BindView(R.id.release_date_tv)TextView mReleaseDateTv;
    @BindView(R.id.user_rating_tv)TextView mVoteAverageTv;
    @BindView(R.id.overview_tv)TextView mOverviewTv;
    @BindView(R.id.backdrop_iv)ImageView mBackdropIv;

    public DetailContentFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_content, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        if (intent.hasExtra(MovieGridFragment.EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(MovieGridFragment.EXTRA_MOVIE);
        } else if (getArguments().containsKey(MovieGridFragment.EXTRA_MOVIE)) {
            mMovie = getArguments().getParcelable(MovieGridFragment.EXTRA_MOVIE);
        }

        if (mMovie != null) {

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

            mTitleTv.setText(mMovie.getOriginalTitle());
            mReleaseDateTv.setText(mMovie.getReleaseDate());
            mVoteAverageTv.setText(String.valueOf(mMovie.getVoteAverage()));
            mOverviewTv.setText(mMovie.getOverview());

        }

    }

}
