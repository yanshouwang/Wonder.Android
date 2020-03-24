package dev.yanshouwang.wonder.launcher.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.yanshouwang.wonder.launcher.R;
import dev.yanshouwang.wonder.launcher.model.AppModel;

public class AppsAdapter extends RecyclerView.Adapter {
    private final List<AppModel> mModels;

    public AppsAdapter(@NonNull List<AppModel> models) {
        mModels = models;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_app, parent, false);
        return new AppsViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppsViewHolder holder1 = (AppsViewHolder) holder;
        AppModel model = mModels.get(position);
        holder1.setModel(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }
}
