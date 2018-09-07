package com.example.ruslanyussupov.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.BuildConfig;
import com.example.ruslanyussupov.popularmovies.data.local.FavouriteMoviesDb;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse;
import com.example.ruslanyussupov.popularmovies.data.model.Video;
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbAPI;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository implements DataSource {

    private static final String TAG = Repository.class.getSimpleName();

    private final FavouriteMoviesDb mFavouriteMoviesDb;
    private final TheMovieDbAPI mMovieDbAPI;
    private MutableLiveData<List<Movie>> mMovies;
    private Callback<MoviesResponse> mCallback;

    public Repository(Context context) {
        mFavouriteMoviesDb = Room.databaseBuilder(context, FavouriteMoviesDb.class,
                "fav_movies").build();
        mMovieDbAPI = createMovieDbApi();
    }

    @Override
    public LiveData<List<Movie>> getMovies(Filter filter) {
        if (mMovies == null) {
            mMovies = new MutableLiveData<>();
        }
        if (mCallback == null) {
            mCallback = new Callback<MoviesResponse>() {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        MoviesResponse moviesResponse = response.body();
                        if (moviesResponse != null) {
                            mMovies.postValue(moviesResponse.getResults());
                        } else {
                            Log.d(TAG, "Movies response is NULL. " + "Code: " + response.code()
                                    + " Message: " + response.message());
                        }
                    } else {
                        Log.d(TAG, "Response isn't successful. " + "Code: " + response.code()
                                + " Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Can't load movies.", t);
                }
            };
        }

        switch (filter) {
            case POPULAR:
                mMovieDbAPI.getPopularMovies().enqueue(mCallback);
                break;
            case TOP_RATED:
                mMovieDbAPI.getTopRatedMovies().enqueue(mCallback);
                break;
        }
        return mMovies;
    }

    @Override
    public LiveData<List<Movie>> getFavouriteMovies() {
        return mFavouriteMoviesDb.movieDao().getFavouriteMovies();
    }

    @Override
    public LiveData<List<Video>> getMovieTrailers(int movieId) {
        final MutableLiveData<List<Video>> trailers = new MutableLiveData<>();
        mMovieDbAPI.getMovieTrailers(movieId).enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {
                if (response.isSuccessful()) {
                    VideosResponse videosResponse = response.body();
                    if (videosResponse != null) {
                        trailers.postValue(videosResponse.getResults());
                    } else {
                        Log.d(TAG, "Movie trailers response is NULL. " + "Code: " + response.code()
                                + " Message: " + response.message());
                    }
                } else {
                    Log.d(TAG, "Response isn't successful. " + "Code: " + response.code()
                            + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Can't load movie trailers.", t);
            }
        });
        return trailers;
    }

    @Override
    public LiveData<List<Review>> getMovieReviews(int movieId) {
        final MutableLiveData<List<Review>> reviews = new MutableLiveData<>();
        mMovieDbAPI.getMovieReviews(movieId).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsResponse> call, @NonNull Response<ReviewsResponse> response) {
                if (response.isSuccessful()) {
                    ReviewsResponse reviewsResponse = response.body();
                    if (reviewsResponse != null) {
                        reviews.postValue(reviewsResponse.getResults());
                    } else {
                        Log.d(TAG, "Movie reviews response is NULL. " + "Code: " + response.code()
                                + " Message: " + response.message());
                    }
                } else {
                    Log.d(TAG, "Response isn't successful. " + "Code: " + response.code()
                            + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Can't load movie reviews.", t);
            }
        });
        return reviews;
    }

    private TheMovieDbAPI createMovieDbApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();

                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                                .addQueryParameter("language", "en-US")
                                .build();

                        return chain.proceed(request.newBuilder().url(url).build());
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbAPI.ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TheMovieDbAPI.class);
    }

}
