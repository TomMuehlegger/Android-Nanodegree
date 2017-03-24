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
import com.example.android.project_02.data.Trailer;

import java.util.List;

/**
 * TrailerRecyclerViewAdapter class
 */
public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    private final String TAG = TrailerRecyclerViewAdapter.class.getSimpleName();
    private List<Trailer> mTrailers;

    public TrailerRecyclerViewAdapter(List<Trailer> trailers) {
        mTrailers = trailers;
    }

    /**
     * ViewHolder class, holding data of each trailer view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Trailer mTrailer;

        final View mView;
        final TextView mTrailerTitle;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTrailerTitle = (TextView) view.findViewById(R.id.tv_trailer_title);
        }
    }

    /**
     * Update the trailer data of the recycler view adapter
     * @param trailerData
     */
    public void updateTrailerData(List<Trailer> trailerData) {
        mTrailers.clear();
        mTrailers.addAll(trailerData);
        notifyDataSetChanged();
    }

    /**
     * Get the trailer at a certain position
     *
     * @param position
     * @return trailer on a certain position
     */
    private Trailer getTrailerAt(int position) {
        return mTrailers.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTrailer = getTrailerAt(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Uri youtubeUrl = Uri.parse("http://www.youtube.com/watch?v=" + holder.mTrailer.getKey());

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(youtubeUrl);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Log.d(TAG, "Couldn't call " + youtubeUrl.toString() + ", no receiving apps installed!");
                }
            }
        });

        holder.mTrailerTitle.setText(holder.mTrailer.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }
}
