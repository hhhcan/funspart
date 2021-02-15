package com.can.funspart.bean.film;

import java.util.List;

public class Recommends {

    private String recommendTitle;
    private List<Movies> movies;
    public void setRecommendTitle(String recommendTitle) {
        this.recommendTitle = recommendTitle;
    }
    public String getRecommendTitle() {
        return recommendTitle;
    }

    public void setMovies(List<Movies> movies) {
        this.movies = movies;
    }
    public List<Movies> getMovies() {
        return movies;
    }

}
