package com.pasc.business.ecardbag.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardSortView;
import com.pasc.business.ecardbag.adapter.EditDragAdapter;
import com.pasc.business.ecardbag.adapter.ItemDragCallback;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.presenter.EcardSortPresenter;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.view.StatusView;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.dialog.OnCloseListener;
import com.pasc.lib.widget.dialog.OnConfirmListener;
import com.pasc.lib.widget.dialog.common.ConfirmDialogFragment;
/**
 *  功能：排序
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */

@Route(path = ArouterPath.ECARD_LIST_SORT)
public class EcardSortActivivity extends BaseEcardActivity implements EcardSortView {
    StatusView statusView;
    EditDragAdapter adapter;
    RecyclerView recyclerView;
    private ConfirmDialogFragment dialogFragment;
    private EcardSortPresenter mPresenter;

    //是否重新排序了
    private boolean hasSorted = false;

    @Override
    protected void initView() {
        mPresenter = new EcardSortPresenter(this);
        setTitle(getResources().getString(R.string.pasc_ecard_sort_title));
        statusView = findViewById(R.id.statusView);
        recyclerView = findViewById(R.id.rcv_my_ecard);
        adapter = new EditDragAdapter(this, new EditDragAdapter.OnEditDragListener() {

            @Override
            public void onEditDrag() {

            }
        });
        ItemDragCallback itemDragAndSwipeCallback = new ItemDragCallback(adapter, new ItemDragCallback.OnDragEndListener() {
            @Override
            public void onEnd() {
                //重新排序了
                hasSorted = true;
            }
        });
        setOnBack(new OnBack() {
            @Override
            public void onBack() {
                keyBack();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        adapter.setNewData(EcardDataManager.getInstance().getEcardListFromCache());

    }

    @Override
    protected int layoutResId() {
        return R.layout.pasc_ecard_activity_list;
    }

    @Override
    public void onSort(Object o) {
        finish();
    }

    @Override
    public void onError(String code, String error) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showDialohg(getResources().getString(R.string.pasc_ecard_net_error_tip));
        } else {
            showDialohg(error);
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
                            mPresenter.sort(adapter.getData());
                        }
                    })
                    .setOnCloseListener(new OnCloseListener<ConfirmDialogFragment>() {
                        @Override
                        public void onClose(ConfirmDialogFragment confirmDialogFragment) {
                            finish();
                        }
                    })
                    .setDesc(errorMsg)
                    .setDescColor(getResources().getColor(R.color.pasc_ecard_dialog_title_color))
                    .setCancelable(false)
                    .setConfirmText(getString(R.string.pasc_ecard_dialog_sort_confirm))
                    .setCloseTextColor(getResources().getColor(R.color.pasc_ecard_dialog_close_color))
                    .setConfirmTextColor(getResources().getColor(R.color.pasc_ecard_dialog_comfirm_color))
                    .build();
        }
        if (dialogFragment.getDialog() == null || !dialogFragment.getDialog().isShowing()) {
            dialogFragment.show(getSupportFragmentManager(), "queryIsCerted");
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
        mPresenter.onDestroy();
    }
    @Override
    public void showServiceError(String error) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
    }
    @Override
    public void onBackPressed() {
        keyBack();
    }

    private void keyBack(){

        if (hasSorted){
            mPresenter.sort(adapter.getData());
        }else {
            finish();
        }
    }
}
