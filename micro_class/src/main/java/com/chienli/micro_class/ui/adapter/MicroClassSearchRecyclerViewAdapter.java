package com.chienli.micro_class.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chienli.micro_class.R;
import com.chienli.micro_class.R2;
import com.chienli.micro_class.data_model.SearchItemInfo;
import com.chienli.micro_class.ui.adapter.recycler_click_interface.DFRecyclerViewClickableAdapter;
import com.chienli.micro_class.ui.adapter.recycler_click_interface.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MicroClassSearchRecyclerViewAdapter extends RecyclerView.Adapter<MicroClassSearchRecyclerViewAdapter.Holder> {

    private List<SearchItemInfo> data;
    private Context context;
    private OnItemClickListener listener;

    public MicroClassSearchRecyclerViewAdapter(Context context, List<SearchItemInfo> data, OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_rcl, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.title.setText(data.get(i).getVideoText());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(List<SearchItemInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;// 视频标题

        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, getLayoutPosition());
            }
        }
    }
}
