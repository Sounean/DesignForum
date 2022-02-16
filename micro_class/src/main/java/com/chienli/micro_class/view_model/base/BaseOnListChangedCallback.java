package com.chienli.micro_class.view_model.base;

import android.databinding.ObservableList;

/**
 * 简单的重写
 * @param <T>
 */
public class BaseOnListChangedCallback<T extends ObservableList> extends ObservableList.OnListChangedCallback<T> {
    @Override
    public void onChanged(T sender) {

    }

    @Override
    public void onItemRangeChanged(T sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeInserted(T sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeMoved(T sender, int fromPosition, int toPosition, int itemCount) {

    }

    @Override
    public void onItemRangeRemoved(T sender, int positionStart, int itemCount) {

    }
}
