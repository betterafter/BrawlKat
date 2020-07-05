package com.example.brawlkat;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class kat_OverdrawActivity extends Service implements View.OnTouchListener {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private float oldXvalue, oldYvalue;
    private View view;
    private Button button;

    private ImageButton btn;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        btn = new ImageButton(this);
        btn.setImageResource(R.drawable.logo);
        btn.setBackgroundColor(Color.TRANSPARENT);



        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(btn, layoutParams);

        btn.setOnTouchListener(this);
    }





    @Override
    public void onDestroy() {
        if(windowManager != null) {        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
            if(btn != null) windowManager.removeView(btn);
        }
        super.onDestroy();
    }

    float mStartingX, mStartingY, mWidgetStartingX, mWidgetStartingY;

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartingX = ev.getRawX();
                mStartingY = ev.getRawY();

                mWidgetStartingX = layoutParams.x;
                mWidgetStartingY = layoutParams.y;
                return false;
            case MotionEvent.ACTION_MOVE:

                float deltaX = mStartingX - ev.getRawX();
                float deltaY = mStartingY - ev.getRawY();
                layoutParams.x = (int) (mWidgetStartingX - deltaX);
                layoutParams.y = (int) (mWidgetStartingY - deltaY);
                windowManager.updateViewLayout(btn, layoutParams);
                return true;
        }
        return false;
    }
}
