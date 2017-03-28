package com.example.android.project_02;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.project_02.data.Movie;
import com.example.android.project_02.movie_detail.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * MovieRecyclerViewAdapter class
 */
public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Movie> mMovies;

    public MovieRecyclerViewAdapter(ArrayList<Movie> movies) {
        mMovies = movies;
    }

    /**
     * ViewHolder class, holding data of each movie view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Movie mMovie;

        final View mView;
        final ImageView mMovieItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMovieItem = (ImageView) view.findViewById(R.id.iv_movie_item);
        }
    }

    /**
     * Update the movie data of the recycler view adapter
     * @param movieData - movie data to update
     */
    public void updateMovieData(ArrayList<Movie> movieData) {
        mMovies.clear();
        mMovies.addAll(movieData);
        notifyDataSetChanged();
    }

    /**
     * Get the movie list of the adapter
     * @return - list of movies
     */
    public ArrayList<Movie> getMovies() {
        // Return the movie list
        return mMovies;
    }

    /**
     * Get the movie at a certain position
     *
     * @param position - position of the movie to get from the recylcer view
     * @return movie on a certain position
     */
    private Movie getMovieAt(int position) {
        return mMovies.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mMovie = getMovieAt(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_EXTRA_NAME, holder.mMovie);

                context.startActivity(intent);
            }
        });

        Picasso.with(holder.mMovieItem.getContext()).load(getMovieAt(position).getPosterUrl()).into(holder.mMovieItem);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
