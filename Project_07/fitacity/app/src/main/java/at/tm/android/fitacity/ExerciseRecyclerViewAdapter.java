package at.tm.android.fitacity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.utilities.CircleTransform;

/**
 * ExerciseRecyclerViewAdapter class
 */
public class ExerciseRecyclerViewAdapter extends RecyclerView.Adapter<ExerciseRecyclerViewAdapter.ViewHolder> {

    private List<Exercise> mExercises;

    public ExerciseRecyclerViewAdapter(List<Exercise> exercises) {
        mExercises = exercises;
    }

    public ExerciseRecyclerViewAdapter() {
        mExercises = new ArrayList<>();
    }

    /**
     * ViewHolder class, holding data of each movie view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Exercise mExercise;

        final View mView;
        final TextView mName;
        final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.name);
            mImageView = (ImageView) view.findViewById(R.id.avatar);
        }
    }

    /**
     * Update the exercise data of the recycler view adapter
     * @param exercises - exercises to update
     */
    public void updateExerciseData(List<Exercise> exercises) {
        mExercises.clear();
        mExercises.addAll(exercises);

        notifyDataSetChanged();
    }

    /**
     * Get the exercise list of the adapter
     * @return - list of exercises
     */
    public List<Exercise> getExercises() {
        // Return the exercise list
        return mExercises;
    }

    /**
     * Get the exercise at a certain position
     *
     * @param position - position of the exercise to get from the recylcer view
     * @return exercise on a certain position
     */
    private Exercise getExerciseAt(int position) {
        return mExercises.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mExercise = getExerciseAt(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ExerciseDetailActivity.class);
                intent.putExtra(ExerciseDetailActivity.EXERCISE_EXTRA_NAME, holder.mExercise);

                context.startActivity(intent);
            }
        });

        holder.mName.setText(holder.mExercise.getName());

        // Load the image to the imageview and use a circle transformation
        Glide.with(holder.mImageView.getContext())
                .load(holder.mExercise.getImgUrl())
                .transform(new CircleTransform(holder.mImageView.getContext()))
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }
}
