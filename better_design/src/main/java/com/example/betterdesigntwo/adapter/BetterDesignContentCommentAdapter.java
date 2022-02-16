package com.example.betterdesigntwo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.betterdesigntwo.R;
import com.example.betterdesigntwo.bean.BetterDesignContentCommentItems;

import java.util.List;

public class BetterDesignContentCommentAdapter extends RecyclerView.Adapter<BetterDesignContentCommentAdapter.ViewHolder> {

    private boolean bLike;
    List<BetterDesignContentCommentItems> commentItems;

    public BetterDesignContentCommentAdapter(List<BetterDesignContentCommentItems> items){
        commentItems = items;
    }
    @NonNull
    @Override
    public BetterDesignContentCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_better_design_content_comment_item , viewGroup , false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BetterDesignContentCommentAdapter.ViewHolder viewHolder, int i) {
        if (commentItems!=null&&commentItems.size()>0) {//先按网上说的先进行判空处理
            //BetterDesignContentCommentItems item = commentItems.get(i);
           // BetterDesignContentCommentItems item = commentItems.get(commentItems.size()-i-1);//因为做了list翻转，所以这句就变了
            BetterDesignContentCommentItems item = commentItems.get(i);
            viewHolder.ivCommentHeardImg.setImageResource(item.getUserhead());//设置头像
            viewHolder.tvCommentUserName.setText(item.getUsername());//设置用户名
            viewHolder.tvCommentLikeNum.setText(item.getLikenum() + "");//设置点赞的数量
            viewHolder.tvCommentWords.setText(item.getCommentwords());//设置评论语
        }
        // “喜欢”的点击事件
        viewHolder.llCommentLikeAreaLll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bLike) {
                    viewHolder.ivCommentLikeImg.setImageResource(R.drawable.ic_better_design_content_like_icon_no_selected);
                } else {
                    viewHolder.ivCommentLikeImg.setImageResource(R.drawable.ic_better_design_content_like_icon_selected);
                }
                bLike = !bLike;
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentItems.size();//这里修改了上次只能显示两次的bug
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llCommentLikeAreaLll;
        TextView tvCommentWords;
        TextView tvCommentLikeNum;
        ImageView ivCommentLikeImg;
        TextView tvCommentUserName;
        ImageView ivCommentHeardImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //头像
            ivCommentHeardImg = itemView.findViewById(R.id.iv_better_design_content_user_head);
            //用户名
            tvCommentUserName = itemView.findViewById(R.id.tv_better_design_content_user_name);
            //是否点赞的图标
            ivCommentLikeImg = itemView.findViewById(R.id.iv_better_design_content_comment_like);
            //点赞的数量
            tvCommentLikeNum = itemView.findViewById(R.id.tv_better_design_content_comment_like_num);
            //评论语
            tvCommentWords = itemView.findViewById(R.id.tv_better_design_content_comment_words);
            //点赞的整个LinearLayout（怕单个图片点不准，索性范围大点，线型图点到都算）
            llCommentLikeAreaLll = itemView.findViewById(R.id.ll_better_design_content_comment_click_like_area);
        }
    }
}
