package com.rmhub.popularmovies.helper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.model.ReviewDetails;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 4/8/2017
 * <p>
 * owm
 * .
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    public static final int SHOW_ALL = 1;
    public static final int SHOW_FIRST_FIVE = 2;
    private static final int LOAD_MORE_BUTTON = 1;
    private static final int REVIEW = 2;
    private static final int SHOW_SIZE = 3;
    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private final int avatarRadius;
    private final Context context;
    private int mode = SHOW_FIRST_FIVE;
    private List<ReviewDetails> details = new ArrayList<>();
    private List<ReviewDetails> detailsTmp = new ArrayList<>();
    private View.OnClickListener mListener;
    private int color = -1;

    public ReviewAdapter(AppCompatActivity context) {
        avatarRadius = context.getResources().getDimensionPixelSize(R.dimen.review_avatar_size);
        this.context = context;
    }

    public static void setBackgroundState(Context context, TextView view, int color) {
        if (view == null) return;
        int height = view.getHeight();
        int width = view.getWidth();

        int[] colorsNormal = {context.getResources().getColor(R.color.load_button_start), context.getResources().getColor(R.color.load_button_end)};
        GradientDrawable normal = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorsNormal);
        normal.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        normal.setShape(GradientDrawable.RECTANGLE);
        normal.setSize(width, height);
        normal.setStroke(context.getResources().getDimensionPixelSize(R.dimen.button_stroke_w),
                context.getResources().getColor(R.color.load_button_stroke));
        normal.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.button_corner_radius));

        int[] colorSelected;
        int strokeColor;
        if (color != 0) {

            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;

            int red = (50 + r) % 256;
            int green = (50 + g) % 256;
            int blue = (50 + b) % 256;
            int endColor = 0xff000000 | red << 16 | green << 8 | blue;

            colorSelected = new int[]{color, endColor};

            red =  (-50 + r) % 256;
            green = (-50 + g) % 256;
            blue =  (-50 + b) % 256;
            strokeColor = 0xff000000 | red << 16 | green << 8 | blue;

        } else {
            colorSelected = new int[]{context.getResources().getColor(R.color.overlay_start), context.getResources().getColor(R.color.overlay_end)};
            strokeColor = context.getResources().getColor(R.color.overlay_stroke);
        }

        GradientDrawable selected = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorSelected);
        selected.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        selected.setShape(GradientDrawable.RECTANGLE);
        selected.setSize(width, height);
        selected.setStroke(context.getResources().getDimensionPixelSize(R.dimen.button_stroke_w),
                strokeColor);
        selected.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.button_corner_radius));

        StateListDrawable bg = new StateListDrawable();

        bg.addState(new int[]{android.R.attr.state_pressed},selected  );
        bg.addState(new int[]{android.R.attr.state_focused}, selected);
        //bg.addState(new int[]{android.R.attr.state_selected},selected );
        bg.addState(new int[]{}, normal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(bg);
        } else {
            view.setBackgroundDrawable(bg);
        }

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed}, // pressed
                new int[]{android.R.attr.state_focused}, // focused
                new int[]{}
        };
        int[] colors = new int[]{
                context.getResources().getColor(R.color.load_button_start), // pressed
                context.getResources().getColor(R.color.load_button_start), // focus
                (color != 0) ? color : context.getResources().getColor(R.color.overlay_start) // normal
        };
        ColorStateList list = new ColorStateList(states, colors);
        view.setFocusable(true);
        view.setClickable(true);
        view.setTextColor(list);
    }

    public void setListener(View.OnClickListener mListener) {
        this.mListener = mListener;
    }

    private void addMovieList(List<ReviewDetails> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        this.details.clear();
        this.detailsTmp.clear();
        this.details.addAll(details);
        if (mode == SHOW_FIRST_FIVE) {
            for (int i = 0; i < SHOW_SIZE; i++) {
                this.detailsTmp.add(this.details.get(i));
            }
            this.detailsTmp.add(null);
        } else {
            detailsTmp.addAll(details);
        }
        notifyDataSetChanged();
    }

    public void setMode(@Mode int mode) {
        this.mode = mode;
    }

    @Override
    public int getItemViewType(int position) {
        return (detailsTmp.get(position) == null) ? LOAD_MORE_BUTTON : REVIEW;
    }

    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == REVIEW) {
            return new ReviewAdapter.ReviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false));

        } else {
            return new ReviewAdapter.LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.MyViewHolder holder, int position) {

        ReviewDetails tag = detailsTmp.get(position);
        if (holder instanceof ReviewHolder) {

            ReviewAdapter.ReviewHolder reviewHolder = (ReviewHolder) holder;
            reviewHolder.reviewer_name.setText(tag.getAuthor());
            reviewHolder.review.setText(tag.getContent());
            String firstLetter = tag.getAuthor().substring(0, 1).toUpperCase();

            TextDrawable drawable1 = TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .withBorder(context.getResources().getDimensionPixelSize(R.dimen.review_avatar_spacing))
                    .endConfig()
                    .buildRoundRect(firstLetter, ColorGenerator.MATERIAL.getColor(tag.getAuthor()), context.getResources().getDimensionPixelSize(R.dimen.review_avatar_size));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                reviewHolder.avatar.setBackground(drawable1);
            } else {
                reviewHolder.avatar.setBackgroundDrawable(drawable1);
            }

        } else if (holder instanceof LoadMoreHolder) {
            ReviewAdapter.LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.readAllButton.setText(String.format(Locale.US, "Read All Reviews %d", details.size()));
            setBackgroundState(context, loadMoreHolder.readAllButton, color);
            if (mListener != null)
                loadMoreHolder.readAllButton.setOnClickListener(mListener);
        }
    }

    @Override
    public int getItemCount() {
        return (details == null) ? 0 : details.size();
    }

    public void setColor(int color) {
        if (mode == SHOW_FIRST_FIVE) {
            this.color = color;
            notifyItemChanged(detailsTmp.size());
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_ALL, SHOW_FIRST_FIVE})
    public @interface Mode {
    }

    static class ReviewHolder extends MyViewHolder {

        @BindView(R.id.review_avatar)
        ImageView avatar;

        @BindView(R.id.review)
        TextView review;
        @BindView(R.id.reviewer_name)
        TextView reviewer_name;

        ReviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class LoadMoreHolder extends MyViewHolder {
        @BindView(R.id.read_all_butt)
        TextView readAllButton;

        LoadMoreHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
