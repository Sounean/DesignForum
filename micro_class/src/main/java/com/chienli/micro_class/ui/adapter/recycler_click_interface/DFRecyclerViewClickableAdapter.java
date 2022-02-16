package com.chienli.micro_class.ui.adapter.recycler_click_interface;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 比较第一代的适配器，简化了操作，将接口传了进来，不在内部实现点击事件，交给有生命周期的Class来完成
 * @param <VH> 类型就不多说了啊
 */
public abstract class DFRecyclerViewClickableAdapter<VH extends DFRecyclerViewClickableAdapter.DFViewHolder> extends RecyclerView.Adapter<VH> {

    protected OnItemClickListener listener;

    public DFRecyclerViewClickableAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class DFViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DFViewHolder(@NonNull View itemView) {
            super(itemView);
            if (listener != null) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getLayoutPosition());
        }
    }
}
