package com.thanhcs.randomphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thanhcs.randomphoto.MainActivity;
import com.thanhcs.randomphoto.R;
import com.thanhcs.randomphoto.entities.PhotoMapItems;

import java.util.Iterator;

/**
 * Created by thanhcs94 on 7/10/2016.
 */
public class JobRenderer extends DefaultClusterRenderer<PhotoMapItems> {

        private final IconGenerator iconGenerator;
        private final IconGenerator clusterIconGenerator;
        private final ImageView imageView;
        private final com.makeramen.roundedimageview.RoundedImageView clusterImageView;
        private final int markerWidth;
        private final int markerHeight;
        private final String TAG = "ClusterRenderer";
        private DisplayImageOptions options;
    Context context;

        public JobRenderer(Context context, GoogleMap map, ClusterManager<PhotoMapItems> clusterManager) {
            super(context, map, clusterManager);
            // initialize cluster icon generator
            this.context = context;
            clusterIconGenerator = new IconGenerator(context.getApplicationContext());
            View clusterView = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
            clusterIconGenerator.setContentView(clusterView);
            clusterImageView = (com.makeramen.roundedimageview.RoundedImageView) clusterView.findViewById(R.id.imgAvatar);

            // initialize cluster item icon generator
            iconGenerator = new IconGenerator(context.getApplicationContext());
            imageView = new ImageView(context.getApplicationContext());
            markerWidth = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
            markerHeight = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
            int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);
            imageView.setPadding(padding, padding, padding, padding);
            iconGenerator.setContentView(imageView);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.holder)
                    .showImageForEmptyUri(R.drawable.holder)
                    .showImageOnFail(R.drawable.holder)
                    .cacheInMemory(false)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

    @Override
    protected void onBeforeClusterRendered(Cluster cluster, final MarkerOptions markerOptions) {
        final Iterator<PhotoMapItems> iterator = cluster.getItems().iterator();
//        MainActivity.mImageLoader.displayImage(iterator.next().getUrl(), clusterImageView, options);
//        Bitmap icon = clusterIconGenerator.makeIcon(iterator.next().getUrl());
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        int size = 60;
        Picasso.with(context).load(iterator.next().getUrl()).resize(size, size).placeholder(R.drawable.holder).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.fromBitmap(bitmap);
                //create marker option
                if (bitmap != null)
                    clusterImageView.setImageBitmap(bitmap);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title(iterator.next().getUrl());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected void onBeforeClusterItemRendered(final PhotoMapItems item,final MarkerOptions markerOptions) {
        int size = 60;
        Picasso.with(context).load(item.getUrl()).resize(size, size).placeholder(R.drawable.holder).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.fromBitmap(bitmap);
                //create marker option
                if (bitmap != null)
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title(item.getUrl());
                    imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    /*    @Override
    protected void onBeforeClusterItemRendered(PhotoMapItems job, MarkerOptions markerOptions) {
        ImageLoader.getInstance().displayImage(job.getUrl(), imageView, options);
        Bitmap icon = iconGenerator.makeIcon(job.getUrl());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(job.getUrl());
        return onBeforeClusterItemRendered();

    }*/

//    @Override
//    protected void onBeforeClusterRendered(Cluster<PhotoMapItems> cluster, MarkerOptions markerOptions) {
//        Iterator<Job> iterator = cluster.getItems().iterator();
//        ImageLoader.getInstance().displayImage(iterator.next().getJobImageURL(), clusterImageView, options);
//        Bitmap icon = clusterIconGenerator.makeIcon(iterator.next().getName());
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//    }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
}
