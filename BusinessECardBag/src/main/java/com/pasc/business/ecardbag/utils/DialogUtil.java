package com.pasc.business.ecardbag.utils;

import android.app.DialogFragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.pasc.business.bike.R;
import com.pasc.lib.widget.dialog.OnCloseListener;
import com.pasc.lib.widget.dialog.OnConfirmListener;
import com.pasc.lib.widget.dialog.common.ConfirmDialogFragment;

/**
 * 功能：
 *
 * @author lichangbao702
 * @email : lichangbao702@pingan.com.cn
 * @date : 2020/2/6
 */
public class DialogUtil {


    public static void showIknowDialog(FragmentActivity activity, String msg){
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment.Builder()
                .setCancelable(true)
                .setOnConfirmListener(new OnConfirmListener<ConfirmDialogFragment>() {
                    @Override
                    public void onConfirm(ConfirmDialogFragment confirmDialogFragment) {
                        confirmDialogFragment.dismiss();
                        confirmDialogFragment.onDestroy();
                    }
                })
                .setDesc(msg)
                .setDescColor(activity.getResources().getColor(R.color.pasc_ecard_dialog_title_color))
                .setCancelable(true)
                .setHideCloseButton(true)
                .setConfirmText(activity.getString(R.string.pasc_i_know))
                .setConfirmTextColor(activity.getResources().getColor(R.color.pasc_ecard_dialog_comfirm_color))
                .build();
        dialogFragment.show(activity.getSupportFragmentManager(),"iknowDialog");
    }
}
