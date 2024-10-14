package com.pasc.business.ecardbag.activity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardListView;
import com.pasc.business.ecardbag.presenter.EcardListPresenter;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.business.user.PascUserCertListener;
import com.pasc.business.user.PascUserConfig;
import com.pasc.business.user.PascUserLoginListener;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.business.ecardbag.adapter.EcardInfoAdapter;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.view.IReTryListener;
import com.pasc.business.ecardbag.view.OperatePopView;
import com.pasc.business.ecardbag.view.SmoothScrollLayoutManager;
import com.pasc.business.ecardbag.view.StatusView;
import com.pasc.business.user.PascUserFaceCheckListener;
import com.pasc.business.user.PascUserManager;
import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.util.DensityUtils;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.out.PascEcardManager;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.router.BaseJumper;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.ICallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pasc.business.ecardbag.utils.ArouterPath.ECARD_LIST_RELEVANCY;

/**
 * 功能：已绑定卡证列表
 * <p>
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
@Route(path = ArouterPath.ECARD_LIST_MAIN)
public class EcardListActivity extends BaseEcardActivity implements EcardListView {
    public static final String HTPP_TAG = "http://";
    public static final String HTPPS_TAG = "https://";
    StatusView statusView;
    EcardInfoAdapter adapter;
    RecyclerView recyclerView;
    private SmoothScrollLayoutManager layoutManager;
    private List<EcardInfoResq.EcardInfoBean> datas = new ArrayList<>();
    private OperatePopView operatePopView;
    private LinearLayout noInfoLayout;
    private TextView tvProcotolInfo;
    private EcardListPresenter mPresenter;

    /**
     * 从其他地方跳转到这里，可以通过设置 showEcardID 来挑选默认显示哪个item
     **/
    private String showEcardID;

    /**
     * 证照列表是否更新了，如果更新了，返回的时候需要通知外部
     **/
    private boolean hasEcardListUpdated = false;

    @Override
    protected void initView() {
        mPresenter = new EcardListPresenter(this);
        showEcardID = getIntent().getStringExtra(ArouterPath.ECARD_LIST_MAIN_PARAM_SHOW_ECARD_ID);
        setTitle(getResources().getString(R.string.pasc_ecard_list_title));
        statusView = findViewById(R.id.statusView);
        noInfoLayout = findViewById(R.id.ll_no_info);
        tvProcotolInfo = findViewById(R.id.tv_protocol);

        statusView.setTryListener(new IReTryListener() {
            @Override
            public void tryAgain() {
                initData();
            }
        });

        SpannableString str = new SpannableString(getResources().getString(R.string.pasc_ecard_protocol_agree_tip) + getResources().getString(R.string.pasc_ecard_protocol_name));

        int start = getResources().getString(R.string.pasc_ecard_protocol_agree_tip).length();
        int end = getResources().getString(R.string.pasc_ecard_protocol_name).length();

        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                String url = getResources().getString(R.string.pasc_ecard_protocol_url);
                if (!TextUtils.isEmpty(url) && !url.startsWith(HTPP_TAG) && !url.startsWith(HTPPS_TAG)) {
                    url = AppProxy.getInstance().getH5Host() + url;
                }
                PascHybrid.getInstance()
                        .start(EcardListActivity.this, url);
            }
        };
        str.setSpan(clickableSpan, start, start + end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new UnderlineSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.pasc_primary));
                ds.setUnderlineText(false);
            }
        }, start, start + end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvProcotolInfo.setText(str);
        tvProcotolInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvProcotolInfo.setHighlightColor(getResources().getColor(android.R.color.transparent));
        findViewById(R.id.tv_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //检测
                checkAuth(new CheckAuthCallback() {
                    @Override
                    public void onSuccess(boolean isCertBack) {
                        if (isCertBack) {
                            BaseJumper.jumpARouter(ECARD_LIST_RELEVANCY);
                        } else {
                            mPresenter.getEcardListFromNet(true);
                        }
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        ToastUtils.toastMsg(msg);
                        Log.e(getClass().getName(), msg);
                    }
                });

            }
        });

        setOnBack(new OnBack() {
            @Override
            public void onBack() {
                keyback();
            }
        });
    }

    @Override
    protected void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        adapter = new EcardInfoAdapter(datas, this);
        mPresenter.getEcardListFromNet(false);
    }

    @Override
    protected int layoutResId() {
        return R.layout.pasc_ecard_activity_my_ecard;
    }

    /**
     * 显示操作卡证列表数据弹出框
     **/
    private void showPopView() {
        if (operatePopView == null) {
            operatePopView = new OperatePopView(this);
        }
        operatePopView.setApha(0.5f);
        operatePopView.showAsDropDown(findViewById(R.id.view_), DensityUtils.dip2px(this, 15), DensityUtils.dip2px(this, 0));

    }

    /**
     * 监听卡证更新
     **/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEven(String msg) {
        if (EventBusUtils.EVENT_ECARD_LIST_SORTED.equals(msg)) {
            datas.clear();
            datas.addAll(EcardDataManager.getInstance().getEcardListFromCache());

            if (adapter != null) {
                //排序后显示第一个卡证
                showEcardID = null;
                adapter.updateOpenId(showEcardID);
                adapter.notifyDataSetChanged();
            }
            hasEcardListUpdated = true;
            /** 添加卡证/关联卡证需要重新从网络上拉最新的列表数据**/
        } else if (EventBusUtils.EVENT_ECARD_LIST_UNBIND.equals(msg)) {
            datas.clear();
            datas.addAll(EcardDataManager.getInstance().getEcardListFromCache());

            if (datas.size() == 0) {
                initData();
            } else {
                datas.clear();
                datas.addAll(EcardDataManager.getInstance().getEcardListFromCache());

                if (adapter != null) {
                    //解绑后显示第一个卡证
                    showEcardID = null;
                    adapter.updateOpenId(showEcardID);
                    adapter.notifyDataSetChanged();
                }

            }
            hasEcardListUpdated = true;
            /** 添加卡证/关联卡证需要重新从网络上拉最新的列表数据**/
        } else if (EventBusUtils.EVENT_ECARD_LIST_ADDED.equals(msg)) {

            //添加后显示第一个卡证
            showEcardID = null;
            mPresenter.getEcardListFromNet(false);
        } else if (EventBusUtils.EVENT_ECARD_LIST_UPDATED.equals(msg)) {

            datas.clear();
            datas.addAll(EcardDataManager.getInstance().getEcardListFromCache());

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            hasEcardListUpdated = true;
        }

    }

    /**
     * 人脸核验
     **/
    public void faceCheck() {
        Log.i(getClass().getSimpleName(), "to check face");
        PascUserManager.getInstance().toFaceCheck(PascEcardManager.getInstance().getConfig().getFaceCheckAppID(), new PascUserFaceCheckListener() {
            @Override
            public void onSuccess(Map<String, String> map) {

                //埋点
                HashMap<String,String> eventMap = new HashMap<>();
                eventMap.put(getString(R.string.pasc_ecard_event_lable_ecard_introduce_cert_status),getString(R.string.pasc_ecard_event_lable_ecard_introduce_cert_status_success));
                StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_ecard_introduce_page_use_click),
                        StatisticsConstants.EVENT_ID,
                        getString(R.string.pasc_ecard_event_lable_ecard_introduce_page_to_facecheck),
                        StatisticsConstants.PAGE_TYPE,
                        eventMap);

                BaseJumper.jumpARouter(ECARD_LIST_RELEVANCY);
            }

            @Override
            public void onFailed() {
                ToastUtils.toastMsg(R.string.pasc_ecard_face_check_failed);
            }

            @Override
            public void onCancled() {

                //埋点
                HashMap<String,String> eventMap = new HashMap<>();
                eventMap.put(getString(R.string.pasc_ecard_event_lable_ecard_introduce_cert_status),getString(R.string.pasc_ecard_event_lable_ecard_introduce_cert_status_cancle));
                StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_ecard_introduce_page_use_click),
                        StatisticsConstants.EVENT_ID,
                        getString(R.string.pasc_ecard_event_lable_ecard_introduce_page_to_facecheck),
                        StatisticsConstants.PAGE_TYPE,
                        eventMap);
            }
        });
    }

    /**
     * 显示卡证列表
     **/
    private void showListInfo() {
        noInfoLayout.setVisibility(View.GONE);
        toolbar.setActionIcon(R.drawable.pasc_ecard_operate_icon);
        toolbar.setActionClickListener(new ICallBack() {
            @Override
            public void callBack() {
                StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_ecard_list_page_more_click),
                        StatisticsConstants.EVENT_ID,
                        getString(R.string.pasc_ecard_event_lable_ecard_list_page_more_click),
                        StatisticsConstants.PAGE_TYPE,
                        null);
                showPopView();
            }
        });
        recyclerView = findViewById(R.id.rcv_my_ecard);
        recyclerView.setLayoutManager(layoutManager = new SmoothScrollLayoutManager(this));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取卡证列表回调
     **/
    @Override
    public void onEcardListSuccess(boolean isBtnClick, List<EcardInfoResq.EcardInfoBean> bindCardList) {

        hasEcardListUpdated = true;
        if (bindCardList != null && bindCardList.size() > 0) {
            statusView.showContent();
            datas.clear();
            datas.addAll(bindCardList);
            /**判断跳进来的时候是否指定了要显示的卡证**/
            adapter.updateOpenId(showEcardID);
            showListInfo();

        } else {
            if (isBtnClick) {
                faceCheck();
            } else {
                noInfoLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showServiceError(String error) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
    }

    /**
     * 列表错误回调
     **/
    @Override
    public void onEcardListError(boolean isBtnClick, int code, String msg) {

        if (isBtnClick) {
            if (code == EcardDataManager.GetEcardListCallBack.ERROR_NET_ERROR) {

                ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
            } else {

                ToastUtils.toastMsg(msg);
            }
        } else {
            if (!NetworkUtils.isNetworkAvailable()) {
                statusView.showError();
            } else {
                statusView.showServerError();
            }
        }

    }

    /**
     * 错误回调
     **/
    @Override
    public void onError(String code, String error) {
        if (!NetworkUtils.isNetworkAvailable()) {
            statusView.showError();
        } else {
            statusView.showServerError();
        }
    }

    /**
     * 显示加载框
     **/
    @Override
    public void showLoadings() {
        showLoading(null, false);
    }

    /**
     * 取消加载框
     **/
    @Override
    public void dismissLoadings() {
        dismissLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mPresenter.onDestroy();
    }

    /**
     * activity返回重写
     **/
    @Override
    public void onBackPressed() {
        keyback();
    }

    /**
     * 返回按钮监听
     **/
    private void keyback() {
        if (hasEcardListUpdated) {
            EventBusUtils.postEcardListUpdated();
        }
        if (datas.size() == 0) {
            StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_ecard_introduce_page_use_click),
                    StatisticsConstants.EVENT_ID,
                    getString(R.string.pasc_ecard_event_lable_ecard_introduce_page_back),
                    StatisticsConstants.PAGE_TYPE,
                    null);
        }
        finish();
    }


    /**
     * 登陆校验
     *
     * @param callback 校验结果回调
     */
    private void checkLogin(CheckAuthCallback callback) {
        if (PascUserManager.getInstance().isLogin()) {
            if (callback != null) {
                callback.onSuccess(false);
            }
        } else {
            //未登陆，跳转到登陆
            PascUserManager.getInstance().toLogin(new PascUserLoginListener() {
                @Override
                public void onLoginSuccess() {
                    if (callback != null) {
                        callback.onSuccess(false);
                    }
                }

                @Override
                public void onLoginFailed() {
                    if (callback != null) {
                        callback.onFailed(ERROR_CODE_LOGIN_FAILED, "");
                    }
                }

                @Override
                public void onLoginCancled() {
                    if (callback != null){
                        callback.onFailed(ERROR_CODE_LOGIN_CANCELD,"");
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticsManager.getInstance().onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        showEcardID = adapter.getItemOpenId();
        StatisticsManager.getInstance().onPause(this);
    }

    /**
     * 认证校验
     *
     * @param callback 校验回调
     */
    private void checkCert(CheckAuthCallback callback) {
        if (PascUserManager.getInstance().isCertification()) {
            if (callback != null) {
                callback.onSuccess(false);
            }
        } else {
            PascUserManager.getInstance().toCertification(PascUserConfig.CERTIFICATION_TYPE_ALL_AND_FINISH_WHEN_SUCCESS, new PascUserCertListener() {
                @Override
                public void onCertificationSuccess() {
                    if (callback != null) {
                        callback.onSuccess(true);
                    }
                }

                @Override
                public void onCertificationFailed() {
                    if (callback != null) {
                        callback.onFailed(ERROR_CODE_CERT_FAILED, "");
                    }
                }

                @Override
                public void onCertificationCancled() {
                    if (callback != null) {
                        callback.onFailed(ERROR_CODE_CERT_CANCELD, "");
                    }
                }
            });
        }

    }

    /**
     * 权限校验
     *
     * @param callback
     */
    private void checkAuth(CheckAuthCallback callback) {
        checkLogin(new CheckAuthCallback() {

            @Override
            public void onSuccess(boolean isCertBack) {
                checkCert(callback);
            }

            @Override
            public void onFailed(int code, String msg) {
                if (callback != null) {
                    callback.onFailed(code, msg);
                }
            }
        });
    }


    //错误码：登陆失败
    private static final int ERROR_CODE_LOGIN_FAILED = 100;
    //错误码：取消登陆
    private static final int ERROR_CODE_LOGIN_CANCELD = 101;
    //错误码：认证失败
    private static final int ERROR_CODE_CERT_FAILED = 102;
    //错误码：取消认证
    private static final int ERROR_CODE_CERT_CANCELD = 103;

    /**
     * 登陆、认证校验回调接口
     */
    interface CheckAuthCallback {
        /**
         * 校验成功
         *
         * @param isCertBack 是否是跳转到认证界面认证返回，如果是到话，不需要人脸核验，即直接跳转到一键添加界面
         */
        void onSuccess(boolean isCertBack);

        /**
         * 校验失败
         *
         * @param code 失败错误码
         * @param msg  失败错误信息
         */
        void onFailed(int code, String msg);
    }



}
