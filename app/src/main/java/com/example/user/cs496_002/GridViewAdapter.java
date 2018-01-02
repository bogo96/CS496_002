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
    private String id;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data, String id){
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
        this.id = id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        imageView = (ImageView) convertView.findViewById(R.id.img);

        ViewHolder holder = new ViewHolder(imageView);
        holder.position = position;
        Log.i("???", "???");
        new ThumbnailTask(position, holder, context.getContentResolver(), data,id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        return convertView;
    }

    private static class ThumbnailTask extends AsyncTask {

        private int mPosition;
        private ViewHolder mHolder;
        private ContentResolver contentResolver;
        private ArrayList<Origin> data;
        private String id;

        public ThumbnailTask(int position, ViewHolder holder, ContentResolver contentResolver, ArrayList<Origin> data,String id) {
            this.mPosition = position;
            this.mHolder = holder;
            this.contentResolver = contentResolver;
            this.data = data;
            this.id = id;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if(data.get(mPosition).from == 1){
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                byte[] b = baos.toByteArray();
//                String encodeImg = Base64.encodeToString(b,Base64.DEFAULT);
//                JSONArray jsonList = new JSONArray();
//                try {
//                    JSONObject temp = new JSONObject();
//                    temp.accumulate("img", encodeImg);
//                    temp.accumulate("id",id);
//                    Log.i("postDB",Integer.toString(encodeImg.length()));
//                    Log.i("position",Integer.toString(mPosition));
//                    jsonList.put(temp);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                NetworkTask postDBimg = new NetworkTask("api/images","post", null, jsonList);
//                postDBimg.execute();
//
//                String result = "";
//                JSONObject imgid_json = null;
//                int imgid = -1;
//                try {
//                    result = postDBimg.get();
//                    imgid_json = new JSONObject(result);
//                    imgid = imgid_json.getInt("img");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                data.get(mPosition).imgid = imgid;
//                Log.i("postDB","after post");
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