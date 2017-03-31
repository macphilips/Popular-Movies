package com.rmhub.popularmovies.helper;

import java.util.List;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class Movies implements ResultCallback {
    public List<MovieDetails> list;
    private int total_count;
    private int total_page;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    @Override
    public void onFetchResult(String result) {
        list = ParserResult.parseMovieList(result);
    }
}
