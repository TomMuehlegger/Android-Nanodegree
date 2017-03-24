package com.example.android.project_02.movie_detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.project_02.R;
import com.example.android.project_02.data.Review;

import java.util.List;

/**
 * ReviewRecyclerViewAdapter class
 */
public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private final String TAG = ReviewRecyclerViewAdapter.class.getSimpleName();
    private List<Review> mReviews;

    public ReviewRecyclerViewAdapter(List<Review> reviews) {
        mReviews = reviews;
    }

    /**
     * ViewHolder class, holding data of each review view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Review mReview;

        final View mView;
        final TextView mReviewAuthor;
        final TextView mReviewContent;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mReviewAuthor = (TextView) view.findViewById(R.id.tv_review_author);
            mReviewContent = (TextView) view.findViewById(R.id.tv_review_content);
        }
    }

    /**
     * Update the review data of the recycler view adapter
     * @param reviewData
     */
    public void updateReviewData(List<Review> reviewData) {
        mReviews.clear();
        mReviews.addAll(reviewData);
        notifyDataSetChanged();
    }

    /**
     * Get the review at a certain position
     *
     * @param position
     * @return review on a certain position
     */
    private Review getReviewAt(int position) {
        return mReviews.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mReview = getReviewAt(position);

        holder.mReviewAuthor.setText(holder.mReview.getAuthor());
        holder.mReviewContent.setText(holder.mReview.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }
}
