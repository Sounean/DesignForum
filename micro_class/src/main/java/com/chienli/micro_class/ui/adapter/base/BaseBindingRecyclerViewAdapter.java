package com.chienli.micro_class.ui.adapter.base;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chienli.micro_class.view_model.base.BaseOnListChangedCallback;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BaseBindingRecyclerViewAdapter<DATA> extends RecyclerView.Adapter<BaseBindingRecyclerViewHolder> {
    private static final String TAG = "BaseBindingRecyclerView";

    private final int itemLayoutId;
    private final ObservableArrayList<DATA> data;
    private final int rclAdapterModelBRId;
    private final int rclAdapterPositionBRId;

    public final BaseOnListChangedCallback<ObservableArrayList<DATA>> callback = new BaseOnListChangedCallback<ObservableArrayList<DATA>>() {
        @SuppressLint("CheckResult")
        @Override
        public void onChanged(ObservableArrayList<DATA> sender) {
            Observable
                    .create((ObservableOnSubscribe<ObservableList>) emitter -> emitter.onNext(sender))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observableList -> notifyDataSetChanged());
        }
    };

    public BaseBindingRecyclerViewAdapter(int itemLayoutId, ObservableArrayList<DATA> data, int rclAdapterModelBRId, int rclAdapterPositionBRId) {
        this.itemLayoutId = itemLayoutId;
        this.data = data;
        this.rclAdapterModelBRId = rclAdapterModelBRId;
        this.rclAdapterPositionBRId = rclAdapterPositionBRId;
        this.data.addOnListChangedCallback(callback);
    }

    @NonNull
    @Override
    public BaseBindingRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BaseBindingRecyclerViewHolder<>(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), itemLayoutId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBindingRecyclerViewHolder holder, int i) {
        holder.binding.setVariable(rclAdapterPositionBRId, i);
        holder.binding.setVariable(rclAdapterModelBRId, data);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
