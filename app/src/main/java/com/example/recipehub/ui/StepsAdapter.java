package com.example.recipehub.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    private List<Step> steps;

    public StepsAdapter(List<Step> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = steps.get(position);

        holder.tvStepNumber.setText(String.valueOf(step.getStep_number()));
        holder.tvStepDescription.setText(step.getDescription());

        // Получаем URL изображения
        String imageUrl = null;
        if (step.getImage_url() != null && !step.getImage_url().isEmpty()) {
            imageUrl = step.getImage_url();
        }

        Log.d("StepsAdapter", "Step " + position + " image URL: " + imageUrl);

        if (isValidImageUrl(imageUrl)) {
            holder.ivStepImage.setVisibility(View.VISIBLE);
            Glide.with(holder.ivStepImage.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_add_image)
                    .error(R.drawable.ic_add_image)
                    .into(holder.ivStepImage);
            Log.d("StepsAdapter", "Image loaded for step " + position);
        } else {
            holder.ivStepImage.setVisibility(View.GONE);
            Log.d("StepsAdapter", "Invalid image URL for step " + position + ": " + imageUrl);
        }
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        // Проверяем что это валидный URL, а не локальный путь
        return url.startsWith("http://") ||
                url.startsWith("https://") ||
                url.startsWith("content://") ||
                url.startsWith("file://");
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber, tvStepDescription;
        ImageView ivStepImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.tvStepNumber);
            tvStepDescription = itemView.findViewById(R.id.tvStepDescription);
            ivStepImage = itemView.findViewById(R.id.ivStepImage);
        }
    }
}