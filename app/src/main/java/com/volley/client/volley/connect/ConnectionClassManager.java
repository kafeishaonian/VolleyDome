package com.volley.client.volley.connect;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于计算用户网络宽带
 * <p>
 * Created by Administrator on 2017/4/10 0010.
 */

public class ConnectionClassManager {

    static final double DEFAULT_SAMPLES_TO_QUALITY_CHANGE = 5;
    public static final int BYTES_TO_BITS = 8;
    /**
     * 网络宽带的值
     */
    static final int DEFAULT_POOR_BANDWIDTH = 150;
    static final int DEFAULT_MODERATE_BANDWIDTH = 550;
    static final int DEFAULT_GOOD_BANDWIDTH = 2000;
    static final long DEFAULT_HYSTERESIS_PERCENT = 20;
    private static final double HYSTERESIS_TOP_MULTIPLIER = 100.0 / (100.0 - DEFAULT_HYSTERESIS_PERCENT);
    private static final double HYSTERESIS_BOTTOM_MULTIPLIER = (100.0 - DEFAULT_HYSTERESIS_PERCENT) / 100.0;

    private static final double DEFAULT_DECAY_CONSTANT = 0.05;

    private ExponentialGeometricAverage mDownloadBandwidth
            = new ExponentialGeometricAverage(DEFAULT_DECAY_CONSTANT);
    private volatile boolean mInitiateStateChange = false;
    private AtomicReference<ConnectionQuality> mCurrentBandwidthConnectionQuality =
            new AtomicReference<ConnectionQuality>(ConnectionQuality.UNKNOWN);
    private AtomicReference<ConnectionQuality> mNextBandwidthConnectionQuality;
    private ArrayList<ConnectionClassStateChangeListener> mListenerList =
            new ArrayList<ConnectionClassStateChangeListener>();
    private int mSampleCounter;

    static final long BANDWIDTH_LOWER_BOUND = 10;

    private static class ConnectionClassManagerHolder {
        public static final ConnectionClassManager instance = new ConnectionClassManager();
    }

    @NonNull
    public static ConnectionClassManager getInstance() {
        return ConnectionClassManagerHolder.instance;
    }

    private ConnectionClassManager() {
    }

    public synchronized void addBandwidth(long bytes, long timeInMs) {
        if (timeInMs == 0 || (bytes) * 1.0 / (timeInMs) * BYTES_TO_BITS < BANDWIDTH_LOWER_BOUND) {
            return;
        }

        double bandwidth = (bytes) * 1.0 / (timeInMs) * BYTES_TO_BITS;
        mDownloadBandwidth.addMeasurement(bandwidth);

        if (mInitiateStateChange) {
            mSampleCounter += 1;
            if (getCurrentBandwidthQuality() != mNextBandwidthConnectionQuality.get()) {
                mInitiateStateChange = false;
                mSampleCounter = 1;
            }
            if (mSampleCounter >= DEFAULT_SAMPLES_TO_QUALITY_CHANGE && significantlyOutsideCurrentBand()) {
                mInitiateStateChange = false;
                mSampleCounter = 1;
                mCurrentBandwidthConnectionQuality.set(mNextBandwidthConnectionQuality.get());
                notifyListeners();
            }
            return;
        }

        if (mCurrentBandwidthConnectionQuality.get() != getCurrentBandwidthQuality()) {
            mInitiateStateChange = true;
            mNextBandwidthConnectionQuality =
                    new AtomicReference<ConnectionQuality>(getCurrentBandwidthQuality());
        }
    }

    private boolean significantlyOutsideCurrentBand() {
        if (mDownloadBandwidth == null) {
            // Make Infer happy. It wouldn't make any sense to call this while mDownloadBandwidth is null.
            return false;
        }
        ConnectionQuality currentQuality = mCurrentBandwidthConnectionQuality.get();
        double bottomOfBand;
        double topOfBand;
        switch (currentQuality) {
            case POOR:
                bottomOfBand = 0;
                topOfBand = DEFAULT_POOR_BANDWIDTH;
                break;
            case MODERATE:
                bottomOfBand = DEFAULT_POOR_BANDWIDTH;
                topOfBand = DEFAULT_MODERATE_BANDWIDTH;
                break;
            case GOOD:
                bottomOfBand = DEFAULT_MODERATE_BANDWIDTH;
                topOfBand = DEFAULT_GOOD_BANDWIDTH;
                break;
            case EXCELLENT:
                bottomOfBand = DEFAULT_GOOD_BANDWIDTH;
                topOfBand = Float.MAX_VALUE;
                break;
            default: // If current quality is UNKNOWN, then changing is always valid.
                return true;
        }
        double average = mDownloadBandwidth.getAverage();
        if (average > topOfBand) {
            if (average > topOfBand * HYSTERESIS_TOP_MULTIPLIER) {
                return true;
            }
        } else if (average < bottomOfBand * HYSTERESIS_BOTTOM_MULTIPLIER) {
            return true;
        }
        return false;
    }


    public synchronized ConnectionQuality getCurrentBandwidthQuality() {
        if (mDownloadBandwidth == null) {
            return ConnectionQuality.UNKNOWN;
        }
        return mapBandwidthQuality(mDownloadBandwidth.getAverage());
    }

    private ConnectionQuality mapBandwidthQuality(double average) {
        if (average < 0) {
            return ConnectionQuality.UNKNOWN;
        }
        if (average < DEFAULT_POOR_BANDWIDTH) {
            return ConnectionQuality.POOR;
        }
        if (average < DEFAULT_MODERATE_BANDWIDTH) {
            return ConnectionQuality.MODERATE;
        }
        if (average < DEFAULT_GOOD_BANDWIDTH) {
            return ConnectionQuality.GOOD;
        }
        return ConnectionQuality.EXCELLENT;
    }

    private void notifyListeners() {
       int size = mListenerList.size();
        for (int i = 0; i < size; i++) {
            mListenerList.get(i).onBandwidthStateChange(mCurrentBandwidthConnectionQuality.get());
        }
    }

    /**
     * 更改状态
     */
    public interface ConnectionClassStateChangeListener {
        void onBandwidthStateChange(ConnectionQuality bandwidthState);
    }


}
