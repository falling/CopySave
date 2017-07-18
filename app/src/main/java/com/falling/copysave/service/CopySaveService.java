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
import com.falling.copysave.sharedutil.ShareUtil;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by falling on 2017/7/15.
 */

public class CopySaveService extends Service {


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

    @Override
    public void onCreate() {
        super.onCreate();
        int position[] = ShareUtil.getPosition(CopySaveService.this);
        mToast = Toast.makeText(CopySaveService.this,"saved",Toast.LENGTH_SHORT);
        x = position[0];
        y = position[1];
        mManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if (mManager.hasPrimaryClip() && mManager.getPrimaryClip().getItemCount() > 0) {

                    CharSequence saveContent = mManager.getPrimaryClip().getItemAt(0).getText();

                    if (saveContent != null) {
                        mSaveContent =saveContent.toString();
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

        //设置监听浮动窗口的触摸移动 等待优化
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                //减25为状态栏的高度
                y = (int) (event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25);
                //刷新
                wmParams.x = x;
                wmParams.y = y;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteBean note = new NoteBean(mSaveContent);
                MyApplication.getNoteDao().save(note);
                mToast.show();
            }
        });
    }

    private void showFloatView() {
        try {
            mTimer.cancel();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    hideFloatView();
                    ShareUtil.savePosition(CopySaveService.this,x,y);
                }
            }, 3000);
            mWindowManager.addView(mFloatLayout, wmParams);
        } catch (Exception ignored) {
            Log.d(TAG,ignored.getMessage());
        }
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

}
