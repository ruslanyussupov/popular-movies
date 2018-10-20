package com.example.ruslanyussupov.popularmovies.detail;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ruslanyussupov.popularmovies.ItemDecoration;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.ReviewAdapter;
import com.example.ruslanyussupov.popularmovies.adapters.VideoAdapter;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.Video;
import com.example.ruslanyussupov.popularmovies.databinding.FragmentDetailContentBinding;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.list.MovieGridFragment;
import com.squareup.picasso.Picasso;


import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DetailContentFragment extends Fragment implements VideoAdapter.OnVideoClickListener,
        ReviewAdapter.OnReviewClickListener {

    private static final String BUNDLE_MOVIE = "movie";
    private static final String BUNDLE_IS_FAVOURITE = "isFavourite";

    private Movie mMovie;
    private boolean mIsFavourite;
    private FragmentDetailContentBinding mBinding;
    private DetailViewModel mViewModel;
    private VideoAdapter mVideosAdapter = new VideoAdapter(Collections.emptyList(), this);
    private ReviewAdapter mReviewsAdapter = new ReviewAdapter(Collections.emptyList(), this);
    private Disposable disposable;

    public DetailContentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Timber.d("onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_content, container, false);
        return mBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated");

        mBinding.videos.videosRv.setAdapter(mVideosAdapter);
        mBinding.videos.videosRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        int videosOffset = getResources().getDimensionPixelOffset(R.dimen.video_item_offset);
        mBinding.videos.videosRv.addItemDecoration(new ItemDecoration(0, 0, videosOffset, 0));


        mBinding.reviews.reviewsRv.setAdapter(mReviewsAdapter);
        mBinding.reviews.reviewsRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        int reviewsOffset = getResources().getDimensionPixelOffset(R.dimen.review_item_offset);
        mBinding.reviews.reviewsRv.addItemDecoration(new ItemDecoration(0,0, reviewsOffset, 0));

        setFavBtnClickListener();

        if (savedInstanceState == null) {

            Intent intent = getActivity().getIntent();

            if (intent.hasExtra(MovieGridFragment.EXTRA_MOVIE)) {
                mMovie = intent.getParcelableExtra(MovieGridFragment.EXTRA_MOVIE);
            } else if (getArguments().containsKey(MovieGridFragment.EXTRA_MOVIE)) {
                mMovie = getArguments().getParcelable(MovieGridFragment.EXTRA_MOVIE);
            }

        } else {

            mMovie = savedInstanceState.getParcelable(BUNDLE_MOVIE);
            mIsFavourite = savedInstanceState.getBoolean(BUNDLE_IS_FAVOURITE);

        }

        if (mMovie != null) {
            DetailViewModelFactory viewModelFactory = new DetailViewModelFactory(mMovie);
            mViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel.class);

            disposable = mViewModel.getMovieFromFavourites()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ignore -> {
                        mIsFavourite = true;
                        updateUi();
                    }, ignore -> {
                        mIsFavourite = false;
                        updateUi();
                    });

            inflateUi();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(BUNDLE_MOVIE, mMovie);
        outState.putBoolean(BUNDLE_IS_FAVOURITE, mIsFavourite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();
        disposable.dispose();
    }

    public static DetailContentFragment create(Movie movie) {
        DetailContentFragment detailContentFragment = new DetailContentFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_MOVIE, movie);
        detailContentFragment.setArguments(args);
        return detailContentFragment;
    }

    private void updateUi() {

        if (mIsFavourite) {

            mBinding.favoriteIb.setSelected(true);

            if (!mViewModel.getUtils().hasNetworkConnection()) {
                Bitmap backdrop = BitmapFactory.decodeFile(mMovie.getBackdropLocalPath());

                if (backdrop == null) {
                    mBinding.backdropIv.setImageResource(R.drawable.backdrop_placeholder);
                } else {
                    mBinding.backdropIv.setImageBitmap(backdrop);
                }
                return;
            }

        } else {
            mBinding.favoriteIb.setSelected(false);
        }

        Picasso.get()
                .load(mMovie.getFullBackdropPath())
                .error(R.drawable.backdrop_error)
                .placeholder(R.drawable.poster_placeholder)
                .into(mBinding.backdropIv);

    }

    private void inflateUi() {
        mBinding.titleTv.setText(mMovie.getOriginalTitle());
        mBinding.releaseDateTv.setText(mMovie.getReleaseDate());
        mBinding.userRatingTv.setText(String.valueOf(mMovie.getVoteAverage()));
        mBinding.overviewTv.setText(mMovie.getOverview());

        mViewModel.getVideosResultLiveData().observe(this, result -> {
            switch (result.state) {
                case SUCCESS:
                    if (result.data == null || result.data.isEmpty()) {
                        mBinding.videosContainer.setVisibility(View.GONE);
                    } else {
                        mBinding.videosContainer.setVisibility(View.VISIBLE);
                        mVideosAdapter.updateData(result.data);
                    }
                    break;
                case ERROR:
                    mBinding.videosContainer.setVisibility(View.GONE);
                    break;
            }
        });

        mViewModel.getReviewsResultLiveData().observe(this, result -> {
            switch (result.state) {
                case SUCCESS:
                    if (result.data == null || result.data.isEmpty()) {
                        mBinding.reviewsContainer.setVisibility(View.GONE);
                    } else {
                        mBinding.reviewsContainer.setVisibility(View.VISIBLE);
                        mReviewsAdapter.updateData(result.data);
                    }
                    break;
                case ERROR:
                    mBinding.reviewsContainer.setVisibility(View.GONE);
                    break;
            }
        });

    }

    private void setFavBtnClickListener() {
        mBinding.favoriteIb.setOnClickListener(v -> {
            if (mIsFavourite) {

                mBinding.favoriteIb.setSelected(false);
                mIsFavourite = false;
                mViewModel.deleteFromFavourites();

                Toast.makeText(getActivity(), getString(R.string.removed_from_favourite),
                        Toast.LENGTH_SHORT).show();

            } else {

                mBinding.favoriteIb.setSelected(true);
                mIsFavourite = true;
                mViewModel.addToFavourites();

                Toast.makeText(getActivity(), getString(R.string.added_to_favourite),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onVideoClick(Video video) {
        Intent openVideoIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(video.getUrl()));
        if (openVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openVideoIntent);
        }
    }

    @Override
    public void onReviewClick(Review review) {
        Intent openReviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
        if (openReviewIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openReviewIntent);
        }
    }

}
