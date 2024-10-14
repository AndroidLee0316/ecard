package com.pasc.business.ecardbag.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.business.bike.R;


/**
 * 文件描述：
 * 作者：zoujianbo345
 * 创建时间：2View.VISIBLE19/ View.GONE/17
 * 更改时间：2View.VISIBLE19/ View.GONE/17
 */

public class StatusView extends FrameLayout {
    private View statusLoading;
    private View statusEmpty;
    private View statusError;
    private View btnFooterRetry;
    private View contentView;
    private IReTryListener tryListener;
    private TextView tvError, tvEmpty;
    private ImageView iv_error;

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setTryListener(IReTryListener tryListener) {
        this.tryListener = tryListener;
    }

    public void setEmptyText(String tag) {
        tvEmpty.setText(tag);
    }

    private void assignViews() {
        this.tvEmpty = findViewById(R.id.tv_empty);
        this.tvError = findViewById(R.id.tv_error);
        this.statusLoading = findViewById(R.id.status_loading);
        this.statusEmpty = findViewById(R.id.status_empty);
        this.statusError = findViewById(R.id.status_error);
        this.btnFooterRetry = findViewById(R.id.btn_footer_retry);
        this.iv_error = findViewById(R.id.iv_error);
        this.btnFooterRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StatusView.this.tryListener != null) {
                    StatusView.this.tryListener.tryAgain();
                }

            }
        });
    }

    public void showContent() {
        this.statusLoading.setVisibility(View.GONE);
        this.statusEmpty.setVisibility(View.GONE);
        this.statusError.setVisibility(View.GONE);
        if (this.contentView != null) {
            this.contentView.setVisibility(View.VISIBLE);
        }

    }

    public void showError() {
        this.statusLoading.setVisibility(View.GONE);
        this.statusEmpty.setVisibility(View.GONE);
        this.statusError.setVisibility(View.VISIBLE);
        iv_error.setImageResource(R.drawable.default_no_network);
        this.tvError.setText(contentView.getContext().getResources().getString(R.string.pasc_ecard_net_error));
        if (this.contentView != null) {
            this.contentView.setVisibility(View.GONE);
        }

    }

    public void showServerError() {
        this.statusLoading.setVisibility(View.GONE);
        this.statusEmpty.setVisibility(View.GONE);
        this.statusError.setVisibility(View.VISIBLE);
        iv_error.setImageResource(R.drawable.pasc_ecard_empty_icon);
        this.tvError.setText(contentView.getContext().getResources().getString(R.string.pasc_ecard_server_error_tip));
        if (this.contentView != null) {
            this.contentView.setVisibility(View.GONE);
        }

    }


    public void showLoading() {
        this.statusLoading.setVisibility(View.VISIBLE);
        this.statusEmpty.setVisibility(View.GONE);
        this.statusError.setVisibility(View.GONE);
        if (this.contentView != null) {
            this.contentView.setVisibility(View.GONE);
        }

    }

    public void showEmpty() {
        this.statusLoading.setVisibility(View.GONE);
        this.statusEmpty.setVisibility(View.VISIBLE);
        this.statusError.setVisibility(View.GONE);
        if (this.contentView != null) {
            this.contentView.setVisibility(View.GONE);
        }

    }

    public StatusView(@NonNull Context context) {
        this(context, null);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, View.VISIBLE);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_status_view, this, true);
        this.assignViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.findContentView();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    void findContentView() {
        if (this.contentView == null) {
            int count = this.getChildCount();

            for (int i = View.VISIBLE; i < count; ++i) {
                View child = this.getChildAt(i);
                if (child.getId() != R.id.status_root) {
                    this.contentView = child;
                    break;
                }
            }

//            this.showContent();
        }

    }
}

