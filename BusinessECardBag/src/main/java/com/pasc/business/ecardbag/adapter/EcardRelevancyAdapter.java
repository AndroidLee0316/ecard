package com.pasc.business.ecardbag.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.view.SimpleCardView;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：关联列表适配器
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardRelevancyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EcardRelationResq.EcardRelationInfo> mSortedList = new ArrayList<>();
    private List<EcardRelationResq.EcardRelationInfo> selectList = new ArrayList<>();
    private OnSelectChangeListener onSelectChangeListener;

    public EcardRelevancyAdapter(Context mContext, OnSelectChangeListener onSelectChangeListener) {
        this.mContext = mContext;
        this.onSelectChangeListener = onSelectChangeListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pasc_ecard_relevancy_item, parent, false);
        return new CardlHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setCardInfo((CardlHolder) holder, mSortedList.get(position));
    }

    /**
     * 设置列表数据
     **/
    public void setNewData(List<EcardRelationResq.EcardRelationInfo> list) {
        mSortedList.clear();
        mSortedList.addAll(list);
        selectList.clear();
        selectList.addAll(mSortedList);
        notifyDataSetChanged();
    }

    /**
     * 设置列表信息
     **/
    private void setCardInfo(final CardlHolder holder, final EcardRelationResq.EcardRelationInfo itemData) {
        if (itemData.bgimgUrl != null) {
            holder.cardView.updateData(itemData.name, itemData.deptName, itemData.bgimgUrl.p2);
        } else {
            holder.cardView.updateData(itemData.name, itemData.deptName, "");
        }
        if (itemData.select) {
            holder.ivSelect.setImageResource(R.drawable.pasc_ecard_select_select);
        } else {
            holder.ivSelect.setImageResource(R.drawable.pasc_ecard_select_normal);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemData.select = !itemData.select;
                if (itemData.select) {
                    selectList.add(itemData);
                } else {
                    selectList.remove(itemData);
                }
                if (onSelectChangeListener != null) {
                    onSelectChangeListener.onSlectChage();

                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSortedList == null ? 0 : mSortedList.size();
    }

    /**
     * 列表view
     **/
    class CardlHolder extends RecyclerView.ViewHolder {

        ImageView ivSelect;
        SimpleCardView cardView;
        View root;

        public CardlHolder(View itemView) {
            super(itemView);
            root = itemView;
            ivSelect = itemView.findViewById(R.id.iv_select);
            cardView = itemView.findViewById(R.id.simple_card);
        }
    }

    public List<EcardRelationResq.EcardRelationInfo> getSelectList() {
        return selectList;
    }

    /**
     * 选择列表数据改变回调
     **/
    public interface OnSelectChangeListener {
        /**
         * 选择列表数据改变回调
         **/
        void onSlectChage();

    }
}
