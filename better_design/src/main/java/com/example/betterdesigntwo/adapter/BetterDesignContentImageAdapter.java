package com.example.betterdesigntwo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.betterdesigntwo.R;
import com.example.betterdesigntwo.util.DisplayUtil;

import java.util.List;

public class BetterDesignContentImageAdapter extends RecyclerView.Adapter<BetterDesignContentImageAdapter.ViewHolder> {

    private List<String> imgId;
    private Context mContext;

    public BetterDesignContentImageAdapter(List<String> imgId){
        this.imgId = imgId;
    }

    @NonNull
    @Override
    public BetterDesignContentImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_betterdesign_content_img_item , viewGroup , false);
        ViewHolder holder = new ViewHolder(view);
        mContext =viewGroup.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BetterDesignContentImageAdapter.ViewHolder viewHolder, int i) {
        //viewHolder.iv.setBackgroundResource(imgId.indexOf(i));
       /* Glide.with(mContext)
                .load(imgId.get(i))
                .into(viewHolder.iv);*/ // 原来的
        /*Glide.with(mContext).load(imgId.get(i))
                .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(viewHolder.iv);*/ // 这里失误，改成圆的去了
        RequestOptions options = new RequestOptions();
        RequestOptions placeholder = options.placeholder(R.drawable.betterdesignplaceholder)
                .error(R.drawable.betterdesignerror);
        Glide.with(mContext).load(imgId.get(i)).apply(placeholder).into(viewHolder.iv);

        /*
        * 这里将宽长按比例，宽占满屏幕
        * */
        viewHolder.iv.setMinimumWidth(DisplayUtil.getDisplayWidth(mContext));
        viewHolder.iv.setMaxWidth(DisplayUtil.getDisplayHeight(mContext));
        viewHolder.iv.setMaxHeight(Integer.MAX_VALUE);
        viewHolder.iv.setMinimumHeight(0);
    }

    @Override
    public int getItemCount() {
        return imgId.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_better_design_content_img);
        }
    }
}
