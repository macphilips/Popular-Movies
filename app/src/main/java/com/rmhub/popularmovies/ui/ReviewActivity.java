package com.rmhub.popularmovies.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.ReviewAdapter;
import com.rmhub.popularmovies.model.ReviewDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 4/15/2017
 * <p>
 * owm
 * .
 */

public class ReviewActivity extends AppCompatActivity {
    public static final String REVIEW_LIST = "review_list";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_movie_list)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        List<ReviewDetail> details = getIntent().getParcelableArrayListExtra(REVIEW_LIST);
        ReviewAdapter adapter = new ReviewAdapter(this);
        adapter.addReviewList(details);
        mRecyclerView.setAdapter(adapter);

    }
}
