package com.pasc.business.ecardbag.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.pasc.business.bike.R;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

/**
 *  功能：排序动画
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public class ItemDragCallback extends ItemTouchHelper.Callback {
    private EditDragAdapter mAdapter;
    private OnDragEndListener onDragEndListener;


    public ItemDragCallback(EditDragAdapter mAdapter, OnDragEndListener onDragEndListener) {
        this.mAdapter = mAdapter;
        this.onDragEndListener = onDragEndListener;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        EditDragAdapter adapter = (EditDragAdapter) recyclerView.getAdapter();
        int position = viewHolder.getLayoutPosition();
        //第一个item不用交换
//        if (position == 0) {
//            return 0;
//        }
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();   //拖动的position
        int toPosition = target.getAdapterPosition();     //释放的position
        int position = viewHolder.getLayoutPosition();
        //第一个item不用交换
//        if (position == 0) {
//            return false;
//        }
        mAdapter.itemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                               RecyclerView.ViewHolder target) {
        return super.canDropOver(recyclerView, current, target);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (dX != 0 && dY != 0 || isCurrentlyActive) {
            EditDragAdapter adapter = (EditDragAdapter) recyclerView.getAdapter();
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG) {
            //长按时调用
            EditDragAdapter.CardlHolder holder = (EditDragAdapter.CardlHolder) viewHolder;
        }else if (actionState == ACTION_STATE_IDLE){
            if (onDragEndListener != null){
                onDragEndListener.onEnd();
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (onDragEndListener != null) {
            onDragEndListener.onEnd();
        }
        //松手时调用
        EditDragAdapter.CardlHolder holder = (EditDragAdapter.CardlHolder) viewHolder;
    }

    public interface OnDragEndListener {
        /**
         * 排序完成
         * **/
        void onEnd();
    }
}
