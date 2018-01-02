package com.example.user.cs496_002;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("position");
        final MyApplication myApp = (MyApplication) getApplication();
        final ImageView imageView = (ImageView) findViewById(R.id.popup_imgview);

        Bitmap bitmap = null;
        if (myApp.imageList.get(position).from == 1){
            byte[] decodeString = Base64.decode(myApp.imageList.get(position).content,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodeString,0,decodeString.length);
        }else{
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(myApp.imageList.get(position).content));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bitmap);

        ImageButton rotateButton = (ImageButton) findViewById(R.id.button_imagerotation);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setRotation(imageView.getRotation() - 90);
            }
        });

    }
}
