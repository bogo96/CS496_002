package com.example.user.cs496_002;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;




public class Fragment2 extends Fragment {
    int PERMISSION_CODE = 100;
    final int REQ_CODE_SELECT_IMAGE=100;
    private GridViewAdapter gridViewAdapter;
    private GridView grid;
    private JSONArray jsonList;
    private JSONObject temp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View resultView = inflater.inflate(R.layout.tab_fragment2, container, false);
        MyApplication myApp = (MyApplication) getActivity().getApplication();


//        NetworkTask getDBimg = new NetworkTask("api/images", "get",null, null);
//        getDBimg.execute();
//
//        try {
//            Log.i("OOM","hi");
//            String result = getDBimg.get();
//            jsonList = new JSONArray(result);
//            for (int i =0; i < jsonList.length(); i++){
//                temp = jsonList.getJSONObject(i);
//                Log.i("OOM","hello");
//                myApp.imageList.add(new Origin(1,temp.getString("img")));
//                Log.i("OOM","bye");
//             }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        /*NetworkTask getDBimg = new NetworkTask("api/images", "get",null, null);
        getDBimg.execute();

        try {
            Log.i("OOM","hi");
            String result = getDBimg.get();
            jsonList = new JSONArray(result);
            for (int i =0; i < jsonList.length(); i++){
                temp = jsonList.getJSONObject(i);
                Log.i("OOM","hello");
                myApp.imageList.add(new Origin(1,temp.getString("img")));
                Log.i("OOM","bye");
             }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/



        grid = (GridView) resultView.findViewById(R.id.gridView);
        Log.i("OOM",Integer.toString(myApp.imageList.size()));
        gridViewAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item, myApp.imageList);
        grid.setAdapter(gridViewAdapter);
        Log.i("grid","start get");
        getImageThread getImage = new getImageThread();
        getImage.start();
        Log.i("OOM", "finish Adapter");

        Button loadImageButton = (Button) resultView.findViewById(R.id.load);
        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, PERMISSION_CODE);
                }
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PopupActivity.class);
                intent.putExtra("position", i);
//                            intent.putExtra("OriginList", imageList);
                startActivity(intent);
            }

        });

        return resultView;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImagefromGallery();
            }
        }
    }

    public void getImagefromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final MyApplication myApp = (MyApplication) getActivity().getApplication();

        if(requestCode == REQ_CODE_SELECT_IMAGE) {
            if(resultCode== Activity.RESULT_OK) {

                ClipData clipData = data.getClipData();
                Uri uri;

                if (clipData != null && clipData.getItemCount() <= 10 ){

                    for(int i=0;i < clipData.getItemCount(); i++){
                        uri = clipData.getItemAt(i).getUri();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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

                        Log.i("uri",uri.toString());
                        myApp.imageList.add(new Origin(0,uri.toString()));
                    }
                    gridViewAdapter.notifyDataSetChanged();



                    grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                            AlertDialog.Builder del_btn = new AlertDialog.Builder(getActivity());
                            del_btn.setMessage("이미지를 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int j) {
                                            myApp.imageList.remove(i);
                                            gridViewAdapter.notifyDataSetChanged();

                                        };
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int j) {
                                }
                            });
                            del_btn.show();
                            return true;
                        }
                    });

                }else if (clipData == null){
                    Toast.makeText(getActivity(),"하나 이상의 사진을 선택하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(getActivity(),"10개 이하의 사진을 선택하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }

    public class getImageThread extends Thread{
        boolean next = true;
        MyApplication myApp = (MyApplication) getActivity().getApplication();

        public getImageThread(){
            next = true;
        }

        public void run(){
            while(next) {
                try {
                    NetworkTask getDBimg = new NetworkTask("api/images", "get",null, null);
                    getDBimg.execute();
                    String result = getDBimg.get();
                    temp = new JSONObject(result);

                    Log.i("OOM","bye");
                    if(temp.getString("img").compareTo("end") == 0){
                        next=false;
                        return;
                    }else{
                        myApp.imageList.add(new Origin(1,temp.getString("img")));
                    }
                    new Thread() {
                        public void run() {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }.start();

                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.i("grid","load image");
                gridViewAdapter.notifyDataSetChanged();  //필자가 원했던 UI 업데이트 작업
            }
        };


    }



}



