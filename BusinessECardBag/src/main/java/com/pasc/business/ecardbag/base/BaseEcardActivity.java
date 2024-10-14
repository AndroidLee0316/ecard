package com.pasc.business.ecardbag.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pasc.business.bike.R;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.base.activity.BaseActivity;
import com.pasc.lib.base.util.StatusBarUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.widget.ICallBack;
import com.pasc.lib.widget.toolbar.PascToolbar;

import io.reactivex.disposables.CompositeDisposable;

/**
 *  功能：基类
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public abstract class BaseEcardActivity extends BaseActivity {
    public PascToolbar toolbar;
    public CompositeDisposable disposables = new CompositeDisposable();
    public OnBack onBack;

    @Override
    protected void onInit(@Nullable Bundle bundle) {
        StatusBarUtils.setStatusBarColor(this,true);
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackIconClickListener(new ICallBack() {
                @Override
                public void callBack() {
                    if (onBack != null) {
                        onBack.onBack();
                    } else {
                        finish();
                    }
                }
            });

        }
        initView();
        initData();
        initLinstener();
    }
    /**
     * 初始化view
     * **/
    protected abstract void initView();
    /**
     * 初始化数据
     * **/

    protected abstract void initData();


    protected void initLinstener() {
    }


    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setMore(int res) {
        if (toolbar != null) {
            toolbar.addRightImageButton(res);
        }

    }

    public void setUnderLineShow(boolean isShow) {
        if (toolbar != null) {
            toolbar.enableUnderDivider(isShow);
        }
    }

    public void setBackGone() {
    }

    public void setOnBack(final OnBack onBack) {
        this.onBack = onBack;
    }


    public void showToast(String str) {
        ToastUtils.toastMsg(str);
    }

    public interface OnBack {
        /**
         * 返回
         * **/
        void onBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
