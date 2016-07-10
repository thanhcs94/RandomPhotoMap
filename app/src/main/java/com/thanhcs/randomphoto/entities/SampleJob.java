package com.thanhcs.randomphoto.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by thanhcs94 on 7/10/2016.
 */
public class SampleJob implements ClusterItem {

    private double latitude;
    private double longitude;

//Create constructor, getter and setter here

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }
}