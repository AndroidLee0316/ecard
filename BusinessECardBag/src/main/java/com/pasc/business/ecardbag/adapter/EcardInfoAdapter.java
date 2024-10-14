package com.pasc.business.ecardbag.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.activity.EcardInfoActivity;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.DialogUtil;
import com.pasc.business.ecardbag.utils.EcardConstants;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.business.ecardbag.utils.StringUtils;
import com.pasc.business.ecardbag.view.RoundImageView;
import com.pasc.business.ecardbag.view.SmoothScrollLayoutManager;
import com.pasc.business.ecardbag.view.ViewHolderAnimator;
import com.pasc.business.user.PascUserFaceCheckListener;
import com.pasc.business.user.PascUserManager;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.pamars.EcardUnbindPamars;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.out.PascEcardManager;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.net.ApiV2Error;
import com.pasc.lib.router.BaseJumper;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.DensityUtils;
import com.pasc.lib.widget.dialog.OnCloseListener;
import com.pasc.lib.widget.dialog.OnConfirmListener;
import com.pasc.lib.widget.dialog.common.ConfirmDialogFragment;
import com.pasc.lib.widget.refreshlayout.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * 功能：卡片列表
 * <p>
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */

public class EcardInfoAdapter extends RecyclerView.Adapter<EcardInfoAdapter.CardMainInfoHolder> {

    private BaseEcardActivity context;
    private List<EcardInfoResq.EcardInfoBean> list = new ArrayList<>();
    private OnSpandListener spandListener;
    /**
     * 记录展开的位置
     **/
    public int mOpenIndex;
    public String itemOpenId;
    private AnimatorSet closeAnimatorSet;
    private AnimatorSet openAnimatorSet;

    public EcardInfoAdapter(List<EcardInfoResq.EcardInfoBean> list, BaseEcardActivity context) {
        this.list = list;
//        this.fragment = fragment;
        this.context = context;
        mOpenIndex = 0;
    }

    public List<EcardInfoResq.EcardInfoBean> getData() {
        return list;
    }

    @NonNull
    @Override
    public CardMainInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.pasc_ecard_item_info, parent, false);
        CardMainInfoHolder holder = new CardMainInfoHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final CardMainInfoHolder itemHolder, final int position) {

        final EcardInfoResq.EcardInfoBean info = list.get(position);
        if (EcardInfoResq.STATUS_USEABLE == info.cardStatus) {
            if (info.bgimgUrl != null) {
                PascImageLoader.getInstance().loadImageUrl(info.bgimgUrl.p1, itemHolder.cardHeader, R.drawable.pasc_ecard_list_bg_default, R.drawable.pasc_ecard_list_bg_default);
            }
        } else {
            itemHolder.cardHeader.setImageResource(R.drawable.pasc_card_bg_failure);

        }


        itemHolder.cardHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();

                if (position == mOpenIndex) {
                    return;
                }
                if (closeAnimatorSet != null && closeAnimatorSet.isRunning()
                        || openAnimatorSet != null && openAnimatorSet.isRunning()) {
                    return;
                }

                //埋点：证照展开
                StatisticsManager.getInstance().onEvent(context.getString(R.string.pasc_ecard_event_id_open_ecard_item),
                        StatisticsConstants.EVENT_ID,
                        String.format(context.getString(R.string.pasc_ecard_event_lable_open_ecard_item),info.name),
                        StatisticsConstants.PAGE_TYPE,
                        null);

                RecyclerView view = (RecyclerView) itemHolder.itemView.getParent();

                //展开点击的Item
                openHolder(itemHolder.itemView, view, itemHolder, true);

                itemOpenId = info.id;
                if (mOpenIndex == list.size() - 1) {
                    /**最后一个item默认打开**/
                    mOpenIndex = position;

                    return;
                }
                /**关闭执上一个点击的Item**/
                CardMainInfoHolder mOldOpenHolder =
                        (CardMainInfoHolder) view.findViewHolderForLayoutPosition(mOpenIndex);

                if (mOldOpenHolder != null) {

                    closeHolder(mOldOpenHolder.itemView, view, mOldOpenHolder, true);
                } else if (mOpenIndex >= 0) {
                    View oldView = ((SmoothScrollLayoutManager) view.getLayoutManager()).getViewForPosition(
                            mOpenIndex);
                    if (oldView != null) {
                        closeHolder(oldView, view, null, false);
                    }
                }
                if (spandListener != null) {
                    spandListener.onclick(position);
                }
                mOpenIndex = position;
            }
        });

        itemHolder.titleView.setText(info.deptName);
        //二维码的显示与隐藏

//        if (EcardInfoResq.IS_SHOW_UNSHOW.equals(info.isShowQrcode)) {//已经废止 或者失效
//            itemHolder.twoBarCodeView.setVisibility(View.GONE);
//        } else {
//            itemHolder.twoBarCodeView.setVisibility(View.VISIBLE);
//        }

        /**
         * /二维码点击事件  只支持驾驶证和行驶证
         * **/
        itemHolder.twoBarCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EcardInfoResq.IS_SHOW_UNSHOW.equals(info.isShowQrcode)) {
                    showNotOnlineDialog(info.name);
                } else {
//                    ModuleSectionItem moduleSectionItem = new ModuleSectionItem();
//                    if (info.qRcodeService == null) return;
//                    moduleSectionItem.service = info.qRcodeService.cardServiceItemBean;
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("用户手机号", UserRouterBiz.fetchUserInfoServices().getMobile());
//                    EventUtils.onEvent("一码通城-我的证照-" + info.cardName, "证照码点击", map);
                }
            }
        });
        itemHolder.subTitleView.setText(info.name);
        itemHolder.visibleView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(info.configValue)) {
            itemHolder.visibleView.setVisibility(View.GONE);
            itemHolder.cardNumView.setVisibility(View.GONE);
        } else {
            itemHolder.visibleView.setVisibility(View.VISIBLE);
            itemHolder.cardNumView.setVisibility(View.VISIBLE);
        }

        if (EcardInfoResq.IS_SHOW_SHOW.equals(info.isShowData)) {
            itemHolder.checkAccountView.setVisibility(View.VISIBLE);
        } else {
            itemHolder.checkAccountView.setVisibility(View.GONE);
        }
        itemHolder.checkAccountView.setText(context.getResources().getString(R.string.pasc_ecard_list_detial));
        itemHolder.checkAccountView.getDelegate().setBackgroundColor(context.getResources().getColor(R.color.pasc_ecard_info_btn_nomarl_color));
        itemHolder.abolishView.setVisibility(View.GONE);
        itemHolder.checkAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //埋点：证照展开
                StatisticsManager.getInstance().onEvent(context.getString(R.string.pasc_ecard_event_id_open_ecard_detail),
                        StatisticsConstants.EVENT_ID,
                        String.format(context.getString(R.string.pasc_ecard_event_lable_open_ecard_detail),info.name),
                        StatisticsConstants.PAGE_TYPE,
                        null);


                Bundle bundle = new Bundle();
                bundle.putSerializable(EcardInfoActivity.ECARD_INFO,info);
                bundle.putInt(EcardInfoActivity.ECARD_POSIVE, itemHolder.getAdapterPosition());
                BaseJumper.jumpBundleARouter(ArouterPath.ECARD_INFO_MAIN, bundle);
            }
        });
        /**是否显示或者隐藏**/
        itemHolder.visibleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EcardInfoResq.IS_SHOW_SHOW.equals(info.isVisible)) {
                    list.get(itemHolder.getLayoutPosition()).isVisible = EcardInfoResq.IS_SHOW_UNSHOW;
                    notifyItemChanged(itemHolder.getLayoutPosition());
                } else {
                    list.get(itemHolder.getLayoutPosition()).isVisible = EcardInfoResq.IS_SHOW_SHOW;
                    notifyItemChanged(itemHolder.getLayoutPosition());
                }
            }
        });
        eyeEvent(info, itemHolder);
        handleService(info, position, itemHolder);
        if (position == list.size() - 1 || position == mOpenIndex) {
            openHolder(itemHolder.itemView, (View) itemHolder.itemView.getParent(), itemHolder, false);
        } else {
            closeHolder(itemHolder.itemView, (View) itemHolder.itemView.getParent(), itemHolder, false);
        }
    }

    /**
     * 是否显示卡证号码
     **/
    private void eyeEvent(EcardInfoResq.EcardInfoBean info,
                          CardMainInfoHolder itemHolder) {
        if (EcardInfoResq.IS_SHOW_SHOW.equals(info.isVisible)) {
            itemHolder.visibleView.setImageResource(R.drawable.pasc_ecard_eye_show);
            itemHolder.cardNumView.setText(StringUtils.handleText(info.configValue, 20));
        } else {
            itemHolder.visibleView.setImageResource(R.drawable.pasc_ecard_eye_hide);
            String visiableNum = info.configValue;
            itemHolder.cardNumView.setText(StringUtils.getString(visiableNum));
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnSpandListener {
        /**
         * 点击事件
         **/
        void onclick(int position);
    }

    public static class CardMainInfoHolder extends RecyclerView.ViewHolder {
        public ImageView cardHeader;
        TextView titleView;
        ImageView twoBarCodeView;
        TextView subTitleView;
        TextView cardNumView;
        ImageView visibleView;
        RoundTextView checkAccountView;
        LinearLayout mContainerContent;
        LinearLayout timeRangeView;
        //        View horizontalDevider;//服务项上面的横线
        LinearLayout serviceView;
        RelativeLayout abolishView;
        View view_botton;

        public CardMainInfoHolder(final View itemView) {
            super(itemView);
            view_botton = itemView.findViewById(R.id.view_botton);
            cardHeader = itemView.findViewById(R.id.card_header);
            titleView = itemView.findViewById(R.id.card_header_title);
            twoBarCodeView = itemView.findViewById(R.id.card_header_two_barcode);
            subTitleView = itemView.findViewById(R.id.card_header_sub_title);
            cardNumView = itemView.findViewById(R.id.card_header_number);
            visibleView = itemView.findViewById(R.id.card_header_isvisiable);
            checkAccountView = itemView.findViewById(R.id.card_header_check_account);
            timeRangeView = itemView.findViewById(R.id.card_center_container);
            serviceView = itemView.findViewById(R.id.card_bottom_container);
            mContainerContent = itemView.findViewById(R.id.container_list_content);
//            horizontalDevider = itemView.findViewById(R.id.cardbag_item_devider);
            abolishView = itemView.findViewById(R.id.cardbag_item_abolish);
        }
    }


    private void handleService(EcardInfoResq.EcardInfoBean data, int position,
                               EcardInfoAdapter.CardMainInfoHolder itemHolder) {
        List<EcardInfoResq.ApplicationInfo> serviceItems = data.applicationVOList;
        Context context = itemHolder.serviceView.getContext();
        itemHolder.serviceView.removeAllViews();
        if (serviceItems.size() <= 0) {
            itemHolder.serviceView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams reparamas = (RelativeLayout.LayoutParams) itemHolder.view_botton.getLayoutParams();
            reparamas.height = 1;
            itemHolder.view_botton.setLayoutParams(reparamas);
//            itemHolder.horizontalDevider.setVisibility(View.GONE);
        } else {
            itemHolder.serviceView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams reparamas = (RelativeLayout.LayoutParams) itemHolder.view_botton.getLayoutParams();
            reparamas.height = DensityUtils.dp2px(8);
            itemHolder.view_botton.setLayoutParams(reparamas);
//            itemHolder.horizontalDevider.setVisibility(View.VISIBLE);
            for (int i = 0; i < serviceItems.size(); i++) {
                final EcardInfoResq.ApplicationInfo item = serviceItems.get(i);
                final View childView = LayoutInflater.from(context)
                        .inflate(R.layout.pasc_ecard_cardbag_item_service, itemHolder.serviceView, false);
                ImageView icon = childView.findViewById(R.id.cardbag_service_icon);
                TextView iconTitle = childView.findViewById(R.id.cardbag_service_title);
                if (EcardInfoResq.STATUS_USEABLE == data.cardStatus) {
                    PascImageLoader.getInstance().loadImageUrl(item.iconUrl, icon, R.drawable.pasc_ecard_service_default, R.drawable.pasc_ecard_service_default);

                } else {
                    PascImageLoader.getInstance().loadImageUrl(item.iconUrl, icon, R.drawable.pasc_ecard_service_gray_default, R.drawable.pasc_ecard_service_gray_default);

                }
                iconTitle.setText(item.name);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) childView.getLayoutParams();
                params.width = (int) ((DensityUtils.getScreenWidth(context) - DensityUtils.dip2px(context, 30)) / 4);
                childView.setLayoutParams(params);
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //埋点：证照展开
                        StatisticsManager.getInstance().onEvent(context.getString(R.string.pasc_ecard_event_id_open_ecard_service),
                                StatisticsConstants.EVENT_ID,
                                String.format(context.getString(R.string.pasc_ecard_event_lable_open_ecard_service),data.name,item.name),
                                StatisticsConstants.PAGE_TYPE,
                                null);


                        if (item.isProto != null) {
                            switch (item.isProto) {
                                case EcardInfoResq.ApplicationInfo.PAGE_TYPE_NO:

                                    break;
                                case EcardInfoResq.ApplicationInfo.PAGE_TYPE_H5:
                                    PascHybrid.getInstance().start(context, item.applicationUrl);
                                    break;
                                case EcardInfoResq.ApplicationInfo.PAGE_TYPE_NATIVE:
                                    if (PascEcardManager.getInstance().getNormalCallback() != null) {
                                        PascEcardManager.getInstance().getNormalCallback().routerClick(item.applicationUrl);
                                    } else {
                                        BaseJumper.jumpARouter(item.applicationUrl);
                                    }
                                    break;


                            }
                        }
                    }
                });
                itemHolder.serviceView.addView(childView);
            }
        }
    }


    /**
     * @param content  传入的内容
     * @param frontNum 保留前面字符串
     * @param endNum   保留后面字符串
     * @return 返回带*号的字符串
     */
    private String getStarString(String content, int frontNum, int endNum) {

        if (TextUtils.isEmpty(content)) {
            return "";
        }
        if (frontNum >= content.length() || frontNum < 0) {
            return content;
        }
        if (endNum >= content.length() || endNum < 0) {
            return content;
        }
        if (frontNum + endNum >= content.length()) {
            return content;
        }
        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
            starStr.append("*");
        }
        return content.substring(0, frontNum) + starStr.toString() + content.substring(content.length() - endNum,
                content.length());
    }


    /**
     * 服务项维护中  不可使用
     */
    private void showNotOnlineDialog(String content) {
        if (dialogFragment == null) {
            dialogFragment = new ConfirmDialogFragment.Builder()
                    .setCancelable(true)
                    .setOnConfirmListener(new OnConfirmListener<ConfirmDialogFragment>() {
                        @Override
                        public void onConfirm(ConfirmDialogFragment confirmDialogFragment) {
                            dialogFragment.dismiss();
                            dialogFragment.onDestroy();
                            dialogFragment = null;
                        }
                    })

                    .setOnCloseListener(new OnCloseListener<ConfirmDialogFragment>() {
                        @Override
                        public void onClose(ConfirmDialogFragment confirmDialogFragment) {
                            dialogFragment.dismiss();
                            dialogFragment.onDestroy();
                            dialogFragment = null;

                        }
                    })
                    .setDesc(content + context.getResources().getString(R.string.pasc_ecard_list_unuser_tip))
                    .setDescColor(context.getResources().getColor(R.color.pasc_ecard_dialog_title_color))
                    .setCancelable(false)
                    .setConfirmText(context.getResources().getString(R.string.pasc_ecard_list_dialog_delect))
                    .setHideConfirmButton(true)
                    .setCloseText(context.getResources().getString(R.string.pasc_ecard_list_unuser_btn))
                    .setCloseTextColor(context.getResources().getColor(R.color.pasc_ecard_dialog_comfirm_color))
                    .setConfirmTextColor(context.getResources().getColor(R.color.pasc_ecard_dialog_close_color))
                    .build();
        }
        if (dialogFragment.getDialog() == null || !dialogFragment.getDialog().isShowing()) {
            dialogFragment.show(context.getSupportFragmentManager(), "showStatus");
        }
    }

    /**
     * 展开ViewHolder
     *
     * @param animate 是否要动画
     */
    public void openHolder(View view, final View parent, RecyclerView.ViewHolder holder, boolean animate) {

        if (animate) {
            //改变高度的动画
            Animator animator = ViewHolderAnimator.ofItemViewHeight(view, parent, holder, true);
            openAnimatorSet = new AnimatorSet();
            openAnimatorSet.playSequentially(animator,
                    marginBottomAnimation(view, DensityUtil.dp2px(5)));
            openAnimatorSet.setInterpolator(new LinearInterpolator());
            openAnimatorSet.start();
        } else { //为false时直接显示
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) view.getLayoutParams();
            param.bottomMargin = DensityUtil.dp2px(5);
            param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    /**
     * 改变ViewHolder的marginBottom的距离
     */
    private Animator marginBottomAnimation(final View view, int endMarginBottom) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) view.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(param.bottomMargin, endMarginBottom);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) view.getLayoutParams();
                param.bottomMargin = (int) animation.getAnimatedValue();
                view.setLayoutParams(param);
            }
        });
        animator.setDuration(Math.abs(param.bottomMargin - endMarginBottom));
        return animator;
    }

    /**
     * 关闭ViewHolder
     *
     * @param animate 是否需要动画
     */
    public void closeHolder(View view, View parent, RecyclerView.ViewHolder holder, boolean animate) {
        if (animate) {
            Animator animator = ViewHolderAnimator.ofItemViewHeight(view, parent, holder, false);
            closeAnimatorSet = new AnimatorSet();
            closeAnimatorSet.playSequentially(marginBottomAnimation(view, -DensityUtil.dp2px(35)),
                    animator);
            closeAnimatorSet.setInterpolator(new LinearInterpolator());
            closeAnimatorSet.start();
        } else {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) view.getLayoutParams();
            param.height = DensityUtil.dp2px(90);
            param.bottomMargin = -DensityUtil.dp2px(35);
        }
    }

    /**
     * 更新展开的卡证id
     **/

    public void updateOpenId(String itemOpenId) {
        this.itemOpenId = itemOpenId;
        boolean openIdIsExit = false;
        if (itemOpenId != null) {
            for (int i = 0; i < list.size(); i++) {
                if (itemOpenId.equals(list.get(i).id)) {
                    mOpenIndex = i;
                    openIdIsExit = true;
                }
            }
            if (!openIdIsExit) {
                mOpenIndex = 0;
            }
        } else {
            mOpenIndex = 0;

        }
    }

    /**
     * 获取展开的卡证id
     **/
    public String getItemOpenId() {
        return itemOpenId;

    }


    /**
     * 提示框
     **/
    private ConfirmDialogFragment dialogFragment;

}
