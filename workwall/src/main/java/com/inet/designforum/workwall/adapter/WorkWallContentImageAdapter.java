package com.inet.designforum.workwall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.inet.designforum.workwall.R;
import com.inet.designforum.workwall.activity.WorkWallContentActivity;
import com.inet.designforum.workwall.bean.WorkInfo;

import java.util.List;

/**
 * 作品墙图片部分适配器
 */
public class WorkWallContentImageAdapter extends RecyclerView.Adapter<WorkWallContentImageAdapter.MyViewHolder> {

    private static final String TAG = "【WorkWallContentImage";

    private WorkInfo mWorkInfo;
    private Context mContext;

    private List<String> workImageList;

    public WorkWallContentImageAdapter(Context mContext, WorkInfo mWorkInfo) {
        this.mContext = mContext;
        this.mWorkInfo = mWorkInfo;
        workImageList = mWorkInfo.getWorkImageList();
        Log.e(TAG, workImageList.size() + "");
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivWorkContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkContent = itemView.findViewById(R.id.iv_work_wall_content_work_content);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // 这里传入WorkWallContentImageItem的布局
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_work_wall_content_image_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String imgPath = mWorkInfo.getPath();
        String host = WorkWallContentActivity.HOST;
        String img = workImageList.get(myViewHolder.getAdapterPosition());

        String url = host + "/images/work/" + imgPath + "/" + img;
        Log.e(TAG, "ImgUrl:" + url);

        RequestOptions options = new RequestOptions();
        RequestOptions placeholder = options.placeholder(R.drawable.ic_work_wall_content_pre_load)
                .error(R.drawable.ic_work_wall_content_error);
        Glide.with(mContext).load(url).apply(placeholder).into(myViewHolder.ivWorkContent);

        if (mOnItemImgClickListener != null) {
            myViewHolder.ivWorkContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemImgClickListener.onItemImgClick(url);
                }
            });
        }

//        new Thread(()->{
//            try {
//                final Bitmap bitmap = Glide.with(myViewHolder.ivWorkContent).asBitmap().load(url).submit().get();
//                int imgWidth = bitmap.getWidth();
//                int imgHeight = bitmap.getHeight();
//                float s = imgWidth / (imgHeight * 1.0f);
//                ViewGroup.LayoutParams params = myViewHolder.ivWorkContent.getLayoutParams();
//                int winWidth = DisplayUtil.getDisplayWidth(mContext);
//                params.width = winWidth;
//                params.height = (int) (winWidth * s);
//
//                new Handler(Looper.getMainLooper()).post(()->{
//                    myViewHolder.ivWorkContent.setLayoutParams(params);
//                    Glide.with(myViewHolder.ivWorkContent).load(bitmap).into(myViewHolder.ivWorkContent);
//                });
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }


    /**
     * ItemImgClickListener的回调接口
     */
    public interface OnItemImgClickListener {
        void onItemImgClick(String imgUrl);
    }

    private OnItemImgClickListener mOnItemImgClickListener;

    public void setOnItemClickListener(OnItemImgClickListener mOnItemImgClickListener) {
        this.mOnItemImgClickListener = mOnItemImgClickListener;
    }

    @Override
    public int getItemCount() {
        return workImageList.size();
    }
}
