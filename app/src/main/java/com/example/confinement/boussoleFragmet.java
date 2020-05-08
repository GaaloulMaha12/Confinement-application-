package com.example.confinement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class boussoleFragmet extends Fragment implements SensorEventListener {

    ImageView img_compass;
    TextView txt_azimuth;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    float [] rMat = new float[9];
    float [] orientation = new float[9];
    private float [] mLastAccelerometer = new float[3];
    private float [] mLastMagnetometer = new float[3];
    private boolean haveSensor=false, haveSensor2=false;
    private boolean mLastAccelerometerSet= false;
    private boolean mLastMagnetometerSet= false;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview =inflater.inflate(R.layout.fragment_boussole, container, false);
        mSensorManager= (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        img_compass = (ImageView) rootview.findViewById(R.id.img_compass);
        txt_azimuth = (TextView) rootview.findViewById(R.id.txt_azimuth);
        start();
        return  rootview;


    }

    private void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)==null){
            if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)==null || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)==null) {
                noSensorAlert();
            }
            else {
                mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor= mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
                haveSensor2= mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_UI);

            }
        }
        else {
            mRotationV= mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor2= mSensorManager.registerListener(this,mRotationV,SensorManager.SENSOR_DELAY_UI);

        }
    }
    public void noSensorAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Your device doesn't support the compass ").setCancelable(false).setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
    }

    public void stop(){
        if (haveSensor && haveSensor2){
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager.unregisterListener(this,mMagnetometer);
        }
        else
            if (haveSensor ){
                mSensorManager.unregisterListener(this,mRotationV);
            }

    }

    @Override
    public void onPause (){
        super.onPause();
        stop();
}
@Override
public void onResume(){
        super.onResume();
        start();

}



    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat,event.values);
            mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360)%360);
        }
        if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values,0,mLastAccelerometer,0,event.values.length);
            mLastAccelerometerSet=true;
        }
        else
            if (event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD){
                System.arraycopy(event.values,0,mLastMagnetometer,0,event.values.length);
                mLastMagnetometerSet=true;
            }
            if (mLastMagnetometerSet && mLastAccelerometerSet){
                SensorManager.getRotationMatrix(rMat,null,mLastAccelerometer,mLastMagnetometer);
                SensorManager.getOrientation(rMat,orientation);
                mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360)%360);

            }
            mAzimuth= Math.round(mAzimuth);
            img_compass.setRotation(-mAzimuth);
            String where ="NO";
            if (mAzimuth >= 350 || mAzimuth <= 10)
                where ="N";
        if (mAzimuth < 350 || mAzimuth > 280)
            where ="NW";
        if (mAzimuth <= 280 || mAzimuth > 260)
            where ="W";
        if (mAzimuth <= 260 || mAzimuth > 190)
            where ="SW";
        if (mAzimuth <= 190 || mAzimuth > 170)
            where ="S";
        if (mAzimuth <= 170 || mAzimuth > 100)
            where ="SE";
        if (mAzimuth <= 100 || mAzimuth > 80)
            where ="E";
        if (mAzimuth <= 80 || mAzimuth > 10)
            where ="NE";

        txt_azimuth.setText(mAzimuth+"°"+where);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
