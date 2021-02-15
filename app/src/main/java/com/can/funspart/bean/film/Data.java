package com.can.funspart.bean.film;

import java.util.List;

public class Data {
    private List<Recommends> recommends;
    private List<Moviecomings> moviecomings;
    private int totalMovieComings;
    public void setRecommends(List<Recommends> recommends) {
        this.recommends = recommends;
    }
    public List<Recommends> getRecommends() {
        return recommends;
    }

    public void setMoviecomings(List<Moviecomings> moviecomings) {
        this.moviecomings = moviecomings;
    }
    public List<Moviecomings> getMoviecomings() {
        return moviecomings;
    }
}
