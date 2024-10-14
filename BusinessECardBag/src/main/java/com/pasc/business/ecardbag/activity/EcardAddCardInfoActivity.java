package com.pasc.business.ecardbag.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.iview.EcardAddView;
import com.pasc.business.ecardbag.base.BaseEcardActivity;
import com.pasc.business.ecardbag.presenter.EcardAddPresenter;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.business.ecardbag.utils.StringUtils;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.statistics.StatisticsManager;
import com.pasc.lib.widget.dialog.OnCloseListener;
import com.pasc.lib.widget.dialog.OnConfirmListener;
import com.pasc.lib.widget.dialog.common.ConfirmDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：绑定卡证界面
 * <p>
 *
 * @author : by zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
@Route(path = ArouterPath.ECARD_INFO_ADD)
public class EcardAddCardInfoActivity extends BaseEcardActivity implements View.OnClickListener, EcardAddView {
    public static final String ECARD_DETIAL = "ECARD_DETIAL";
    private TextView tvAddress, tvCarsdName, tvId, tvUserName;
    private ImageView bgImag, eyeImg;
    private boolean isShow;
    private EcardDetailResq info;
    private EcardAddPresenter mPresenter;
    private ConfirmDialogFragment dialogFragment;
    List<EcardRelationResq.EcardRelationInfo> datas = new ArrayList<>();
    private RelativeLayout rl;
    private TextView tvBtn, tvCacle;


    @Override
    protected void initView() {
        rl = findViewById(R.id.rl);
        mPresenter = new EcardAddPresenter(this);
        info = (EcardDetailResq) getIntent().getSerializableExtra(ECARD_DETIAL);
        tvBtn = findViewById(R.id.tv_btn);
        tvBtn.setOnClickListener(this::onClick);
        tvCacle = findViewById(R.id.tv_cancel);
        tvCacle.setOnClickListener(this::onClick);
        tvAddress = findViewById(R.id.tv_address);
        tvCarsdName = findViewById(R.id.tv_card_name);
        tvId = findViewById(R.id.tv_id);
        tvUserName = findViewById(R.id.tv_name);
        bgImag = findViewById(R.id.iv_bg);
        eyeImg = findViewById(R.id.iv_eye);
        eyeImg.setOnClickListener(this);

        setOnBack(new OnBack() {
            @Override
            public void onBack() {
                keyBack();
            }
        });
    }

    @Override
    protected void initData() {
        if (info != null && !TextUtils.isEmpty(info.name)) {
            EcardRelationResq.EcardRelationInfo pamars = new EcardRelationResq.EcardRelationInfo();
            pamars.identifier = info.identifier;
            pamars.cardStatus = info.cardStatus;
            pamars.configValue = info.configValue;

            datas.add(pamars);
            setTitle(info.name);
            rl.setVisibility(View.VISIBLE);
            tvBtn.setVisibility(View.VISIBLE);
            tvCacle.setVisibility(View.VISIBLE);
            tvAddress.setText(info.deptName);
            tvCarsdName.setText(info.name);
            tvId.setText(StringUtils.getString(info.configValue));
            eyeImg.setImageResource(R.drawable.pasc_ecard_eye_hide);
            tvUserName.setText(StringUtils.getString(info.userName));
            if (TextUtils.isEmpty(info.configValue)) {
                eyeImg.setVisibility(View.INVISIBLE);
                tvId.setVisibility(View.INVISIBLE);
            } else {
                eyeImg.setVisibility(View.VISIBLE);
                tvId.setVisibility(View.VISIBLE);
            }

            if (info.bgimgUrl != null) {
                PascImageLoader.getInstance().loadImageUrl(info.bgimgUrl.p4, bgImag, R.drawable.pasc_ecard_add_info_bg, R.drawable.pasc_ecard_add_info_bg);
            }
        }
    }

    @Override
    protected int layoutResId() {
        return R.layout.pasc_ecard_activity_add_info;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            //埋点
            StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_bind),
                    StatisticsConstants.EVENT_ID,
                    getString(R.string.pasc_ecard_event_lable_bind_no_bind),
                    StatisticsConstants.PAGE_TYPE,
                    null);
            finish();
        } else if (v.getId() == R.id.tv_btn) {
            //埋点
            StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_bind),
                    StatisticsConstants.EVENT_ID,
                    getString(R.string.pasc_ecard_event_lable_bind_bind),
                    StatisticsConstants.PAGE_TYPE,
                    null);
            showAddDialog(info == null ? "" : info.name);
        } else if (v.getId() == R.id.iv_eye) {
            if (isShow) {
                isShow = false;
                tvId.setText(StringUtils.getString(info.configValue));
                eyeImg.setImageResource(R.drawable.pasc_ecard_eye_hide);
            } else {
                tvId.setText(StringUtils.handleText(info.configValue, 20));
                eyeImg.setImageResource(R.drawable.pasc_ecard_eye_show);
                isShow = true;
            }

        }
    }

    @Override
    public void onListError(String code, String error) {

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
     * 绑定成功回调
     **/
    @Override
    public void bindALl(Object o) {
        ToastUtils.toastMsg(R.string.pasc_ecard_add_card_success);
        EventBusUtils.postEcardListAdded();
        finish();

    }

    @Override
    public void addList(AddListResq resq) {

    }

    /**
     * 获取证照详情回调
     **/
    @Override
    public void getDetial(EcardDetailResq resq, int pos) {
        info = resq;
        if (info != null && !TextUtils.isEmpty(info.name)) {
            EcardRelationResq.EcardRelationInfo pamars = new EcardRelationResq.EcardRelationInfo();
            pamars.identifier = info.identifier;
            pamars.cardStatus = info.cardStatus;
            pamars.configValue = info.configValue;

            datas.add(pamars);
            setTitle(info.name);
            rl.setVisibility(View.VISIBLE);
            tvBtn.setVisibility(View.VISIBLE);
            tvCacle.setVisibility(View.VISIBLE);
            tvAddress.setText(info.deptName);
            tvCarsdName.setText(info.name);
            tvId.setText(StringUtils.handleText(info.configValue, 20));
            eyeImg.setImageResource(R.drawable.pasc_ecard_eye_hide);
            tvUserName.setText(StringUtils.getString(info.userName));
            if (info.bgimgUrl != null) {
                PascImageLoader.getInstance().loadImageUrl(info.bgimgUrl.p4, bgImag, R.drawable.pasc_ecard_add_info_bg, R.drawable.pasc_ecard_add_info_bg);
            }
        }
    }

    /**
     * 错误弹框提示回调
     **/
    @Override
    public void onDialogError(String code, String error) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showDialohg(getResources().getString(R.string.pasc_ecard_net_error_tip));
        } else {
            showDialohg(error);

        }
    }

    /**
     * 接口错误回调
     **/
    @Override
    public void onError(String code, String error) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showDialohg(getResources().getString(R.string.pasc_ecard_net_error_tip));
        } else {
            ToastUtils.toastMsg(error);

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
    public void showServiceError(String error) {
        ToastUtils.toastMsg(getResources().getString(R.string.pasc_ecard_server_error_toast));
    }


    /**
     * 显示是否添加的对话框
     */
    private void showAddDialog(String cardName){
        ConfirmDialogFragment dialog = new ConfirmDialogFragment.Builder()
                .setCancelable(true)
                .setOnConfirmListener(new OnConfirmListener<ConfirmDialogFragment>() {
                    @Override
                    public void onConfirm(ConfirmDialogFragment confirmDialogFragment) {
                        confirmDialogFragment.dismiss();
                        confirmDialogFragment.onDestroy();

                        //埋点
                        StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_bind_sure),
                                StatisticsConstants.EVENT_ID,
                                getString(R.string.pasc_ecard_event_lable_bind_sure_sure),
                                StatisticsConstants.PAGE_TYPE,
                                null);

                        mPresenter.commitInfo(datas);
                    }
                })
                .setOnCloseListener(new OnCloseListener<ConfirmDialogFragment>() {
                    @Override
                    public void onClose(ConfirmDialogFragment confirmDialogFragment) {
                        confirmDialogFragment.dismiss();
                        confirmDialogFragment.onDestroy();

                        //埋点
                        StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_bind_sure),
                                StatisticsConstants.EVENT_ID,
                                getString(R.string.pasc_ecard_event_lable_bind_sure_cancle),
                                StatisticsConstants.PAGE_TYPE,
                                null);

                    }
                })
                .setDesc(String.format(getString(R.string.pasc_ecard_add_warning),cardName))
                .setDescColor(getResources().getColor(R.color.pasc_ecard_dialog_title_color))
                .setCancelable(false)
                .setCloseText(getString(R.string.widget_btn_cancel))
                .setConfirmText(getString(R.string.widget_btn_ok))
                .setCloseTextColor(getResources().getColor(R.color.pasc_ecard_dialog_close_color))
                .setConfirmTextColor(getResources().getColor(R.color.pasc_ecard_dialog_comfirm_color))
                .build();
        dialog.show(getSupportFragmentManager(),"showAddDialog");
    }

    /**
     * 显示错误弹框
     **/
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
                            mPresenter.commitInfo(datas);

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
            dialogFragment.show(getSupportFragmentManager(), "showDialohg");
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
    public void onBackPressed() {
        keyBack();
    }

    private void keyBack(){
        //埋点
        StatisticsManager.getInstance().onEvent(getString(R.string.pasc_ecard_event_id_bind),
                StatisticsConstants.EVENT_ID,
                getString(R.string.pasc_ecard_event_lable_bind_back),
                StatisticsConstants.PAGE_TYPE,
                null);
        finish();
    }
}
