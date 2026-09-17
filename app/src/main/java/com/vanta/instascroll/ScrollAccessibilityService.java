package com.vanta.instascroll;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class ScrollAccessibilityService extends AccessibilityService {

    private Handler handler;
    private Runnable scrollTask;
    private volatile boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
        int code = event.getKeyCode();

        if (code == KeyEvent.KEYCODE_VOLUME_UP) {
            startScroll();
            return true;
        }
        if (code == KeyEvent.KEYCODE_VOLUME_DOWN) {
            stopScroll();
            return true;
        }
        return false;
    }

    private void startScroll() {
        if (running) return;
        running = true;
        scrollTask = new Runnable() {
            @Override
            public void run() {
                if (!running) return;
                performSwipe();
                int delay = Prefs.getDelay(ScrollAccessibilityService.this);
                handler.postDelayed(this, delay);
            }
        };
        handler.post(scrollTask);
    }

    private void stopScroll() {
        running = false;
        if (scrollTask != null) handler.removeCallbacks(scrollTask);
    }

    private void performSwipe() {
        int direction = Prefs.getDirection(this);
        int lengthPct = Prefs.getLengthPct(this);
        int duration = Math.max(50, Prefs.getDelay(this) / 2);

        int screenH = getResources().getDisplayMetrics().heightPixels;
        int screenW = getResources().getDisplayMetrics().widthPixels;

        float x = screenW / 2f;
        float center = screenH / 2f;
        float halfLen = (screenH * lengthPct / 100f) / 2f;

        float startY, endY;
        if (direction == 0) {
            startY = center - halfLen;
            endY = center + halfLen;
        } else {
            startY = center + halfLen;
            endY = center - halfLen;
        }

        Path path = new Path();
        path.moveTo(x, startY);
        path.lineTo(x, endY);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription gesture =
                new GestureDescription.Builder().addStroke(stroke).build();

        dispatchGesture(gesture, null, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {
        stopScroll();
    }

    @Override
    public void onDestroy() {
        stopScroll();
        super.onDestroy();
    }
}
