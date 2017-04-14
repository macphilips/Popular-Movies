package com.rmhub.popularmovies.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rmhub.popularmovies.helper.MovieQuery;
import com.rmhub.popularmovies.helper.ResultHandler;
import com.rmhub.popularmovies.model.MovieDetail;

import java.io.UnsupportedEncodingException;

/**
 * Created by MOROLANI on 4/10/2017
 * <p>
 * owm
 * .
 */

public class MovieRequest<T extends ResultHandler> extends Request<T> {
    public static final String QUERY_URL = "url";
    public static final String MOVIE_DETAILS = "movieDetail";
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private final MovieDetail mMovieDetail;
    private final Context mCtx;

    MovieRequest(Context mCtx, MovieQuery query, Class<T> clazz, MovieRequestListener<T> listener) {
        super(Method.GET, query.queryBundle().getString(QUERY_URL), listener);
        Log.d(getClass().getSimpleName(),"http requestUrl - "+query.queryBundle().getString(QUERY_URL));
        this.mCtx = mCtx;
        Bundle arg = query.queryBundle();
        mMovieDetail = arg.getParcelable(MOVIE_DETAILS);
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T obj = gson.fromJson(json, clazz);
            if (mMovieDetail != null) {
                obj.saveToDatabase(mCtx, mMovieDetail);
            } else {
                obj.saveToDatabase(mCtx);
            }
            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    public static class MovieRequestListener<T extends ResultHandler> implements Response.Listener<T>, Response.ErrorListener {

        @Override
        public void onResponse(T response) {

        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        public void onNetworkError() {
        }
    }

}
