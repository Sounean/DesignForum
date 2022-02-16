package com.inet.designforum.view.callback;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * DF就是DesignForum的意思啦, 主要是实现了itemView(或者是item根布局)的点击事件，以后直接用就OK
 * <p>
 * 使用这个就不可以直接用RecyclerView.ViewHolder了，要用DFRecyclerViewAdapter.Holder
 *
 * 现在是Parent专用，后面还有一个维护子视图的点击事件
 */
public abstract class DFRecyclerViewAdapter<H extends DFRecyclerViewAdapter.Holder> extends RecyclerView.Adapter<H> {

    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private OnItemClickListener onItemClickListener;
        private OnItemLongClickListener onItemLongClickListener;

        public Holder(@NonNull View itemView) {
            super(itemView);
            // 此处的点击事件是直接点击itemView, 所以直接点击啦
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, this.getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(v, this.getLayoutPosition());
                return true;
            }
            return false;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            this.onItemLongClickListener = onItemLongClickListener;
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
