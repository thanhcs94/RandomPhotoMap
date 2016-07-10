package com.thanhcs.randomphoto.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by thanhcs94 on 7/8/2016.
 */
public class PhotoMapItems implements ClusterItem {
    private final LatLng mPosition;
    String url;
    public PhotoMapItems(double lat, double lng , String url) {
        mPosition = new LatLng(lat, lng);
        this.url = url;
    }

    public String getUrl(){
        return  url;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}