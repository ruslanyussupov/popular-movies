package com.example.ruslanyussupov.popularmovies.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.ruslanyussupov.popularmovies.data.DataSource;
import com.example.ruslanyussupov.popularmovies.data.Repository;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;

import java.util.List;

import static com.example.ruslanyussupov.popularmovies.data.DataSource.Filter;

public class MovieGridViewModel extends AndroidViewModel {

    private static final String TAG = MovieGridViewModel.class.getSimpleName();

    private final DataSource mDataSource;
    private Filter mFilter;
    private LiveData<List<Movie>> mMovies;

    public MovieGridViewModel(@NonNull Application application) {
        super(application);

        mDataSource = new Repository(application);
        mFilter = Filter.POPULAR;
    }

    public void setFilter(Filter filter) {
        mFilter = filter;
        loadMovies();
    }

    public Filter getFilter() {
        return mFilter;
    }

    public LiveData<List<Movie>> getMovies() {
        if (mMovies == null) {
            loadMovies();
        }
        return mMovies;
    }

    private void loadMovies() {
        mMovies = mDataSource.getMovies(mFilter);
    }

}
