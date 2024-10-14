package com.pasc.business.ecardbag.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.business.bike.R;
import com.pasc.lib.ecardbag.out.EcardOutInfo;
import com.pasc.lib.imageloader.PascImageLoader;

/**
 * 功能：简单的卡证样式，包括 （卡证背景 + 卡证名字 + 卡证描述）
 * <p>
 * @author lichangbao702
 * email : lichangbao702@pingan.com.cn
 * date : 2020/1/6
 */
public class SimpleCardOutView extends FrameLayout{

    //卡证背景
    private ImageView mBgIV;
    //卡证名字
    private TextView mNameTV;
    //卡证描述
    private TextView mDescTV;
    //卡证信息
    private EcardOutInfo mEcardOutInfo;
    //默认卡证背景
    private int defaultBg;


    public SimpleCardOutView(@NonNull Context context) {
        super(context,null);
    }

    public SimpleCardOutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.pasc_ecard_simple_card_out_view,this);
        initView(view);
        defaultBg = R.drawable.pasc_ecard_retation_bg_default;
    }

    private void initView(View view){
        mBgIV = view.findViewById(R.id.pasc_ecard_simple_card_out_view_bg_iv);
        mNameTV = view.findViewById(R.id.pasc_ecard_simple_card_out_view_name_tv);
        mDescTV = view.findViewById(R.id.pasc_ecard_simple_card_out_view_desc_tv);
    }

    public void updateData(String ecardName, String ecardDesc, String bgUrl){
        mNameTV.setText(ecardName);
        mDescTV.setText(ecardDesc);
        PascImageLoader.getInstance().loadImageUrl(bgUrl,mBgIV,defaultBg,defaultBg);
    }

    /**
     * 刷新、填充卡证卡片样式内容
     * @param ecardOutInfo  卡证内容
     */
    public void updateData(EcardOutInfo ecardOutInfo){
        mEcardOutInfo = ecardOutInfo;
        if (mEcardOutInfo != null){
            updateData(ecardOutInfo.getEcardName(), ecardOutInfo.getEcardDesc(), ecardOutInfo.getEcardBgUrl());
        }
    }

    public void setNameTextSize(float size){
        mNameTV.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }

    public void setDescTextSize(float size){
        mDescTV.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
    }

    public void setDefaultBg(int defaultBg) {
        this.defaultBg = defaultBg;
    }

    public EcardOutInfo getEcardOutInfo() {
        return mEcardOutInfo;
    }

    public String getEcardID(){
        if (mEcardOutInfo == null){
            return null;
        }
        return mEcardOutInfo.getEcardID();
    }
}
