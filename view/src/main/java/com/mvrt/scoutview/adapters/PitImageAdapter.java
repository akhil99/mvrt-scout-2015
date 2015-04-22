package com.mvrt.scoutview.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvrt.scoutview.R;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PitImageAdapter extends RecyclerView.Adapter<PitImageAdapter.ViewHolder> {

    ArrayList<Image> images;

    public PitImageAdapter(){
        images = new ArrayList<>();
    }

    public void addImage(Image i){
        images.add(i);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pitimg,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PitImageAdapter.ViewHolder holder, int position) {
        holder.textView.setText(images.get(position).caption);
        String url = images.get(position).url;
        DownloadImageTask task = new DownloadImageTask(holder.imageView);
        task.execute(url);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.caption);
            imageView = (ImageView) itemView.findViewById(R.id.pit_image);
        }
    }

    public static class Image{
        public String url;
        public String caption;

        public Image(String url, String cap){
            this.url = url;
            caption = cap;
        }
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bmImage.setImageBitmap(result);
        }
    }
}