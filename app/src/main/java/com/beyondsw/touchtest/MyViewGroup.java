package com.beyondsw.touchtest;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by wensefu on 17-3-10.
 */
public class MyViewGroup extends FrameLayout{

    private static final String TAG = "touch-test";

    private boolean mBeingDragged;
    private float mInitDownX;
    private float mInitDownY;
    private float mLastX;
    private float mLastY;
    private float mTouchSlop;
    private View mTouchView;
    private boolean mTouchOnChild;


    public MyViewGroup(Context context) {
        this(context,null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    private void updateTouchView() {
        int cnt = getChildCount();
        if (cnt > 0) {
            mTouchView = getChildAt(0);
        } else {
            mTouchView = null;
        }
    }

    @Override
    public void onViewAdded(View child) {
        updateTouchView();
    }

    @Override
    public void onViewRemoved(View child) {
        updateTouchView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        Log.d(TAG, "MyViewGroup dispatchTouchEvent: action=" + action);
        return super.dispatchTouchEvent(ev);
    }

    private static boolean isTouchOnView(float x, float y, View view) {
        if (view == null) {
            return false;
        }
        Rect rect = new Rect();
        view.getHitRect(rect);
        return rect.contains((int) x, (int) y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        Log.d(TAG, "MyViewGroup onInterceptTouchEvent: action=" + action);
        float x = ev.getX();
        float y = ev.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                //down时记录下初始坐标
                mTouchOnChild = isTouchOnView(x,y,mTouchView);
                if (!mTouchOnChild) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                mLastX = mInitDownX = x;
                mLastY = mInitDownY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                float dx = x - mInitDownX;
                float dy = y - mInitDownY;
                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
                    mBeingDragged = true;
                }
                mLastX = x;
                mLastY = y;
                break;
            }
            case MotionEvent.ACTION_UP:{
                break;
            }
        }
        return mBeingDragged;
    }

    private void performDrag(float dx, float dy) {
        if (mTouchView == null) {
            return;
        }
        mTouchView.offsetLeftAndRight((int) dx);
        mTouchView.offsetTopAndBottom((int) dy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        float x = ev.getX();
        float y = ev.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if(!mTouchOnChild){
                    break;
                }
                if (!mBeingDragged) {
                    mBeingDragged = true;
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                performDrag(dx,dy);
                mLastX = x;
                mLastY = y;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{
                mBeingDragged = false;
                break;
            }
        }
        return true;
    }
}
