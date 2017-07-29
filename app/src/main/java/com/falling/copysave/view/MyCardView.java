package com.falling.copysave.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by falling on 2017/7/29.
 */

public class MyCardView extends CardView {
    private int downY;
    private int downX;
    private int layoutLeft;
    private int layoutRight;
    private int layoutTop;
    private int layoutBottom;
    private int screenWidth;
    private Scroller scroller;
    private static final int SNAP_VELOCITY = 600;
    private VelocityTracker velocityTracker;
    private boolean isSlide = false;
    private int mTouchSlop;
    /**
     * downX
     * 移除item后的回调接口
     */
    private RemoveListener mRemoveListener;
    /**
     * 用来指示item滑出屏幕的方向,向左或者向右,用一个枚举值来标记
     */
    private RemoveDirection removeDirection;


    // 滑动删除方向的枚举值
    public enum RemoveDirection {
        RIGHT, LEFT
    }

    public MyCardView(Context context) {
        this(context, null);
    }

    public MyCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 设置滑动删除的回调接口
     *
     * @param removeListener
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    /**
     * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);

                // 假如scroller滚动还没有结束，我们直接返回
                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                layoutLeft = getLeft();
                layoutRight = getRight();
                layoutTop = getTop();
                layoutBottom = getBottom();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                        || (Math.abs(event.getRawX() - downX) > mTouchSlop && Math
                        .abs(event.getRawY() - downY) < mTouchSlop)) {
                    isSlide = true;

                }
                break;
            }
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private void scrollRight() {
        System.out.println("right");
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (screenWidth + this.getScrollX());
        scroller.startScroll(getLeft(), 0, screenWidth, 0, delta);
        postInvalidate();
    }

    private void scrollLeft() {
        System.out.println("left");
        removeDirection = RemoveDirection.LEFT;
        scroller.startScroll(getLeft(), 0, -screenWidth, 0, screenWidth);
        postInvalidate();
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    private void scrollByDistanceX() {
        // 如果向左滚动的距离大于屏幕的二分之一，就让其删除
        if (this.getLeft() - layoutLeft >= screenWidth / 2) {
            scrollRight();
        } else if (this.getLeft() - layoutLeft <= -screenWidth / 2) {
            scrollLeft();
        } else {
            System.out.println(layoutLeft);
            this.layoutTo(layoutLeft);
        }

    }

    /**
     * 处理我们拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide) {
            requestDisallowInterceptTouchEvent(true);
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getRawX();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    onTouchEvent(cancelEvent);
                    int deltaX = x - downX;
                    downX = x;
                    this.layoutBy(deltaX);
                    return true;
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }
                    recycleVelocityTracker();
                    isSlide = false;
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            this.layoutTo(scroller.getCurrX());
            postInvalidate();
            // 滚动动画结束的时候调用回调接口
            if (scroller.isFinished()) {
                if (mRemoveListener == null) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }
                mRemoveListener.removeItem(this, removeDirection);
            }
        }
    }

    /**
     * 添加用户的速度跟踪器
     *
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        return (int) velocityTracker.getXVelocity();
    }

    private void layoutBy(int offsetX) {
        layout(getLeft() + offsetX, getTop(), getRight() + offsetX, getBottom());
    }

    private void layoutTo(int x) {
        layout(x, layoutTop, layoutRight - layoutLeft + x, layoutBottom);
    }

    public interface RemoveListener {
        void removeItem(MyCardView view, RemoveDirection direction);
    }

}
