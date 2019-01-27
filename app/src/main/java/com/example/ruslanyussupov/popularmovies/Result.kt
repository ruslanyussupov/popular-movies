package com.example.ruslanyussupov.popularmovies

class Result<T> private constructor(val data: T?,
                                    val state: State,
                                    val error: String) {

    enum class State {
        LOADING, SUCCESS, ERROR
    }

    companion object {

        fun <T> success(data: T): Result<T> {
            return Result(data, State.SUCCESS, "")
        }

        fun <T> loading(): Result<T> {
            return Result(null, State.LOADING, "")
        }

        fun <T> error(error: String): Result<T> {
            return Result(null, State.ERROR, error)
        }
    }

}
