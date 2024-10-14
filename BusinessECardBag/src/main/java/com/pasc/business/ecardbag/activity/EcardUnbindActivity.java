package com.pasc.business.ecardbag.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardUnBIndView;
import com.pasc.business.ecardbag.presenter.EcardUnbindPresenter;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.business.ecardbag.adapter.EcardUnbindAdapter;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.view.StatusView;
import com.pasc.business.user.PascUserFaceCheckListener;
import com.pasc.business.user.PascUserManager;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.out.PascEcardManager;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.dialog.OnCloseListener;
import com.pasc.lib.widget.dialog.OnConfirmListener;
import com.pasc.lib.widget.dialog.common.ConfirmDialogFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能：解绑
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
@Route(path = ArouterPath.ECARD_LIST_UNBIND)
public class EcardUnbindActivity extends BaseEcardActivity implements EcardUnBIndView {
    private Context mContext;
    StatusView statusView;
    EcardUnbindAdapter adapter;
    RecyclerView recyclerView;
    private EcardUnbindPresenter mPresenter;
    private String name;

    @Override
    protected void initView() {
        mPresenter = new EcardUnbindPresenter(this);
        setTitle(getResources().getString(R.string.pasc_ecard_unbind_title));
        statusView = findViewById(R.id.statusView);
        statusView.setEmptyText(getResources().getString(R.string.pasc_ecard_unbind_no_date));
        recyclerView = findViewById(R.id.rcv_my_ecard);
        adapter = new EcardUnbindAdapter(this, new EcardUnbindAdapter.OnDeletListerner() {
            @Override
            public void onDelect(EcardInfoResq.EcardInfoBean info, int pos) {
                name = info.name;
                showDialohg(info.name, info.identifier, pos);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        mContext = this;
        adapter.setNewData(EcardDataManager.getInstance().getEcardListFromCache());

    }

    /**
     * 人脸核验
     *
     * @param identifier
     * @param pos
     */
    public void faceCheck(String identifier, int pos) {

        PascUserManager.getInstance().toFaceCheck(PascEcardManager.getInstance().getConfig().getFaceCheckAppID(), new PascUserFaceCheckListener() {
            @Override
            public void onSuccess(Map<String, String> map) {
                mPresenter.unBind(identifier, map.get("certId"), pos);
            }

            @Override
            public void onFailed() {
                ToastUtils.toastMsg(R.string.pasc_ecard_face_check_failed);
            }

            @Override
            public void onCancled() {
            }
        });
    }

    @Override
    protected int layoutResId() {
        return R.layout.pasc_ecard_activity_list;
    }

    /**
     * 解绑成功
     **/
    @Override
    public void unBind(Object o) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_unbind_success) + name);
        adapter.setNewData(EcardDataManager.getInstance().getEcardListFromCache());
        if (adapter.getData().size() == 0) {
            statusView.showEmpty();
        }
    }

    @Override
    public void onError(String code, String error) {
        if (NetworkUtils.isNetworkAvailable()) {
            ToastUtils.toastMsg(error);
        } else {
            ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_net_error_tip));
        }
    }

    @Override
    public void showLoadings() {
        showLoading(null,false);
    }

    @Override
    public void dismissLoadings() {
        dismissLoading();
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatisticsManager.getInstance().onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        StatisticsManager.getInstance().onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    /**
     * 解绑弹框确认
     **/
    private ConfirmDialogFragment dialogFragment;

    /***
     * 移除提示
     * **/
    private void showDialohg(String ecardName, final String identifier, int pos) {
        if (dialogFragment == null) {
            dialogFragment = new ConfirmDialogFragment.Builder()
                    .setCancelable(true)
                    .setOnConfirmListener(new OnConfirmListener<ConfirmDialogFragment>() {
                        @Override
                        public void onConfirm(ConfirmDialogFragment confirmDialogFragment) {
                            dialogFragment.dismiss();
                            dialogFragment.onDestroy();
                            dialogFragment = null;

                            //埋点：解绑证照 - 解绑
                            HashMap<String, String> eventMap = new HashMap<>();
                            eventMap.put(ecardName, getString(R.string.pasc_ecard_event_lable_unbind_unbind));
                            StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_unbind),
                                    StatisticsConstants.EVENT_ID,
                                    getString(R.string.pasc_ecard_event_lable_unbind),
                                    StatisticsConstants.PAGE_TYPE,
                                    eventMap);

                            faceCheck(identifier, pos);
                        }
                    })

                    .setOnCloseListener(new OnCloseListener<ConfirmDialogFragment>() {
                        @Override
                        public void onClose(ConfirmDialogFragment confirmDialogFragment) {
                            dialogFragment.dismiss();
                            dialogFragment.onDestroy();
                            dialogFragment = null;

                            //埋点：解绑证照 - 保留

                            HashMap<String, String> eventMap = new HashMap<>();
                            eventMap.put(ecardName, getString(R.string.pasc_ecard_event_lable_unbind_no_unbind));
                            StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_unbind),
                                    StatisticsConstants.EVENT_ID,
                                    getString(R.string.pasc_ecard_event_lable_unbind),
                                    StatisticsConstants.PAGE_TYPE,
                                    eventMap);

                        }
                    })
                    .setDesc(getResources().getString(R.string.pasc_ecard_unbind_dialog_tip) + ecardName + "？")
                    .setDescColor(getResources().getColor(R.color.pasc_ecard_dialog_title_color))
                    .setCancelable(false)
                    .setConfirmText(getResources().getString(R.string.pasc_ecard_unbind_dialog_unbind))
                    .setCloseText(getResources().getString(R.string.pasc_ecard_unbind_dialog_calce))
                    .setCloseTextColor(getResources().getColor(R.color.pasc_ecard_dialog_comfirm_color))
                    .setConfirmTextColor(getResources().getColor(R.color.pasc_ecard_dialog_close_color))
                    .build();
        }
        if (dialogFragment.getDialog() == null || !dialogFragment.getDialog().isShowing()) {
            dialogFragment.show(getSupportFragmentManager(), "delectDialog");
        }
    }

    @Override
    public void showServiceError(String error) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
    }

}
