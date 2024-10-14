package com.pasc.business.ecardbag.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.activity.EcardAddCardInfoActivity;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.router.BaseJumper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */

public class EcardAddAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AddListResq.AddListBean> mSortedList = new ArrayList<>();
    private OnBindCardLersenter onBindCardLersenter;

    public EcardAddAdapter(Context mContext, OnBindCardLersenter onBindCardLersenter) {
        this.mContext = mContext;
        this.onBindCardLersenter = onBindCardLersenter;
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
     * 重置数据并更新列表
     **/
    public void setNewData(List<AddListResq.AddListBean> list) {
        mSortedList.clear();
        mSortedList.addAll(list);
        notifyDataSetChanged();

    }

    /**
     * 获取列表数据
     **/
    public List<AddListResq.AddListBean> getData() {
        return mSortedList;

    }

    /**
     * 设置卡片信息
     **/
    private void setCardInfo(final CardlHolder holder, final AddListResq.AddListBean bean) {
        holder.name.setText(bean.name);
        holder.delete.setImageResource(R.drawable.pasc_ecard_right_arrow);
        if (bean.bgimgUrl != null) {
            PascImageLoader.getInstance().loadImageUrl(bean.bgimgUrl.p3, holder.card, R.drawable.pasc_ecard_mini_default, R.drawable.pasc_ecard_mini_default);

        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBindCardLersenter != null) {
                    onBindCardLersenter.onBind(bean, holder.getLayoutPosition());

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSortedList == null ? 0 : mSortedList.size();
    }

    /**
     * 卡证信息
     **/
    class CardlHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView delete;
        ImageView card;
        View root;

        public CardlHolder(View itemView) {
            super(itemView);

            root = itemView;
            name = itemView.findViewById(R.id.tv_name);
            delete = itemView.findViewById(R.id.iv_delect);
            card = itemView.findViewById(R.id.iv_card);
        }
    }

    /**
     * 卡证绑定回调
     **/
    public interface OnBindCardLersenter {
        /**
         * 卡证绑定回调
         **/
        void onBind(AddListResq.AddListBean info, int pos);

    }
}
