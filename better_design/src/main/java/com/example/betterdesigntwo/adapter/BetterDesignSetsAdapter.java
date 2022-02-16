package com.example.betterdesigntwo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.betterdesigntwo.R;
import com.example.betterdesigntwo.activity.BetterDesignActivity;
import com.example.betterdesigntwo.activity.BetterDesignDeepActivity;
import com.example.betterdesigntwo.bean.BetterDesignSets;
import com.example.betterdesigntwo.util.DisplayUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/*
* 优设计的作品集页面
*
* */
public class BetterDesignSetsAdapter extends RecyclerView.Adapter<BetterDesignSetsAdapter.ViewHolder> {


    private List<BetterDesignSets> mBetterDesignSets;
    private Context mContext;
    private BetterDesignSets betterDesignSet;
    String mlistValues;//大的list，string类型


    public class ViewHolder extends RecyclerView.ViewHolder {
        View betterDesignView;//定义出一个view来保存外层item
        private final ImageView ivBetterDesign;
        private final TextView tvBetterDesign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            betterDesignView = itemView;
            ivBetterDesign = itemView.findViewById(R.id.iv_betterdesignsets);
            tvBetterDesign = itemView.findViewById(R.id.tv_betterdesignsets);
        }
    }

    public BetterDesignSetsAdapter(List<BetterDesignSets> list , String listValue){
        mBetterDesignSets = list;
        mlistValues = listValue;
    }

    @NonNull
    @Override
    public BetterDesignSetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_betterdesign_item , viewGroup , false);
        final ViewHolder holder = new ViewHolder(view);
        /*
        * 这里作为测试，就把viewGroup看下是不是主方法里的Context
        *
        * */
        mContext = viewGroup.getContext();
        /*
        * item的点击事件
        * */
        holder.betterDesignView.setOnClickListener(new View.OnClickListener() {

            private String objString;
            private String path;
            private int id;
            private JSONArray jsonArray;

            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();//第几个item
                try {
                    //大的list  jsonobject类型
                    jsonArray = new JSONArray(mlistValues);//大的list，JSONArray类型
                    JSONObject obj = jsonArray.getJSONObject(position);
                    objString = String.valueOf(jsonArray.getJSONObject(position));
                    path = obj.getString("path");

                    /*+++++++++++*/
                    id = obj.getInt("id");//企图把作品id值传递过去，如果那么收到了，那么就可以通过作品id来获取评论内容了。
                    /*+++++++++++*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                        Intent intent = new Intent(mContext , BetterDesignDeepActivity.class);
                        intent.putExtra("values" , objString);//将小的list传了进去
                        intent.putExtra("position" , position);
                        intent.putExtra("path" , path);
                        /*+++++++++++*/
                        intent.putExtra("id" , id);
                        /*+++++++++++*/
                        mContext.startActivity(intent);

                /*String data = "要传递给最后一层activity的内容";
                Intent intent = new Intent(mContext , BetterDesignDeepActivity.class);
                intent.putExtra("这是键" , data);
                mContext.startActivity(intent);*/
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BetterDesignSetsAdapter.ViewHolder viewHolder, int i) {
        betterDesignSet = mBetterDesignSets.get(i);
        /*Glide.with(mContext)
                .load(betterDesignSet.getPic())
                .into(viewHolder.ivBetterDesign);*/ // 原来的
       /* Glide.with(mContext).load(betterDesignSet.getPic())
                .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(viewHolder.ivBetterDesign);*/ // 这里失误
        RequestOptions options = new RequestOptions();
        RequestOptions placeholder = options.placeholder(R.drawable.betterdesignplaceholder)
                .error(R.drawable.betterdesignerror);
        Glide.with(mContext).load(betterDesignSet.getPic()).apply(placeholder).into(viewHolder.ivBetterDesign);

        viewHolder.tvBetterDesign.setText(betterDesignSet.getText());

        /*
         * 这里将宽长按比例，宽占满屏幕
         * */
        viewHolder.ivBetterDesign.setMinimumWidth(DisplayUtil.getDisplayWidth(mContext));
        viewHolder.ivBetterDesign.setMaxWidth(DisplayUtil.getDisplayHeight(mContext));
        viewHolder.ivBetterDesign.setMaxHeight(Integer.MAX_VALUE);
        viewHolder.ivBetterDesign.setMinimumHeight(0);

    }

    @Override
    public int getItemCount() {
        return mBetterDesignSets.size();
    }


}
