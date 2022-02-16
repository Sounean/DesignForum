package com.chienli.micro_class.ui.adapter.base;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public class BaseMultiDataBindingRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ViewDataBinding binding;

    public  BaseMultiDataBindingRecyclerViewHolder(@NonNull ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
