package com.pasc.business.ecardbag.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasc.business.bike.R;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：卡证详情适配器
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardDetialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EcardDetailResq.EcardDetailInfo> mSortedList = new ArrayList<>();


    public EcardDetialAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pasc_ecard_detial_item, parent, false);
        return new CardlHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setCardInfo((CardlHolder) holder, mSortedList.get(position));
    }

    /**
     * 重置列表数据并更新
     **/
    public void setNewData(List<EcardDetailResq.EcardDetailInfo> list) {
        mSortedList.clear();
        mSortedList.addAll(list);
        notifyDataSetChanged();

    }

    /**
     * 获取列表数据
     **/
    public List<EcardDetailResq.EcardDetailInfo> getData() {
        return mSortedList;

    }

    /**
     * 设置列表数据
     **/
    private void setCardInfo(final CardlHolder holder, final EcardDetailResq.EcardDetailInfo bean) {
        if (holder.getLayoutPosition() == 0) {
            holder.viewLine10.setVisibility(View.VISIBLE);
            if (mContext.getResources().getString(R.string.pasc_ecard_detial_accout_nomarl).equals(bean.value)) {
                holder.value.setTextColor(mContext.getResources().getColor(R.color.pasc_ecard_detial_nomarl_color));
            } else {
                holder.value.setTextColor(mContext.getResources().getColor(R.color.pasc_ecard_detial_unEnbal_color));
            }
        } else {
            holder.viewLine10.setVisibility(View.GONE);
            holder.value.setTextColor(mContext.getResources().getColor(R.color.pasc_ecard_tip_color));
        }
        holder.name.setText(bean.name);
        holder.value.setText(bean.value);
    }

    @Override
    public int getItemCount() {
        return mSortedList == null ? 0 : mSortedList.size();
    }

    /**
     * 列表view
     **/
    class CardlHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView value;
        View viewLine10;

        public CardlHolder(View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.tv_name);
            value = itemView.findViewById(R.id.tv_value);
            viewLine10 = itemView.findViewById(R.id.view_line_10);

        }
    }

    public interface OnDeletListerner {
        /**
         * 删除接口
         **/
        void onDelect(EcardInfoResq.EcardInfoBean info, int pos);

    }
}
