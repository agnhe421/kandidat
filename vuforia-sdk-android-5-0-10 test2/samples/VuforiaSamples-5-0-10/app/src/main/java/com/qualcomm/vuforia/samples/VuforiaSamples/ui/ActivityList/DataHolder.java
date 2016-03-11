package com.qualcomm.vuforia.samples.VuforiaSamples.ui.ActivityList;

public class DataHolder {
    private float[] data;
    public float[] getData() {return data;}
    public void setData(float[] data) {this.data = data;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}