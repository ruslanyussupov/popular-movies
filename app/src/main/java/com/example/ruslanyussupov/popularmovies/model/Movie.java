package com.example.ruslanyussupov.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private int id;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private double voteAverage;
    private String releaseDate;
    private String backdropPath;
    private String posterLocalPath;
    private String backdropLocalPath;
    private boolean isFavourite;

    public Movie(int id, String originalTitle, String posterPath, String overview, double voteAverage,
                 String releaseDate, String backdropPath, boolean isFavourite) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.isFavourite = isFavourite;
    }

    public Movie(int id, String originalTitle, String posterPath, String overview, double voteAverage,
                 String releaseDate, String backdropPath, String posterDbPath, String backdropDbPath,
                 boolean isFavourite) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.posterLocalPath = posterDbPath;
        this.backdropLocalPath = backdropDbPath;
        this.isFavourite = isFavourite;
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
        isFavourite = in.readInt() == 1;
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

    public String getPosterLocalPath() {
        return posterLocalPath;
    }

    public String getBackdropLocalPath() {
        return backdropLocalPath;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setPosterLocalPath(String posterLocalPath) {
        this.posterLocalPath = posterLocalPath;
    }

    public void setBackdropLocalPath(String backdropLocalPath) {
        this.backdropLocalPath = backdropLocalPath;
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
        dest.writeInt(isFavourite ? 1 : 0);
    }
}
