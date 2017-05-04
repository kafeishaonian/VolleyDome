package com.volley.client.volley.connect;

/**
 *
 * Created by Administrator on 2017/4/10 0010.
 */

public class ExponentialGeometricAverage {

    private final double mDecayConstant;
    private final int mCutover;

    private double mValue = -1;
    private int mCount;

    public ExponentialGeometricAverage(double decayConstant){
        mDecayConstant = decayConstant;
        mCutover = decayConstant == 0.0 ? Integer.MAX_VALUE : (int) Math.ceil(1 / decayConstant);
    }


    public void addMeasurement(double measurement){
        double keepConstant = 1 - mDecayConstant;
        if (mCount > mCutover){
            mValue = Math.exp(keepConstant * Math.log(mValue) + mDecayConstant * Math.log(measurement));
        } else if (mCount > 0){
            double retained = keepConstant * mCount / (mCount + 1.0);
            double nowcomer = 1.0 - retained;
            mValue = Math.exp(retained * Math.log(mValue) + nowcomer * Math.log(measurement));
        } else {
            mValue = measurement;
        }
        mCount++;
    }

    public double getAverage(){
        return mValue;
    }
}
