package com.example.ruslanyussupov.popularmovies.di;

import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter;
import com.example.ruslanyussupov.popularmovies.data.Repository;
import com.example.ruslanyussupov.popularmovies.detail.DetailViewModel;
import com.example.ruslanyussupov.popularmovies.list.MainViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, DataModule.class})
public interface AppComponent {

    void inject(MainViewModel mainViewModel);
    void inject(DetailViewModel detailViewModel);
    void inject(Repository repository);
    void inject(MovieAdapter movieAdapter);

}
