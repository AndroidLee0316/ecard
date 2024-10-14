package com.pasc.business.ecardbag.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pasc.business.bike.R;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.widget.DensityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *  功能：排序列表
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public class EditDragAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EcardInfoResq.EcardInfoBean> mSortedList = new ArrayList<>();
    private OnEditDragListener onEditListener;

    public EditDragAdapter(Context mContext, OnEditDragListener onEditListener) {
        this.mContext = mContext;
        this.onEditListener = onEditListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pasc_ecard_drag_item, parent, false);
        return new CardlHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setCardInfo((CardlHolder) holder, mSortedList.get(position));
    }
    /**
     * 设置列表数据
     **/
    public void setNewData(List<EcardInfoResq.EcardInfoBean> list) {
        mSortedList.clear();
        mSortedList.addAll(list);
        notifyDataSetChanged();

    }
    /**
     * 获取列表数据
     **/
    public List<EcardInfoResq.EcardInfoBean> getData() {
        return mSortedList;

    }
    /**
     * 设置列表数据
     **/
    private void setCardInfo(final CardlHolder holder, final EcardInfoResq.EcardInfoBean bean) {
        holder.name.setText(bean.name);

        if (bean.bgimgUrl != null) {
            PascImageLoader.getInstance().loadImageUrl(bean.bgimgUrl.p3, holder.card,R.drawable.pasc_ecard_mini_default,R.drawable.pasc_ecard_mini_default);
        }

    }

    /**
     * 删除已选标签
     *
     * @param holder
     */
    public void removeFromSelected(CardlHolder holder) {
        int position = holder.getLayoutPosition();
        holder.delete.setVisibility(View.GONE);
        notifyItemRemoved(position);
        mSortedList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 对拖拽的元素进行排序
     *
     * @param fromPosition
     * @param toPosition
     */
    void itemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mSortedList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mSortedList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return mSortedList == null ? 0 : mSortedList.size();
    }

    class CardlHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView delete;
        ImageView card;

        public CardlHolder(View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.tv_name);
            delete = itemView.findViewById(R.id.iv_delect);
            card = itemView.findViewById(R.id.iv_card);
        }
    }

    public interface OnEditDragListener {
        /**
         * 排序回调
         * **/
        void onEditDrag();
    }

}
