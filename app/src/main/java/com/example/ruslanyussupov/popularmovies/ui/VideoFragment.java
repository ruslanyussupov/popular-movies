package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.VideoAdapter;
import com.example.ruslanyussupov.popularmovies.model.Video;
import com.example.ruslanyussupov.popularmovies.network.VideoLoader;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Video>>,
        VideoAdapter.OnVideoClickListener {

    private static final String LOG_TAG = VideoFragment.class.getSimpleName();

    private static final String BUNDLE_MOVIE_ID = "movie_id";
    private static final String BUNDLE_VIDEOS = "videos";

    private List<Video> mVideos;
    private int mMovieId;
    private VideoAdapter mVideoAdapter;

    private static final int VIDEO_LOADER = 222;

    @BindView(R.id.videos_rv)RecyclerView mVideosRv;
    @BindView(R.id.state_tv)TextView mStateTv;

    public VideoFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        mStateTv.setVisibility(View.GONE);

        mVideoAdapter = new VideoAdapter(new ArrayList<Video>(), this);
        mVideosRv.setAdapter(mVideoAdapter);
        mVideosRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        int offset = getResources().getDimensionPixelOffset(R.dimen.video_item_offset);
        mVideosRv.addItemDecoration(new ItemDecoration(0, 0, offset, 0));

        if (savedInstanceState == null) {

            mMovieId = getArguments().getInt(BUNDLE_MOVIE_ID);

            if (NetworkUtils.hasNetworkConnection(getActivity())) {

                LoaderManager loaderManager = getLoaderManager();

                if (loaderManager.getLoader(VIDEO_LOADER) == null) {
                    loaderManager.initLoader(VIDEO_LOADER, null, this);
                } else {
                    loaderManager.restartLoader(VIDEO_LOADER, null, this);
                }

            } else {

                showNoNetworkConnectionState();

            }

        } else {

            mMovieId = savedInstanceState.getInt(BUNDLE_MOVIE_ID);
            mVideos = savedInstanceState.getParcelableArrayList(BUNDLE_VIDEOS);

            if (mVideos != null) {
                mStateTv.setVisibility(View.GONE);
                mVideoAdapter.updateData(mVideos);
            } else {
                showEmptyState();
            }

        }



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_VIDEOS, (ArrayList<? extends Parcelable>) mVideos);
        outState.putInt(BUNDLE_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
        return new VideoLoader(getActivity(), NetworkUtils.getMovieVideosUrl(mMovieId));
    }

    @Override
    public void onLoadFinished(Loader<List<Video>> loader, List<Video> data) {

        if (data == null || data.size() == 0) {
            showEmptyState();
            return;
        }

        mStateTv.setVisibility(View.GONE);
        mVideos = data;
        mVideoAdapter.updateData(mVideos);

    }

    @Override
    public void onLoaderReset(Loader<List<Video>> loader) {
        mVideoAdapter.updateData(new ArrayList<Video>());
    }

    @Override
    public void onVideoClick(int position) {
        Intent openVideoIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(mVideos.get(position).getUrl()));
        if (openVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openVideoIntent);
        }
    }

    // Create video fragment with args
    public static VideoFragment create(int movieId) {

        VideoFragment videoFragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_MOVIE_ID, movieId);
        videoFragment.setArguments(args);
        return videoFragment;

    }

    private void showNoNetworkConnectionState() {

        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.no_network_connection_state);

    }

    private void showEmptyState() {

        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.empty_state_videos);

    }

}
