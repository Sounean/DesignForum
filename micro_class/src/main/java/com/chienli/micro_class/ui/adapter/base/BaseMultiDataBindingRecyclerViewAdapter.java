package com.chienli.micro_class.ui.adapter.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class BaseMultiDataBindingRecyclerViewAdapter<MAIN_DATA> extends RecyclerView.Adapter<BaseMultiDataBindingRecyclerViewHolder> {

    private final Context context;
    private final int itemLayoutId;
    private final int rclAdapterPositionBRId;
    private final Object[] data;
    private final int[] dataBRIds;
    private final ObservableInt size;// item的数量

    public BaseMultiDataBindingRecyclerViewAdapter(Context context, int itemLayoutId, int rclAdapterPositionBRId, Object[] data, int[] dataBRIds, ObservableInt size) {
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.rclAdapterPositionBRId = rclAdapterPositionBRId;
        this.data = data;
        this.dataBRIds = dataBRIds;
        this.size = size;
    }

    @NonNull
    @Override
    public BaseMultiDataBindingRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), itemLayoutId, viewGroup, false);
        return new BaseMultiDataBindingRecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMultiDataBindingRecyclerViewHolder holder, int i) {
        for (int j = 0; j < data.length; j++) {
            holder.binding.setVariable(dataBRIds[j], data[i]);
        }
    }

    @Override
    public int getItemCount() {
        return size.get();
    }
}
