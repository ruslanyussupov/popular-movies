package com.example.ruslanyussupov.popularmovies.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.BuildConfig;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_MOVIES_URL = "https://api.themoviedb.org/3/movie/top_rated";

    private static final String MOVIE_POSTER_MAIN_PATH = "http://image.tmdb.org/t/p/w185";
    private static final String MOVIE_BACKDROP_MAIN_PATH = "http://image.tmdb.org/t/p/w780";

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String MOVIE_REVIEWS = "reviews";
    private static final String MOVIE_VIDEOS = "videos";

    private static final String QUERY_KEY_API_KEY = "api_key";
    private static final String QUERY_KEY_LANGUAGE = "language";

    private static final String LANGUAGE = Locale.getDefault().toString();
    private static final String API_KEY = BuildConfig.THEMOVIEDB_API_KEY;

    public static TheMovieDbAPI getMovieDbApi() {
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

    /**
     * Takes URL as String and make connection to read the data and return it.
     *
     * @param url the URL from we want to get data.
     * */
    public static String getResponseFromUrl(URL url) {

        // If the URL is null then return null
        if (url == null) {
            return null;
        }

        String response = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            // Create connection through the URL
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            // Get InputStream from the connection
            inputStream = urlConnection.getInputStream();

            // Decode bytes from the InputStream and put them into BufferedReader for the
            // efficient reading
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Create StringBuilder that will contain our data from the BufferedReader
            StringBuilder stringBuilder = new StringBuilder();

            // Read each line from BufferedReader and append them to the StringBuilder
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            response = stringBuilder.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while getting response from URL.", e);

        } finally {

            if (urlConnection !=  null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "InputStream closing error.", e);
                }

            }
        }

        return response;

    }

    // Takes URL as String and returns URL object
    private static URL makeUrl(String urlString) {

        // If URL is empty then return null
        if (TextUtils.isEmpty(urlString)) {
            return null;
        }

        URL url = null;

        // Create and return URL object from String
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Creating URL error.", e);
        }

        return url;

    }

    public static boolean hasNetworkConnection(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();

        }

        return false;

    }

    public static URL getPopularMoviesUrl() {

        return buildEndpointUrl(POPULAR_MOVIES_URL);

    }

    public static URL getTopRatedMoviesUrl() {

        return buildEndpointUrl(TOP_RATED_MOVIES_URL);

    }

    public static URL getMovieReviewsUrl(int movieId) {
        return movieDetailUrlBuilder(movieId, MOVIE_REVIEWS);
    }

    public static URL getMovieVideosUrl(int movieId) {
        return movieDetailUrlBuilder(movieId, MOVIE_VIDEOS);
    }

    public static String buildMoviePosterUrlPath(String posterPath) {
        return MOVIE_POSTER_MAIN_PATH + posterPath;
    }

    public static String buildMovieBackdropUrlPath(String backdropPath) {
        return MOVIE_BACKDROP_MAIN_PATH + backdropPath;
    }

    private static URL movieDetailUrlBuilder(int movieId, String detail) {

        String url = MOVIE_BASE_URL + "/" + movieId + "/" + detail;

        return buildEndpointUrl(url);

    }

    private static URL buildEndpointUrl(String url) {

        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(QUERY_KEY_API_KEY, API_KEY)
                .appendQueryParameter(QUERY_KEY_LANGUAGE, LANGUAGE)
                .build();

        return makeUrl(uri.toString());

    }

}
