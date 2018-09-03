package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.VideoAdapter;
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbAPI;
import com.example.ruslanyussupov.popularmovies.events.ShareEvent;
import com.example.ruslanyussupov.popularmovies.data.model.Video;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoFragment extends Fragment implements VideoAdapter.OnVideoClickListener {

    private static final String LOG_TAG = VideoFragment.class.getSimpleName();

    private static final String BUNDLE_MOVIE_ID = "movie_id";
    private static final String BUNDLE_VIDEOS = "videos";

    private List<Video> mVideos;
    private int mMovieId;
    private VideoAdapter mVideoAdapter;
    private TheMovieDbAPI mTheMovieDbAPI;

    @BindView(R.id.videos_rv)RecyclerView mVideosRv;
    @BindView(R.id.state_tv)TextView mStateTv;

    public VideoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

                mTheMovieDbAPI = NetworkUtils.getMovieDbApi();
                mTheMovieDbAPI.getMovieTrailers(mMovieId).enqueue(new Callback<VideosResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VideosResponse> call,
                                           @NonNull Response<VideosResponse> response) {
                        if (response.isSuccessful()) {
                            VideosResponse videosResponse = response.body();
                            if (videosResponse == null) {
                                showEmptyState();
                            } else {
                                List<Video> videos = videosResponse.getResults();
                                if (videos == null || videos.isEmpty()) {
                                    showEmptyState();
                                } else {
                                    showVideos();
                                    mVideoAdapter.updateData(videos);
                                }
                            }
                        } else {
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {
                        showEmptyState();
                        Log.e(LOG_TAG, "Can't load videos for " + mMovieId, t);
                    }
                });

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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
    public void onVideoClick(int position) {
        Intent openVideoIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(mVideos.get(position).getUrl()));
        if (openVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openVideoIntent);
        }
    }

    @Subscribe
    public void onShare(ShareEvent event) {
        if (mVideos != null && !mVideos.isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, mVideos.get(0).getUrl());
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_subject)));
        } else {
            Toast.makeText(getActivity(), getString(R.string.nothing_to_share), Toast.LENGTH_SHORT)
                    .show();
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
        mVideosRv.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.no_network_connection_state);
    }

    private void showEmptyState() {
        mVideosRv.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.empty_state_videos);
    }

    private void showVideos() {
        mStateTv.setVisibility(View.GONE);
        mVideosRv.setVisibility(View.VISIBLE);
    }

}
