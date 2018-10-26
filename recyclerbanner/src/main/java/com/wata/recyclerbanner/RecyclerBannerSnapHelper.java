package com.wata.recyclerbanner;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class RecyclerBannerSnapHelper extends LinearSnapHelper {
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
        final View currentView = findSnapView(layoutManager);
        if (targetPos != RecyclerView.NO_POSITION && currentView != null) {
            int currentPosition = layoutManager.getPosition(currentView);
            int first = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            int last = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            currentPosition = targetPos < currentPosition ? last : (targetPos > currentPosition ? first : currentPosition);
            targetPos = targetPos < currentPosition ? currentPosition - 1 : (targetPos > currentPosition ? currentPosition + 1 : currentPosition);
        }
        return targetPos;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
    }
}
