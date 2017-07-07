package com.example.hp.movie;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ImageAdapter extends BaseAdapter{


        private Context mContext;
        // references to our images

String []arrayPoster;
        public ImageAdapter(Context c,String []array){
            super();
            mContext = c;
            arrayPoster =array;
        }

        public int getCount() {
            //  return mThumbIds.length;
            return arrayPoster.length;
        }

        public Object getItem(int position) {
            return arrayPoster[position];
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                convertView = imageView;
                // imageView.setPadding(5, 5, 5, 5);
                //  imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(imageView.getContext())
                    .load(arrayPoster[position])//load images here
                    .resize(300, 300)
                    .into(imageView);
            //imageView.setImageResource(mThumbIds[position]);
            return convertView;
        }

    public void clearItems() {
       arrayPoster = null;
    }


    }
