package com.inet.designforum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inet.designforum.R;
import com.inet.designforum.activity.PersonalCenterToolsActivity;
import com.inet.designforum.activity.SettingActivity;
import com.inet.designforum.adapter.recycler_click_interface.DFRecyclerViewClickableAdapter;
import com.inet.designforum.adapter.recycler_click_interface.OnItemClickListener;
import com.inet.designforum.fragment.MainPersonalCenterFragment;
import com.inet.designforum.fragment.SettingFragment;
import com.inet.designforum.view.callback.DFRecyclerViewAdapter;

import java.util.List;

public class MainPersonalCenterToolsRecyclerAdapter
        extends DFRecyclerViewClickableAdapter<MainPersonalCenterToolsRecyclerAdapter.ViewHolder> {

    private List<MainPersonalCenterFragment.Tool> tools;
    private Context mContext;


    public MainPersonalCenterToolsRecyclerAdapter(Context context, List<MainPersonalCenterFragment.Tool> tools, OnItemClickListener listener) {
        super(listener);
        this.tools = tools;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(
                viewGroup.getContext(),
                R.layout.layout_main_personal_center_recycler_item,
                null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Glide.with(mContext).load(tools.get(i).getIcon()).into(viewHolder.icon);
        viewHolder.toolName.setText(tools.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return tools.size();
    }


    class ViewHolder extends DFRecyclerViewClickableAdapter.DFViewHolder{
        View itemView;
        ImageView icon;
        TextView toolName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            icon = itemView.findViewById(R.id.iv_main_personal_center_recycler_item);
            toolName = itemView.findViewById(R.id.tv_main_personal_center_recycler_item);
            // item的点击事件在父类中
        }

    }
}
