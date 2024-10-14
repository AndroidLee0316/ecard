package com.pasc.business.ecardbag.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.pasc.lib.widget.refreshlayout.util.DensityUtil;

/**
 * 功能：动画
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class ViewHolderAnimator {

    public static class ViewHolderAnimatorListener extends AnimatorListenerAdapter {
        private final RecyclerView.ViewHolder mHolder; //holder对象

        /**
         * 设定在动画开始结束和取消状态下是否可以被回收
         **/
        public ViewHolderAnimatorListener(RecyclerView.ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mHolder.setIsRecyclable(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) { //
            /** 结束时**/
            mHolder.setIsRecyclable(true);
        }

        @Override
        public void onAnimationCancel(Animator animation) { //取消时
            mHolder.setIsRecyclable(true);
        }
    }

    /**
     * 设定在动画结束后View的宽度和高度分别为match_parent,warp_content
     **/
    public static class LayoutParamsAnimatorListener extends AnimatorListenerAdapter {
        private final View mView;
        private final int mParamsWidth;
        private final int mParamsHeight;

        public LayoutParamsAnimatorListener(View view, int paramsWidth, int paramsHeight) {
            mView = view;
            mParamsWidth = paramsWidth;
            mParamsHeight = paramsHeight;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mView.getLayoutParams();

            params.width = mParamsWidth;
            params.height = mParamsHeight;
            mView.setLayoutParams(params);
        }
    }

    /**
     * OpenHolder中动画的具体操作方法
     **/
    public static Animator ofItemViewHeight(View view, View parent, RecyclerView.ViewHolder holder,
                                            boolean open) {

        if (parent == null) {
            throw new IllegalStateException("Cannot animate the layout of a view that has no parent");
        }
        int start = view.getMeasuredHeight();
        int end;
        /**测量扩展动画的起始高度和结束高度  展开**/
        if (open) {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            end = view.getMeasuredHeight();
        } else {//关闭
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(DensityUtil.dp2px(90), View.MeasureSpec.EXACTLY));
            end = view.getMeasuredHeight();
        }
        /** 具体的展开动画**/
        final Animator animator = LayoutAnimator.ofHeight(view, start, end);
        /**设定该Item在动画开始结束和取消时能否被recycle**/
        if (holder != null) {
            animator.addListener(new ViewHolderAnimatorListener(holder));
        }
        /**设定结束时这个Item的宽高**/
        animator.addListener(
                new LayoutParamsAnimatorListener(view, ViewGroup.LayoutParams.MATCH_PARENT, end));
        animator.setDuration(Math.abs(start - end));
        return animator;
    }
}