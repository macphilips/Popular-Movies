package com.rmhub.popularmovies.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.ReviewAdapter;
import com.rmhub.popularmovies.model.ReviewDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewScreen extends AppCompatActivity {

    public static final String REVIEW_COLOR = "color";
    public static final String REVIEW_LIST = "review list";
    public static final String REVIEW_LIST_BUNDLE = "bundle list";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_review_list)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_screen);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundleExtra = getIntent().getBundleExtra(REVIEW_LIST_BUNDLE);
        ArrayList<ReviewDetail> details = bundleExtra.getParcelableArrayList(REVIEW_LIST);
        int bgColor = bundleExtra.getInt(REVIEW_COLOR);
        ReviewAdapter mAdapter = new ReviewAdapter(this);
        mRecyclerView.setBackgroundColor(Color.BLACK);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter.addReviewList(details);
    }

}
