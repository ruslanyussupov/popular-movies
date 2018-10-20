package com.example.ruslanyussupov.popularmovies.di;


import com.example.ruslanyussupov.popularmovies.BuildConfig;
import com.example.ruslanyussupov.popularmovies.IoScheduler;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService;


import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private static final String API_CLIENT = "API_CLIENT";

    @Provides
    @Singleton
    public OkHttpClient providesClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    public Request.Builder providesRequestBuilder() {
        return new Request.Builder();
    }

    @Provides
    @Singleton
    @Named(API_CLIENT)
    public OkHttpClient providesApiClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    HttpUrl url = request.url().newBuilder()
                            .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                            .addQueryParameter("language", "en-US")
                            .build();

                    return chain.proceed(request.newBuilder().url(url).build());
                }).build();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(@Named(API_CLIENT) OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(TheMovieDbService.ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public IoScheduler providesIoScheduler() {
        return new IoScheduler();
    }

}
