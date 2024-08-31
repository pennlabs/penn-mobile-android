package com.pennapps.labs.pennmobile.components.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import eightbitlab.com.blurview.BlurView;
import stream.customalert.CustomAlertDialogue;
import stream.customalert.ui.RoundedCornersDrawable;

public class CustomBlurDialogue extends BlurView {

    public CustomBlurDialogue(Context context) {
        super(context);
        init();
    }

    public CustomBlurDialogue(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initCorners(context);
    }

    public CustomBlurDialogue(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initCorners(context);
    }

    private void init() {

    }

    private void initCorners(Context context) {
            setRoundedCorners(CustomAlertDialogue.Units.dpToPx(context, 15));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void create(View decorView, float radius) {

        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius);
    }

    /**
     * Set Rounded Corners
     * @param cornerRadius - set corner radius in pixels.
     */
    private void setRoundedCorners(int cornerRadius) {

        setBackground(new RoundedCornersDrawable(cornerRadius));
        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        setClipToOutline(true);
    }
}
