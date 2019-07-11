package com.bytedance.clockapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.clockapplication.R;
import com.bytedance.clockapplication.widget.Clock;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClockFragment extends Fragment {

    private View mRootView;
    private Clock mClockView;
    private Handler mHandler = new ClockHandler(this);
    private ScheduledExecutorService service;

    private String timeZone = "GMT+8";

    static class ClockHandler extends Handler {

        public static final int MSG_CLK = 1;
        // weak reference to prevent memory leak
        private WeakReference<ClockFragment> mFragment;

        public ClockHandler(ClockFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ClockFragment fragment = mFragment.get();
            if (fragment != null) {
                if (msg.what == MSG_CLK) {
                    fragment.mClockView.invalidate(); // Refresh the clock
                    Log.d("Handler", "Refresh Clock");
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        mRootView = view.findViewById(R.id.root);
        mClockView = view.findViewById(R.id.clock);
        mClockView.setTimeZone(timeZone);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClockView.setShowAnalog(!mClockView.isShowAnalog());
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start a thread to refresh time every second
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(ClockHandler.MSG_CLK);
            }
        }, 1, 1, TimeUnit.SECONDS);
        Log.d("Handler", "Start Refresh Clock");
    }

    @Override
    public void onStop() {
        super.onStop();
        service.shutdown(); // Shutdown the thread when the fragment is stopped
        service = null;
        Log.d("Handler", "Stop Refresh Clock");
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

}
