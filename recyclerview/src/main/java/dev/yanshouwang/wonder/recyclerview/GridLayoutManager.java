package dev.yanshouwang.wonder.recyclerview;

import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }
}
