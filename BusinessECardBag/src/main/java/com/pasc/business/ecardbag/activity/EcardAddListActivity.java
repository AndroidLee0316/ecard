package com.pasc.business.ecardbag.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardAddView;
import com.pasc.business.ecardbag.adapter.EcardAddAdapter;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.presenter.EcardAddPresenter;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.view.IReTryListener;
import com.pasc.business.ecardbag.view.StatusView;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.router.BaseJumper;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.dialog.OnCloseListener;
import com.pasc.lib.widget.dialog.OnConfirmListener;
import com.pasc.lib.widget.dialog.common.ConfirmDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 功能：未绑定卡证列表
 * <p>
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
@Route(path = ArouterPath.ECARD_LIST_ADD)
public class EcardAddListActivity extends BaseEcardActivity implements EcardAddView {
    public static final String ECARD_DETIAL = "ECARD_DETIAL";
    StatusView statusView;
    EcardAddAdapter adapter;
    RecyclerView recyclerView;
    private EcardAddPresenter mPresenter;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mPresenter = new EcardAddPresenter(this);
        setTitle(getResources().getString(R.string.pasc_ecard_add_title));
        statusView = findViewById(R.id.statusView);
        recyclerView = findViewById(R.id.rcv_my_ecard);
        adapter = new EcardAddAdapter(this, new EcardAddAdapter.OnBindCardLersenter() {
            @Override
            public void onBind(AddListResq.AddListBean info, int pos) {
                mPresenter.getDetial(info.identifier, pos);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statusView.setTryListener(new IReTryListener() {
            @Override
            public void tryAgain() {
                mPresenter.addList();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        mPresenter.addList();
    }


    @Override
    protected int layoutResId() {
        return R.layout.pasc_ecard_activity_list;
    }


    @Override
    public void bindALl(Object o) {

    }

    /**
     * 获取未绑定卡证列表回调
     **/
    @Override
    public void addList(AddListResq resq) {
        if (resq.notBindList != null && resq.notBindList.size() > 0) {
            statusView.showContent();
            adapter.setNewData(resq.notBindList);
        } else {
            statusView.showEmpty();

        }
    }

    @Override
    public void getDetial(EcardDetailResq resq, int pos) {
        if (TextUtils.isEmpty(resq.name)) {
            showDialohg(adapter.getData().get(pos).name);

        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(EcardAddCardInfoActivity.ECARD_DETIAL, resq);
            BaseJumper.jumpBundleARouter(ArouterPath.ECARD_INFO_ADD, bundle);
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
        StatisticsManager.getInstance().onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.onDestroy();
    }

    @Override
    public void onDialogError(String code, String error) {

    }

    /**
     * 监听卡证更新
     **/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEven(String msg) {
        //卡证添加成功后需要关闭添加列表页
        if (EventBusUtils.EVENT_ECARD_LIST_ADDED.equals(msg)) {
            finish();
        }

    }

    @Override
    public void onError(String code, String error) {
        ToastUtils.toastMsg(error);
    }

    @Override
    public void onListError(String code, String error) {
        if (!NetworkUtils.isNetworkAvailable()) {
            statusView.showError();
        } else {
            statusView.showServerError();
        }
    }
    @Override
    public void showServiceError(String error) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
    }

    /**
     * 显示加载框
     **/
    @Override
    public void showLoadings() {
        showLoading(null,false);
    }

    /**
     * 取消加载框
     **/
    @Override
    public void dismissLoadings() {
        dismissLoading();
    }

    private ConfirmDialogFragment dialogFragment;

    /***
     * 移除提示
     * **/
    private void showDialohg(String errorMsg) {
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
                    .setDesc(getResources().getString(R.string.pasc_ecard_no_bind_tip) + errorMsg)
                    .setDescColor(getResources().getColor(R.color.pasc_ecard_dialog_title_color))
                    .setCancelable(false)
                    .setHideConfirmButton(true)
                    .setCloseText(getResources().getString(R.string.pasc_ecard_list_unuser_btn))
                    .setConfirmText(getResources().getString(R.string.pasc_ecard_list_dialog_delect))
                    .setCloseTextColor(getResources().getColor(R.color.pasc_ecard_dialog_comfirm_color))
                    .setConfirmTextColor(getResources().getColor(R.color.pasc_ecard_dialog_close_color))
                    .build();
        }
        if (dialogFragment.getDialog() == null || !dialogFragment.getDialog().isShowing()) {
            dialogFragment.show(getSupportFragmentManager(), "queryIsCerted");
        }
    }
}
