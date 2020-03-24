package dev.yanshouwang.wonder.launcher.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import dev.yanshouwang.wonder.launcher.R;
import dev.yanshouwang.wonder.launcher.model.AppModel;

class AppsViewHolder extends RecyclerView.ViewHolder {
    private final Context mContext;
    private final ImageView mIconView;
    private final TextView mLabelView;
    private ComponentName mComponentName;

    public AppsViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        mIconView = view.findViewById(R.id.iconView);
        mLabelView = view.findViewById(R.id.labelView);

        mIconView.setOnClickListener(v -> onClick());
    }

    private void onClick() {
        if (mComponentName == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(mComponentName);
        mContext.startActivity(intent);
    }

    public void setModel(AppModel model) {
        mIconView.setImageDrawable(model.icon);
        mLabelView.setText(model.label);
        mComponentName = model.componentName;
    }
}
