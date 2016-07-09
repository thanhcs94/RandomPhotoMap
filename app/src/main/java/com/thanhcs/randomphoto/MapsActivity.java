package com.thanhcs.randomphoto;

import android.*;
import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thanhcs.randomphoto.adapter.RecycleRandom;
import com.thanhcs.randomphoto.api.API;
import com.thanhcs.randomphoto.api.Parse;
import com.thanhcs.randomphoto.entities.GetPhoto;
import com.thanhcs.randomphoto.entities.Photo;
import com.thanhcs.randomphoto.entities.PhotoMapItems;
import com.thanhcs.randomphoto.service.GPSTracker;
import com.thanhcs.randomphoto.service.PlaceProvider;
import com.thanhcs.randomphoto.utils.Check_Connection;

import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        , LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;
    List<Photo> listPhoto;
    List<Photo> listPhotoTemp;
    int LIMIT = 100;
    int PAGE = 1;
    ProgressBar progressBar2;
    ClusterManager<PhotoMapItems> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        mapFragment.getMapAsync(this);
        if (getIntent() != null)
            handleIntent(getIntent());
    }

    private void updateLocation() {
        GPSTracker mGPS = new GPSTracker(this);
        double LAT = mGPS.getLatitude();
        double LON = mGPS.getLongitude();
//        LatLng mLocation = new LatLng(LAT, LON);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 10));
        if (mGPS.canGetLocation()) {
            mGPS.getLocation();
            //setTitle(("Lat" + mGPS.getLatitude() + "Lon" + mGPS.getLongitude()));
            Log.wtf("GPS", ("Lat : " + mGPS.getLatitude() + "Lon : " + mGPS.getLongitude()));
        } else {
            setTitle("Unabletofind");
            System.out.println("Unable");
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
        updateLocation();
        new LoadPhoto().execute();

    }

    private void handleIntent(Intent intent){
        try {
            if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
                doSearch(intent.getStringExtra(SearchManager.QUERY));
            } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
            }
        }catch(Exception e){
            Log.wtf("SEARCH", e.toString() +"--");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void doSearch(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(0, data, this);
    }

    private void getPlace(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(1, data, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_search:
                onSearchRequested();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.action_search:
//                onSearchRequested();
//                break;
//        }
//        return super.onMenuItemSelected(featureId, item);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;
        if(arg0==0)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
        else if(arg0==1)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
        return cLoader;
    }

    private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        mMap.clear();
        while(c.moveToNext()){
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
            markerOptions.position(position);
            markerOptions.title(c.getString(0));
            mMap.addMarker(markerOptions);
        }
        if(position!=null){
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            mMap.animateCamera(cameraPosition);
        }
    }

    private void setUpClusterer() {
        // Declare a variable for the cluster manager.
//        // Position the map.
//        m.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
//
//        // Initialize the manager with the context and the map.
//        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<PhotoMapItems>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 10.771982;
        double lng = 106.657936;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            PhotoMapItems offsetItem = new PhotoMapItems(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent i = new Intent(MapsActivity.this , ViewFullPhotoActivity.class);
        i.putExtra("link", marker.getSnippet());
        startActivity(i);
        overridePendingTransition(0,0);
        return false;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    class LoadPhoto extends AsyncTask<Void , Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar2.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            GetPhoto dataPost = null;
            try {
                URL URL = new URL(API.GET_PHOTO+"?limit="+LIMIT+"&page="+PAGE);
                Log.wtf("LINK :", API.GET_PHOTO+"?limit="+LIMIT+"&page="+PAGE);
                Reader reader = Parse.getData(URL);
                if (reader != null) {
                    dataPost = new GsonBuilder().create().fromJson(reader, GetPhoto.class);
                    listPhoto = dataPost.getRingtone();
                    Log.wtf("data", listPhoto.size() + "");
                } else
                    listPhoto = null;

            } catch (Exception e) {
                System.err.println("Error data");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for(int i = 0 ; i < listPhoto.size() ; i++){
                LatLng latLng = new LatLng(Double.parseDouble(listPhoto.get(i).getLat()), Double.parseDouble(listPhoto.get(i).getLon()));
                showMyLocationPic(latLng , listPhoto.get(i).getPath());
            }
            progressBar2.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }

        private void showMyLocationPic(final LatLng latLng, final String url) {
            int size = 80;
            Picasso.with(MapsActivity.this).load(url).resize(size, size).placeholder(R.drawable.holder).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.fromBitmap(bitmap);
                    //create marker option
                    if (bitmap != null)
                       mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapMarker).snippet(url));
                    else
                       mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.holder)).snippet(url));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                   mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.holder)).snippet(url));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }
}
