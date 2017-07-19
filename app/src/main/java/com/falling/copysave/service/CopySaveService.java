package com.falling.copysave.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.falling.copysave.R;
import com.falling.copysave.application.MyApplication;
import com.falling.copysave.bean.NoteBean;
import com.falling.copysave.util.ShareUtil;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by falling on 2017/7/15.
 */

public class CopySaveService extends Service implements View.OnTouchListener{


    private ClipboardManager mManager;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private LinearLayout mFloatLayout;
    private Button mFloatView;
    private Timer mTimer = new Timer();
    private int x = 0;
    private int y = 0;
    private String mSaveContent;
    private Toast mToast;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    public static final int FLOAT_SHOW_TIME = 3000;
    long lastClickTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        int position[] = ShareUtil.getPosition(CopySaveService.this);
        mToast = Toast.makeText(CopySaveService.this, "saved", Toast.LENGTH_SHORT);
        x = position[0];
        y = position[1];
        mManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if (mManager.hasPrimaryClip() && mManager.getPrimaryClip().getItemCount() > 0) {

                    CharSequence saveContent = mManager.getPrimaryClip().getItemAt(0).getText();

                    if (saveContent != null) {
                        mSaveContent = saveContent.toString();
                        createFloatView();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createFloatView() {
        if (mWindowManager != null && mFloatLayout != null && wmParams != null) {
            showFloatView();
            return;
        }
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.x = x;
        wmParams.y = y;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_save, null);
        //浮动窗口按钮
        mFloatView = (Button) mFloatLayout.findViewById(R.id.float_save);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        showFloatView();

        mFloatView.setOnTouchListener(this);
    }

    private void showFloatView() {
        try {
            renewTimer();
            mWindowManager.addView(mFloatLayout, wmParams);
        } catch (Exception ignored) {
            Log.d(TAG, ignored.getMessage());
        }
    }

    private void renewTimer() {
        mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                hideFloatView();
                ShareUtil.savePosition(CopySaveService.this, x, y);
            }
        }, FLOAT_SHOW_TIME);
    }

    private void hideFloatView() {
        try {
            mWindowManager.removeView(mFloatLayout);
        } catch (Exception ignored) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        x = (int) (event.getRawX() - mFloatView.getMeasuredWidth() / 2);
        y = (int) (event.getRawY() - mFloatView.getMeasuredHeight());

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getEventTime() - event.getDownTime() < 100) {
                    return false;
                }
                wmParams.x = x;
                wmParams.y = y;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                renewTimer();
                return true;
            case MotionEvent.ACTION_UP: // click
                long now = Calendar.getInstance().getTimeInMillis();
                if (event.getEventTime() - event.getDownTime() < 100
                        && now - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = now;
                    NoteBean note = new NoteBean(mSaveContent);
                    MyApplication.getNoteDao().save(note);
                    mToast.show();
                    mTimer.cancel();
                    hideFloatView();
                    ShareUtil.savePosition(CopySaveService.this, x, y);
                }
                break;
            default:
        }
        return false;
    }
}
