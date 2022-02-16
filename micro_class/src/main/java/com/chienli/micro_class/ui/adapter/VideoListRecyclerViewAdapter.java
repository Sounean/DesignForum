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
import com.chienli.micro_class.data_model.MicroClassInfo;
import com.chienli.micro_class.databinding.ItemVideoListRclBinding;
import com.chienli.micro_class.ui.activity.MicroClassActivity;
import com.chienli.micro_class.ui.adapter.base.BaseBindingRecyclerViewHolder;
import com.chienli.micro_class.util.ActivityManagerUtil;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

public class VideoListRecyclerViewAdapter extends RecyclerView.Adapter<BaseBindingRecyclerViewHolder<ItemVideoListRclBinding>> {

    private final Context context;
    private final ObservableField<MicroClassInfo> info;
    private final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
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

    public VideoListRecyclerViewAdapter(Context context, ObservableField<MicroClassInfo> info) {
        this.context = context;
        this.info = info;
        this.info.addOnPropertyChangedCallback(callback);
    }

    @NonNull
    @Override
    public BaseBindingRecyclerViewHolder<ItemVideoListRclBinding> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BaseBindingRecyclerViewHolder<>(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_video_list_rcl, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBindingRecyclerViewHolder<ItemVideoListRclBinding> holder, int i) {
        holder.binding.setPosition(i);
        holder.binding.setData(info);
        holder.binding.setActivityVM(ActivityManagerUtil.findActivityByClass(MicroClassActivity.class).getBinding().getMicroClassVM());
    }

    @Override
    public int getItemCount() {
        MicroClassInfo info = this.info.get();
        if (info != null) {
            if (info.getVideo() != null) {
                return info.getVideo().size();
            }
        }
        return 0;
    }
}
