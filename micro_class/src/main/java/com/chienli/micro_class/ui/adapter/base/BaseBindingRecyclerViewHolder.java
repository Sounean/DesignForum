package com.chienli.micro_class.ui.adapter.base;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public class BaseBindingRecyclerViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public T binding;

    public  BaseBindingRecyclerViewHolder(@NonNull T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
