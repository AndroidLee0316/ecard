package com.pasc.business.ecardbag.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;


import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.lib.base.util.DensityUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.router.BaseJumper;
import com.pasc.lib.statistics.StatisticsManager;

/**
 *  功能：卡证操作弹框
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public class OperatePopView extends PopupWindow implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private Activity mContext;
    private View popupWindowView;

    public OperatePopView(Activity context) {
        super();
        mContext = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initView(context);
    }

    public OperatePopView(Activity context, int with, int height) {
        super(with, height);
        mContext = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initView(context);
    }

    private void initView(Activity context) {
        popupWindowView = layoutInflater.inflate(R.layout.pasc_ecard_pop_operate, null);
        popupWindowView.findViewById(R.id.ll_add).setOnClickListener(this::onClick);
        popupWindowView.findViewById(R.id.ll_sort).setOnClickListener(this::onClick);
        popupWindowView.findViewById(R.id.ll_unbind).setOnClickListener(this::onClick);
        setContentView(popupWindowView);
        setWidth(DensityUtils.dip2px(context, 140));
        setHeight(DensityUtils.dip2px(context, 135));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
    }


    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public void showAsDropDownOnN(View view) {
        if (Build.VERSION.SDK_INT ==  Build.VERSION_CODES.N) {
            int[] a = new int[2];
            view.getLocationInWindow(a);
            showAtLocation(view, 0, 0, a[1] + view.getHeight());
        } else {
            super.showAsDropDown(view);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_add) {

            StatisticsManager.getInstance().onEvent(mContext.getString(R.string.pasc_ecard_event_id_ecard_list_page_more_click),
                    StatisticsConstants.EVENT_ID,
                    mContext.getString(R.string.pasc_ecard_event_lable_ecard_list_page_more_add_click),
                    StatisticsConstants.PAGE_TYPE,
                    null);

            BaseJumper.jumpARouter(ArouterPath.ECARD_LIST_ADD);
        } else if (v.getId() == R.id.ll_sort) {

            StatisticsManager.getInstance().onEvent(mContext.getString(R.string.pasc_ecard_event_id_ecard_list_page_more_click),
                    StatisticsConstants.EVENT_ID,
                    mContext.getString(R.string.pasc_ecard_event_lable_ecard_list_page_more_sort_click),
                    StatisticsConstants.PAGE_TYPE,
                    null);

            if (EcardDataManager.getInstance().getEcardListFromCache() != null && EcardDataManager.getInstance().getEcardListFromCache().size() <= 0) {
                ToastUtils.toastMsg(mContext.getResources().getString(R.string.pasc_ecard_tip_no_data));
                return;
            }
            BaseJumper.jumpARouter(ArouterPath.ECARD_LIST_SORT);
        } else if (v.getId() == R.id.ll_unbind) {

            StatisticsManager.getInstance().onEvent(mContext.getString(R.string.pasc_ecard_event_id_ecard_list_page_more_click),
                    StatisticsConstants.EVENT_ID,
                    mContext.getString(R.string.pasc_ecard_event_lable_ecard_list_page_more_unbind_click),
                    StatisticsConstants.PAGE_TYPE,
                    null);

            if (EcardDataManager.getInstance().getEcardListFromCache() != null && EcardDataManager.getInstance().getEcardListFromCache().size() <= 0) {
                ToastUtils.toastMsg(mContext.getResources().getString(R.string.pasc_ecard_tip_no_data));
                return;
            }
            BaseJumper.jumpARouter(ArouterPath.ECARD_LIST_UNBIND);

        }
        dismiss();

    }

    @Override
    public void dismiss() {
        setApha(1.0f);
        super.dismiss();
    }

    public void setApha(float bgApha) {
        WindowManager.LayoutParams layoutParams = mContext.getWindow().getAttributes();
        layoutParams.alpha = bgApha;
        mContext.getWindow().setAttributes(layoutParams);

    }
}

