package com.qualcomm.vuforia.samples.singletons;

public class DataHolder {
    private float[] data;
    public float[] getData() {return data;}
    public void setData(float[] data) {this.data = data;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}


    private float[] data2;
    public float[] getData2() {return data2;}
    public void setData2(float[] data2) {this.data2 = data2;}

    private boolean activateCamera;
    public boolean getActivateCamera() {return activateCamera;}
    public void setActivateCamera(boolean activateCamera) {this.activateCamera = activateCamera;}

    private boolean isTracking = false;
    public boolean getIsTracking() {return isTracking;}
    public void setIsTracking(boolean isTracking) {this.isTracking = isTracking;}

}