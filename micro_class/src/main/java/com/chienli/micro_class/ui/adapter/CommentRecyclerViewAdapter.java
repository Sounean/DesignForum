package com.chienli.micro_class.ui.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chienli.micro_class.R;
import com.chienli.micro_class.data_model.CommentInfo;
import com.chienli.micro_class.databinding.ItemCommentRclBinding;
import com.chienli.micro_class.ui.adapter.base.BaseBindingRecyclerViewHolder;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<BaseBindingRecyclerViewHolder<ItemCommentRclBinding>> {

    private final Context context;
    private final ObservableField<CommentInfo> info;

    public CommentRecyclerViewAdapter(Context context, ObservableField<CommentInfo> info) {
        this.context = context;
        this.info = info;
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                io.reactivex.Observable
                        .empty()
                        .observeOn(AndroidSchedulers.mainThread())
                        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) context)))
                        .subscribe(new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Object o) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                notifyDataSetChanged();
                            }
                        });
            }
        };
        this.info.addOnPropertyChangedCallback(callback);
    }

    @NonNull
    @Override
    public BaseBindingRecyclerViewHolder<ItemCommentRclBinding> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BaseBindingRecyclerViewHolder<>(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_comment_rcl, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBindingRecyclerViewHolder<ItemCommentRclBinding> holder, int i) {
        holder.binding.setData(info);
        holder.binding.setPosition(i);
    }

    @Override
    public int getItemCount() {
        CommentInfo commentInfo = info.get();
        if (commentInfo != null) {
            List<CommentInfo.CommentBean> beans = commentInfo.getList();
            return beans.size();
        }
        return 0;
    }
}
