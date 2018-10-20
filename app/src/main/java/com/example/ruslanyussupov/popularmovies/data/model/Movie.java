package com.example.ruslanyussupov.popularmovies.data.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies")
public class Movie implements Parcelable {

    private static final String MOVIE_POSTER_MAIN_PATH = "https://image.tmdb.org/t/p/w185";
    private static final String MOVIE_BACKDROP_MAIN_PATH = "https://image.tmdb.org/t/p/w780";

    @PrimaryKey
    private int id;

    private String overview;
    private String posterLocalPath;
    private String backdropLocalPath;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @Ignore
    public Movie(int id, String originalTitle, String posterPath, String overview, double voteAverage,
                 String releaseDate, String backdropPath) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
    }

    public Movie(int id, String originalTitle, String posterPath, String overview, double voteAverage,
                 String releaseDate, String backdropPath, String posterLocalPath, String backdropLocalPath) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.posterLocalPath = posterLocalPath;
        this.backdropLocalPath = backdropLocalPath;
    }


    protected Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        backdropPath = in.readString();
        posterLocalPath = in.readString();
        backdropLocalPath = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getFullPosterPath() {
        return MOVIE_POSTER_MAIN_PATH + posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getFullBackdropPath() {
        return MOVIE_BACKDROP_MAIN_PATH + backdropPath;
    }

    public String getPosterLocalPath() {
        return posterLocalPath;
    }

    public String getBackdropLocalPath() {
        return backdropLocalPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterLocalPath(String posterLocalPath) {
        this.posterLocalPath = posterLocalPath;
    }

    public void setBackdropLocalPath(String backdropLocalPath) {
        this.backdropLocalPath = backdropLocalPath;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(backdropPath);
        dest.writeString(posterLocalPath);
        dest.writeString(backdropLocalPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Movie) {
            Movie movie = (Movie) obj;
            return id == movie.getId();
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return id + " | " + originalTitle + " | " + posterPath + " | " + backdropPath;
    }
}
