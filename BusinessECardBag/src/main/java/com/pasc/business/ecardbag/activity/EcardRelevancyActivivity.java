package com.pasc.business.ecardbag.activity;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardRelevancyView;
import com.pasc.business.ecardbag.adapter.EcardRelevancyAdapter;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.presenter.EcardRelevancyPresenter;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.business.ecardbag.view.IReTryListener;
import com.pasc.business.ecardbag.view.StatusView;
import com.pasc.lib.base.util.DensityUtils;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
import com.pasc.lib.statistics.StatisticsManager;

/**
 * 功能：卡证关联列表（一键绑定）
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
@Route(path = ArouterPath.ECARD_LIST_RELEVANCY)
public class EcardRelevancyActivivity extends BaseEcardActivity implements View.OnClickListener, EcardRelevancyView {
    StatusView statusView;
    EcardRelevancyAdapter adapter;
    RecyclerView recyclerView;
    TextView btn;
    EcardRelevancyPresenter mPresenter;

    @Override
    protected void initView() {
        mPresenter = new EcardRelevancyPresenter(this);
        setTitle(getResources().getString(R.string.pasc_ecard_relevancy_title));
        statusView = findViewById(R.id.statusView);
        statusView.setEmptyText(getResources().getString(R.string.pasc_ecard_relation_no_data));
        statusView.setTryListener(new IReTryListener() {
            @Override
            public void tryAgain() {
                mPresenter.getEcardRelation();
            }
        });
        setOnBack(new OnBack() {
            @Override
            public void onBack() {
                keyBack();
            }
        });
        btn = findViewById(R.id.tv_btn);
        btn.setOnClickListener(this::onClick);
        recyclerView = findViewById(R.id.rcv_my_ecard);
        adapter = new EcardRelevancyAdapter(this, new EcardRelevancyAdapter.OnSelectChangeListener() {
            @Override
            public void onSlectChage() {
                if (adapter.getSelectList().size() <= 0) {

                    btn.setEnabled(false);
                    btn.setAlpha(0.3f);
                } else {
                    btn.setEnabled(true);
                    btn.setAlpha(1.0f);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = DensityUtils.dip2px(view.getContext(), 12);
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.getEcardRelation();

    }

    @Override
    protected int layoutResId() {
        return R.layout.pasc_ecard_activity_relevancy_ecard;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_btn) {

            //埋点：电子证照一键绑定 ：绑定

            StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_relevancy),
                    StatisticsConstants.EVENT_ID,
                    getString(R.string.pasc_ecard_event_lable_relevancy_bind),
                    StatisticsConstants.PAGE_TYPE,
                    null);

            commitInfo();
        }
    }

    /**
     * 提交关联卡证
     **/
    private void commitInfo() {
        mPresenter.commitInfo(adapter.getSelectList());
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
     * 获取未关联列表
     **/
    @Override
    public void getEcardRelation(EcardRelationResq relationResq) {
        statusView.showContent();
        if (relationResq.relationList != null && relationResq.relationList.size() > 0) {
            btn.setVisibility(View.VISIBLE);
            adapter.setNewData(relationResq.relationList);
        } else {
            btn.setVisibility(View.GONE);
            statusView.showEmpty();

        }
    }

    /**
     * 绑定成功回调
     **/
    @Override
    public void bindALl(Object o) {
        finish();
    }

    /**
     * 错误回调
     **/
    @Override
    public void onError(String code, String error) {
        if (!NetworkUtils.isNetworkAvailable()) {
            ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_net_error_tip));
        } else {
            ToastUtils.toastMsg(error);

        }
    }

    /**
     * 获取列表出错
     **/
    @Override
    public void onListError(String code, String error) {
        btn.setVisibility(View.GONE);
        if (!NetworkUtils.isNetworkAvailable()) {
            statusView.showError();
        } else {
            ToastUtils.toastMsg(error);
            statusView.showEmpty();
//            statusView.showServerError();

        }
    }
    @Override
    public void showServiceError(String error) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
    }

    /**
     * 加载框展示
     **/
    @Override
    public void showLoadings() {
        showLoading(null,false);
    }

    /**
     * 取消加载框展示
     **/
    @Override
    public void dismissLoadings() {
        dismissLoading();
    }

    @Override
    public void onBackPressed() {
        keyBack();
    }

    private void keyBack(){
        //埋点
        StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_relevancy),
                StatisticsConstants.EVENT_ID,
                getString(R.string.pasc_ecard_event_lable_relevancy_back),
                StatisticsConstants.PAGE_TYPE,
                null);
        finish();
    }
}
