package com.thanhcs.randomphoto;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.GsonBuilder;
import com.thanhcs.randomphoto.adapter.RecycleRandom;
import com.thanhcs.randomphoto.api.API;
import com.thanhcs.randomphoto.api.Parse;
import com.thanhcs.randomphoto.entities.GetPhoto;
import com.thanhcs.randomphoto.entities.Photo;

import java.io.Reader;
import java.net.URL;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity{
    List<Photo> listPhoto;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecycleRandom mAdapter;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        rv =(RecyclerView)findViewById(R.id.rv);
        new LoadPhoto().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoadPhoto extends AsyncTask<Void , Void, Void> {

        public LoadPhoto(){

        }
        @Override
        protected Void doInBackground(Void... params) {
            GetPhoto dataPost = null;
            try {
                URL URL = new URL(API.GET_PHOTO);
                Log.wtf("LINK :", API.GET_PHOTO);
                Reader reader = Parse.getData(URL);
                if (reader != null) {
                    dataPost = new GsonBuilder().create().fromJson(reader, GetPhoto.class);
                    listPhoto = dataPost.getRingtone();
                } else
                    listPhoto = null;

            } catch (Exception e) {
                System.err.println("Error data");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(3, 1);
            rv.setLayoutManager(llm);
            mAdapter = new RecycleRandom(listPhoto, ScrollingActivity.this);
            rv.setAdapter(mAdapter);
            super.onPostExecute(aVoid);
        }
    }
}
