package com.pasc.business.ecardbag.activity;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardDetailView;
import com.pasc.business.ecardbag.adapter.EcardDetialAdapter;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.presenter.EcardDetialPresenter;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.view.IReTryListener;
import com.pasc.business.ecardbag.view.StatusView;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.statistics.StatisticsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：卡证详情
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */

@Route(path = ArouterPath.ECARD_INFO_MAIN)
public class EcardInfoActivity extends BaseEcardActivity implements EcardDetailView {
    public static final String ECARD_INFO = "ecardInfo";
    public static final String ECARD_POSIVE = "id_posive";
    StatusView statusView;
    EcardDetialAdapter adapter;
    RecyclerView recyclerView;
    private EcardDetialPresenter mPresenter;
    private EcardInfoResq.EcardInfoBean ecardInfo;
    private int posvise;

    @Override
    protected void initView() {
        ecardInfo = (EcardInfoResq.EcardInfoBean) getIntent().getSerializableExtra(ECARD_INFO);
        posvise = getIntent().getIntExtra(ECARD_POSIVE, -1);

        mPresenter = new EcardDetialPresenter(this);
        setTitle(getResources().getString(R.string.pasc_ecard_detial_title));
        statusView = findViewById(R.id.statusView);
        statusView.showContent();
        recyclerView = findViewById(R.id.rcv_my_ecard);
        adapter = new EcardDetialAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statusView.setTryListener(new IReTryListener() {
            @Override
            public void tryAgain() {

                mPresenter.getDetial(ecardInfo.identifier);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        mPresenter.getDetial(ecardInfo.identifier);
    }

    @Override
    public void showServiceError(String error) {
        statusView.showServerError();
    }

    @Override
    protected int layoutResId() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.pasc_ecard_activity_list;
    }

    /**
     * 获取卡证详情回调
     **/
    @Override
    public void getDetial(EcardDetailResq resq) {
        statusView.showContent();
        EcardDetailResq.EcardDetailInfo info = new EcardDetailResq.EcardDetailInfo();
        List<EcardDetailResq.EcardDetailInfo> datas = new ArrayList<>();
        if (resq != null) {
            info.name = getResources().getString(R.string.pasc_ecard_detial_accout);
            if (EcardDetailResq.STATUE_NOMARL == (resq.cardStatus)) {
                info.value = getResources().getString(R.string.pasc_ecard_detial_accout_nomarl);
            }else if (EcardDetailResq.STATUE_UNKNOW == (resq.cardStatus)){
                if (ecardInfo.cardStatus == EcardDetailResq.STATUE_NOMARL){
                    info.value = getResources().getString(R.string.pasc_ecard_detial_accout_nomarl);
                }else {
                    info.value = getResources().getString(R.string.pasc_ecard_detial_accout_unebal);
                }
            }else {
                info.value = getResources().getString(R.string.pasc_ecard_detial_accout_unebal);
            }
            datas.add(info);
        }

        if (resq.ecardMetaDataVOList != null && resq.ecardMetaDataVOList.size() > 0) {
            datas.addAll(resq.ecardMetaDataVOList);
        }
        adapter.setNewData(datas);
        if (posvise >= 0 && resq.cardStatus != EcardDetailResq.STATUE_UNKNOW) {
            if (EcardDataManager.getInstance().getEcardListFromCache() != null
                    && EcardDataManager.getInstance().getEcardListFromCache().size() > posvise
                    && EcardDataManager.getInstance().getEcardListFromCache().get(posvise) != null) {
                    EcardInfoResq.EcardInfoBean ecardInfoBean = EcardDataManager.getInstance().getEcardListFromCache().get(posvise);
                    ecardInfoBean.userName = resq.userName;
                    ecardInfoBean.configValue = resq.configValue;
                    ecardInfoBean.name = resq.name;
                    ecardInfoBean.deptName = resq.deptName;
                    ecardInfoBean.cardStatus = resq.cardStatus;
                    ecardInfoBean.bgimgUrl = resq.bgimgUrl;
                    EcardDataManager.getInstance().getEcardListFromCache().set(posvise,ecardInfoBean);
                    EventBusUtils.postEcardListUpdated();
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
        showLoading(null,false);
    }

    /**
     * 取消加载框
     **/
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
}
