package com.inet.designforum.workwall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chienli.design_forum_all_lib.bean.UserInfo;
import com.chienli.design_forum_all_lib.service.MainServices;
import com.inet.designforum.workwall.R;
import com.inet.designforum.workwall.activity.WorkWallContentActivity;
import com.inet.designforum.workwall.bean.WorkComment;
import com.inet.designforum.workwall.util.MyUtil;

import java.util.List;

/**
 * 作品墙评论部分适配器
 */
public class WorkWallContentCommentAdapter extends RecyclerView.Adapter<WorkWallContentCommentAdapter.MyViewHolder> {

    private List<WorkComment> mWorkCommentList;
    private Context mContext;

    private String HOST;
    private MainServices.MainBinder mainBinder;
    private UserInfo userInfo;


    // 得到全局数据
    private void getApplicationData() {
        mainBinder = WorkWallContentActivity.mainBinder;
        HOST = WorkWallContentActivity.HOST;
        userInfo = WorkWallContentActivity.userInfo;
    }

    public void setmWorkCommentList(List<WorkComment> mWorkCommentList) {
        this.mWorkCommentList = mWorkCommentList;
    }


    // 构造方法
    public WorkWallContentCommentAdapter(Context mContext, List<WorkComment> mWorkCommentList) {
        this.mWorkCommentList = mWorkCommentList;
        this.mContext = mContext;
        getApplicationData();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserHead;
        private TextView tvUserName;
        public ImageView ivCommentLike;
        public TextView tvCommentLikeNum;
        private TextView tvDiscussTime;
        private LinearLayout llCommentLikeArea;
        private TextView tvDiscussText;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserHead = itemView.findViewById(R.id.iv_work_wall_content_user_head);
            tvUserName = itemView.findViewById(R.id.tv_work_wall_content_user_name);
            ivCommentLike = itemView.findViewById(R.id.iv_work_wall_content_comment_like);
            tvCommentLikeNum = itemView.findViewById(R.id.tv_work_wall_content_comment_like_num);
            tvDiscussTime = itemView.findViewById(R.id.tv_work_wall_content_comment_time);
            llCommentLikeArea = itemView.findViewById(R.id.ll_work_wall_content_comment_click_like_area);
            tvDiscussText = itemView.findViewById(R.id.tv_work_wall_content_comment_words);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_work_wall_content_comment_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    /**
     * 对控件进行数据绑定【！！！网络交互在这一块】
     *
     * @param myViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        // 取出所需数据
        WorkComment workComment = mWorkCommentList.get(myViewHolder.getAdapterPosition());
        int id = workComment.getId();   //该条评论的id
        int discussLikeSize = workComment.getDiscussLikeSize(); //评论点赞数
        String discussText = workComment.getDiscussText();  // 评论内容
        String discussTime = workComment.getDiscussTime();  // 评论时间

        /* 注：UserHead与UserName都没有，所以只能先虚拟一个 */
        String userHead = "head.png";
        String userName = "评论者";

        // 绑定数据
        String headImgUrl = HOST + "/images/head/" + userHead;  // 评论者头像图片
        Glide.with(mContext).load(headImgUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(myViewHolder.ivUserHead);
        myViewHolder.tvUserName.setText(userName);  // 评论者名字

        String pattern = "yyyy-MM-dd HH:mm:ss.s";   // 日期的格式
        String showTime = MyUtil.getCompareTime(pattern, discussTime);
        myViewHolder.tvDiscussTime.setText(showTime);   // 评论日期
        myViewHolder.tvCommentLikeNum.setText("" + discussLikeSize);    // 评论点赞数
        myViewHolder.tvDiscussText.setText(discussText);    // 评论内容

        // 已经点过赞，显示已点赞状态的图片
        if (userInfo.getDiscuss_like().contains(id)) {   // 若有该条评论的id
            myViewHolder.ivCommentLike.setBackgroundResource(R.drawable.ic_work_wall_content_like_icon_selected);
        } else {
            myViewHolder.ivCommentLike.setBackgroundResource(R.drawable.ic_work_wall_content_like_icon_no_selected);
        }

        // 如果listener不为空，且之前未点过赞的评论内容才能进行点击
        if (commentLikeListener != null) {
            // 喜欢评论的点击事件【改变是否喜欢的状态】
            myViewHolder.llCommentLikeArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo.getDiscuss_like().contains(id)) {
                        Toast.makeText(mContext, "你已经点过赞了", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    commentLikeListener.onCommentLikeClicked(myViewHolder, myViewHolder.getAdapterPosition());  //进行回调，向服务器端发送点赞信息
                }
            });
        }
    }

    /**
     * 当评论被赞时的回调接口
     */
    public interface onCommentLikeClickedListener {
        void onCommentLikeClicked(MyViewHolder holder, int position);
    }

    private onCommentLikeClickedListener commentLikeListener;

    public void setOnCommentLikeClickedListener(onCommentLikeClickedListener listener) {
        this.commentLikeListener = listener;
    }

    @Override
    public int getItemCount() {
        return mWorkCommentList.size();
    }

}
