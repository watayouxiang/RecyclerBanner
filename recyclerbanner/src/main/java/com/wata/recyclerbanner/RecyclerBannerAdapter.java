package com.wata.recyclerbanner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerBannerAdapter<BannerView extends View, Data> extends RecyclerView.Adapter {
    private List<Data> dataList = new ArrayList<>();
    private OnListener listener;

    public void setData(List<Data> data) {
        dataList.clear();
        if (data != null) {
            dataList.addAll(data);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onDataChanged();
        }
    }

    @NonNull
    public List<Data> getData() {
        return dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BannerView view = onCreateBannerItem(parent);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setId(R.id.icon);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BannerView view = holder.itemView.findViewById(R.id.icon);
        int realPosition = position % dataList.size();
        Data data = dataList.get(realPosition);
        onSetBannerItem(view, data);
    }

    @Override
    public int getItemCount() {
        return dataList.size() > 0 ? Integer.MAX_VALUE : 0;
    }

    public abstract BannerView onCreateBannerItem(ViewGroup parent);

    public abstract void onSetBannerItem(BannerView bannerView, Data data);

    interface OnListener {
        void onDataChanged();
    }

    void setListener(OnListener listener) {
        this.listener = listener;
    }
}
