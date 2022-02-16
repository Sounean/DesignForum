package com.inet.designforum.adapter;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chienli.design_forum_all_lib.application.DesignForumApplication;
import com.chienli.design_forum_all_lib.bean.CommentInfo;
import com.chienli.design_forum_all_lib.bean.CourseInfo;
import com.chienli.design_forum_all_lib.bean.WorkWallInfo;
import com.inet.designforum.R;
import com.inet.designforum.adapter.recycler_click_interface.DFRecyclerViewClickableAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;

import java.util.List;

import static com.inet.designforum.activity.PersonalCenterToolsActivity.TOOL_TYPE_COLLECT;
import static com.inet.designforum.activity.PersonalCenterToolsActivity.TOOL_TYPE_COMMENT;
import static com.inet.designforum.activity.PersonalCenterToolsActivity.TOOL_TYPE_COURSE;
import static com.inet.designforum.activity.PersonalCenterToolsActivity.TOOL_TYPE_FOLLOW;
import static com.inet.designforum.activity.PersonalCenterToolsActivity.TOOL_TYPE_LIKE;
import static com.inet.designforum.activity.PersonalCenterToolsActivity.TOOL_TYPE_WORKS;

public class PersonalCenterToolsRecyclerAdapter extends DFRecyclerViewClickableAdapter<PersonalCenterToolsRecyclerAdapter.Holder> {

    private List infos;
    private int type;

    public PersonalCenterToolsRecyclerAdapter(List infos, int type, OnItemClickListener listener) {
        super(listener);
        this.infos = infos;
        this.type = type;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_personal_center_tools_recycler_item, viewGroup, false);
        return new Holder(view);
    }

    private static final String TAG = "PersonalCenterToolsRecy";

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        RequestOptions requestOptions = RequestOptions.centerCropTransform().error(R.drawable.ic_error_cloud);
        switch (type) {
            case TOOL_TYPE_COURSE: {
                // 没写接口啦
                CourseInfo info = (CourseInfo) infos.get(i);
                holder.title.setText(info.getCourseName());
                holder.subTitle.setText(info.getCourseText());
                holder.likeAndFollow.setText(String.format("%s赞 · %s收藏", info.getLikeSize(), info.getCollectSize()));
                holder.type.setText("微课堂");
                holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.small_course, holder.type.getContext().getTheme()));
                if (null == info.getCourseImage() || !info.getCourseImage().equals("")) {
                    holder.img.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder(info.getCourseImage());
                    sb.insert(sb.lastIndexOf("."),"_compress");
                    Glide.with(holder.img).load(DesignForumApplication.HOST + "images/Course/" + sb.toString()).into(holder.img);
                } else {
                    holder.img.setVisibility(View.GONE);
                }
                break;
            }
            case TOOL_TYPE_WORKS: {
                // 图片路径为 域名/images/work/文件名
                WorkWallInfo info = (WorkWallInfo) infos.get(i);
                String imgUrl = info.getWorkImageList().size() == 0 ? null : info.getWorkImageList().get(0);
                String path = info.getPath(); // 文件名
                String title = info.getWorkText();
                String subTitle = info.getWorkLabelText();
                String likeAndFollow = String.format("%s赞 · %s收藏", info.getWorkLikeSize(), info.getWorkCollect());
                String type = "作品墙";

                if (imgUrl != null) {
                    holder.img.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder(imgUrl);
                    sb.insert(sb.lastIndexOf("."),"_compress");
                    Glide.with(holder.img).load(DesignForumApplication.HOST +"images/work/" + path + "/" + sb.toString()).apply(requestOptions).into(holder.img);
                } else {
                    holder.img.setVisibility(View.GONE);
                }
                holder.title.setText(title);
                holder.subTitle.setText(subTitle);
                holder.likeAndFollow.setText(likeAndFollow);
                holder.type.setText(type);
                holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.work_wall, holder.type.getContext().getTheme()));
                break;
            }
            case TOOL_TYPE_COMMENT: {
                // 在评论界面将title隐藏，然后likeAndFollow显示时间
                holder.title.setVisibility(View.GONE);

                CommentInfo info = (CommentInfo) infos.get(i);

                String comment = info.getDiscussText();
                String time = info.getDiscussTime().substring(0, 10);
                switch (info.getDiscussType()) {
                    case "1":
                        holder.type.setText("作品墙");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.work_wall, holder.type.getContext().getTheme()));
                        break;
                    case "2":
                        holder.type.setText("优设计");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.batter_design, holder.type.getContext().getTheme()));
                        break;
                    case "3":
                        holder.type.setText("微课堂");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.small_course, holder.type.getContext().getTheme()));
                        break;
                    default:
                        holder.type.setText("未知类型");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.config_color_75_pure_black, holder.type.getContext().getTheme()));
                        break;
                }
                holder.likeAndFollow.setText(time);
                holder.subTitle.setText(comment);
                break;
            }
            case TOOL_TYPE_COLLECT: {
                // 我的收藏
                // 图片路径为 域名/images/work/文件名
                WorkWallInfo info = (WorkWallInfo) infos.get(i);
                String imgUrl = info.getWorkImageList().size() == 0 ? null : info.getWorkImageList().get(0);
                String path = info.getPath(); // 文件名
                String title = info.getWorkText();
                String subTitle = info.getWorkLabelText();
                String likeAndFollow = String.format("%s赞 · %s收藏", info.getWorkLikeSize(), info.getWorkCollect());
                String type = "作品墙";

                if (imgUrl != null) {
                    holder.img.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder(imgUrl);
                    sb.insert(sb.lastIndexOf("."),"_compress");
                    Glide.with(holder.img).load(DesignForumApplication.HOST + "images/work/" + path + "/" + sb.toString()).apply(requestOptions).into(holder.img);
                } else {
                    holder.img.setVisibility(View.GONE);
                }
                holder.title.setText(title);
                holder.subTitle.setText(subTitle);
                holder.likeAndFollow.setText(likeAndFollow);
                holder.type.setText(type);
                holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.work_wall, holder.type.getContext().getTheme()));
                switch (info.getWorkType()) {
                    case "1":
                        holder.type.setText("作品墙");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.work_wall, holder.type.getContext().getTheme()));
                        break;
                    case "2":
                        holder.type.setText("优设计");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.batter_design, holder.type.getContext().getTheme()));
                        break;
                    case "3":
                        holder.type.setText("微课堂");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.small_course, holder.type.getContext().getTheme()));
                        break;
                    default:
                        holder.type.setText("未知类型");
                        holder.type.setTextColor(ResourcesCompat.getColor(holder.type.getResources(), R.color.config_color_75_pure_black, holder.type.getContext().getTheme()));
                        break;
                }
                break;
            }
            case TOOL_TYPE_LIKE: {
                break;
            }
            case TOOL_TYPE_FOLLOW: {
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }


    class Holder extends DFRecyclerViewClickableAdapter.DFViewHolder {

        ImageView img;
        TextView title;
        TextView subTitle;
        TextView type;
        TextView likeAndFollow;

        Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_personal_center_tools_recycler_item);
            title = itemView.findViewById(R.id.tv_personal_center_tools_recycler_item_title);
            subTitle = itemView.findViewById(R.id.tv_personal_center_tools_recycler_item_subtitle);
            type = itemView.findViewById(R.id.tv_personal_center_tools_recycler_type);
            likeAndFollow = itemView.findViewById(R.id.tv_personal_center_tools_recycler_item_like_and_follow);
            // 点击事件在父类
        }
    }
}
