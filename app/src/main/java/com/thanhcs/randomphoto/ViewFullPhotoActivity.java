package com.thanhcs.randomphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by thanhcs94 on 6/16/2016.
 */
public class ViewFullPhotoActivity extends AppCompatActivity {
    TouchImageView imgView;
   // ProgressBar progressBar;
    public ArrayList<String>arrString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullphoto_activity);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setTitle("");
        try {
            getActionBar().hide();
        }catch (Exception e){};
        imgView = (TouchImageView)findViewById(R.id.imgView);
        //progressBar = (ProgressBar)findViewById(R.id.progressBar4);
        Intent i = getIntent();
        if(i.getStringExtra("type").equalsIgnoreCase("link")) {
            String url = i.getStringExtra("link");
            if (!url.contains("http")) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(url, options);
                imgView.setImageBitmap(bitmap);
            } else {
                Picasso.with(ViewFullPhotoActivity.this).load(url).into(imgView);
            }
        }else{
            //array
            arrString = getIntent().getStringArrayListExtra("arrlink");
            imgView.setVisibility(View.GONE);

            FragmentManager supportFragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            LoopViewPagerFragment fragment = new LoopViewPagerFragment().newInstance(arrString);
            fragmentTransaction.add(R.id.framroot, fragment, "");
            fragmentTransaction.commit();
            getSupportFragmentManager().addOnBackStackChangedListener(
                    new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {
                            int count = getSupportFragmentManager().getBackStackEntryCount();
                            ActionBar actionbar = getSupportActionBar();
                            if (actionbar != null) {
                                actionbar.setDisplayHomeAsUpEnabled(count > 0);
                                actionbar.setDisplayShowHomeEnabled(count > 0);
                            }
                        }
                    });
        }


        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });
        findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(arrString!=null)
        arrString.clear();
        super.onDestroy();
    }
}
//http://stackoverflow.com/questions/1362723/how-can-i-get-a-dialog-style-activity-window-to-fill-the-screen
//http://stackoverflow.com/questions/6070505/android-how-to-create-a-transparent-dialog-themed-activity