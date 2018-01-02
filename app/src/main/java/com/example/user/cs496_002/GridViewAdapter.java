package com.example.user.cs496_002;

/**
 * Created by user on 2017-12-31.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class GridViewAdapter extends ArrayAdapter {
    private int layoutResourceId;
    private Context context;
    private ArrayList<Origin> data = new ArrayList<Origin>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data){
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        imageView = (ImageView) convertView.findViewById(R.id.img);

        ViewHolder holder = new ViewHolder(imageView);
        holder.position = position;

        new ThumbnailTask(position, holder, context.getContentResolver(), data).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        return convertView;
    }

    private static class ThumbnailTask extends AsyncTask {

        private int mPosition;
        private ViewHolder mHolder;
        private ContentResolver contentResolver;
        private ArrayList<Origin> data;

        public ThumbnailTask(int position, ViewHolder holder, ContentResolver contentResolver, ArrayList<Origin> data) {
            this.mPosition = position;
            this.mHolder = holder;
            this.contentResolver = contentResolver;
            this.data = data;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if(data.get(mPosition).from == 1){
//                String[] temp = data.get(mPosition).content.split(" ");
//                byte[] decodeString = new byte[temp.length];
//                for (int i =0; i<decodeString.length; i++){
//                    decodeString[i] = temp[i];
//                }
                Log.i("OOM","IN");
                byte[] decodeString = Base64.decode(data.get(mPosition).content,Base64.DEFAULT);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap decodeImg = BitmapFactory.decodeByteArray(decodeString,0,decodeString.length);
                Log.i("OOM",Integer.toString(mPosition));
                return Bitmap.createScaledBitmap(decodeImg, 100, 100, true);

            }else{
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(data.get(mPosition).content));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("postDB", Integer.toString(mPosition));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//            Log.i("postDB",data.get(mPosition).toString());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[] b = baos.toByteArray();
                String encodeImg = Base64.encodeToString(b,Base64.DEFAULT);
                JSONArray jsonList = new JSONArray();
                try {
                    JSONObject temp = new JSONObject();
                    temp.accumulate("img", encodeImg);
                    Log.i("postDB","before post");
                    jsonList.put(temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                NetworkTask post2db = new NetworkTask("api/images","post", null, jsonList);
                post2db.execute();

                try {
                    String result = post2db.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.i("postDB","after post");
                return resized;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHolder.thumbnail.setImageResource(R.mipmap.loading);
        }

        @Override
        protected void onPostExecute(Object o) {
            mHolder.thumbnail.setImageBitmap((Bitmap) o);
        }
    }

    private static class ViewHolder {
        public ImageView thumbnail;
        public int position;

        public ViewHolder(ImageView imageView) {
            this.thumbnail = imageView;
        }
    }

}