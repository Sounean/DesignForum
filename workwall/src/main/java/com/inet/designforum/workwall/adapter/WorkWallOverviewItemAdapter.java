package com.inet.designforum.workwall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.inet.designforum.workwall.R;
import com.inet.designforum.workwall.activity.WorkWallOverviewActivity;
import com.inet.designforum.workwall.bean.WorkInfo;
import com.inet.designforum.workwall.util.MyOverviewUtil;
import com.inet.designforum.workwall.util.MyUtil;

import java.util.List;

/**
 * 作品墙预览项适配器
 */
public class WorkWallOverviewItemAdapter extends RecyclerView.Adapter<WorkWallOverviewItemAdapter.MyViewHolder> {

    private static final String TAG = "【WorkWallOvervi";

    private List<WorkInfo> mWorkInfoList;
    private Context mContext;

    public WorkWallOverviewItemAdapter(Context mContext, List<WorkInfo> mWorkInfoList) {
        this.mWorkInfoList = mWorkInfoList;
        this.mContext = mContext;
    }

    public List<WorkInfo> getmWorkInfoList() {
        return mWorkInfoList;
    }

    public void setmWorkInfoList(List<WorkInfo> mWorkInfoList) {
        this.mWorkInfoList = mWorkInfoList;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 内部定义的MyViewHolder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // 以下为要获取的控件
        private TextView tvWorkName;
        private ImageView ivPreviewPic;
        private ImageView ivPublisherIcon;
        private TextView tvPublisherName;
        private TextView tvIssueTime;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // 在 MyViewHolder 中进行控件的获取
            // 作品名
            tvWorkName = itemView.findViewById(R.id.tv_work_wall_overview_work_name);

            // 预览图
            ivPreviewPic = itemView.findViewById(R.id.iv_work_wall_overview_work_preview_pic);

            // 发布者头像【在 web 端是UserIcon】
            ivPublisherIcon = itemView.findViewById(R.id.iv_work_wall_overview_work_publisher_icon);

            // 发布者姓名【在 web 端是UserName】
            tvPublisherName = itemView.findViewById(R.id.tv_work_wall_overview_work_publisher_name);

            // 发布时间【注：这是一个比较值，在客户端中要进行处理】
            tvIssueTime = itemView.findViewById(R.id.tv_work_wall_overview_work_issue_time);
        }
    }

    /**
     * 创建并返回 MyViewHolder 对象
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // 由于这里只传入 WorkWallOverview 的子项布局，因此可写死
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_work_wall_overview_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * 对控件进行数据绑定【！！！网络交互在这一块】
     *
     * @param myViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        WorkInfo workInfo = mWorkInfoList.get(myViewHolder.getAdapterPosition());
        // 取出所需要的数据
        int id = workInfo.getId();
        String userHead = workInfo.getUserHead();   // 上传者头像
        String userName = workInfo.getUserName();   // 上传者名字
        String workText = workInfo.getWorkText();   // 作品名称
        String workTime = workInfo.getWorkTime();   // 上传时间
        String imgPath = workInfo.getPath();   // 图片路径
        List<String> workImageList = workInfo.getWorkImageList();   // 作品中的图片
        Log.e(TAG, workImageList.get(0));

        // 进行数据绑定
        myViewHolder.tvWorkName.setText(workText);

        String HOST = WorkWallOverviewActivity.HOST;    //作品第一张
        String workImgUrl = HOST + "/images/work/" + imgPath + "/" + workImageList.get(0);
        Glide.with(mContext).load(workImgUrl).into(myViewHolder.ivPreviewPic);

        String headImgUrl = HOST + "/images/head/" + userHead;     // 上传者头像
        Glide.with(mContext).load(headImgUrl)
                .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(myViewHolder.ivPublisherIcon);
        myViewHolder.tvPublisherName.setText(userName);     // 上传者名字


        String pattern = "yyyy-MM-dd HH:mm:ss.s";   // 日期的格式
        String showTime = MyUtil.getCompareTime(pattern, workTime);
        myViewHolder.tvIssueTime.setText(showTime);

        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(myViewHolder.itemView, myViewHolder.getAdapterPosition());
                }
            });
        }
    }

    /**
     * @return 返回总共有多少项
     */
    @Override
    public int getItemCount() {
        return mWorkInfoList.size();
    }

}
