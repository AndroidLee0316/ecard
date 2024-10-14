package com.pingan.smt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.pasc.business.ecardbag.EcardManagerImpl;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.user.PascUserConfig;
import com.pasc.business.user.PascUserLoginListener;
import com.pasc.business.user.PascUserManager;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.out.EcardManagerInter;
import com.pasc.lib.ecardbag.out.EcardOutInfo;
import com.pasc.lib.ecardbag.out.PascEcardManager;
import com.pasc.lib.router.BaseJumper;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);


        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PascUserManager.getInstance().toLogin(null);
            }
        });


        findViewById(R.id.cert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PascUserManager.getInstance().toCertification(PascUserConfig.CERTIFICATION_TYPE_ALL_AND_FINISH_WHEN_SUCCESS,null);
            }
        });
        findViewById(R.id.to_my_cardlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PascEcardManager.getInstance().toMyEcardList(null, new EcardManagerInter.EcardUpdateListener() {
                    @Override
                    public void onUpdate() {
                        ToastUtils.toastMsg("接收到了更新的通知");
                    }
                });
            }
        });

        PascUserManager.getInstance().setLoginListener(new PascUserLoginListener() {
            @Override
            public void onLoginSuccess() {
                addEcardListView();
            }

            @Override
            public void onLoginFailed() {

            }

            @Override
            public void onLoginCancled() {

            }
        });

        addEcardListView();
    }

    private void addEcardListView(){
        FrameLayout card_hv = findViewById(R.id.card_hv);
        PascEcardManager.getInstance().addEcardViewList(MainActivity.this, card_hv, EcardManagerInter.ADD_ECARD_VIEW_LIST_TYPE.TYPE_SIMPLE_CENTER_VIEW, new EcardManagerInter.EcardAddViewListener() {
            @Override
            public void onScucess() {

            }

            @Override
            public void onFailed(int code, String msg) {

            }

            @Override
            public boolean interruptItemClick(EcardOutInfo ecardOutInfo) {
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PascEcardManager.getInstance().onDestroy();
    }
}
