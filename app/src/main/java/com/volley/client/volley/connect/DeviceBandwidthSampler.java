package com.volley.client.volley.connect;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class DeviceBandwidthSampler {

    private final ConnectionClassManager mConnectionClassManager;

    private AtomicInteger mSamplingCounter;

    private SamplingHandler mHandler;
    private HandlerThread mThread;

    private long mLastTimeReading;
    private static long sPreviousBytes = -1;

    private static class DeviceBandwidthSamplerHolder {
        public static final DeviceBandwidthSampler instance =
                new DeviceBandwidthSampler(ConnectionClassManager.getInstance());
    }

    @NonNull
    public static DeviceBandwidthSampler getInstance() {
        return DeviceBandwidthSamplerHolder.instance;
    }

    private DeviceBandwidthSampler(
            ConnectionClassManager connectionClassManager) {
        mConnectionClassManager = connectionClassManager;
        mSamplingCounter = new AtomicInteger();
        mThread = new HandlerThread("ParseThread");
        mThread.start();
        mHandler = new SamplingHandler(mThread.getLooper());
    }


    public void startSampling() {
        if (mSamplingCounter.getAndIncrement() == 0) {
            mHandler.startSamplingThread();
            mLastTimeReading = SystemClock.elapsedRealtime();
        }
    }


    public void stopSampling() {
        if (mSamplingCounter.decrementAndGet() == 0) {
            mHandler.stopSamplingThread();
            addFinalSample();
        }
    }

    protected void addSample() {
        long newBytes = TrafficStats.getTotalRxBytes();
        long byteDiff = newBytes - sPreviousBytes;
        if (sPreviousBytes >= 0) {
            synchronized (this) {
                long curTimeReading = SystemClock.elapsedRealtime();
                mConnectionClassManager.addBandwidth(byteDiff, curTimeReading - mLastTimeReading);

                mLastTimeReading = curTimeReading;
            }
        }
        sPreviousBytes = newBytes;
    }

    protected void addFinalSample() {
        addSample();
        sPreviousBytes = -1;
    }

    private class SamplingHandler extends Handler {
        static final long SAMPLE_TIME = 1000;

        static private final int MSG_START = 1;

        public SamplingHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    addSample();
                    sendEmptyMessageDelayed(MSG_START, SAMPLE_TIME);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown what=" + msg.what);
            }
        }


        public void startSamplingThread() {
            sendEmptyMessage(SamplingHandler.MSG_START);
        }

        public void stopSamplingThread() {
            removeMessages(SamplingHandler.MSG_START);
        }
    }

}
