package com.example.ruslanyussupov.popularmovies;


import android.support.annotation.Nullable;

public class Result<T> {

    @Nullable
    public final T data;

    @Nullable
    public final String error;

    public final State state;


    private Result(@Nullable T data, State state, @Nullable String error) {
        this.data = data;
        this.state = state;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, State.SUCCESS, null);
    }

    public static <T> Result<T> loading() {
        return new Result<>(null, State.LOADING, null);
    }

    public static <T> Result<T> error(String error) {
        return new Result<>(null, State.ERROR, error);
    }

    public enum State {
        LOADING, SUCCESS, ERROR
    }

}
