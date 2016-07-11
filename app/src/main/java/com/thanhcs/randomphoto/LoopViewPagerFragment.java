package com.thanhcs.randomphoto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.thanhcs.randomphoto.ViewPaper.CircleIndicator;
import com.thanhcs.randomphoto.ViewPaper.LoopViewPager;

import java.util.ArrayList;

/**
 * Created by thanhcs94 on 6/7/2016.
 */
public class LoopViewPagerFragment extends Fragment {
    ArrayList<String>arrString;
    public static LoopViewPagerFragment newInstance(ArrayList<String> arrString) {
        LoopViewPagerFragment fragment = new LoopViewPagerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("data", arrString);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_viewpager, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LoopViewPager viewpager = (LoopViewPager) view.findViewById(R.id.viewpager);
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        ArrayList<String> arrayList  = (ArrayList<String>) getArguments().get("data");
        Log.wtf("PHOTO", arrayList.size()+"");
        viewpager.setAdapter(new PostPhotoViewpaperAdapter(getActivity(), arrayList));
        indicator.setViewPager(viewpager);
    }



    class PostPhotoViewpaperAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<String>arrayList;
        public PostPhotoViewpaperAdapter(Context context, ArrayList<String> arrayList) {
            mContext = context;
            this.arrayList = arrayList;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override public Object instantiateItem(ViewGroup view, int position) {
            Log.wtf("POST_IMG3", arrayList.get(position) + "");
            ImageView imgview;
            imgview = new ImageView(view.getContext());
            imgview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imgview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Picasso.with(view.getContext()).load(arrayList.get(position)).into(imgview);
            view.addView(imgview, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return imgview;
//        }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
}