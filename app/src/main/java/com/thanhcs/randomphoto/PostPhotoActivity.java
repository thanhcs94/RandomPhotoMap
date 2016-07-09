package com.thanhcs.randomphoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.thanhcs.randomphoto.api.BitmapProcessor;
import com.thanhcs.randomphoto.service.GPSTracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostPhotoActivity extends AppCompatActivity {
    private Uri mImageUri;
    File photo = null;
    String URL_TAG ="";
    TouchImageView img;
    ProgressDialog  progressDialog;
    private static final int IMAGE_CAPTURE 	= 2;
    public static final String IMAGE_DIRECTORY_NAME = "RanPhoto";
    public static boolean isPost = false;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        img = (TouchImageView) findViewById(R.id.imageView);
        setContentView(R.layout.activity_post_photo);
        choseFromCamera();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new PostImg().execute();
            }
        });
    }

    private void choseFromCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        try
        {
            // place where to store camera taken picture
            photo = createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
            Log.v("TAG", "Can't create file to take picture!");
            Toast.makeText(this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show();
        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.wtf("TAG", "Oops! Failed create "
                //        + AppConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    /**
     * Receive the result from the startActivity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_CAPTURE:
//                    Log.wtf("PATH", photo.getAbsolutePath() + "");
//                    URL_TAG = photo.getAbsolutePath() + "";
//                    Bitmap image = BitmapFactory.decodeFile(photo.getAbsolutePath());
//                    BitmapProcessor bm = new BitmapProcessor(image, 300 , 300);
//                    img.setImageBitmap(bm.getBitmap());
                    new PostImg().execute();
                    break;
                default:
                    break;
            }
        }
    }


    public class PostImg extends AsyncTask<Void, Void , Void> {
        Response response = null;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PostPhotoActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog.setTitle("sending your photo to universe...");
            progressDialog.setMessage("wait a moment..");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "";
            MultipartBody.Builder buildernew = new MultipartBody.Builder();
            buildernew.setType(MultipartBody.FORM);
            buildernew.addFormDataPart("title", "title");
            buildernew.addFormDataPart("path", "des");
            buildernew.addFormDataPart("lon", MainActivity.LON+"");
            buildernew.addFormDataPart("lat", MainActivity.LAT+"");
            buildernew.addFormDataPart("devide", "lon");
            String name[] =  photo.getAbsolutePath().split("/");
            final MediaType MEDIA_TYPE = MediaType.parse(URL_TAG.endsWith("png") ? "image/png" : "image/jpeg");
                buildernew.addFormDataPart("userfile", name[name.length-1],
                        RequestBody.create(MEDIA_TYPE, new File(photo.getAbsolutePath())))
                                .build();
            RequestBody requestBody = buildernew.build();
            Request request = new Request.Builder()
                    .url("http://the360lifechange.com/ranpic/insert.php")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response!=null&&!response.isSuccessful()) try {
                throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(PostPhotoActivity.this ,  response.message(), Toast.LENGTH_SHORT).show();
            isPost = true;
            progressDialog.dismiss();
            finish();
            super.onPostExecute(aVoid);
        }
    }
}
