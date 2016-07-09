package com.thanhcs.randomphoto.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by thanhcs94 on 7/8/2016.
 */
public class PhotoMapItems implements ClusterItem {
    private final LatLng mPosition;

    public PhotoMapItems(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}