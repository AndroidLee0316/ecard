package com.pasc.business.ecardbag.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.business.bike.R;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.imageloader.PascImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 功能：解绑列表适配器
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardUnbindAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EcardInfoResq.EcardInfoBean> mSortedList = new ArrayList<>();
    private OnDeletListerner listerner;

    public EcardUnbindAdapter(Context mContext, OnDeletListerner listerner) {
        this.mContext = mContext;
        this.listerner = listerner;
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
     * 设置列表新数据
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
     * 设置卡片信息
     **/
    private void setCardInfo(final CardlHolder holder, final EcardInfoResq.EcardInfoBean bean) {
        holder.name.setText(bean.name);
        if (bean.bgimgUrl != null) {
            PascImageLoader.getInstance().loadImageUrl(bean.bgimgUrl.p3, holder.card, R.drawable.pasc_ecard_mini_default, R.drawable.pasc_ecard_mini_default);

        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listerner != null) {
                    listerner.onDelect(bean, holder.getLayoutPosition());
                }
            }
        });
        holder.delete.setImageResource(R.drawable.pasc_ecard_unbind_icon);
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
        ImageView delete;
        ImageView card;

        public CardlHolder(View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.tv_name);
            delete = itemView.findViewById(R.id.iv_delect);
            card = itemView.findViewById(R.id.iv_card);
        }
    }

    public interface OnDeletListerner {
        /**
         * 删除
         **/
        void onDelect(EcardInfoResq.EcardInfoBean info, int pos);

    }
}
