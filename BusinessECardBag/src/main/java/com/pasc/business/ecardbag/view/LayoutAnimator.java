package com.pasc.business.ecardbag.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 *  功能：动画处理
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public class LayoutAnimator {

  /**监听动画的变化，不断设定view的高度值**/
  public static class LayoutHeightUpdateListener implements ValueAnimator.AnimatorUpdateListener {

    private final View mView;

    public LayoutHeightUpdateListener(View view) {
      mView = view;
    }

    @Override public void onAnimationUpdate(ValueAnimator animation) {
      final ViewGroup.LayoutParams lp = mView.getLayoutParams();
      lp.height = (int) animation.getAnimatedValue();
      mView.setLayoutParams(lp);
    }
  }

  /**真正实现具体展开动画的方法，使用ValueAnimator.ofInt生成一系列高度值，然后添加上面的监听**/
  public static Animator ofHeight(View view, int start, int end) {
    ValueAnimator animator = ValueAnimator.ofInt(start, end);
    animator.addUpdateListener(new LayoutHeightUpdateListener(view));
    return animator;
  }
}