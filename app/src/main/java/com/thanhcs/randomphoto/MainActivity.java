package com.thanhcs.randomphoto;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.thanhcs.randomphoto.adapter.RecycleRandom;
import com.thanhcs.randomphoto.api.API;
import com.thanhcs.randomphoto.api.Parse;
import com.thanhcs.randomphoto.entities.GetPhoto;
import com.thanhcs.randomphoto.entities.Photo;
import com.thanhcs.randomphoto.service.GPSTracker;
import com.thanhcs.randomphoto.utils.Check_Connection;

import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION =-1;
    List<Photo> listPhoto;
    List<Photo> listPhotoTemp;
    RecycleRandom mAdapter;
    RecyclerView rv;
    public static Double LON,LAT;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public String link = "";
    int firstVisibleItemsGrid[] ;
    int LIMIT = 20;
    boolean _areLecturesLoaded = false;
    int PAGE =1;
    ProgressBar progressBar2;
    OnResultsScrollListener onResultsScrollListener;
    StaggeredGridLayoutManager llm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);
        setTitle("1000+ PHOTOS");
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        rv =(RecyclerView)findViewById(R.id.rv);
        llm = new StaggeredGridLayoutManager(2, 1);
        rv.setLayoutManager(llm);
        progressBar2 = (ProgressBar)findViewById(R.id.progressBar3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("STORE", "onRefresh called from SwipeRefreshLayout");
                initiateRefresh();
            }
        });
        initiateRefresh();
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostPhotoActivity.class));
    }
});

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant

            return;
        }
        GPSTracker mGPS = new GPSTracker(this);
        LAT = mGPS.getLatitude();
        LON = mGPS.getLongitude();
        if(mGPS.canGetLocation()){
        mGPS.getLocation();
        //setTitle(("Lat" + mGPS.getLatitude() + "Lon" + mGPS.getLongitude()));
        Log.wtf("GPS", ("Lat : " + mGPS.getLatitude() + "Lon : " + mGPS.getLongitude()));
        }else{
        setTitle("Unabletofind");
        System.out.println("Unable");
        }
        }
    private void initiateRefresh() {
    PAGE =1;
    try {
        listPhoto.clear();
        mAdapter.notifyDataSetChanged();
    }catch(Exception e){}
    try {
        listPhotoTemp.clear();
        mAdapter.notifyDataSetChanged();
    }catch(Exception e){}
        new LoadPhoto(PAGE).execute();
    }

    @Override
    protected void onResume() {
        if(PostPhotoActivity.isPost) {
            PAGE = 1;
            initiateRefresh();
            PostPhotoActivity.isPost = false;
        }else{

        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onRefreshComplete() {
        Log.i("STORE", "onRefreshComplete");
        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission was granted, yay!", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {
                    Toast.makeText(MainActivity.this, "Permission denied, boo!", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
    class LoadPhoto extends AsyncTask<Void , Void, Void> {
        int PAGE = 1;
        public LoadPhoto(int page){
            PAGE = page;
        }

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
//            Collections.reverse(listPhoto);
            if(listPhoto!=null) {
                if (listPhoto.size() > 1) {
                    if (PAGE == 1) {
                        Collections.reverse(listPhoto);
                        listPhotoTemp = listPhoto;
                        mAdapter = new RecycleRandom(listPhoto, MainActivity.this);
                        rv.setAdapter(mAdapter);
                        onResultsScrollListener = new OnResultsScrollListener(llm);
                        rv.addOnScrollListener(onResultsScrollListener);
                    } else {
                        if (listPhoto != null) {
                            listPhotoTemp.addAll(listPhoto);
                            mAdapter.notifyDataSetChanged();
//                            if (mGoogleNowLoad != null)
//                                mGoogleNowLoad.setVisibility(View.GONE);
                        }
                    }

                    super.onPostExecute(aVoid);
                    listPhoto = null;
                    onRefreshComplete();
                } else {
                    //Log.wtf("DATA :", "NULL");
                }
            }else{
                //data null
                Check_Connection c = new Check_Connection(MainActivity.this);
                if(!c.isConnectingToInternet()){
                    Toast.makeText(MainActivity.this, "Network error ! please check", Toast.LENGTH_LONG).show();
                }else{
                }
            }
            progressBar2.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }

    private class OnResultsScrollListener extends RecyclerView.OnScrollListener {

        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 4; // The minimum amount of items to have below your current scroll position before loading more.
        int visibleItemCount, totalItemCount;

        private StaggeredGridLayoutManager mLayoutManager;

        public OnResultsScrollListener(StaggeredGridLayoutManager mLayoutManager) {
            this.mLayoutManager = mLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            try {
                firstVisibleItemsGrid = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItemsGrid);
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - firstVisibleItemsGrid[0]) <= (firstVisibleItemsGrid[0] + visibleThreshold)) {
                    loadMoreImages();
                    //Log.wtf("load data", "load more on end");
                    loading = true;
                }
            }catch(Exception e){
                Log.wtf("BUG", "firstVisibleItemsGrid");
            }

        }

        public void reset(int previousTotal, boolean loading) {
            this.previousTotal = previousTotal;
            this.loading = loading;
        }
        private boolean isEnd(){
            return !rv.canScrollVertically(1) && firstVisibleItemsGrid[0] != 0;
        }
        private void loadMoreImages(){
            PAGE++;
            new LoadPhoto(PAGE).execute();
            Log.d("myTag", "LAST-------HERE------PAGE ");
        }
    }
}
