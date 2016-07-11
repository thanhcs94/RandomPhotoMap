package com.thanhcs.randomphoto.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.thanhcs.randomphoto.R;
import com.thanhcs.randomphoto.ViewFullPhotoActivity;
import com.thanhcs.randomphoto.entities.Photo;

import java.util.List;

/**
 * Created by thanhcs94 on 4/11/2016.
 */
public class RecycleRandom extends RecyclerView.Adapter<RecycleRandom.BookViewHolder> {
    public List<Photo> book;
    public Activity mContext ;
    public RecycleRandom(List<Photo> book, Activity mmContext){
        this.book = book;
        this.mContext = mmContext ;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
        BookViewHolder pvh = new BookViewHolder(v, mContext, book);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BookViewHolder bookViewHolder, int i) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int size = 0;
        size = width/3-5;
            Picasso.with(mContext)
                    .load(book.get(i).getPath())
                    .resize(size, size)
                    .centerCrop()
                    .into(bookViewHolder.Thumbnail);
            Log.wtf("LINK :", book.get(i).getPath());
    }
    @Override
    public int getItemCount() {
        return book.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public com.makeramen.roundedimageview.RoundedImageView Thumbnail;
        public Activity mContext;
        public List<Photo> book;
        public String id , name;
        public BookViewHolder(View itemView , Activity mContext, List<Photo> book) {
            super(itemView);
            this.mContext = mContext;
            this.book = book;
            Thumbnail = (com.makeramen.roundedimageview.RoundedImageView)itemView.findViewById(R.id.imgAvatar);
            Thumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent  i = new Intent(mContext , ViewFullPhotoActivity.class);
            i.putExtra("type", "link");
            i.putExtra("link", book.get(getLayoutPosition()).getPath());
            mContext.startActivity(i);
            mContext.overridePendingTransition(0,0);
            }
        }
}