package com.pasc.business.ecardbag.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by lingchun147 on 2019/4/26.
 */
public class SmoothScrollLayoutManager extends LinearLayoutManager {

  private RecyclerView.State mState;
  private RecyclerView.Recycler mRecycler;
  private float millisecondsPreInch;


  public SmoothScrollLayoutManager(Context context) {
    super(context);
    millisecondsPreInch = context.getResources().getDisplayMetrics().density * 0.2f;
  }

  public SmoothScrollLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  public SmoothScrollLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    super.onLayoutChildren(recycler, state);
    mState = state;
    mRecycler = recycler;
  }

  public void smoothScrollToPosition(RecyclerView recyclerView, int position) {
    smoothScrollToPosition(recyclerView, mState, position);
  }

  @Override public void smoothScrollToPosition(RecyclerView view, RecyclerView.State state,
      final int position) {
    LinearSmoothScroller scroller = new LinearSmoothScroller(view.getContext()) {
      @Override protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return millisecondsPreInch / displayMetrics.density;
      }

      @Override public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd,
          int snapPreference) {
        return boxStart - viewStart;
      }
    };

    scroller.setTargetPosition(position);
    startSmoothScroll(scroller);
  }

  public View getViewForPosition(int position) {
    if (mRecycler != null) {
      return mRecycler.getViewForPosition(position);
    }
    return null;
  }
}
