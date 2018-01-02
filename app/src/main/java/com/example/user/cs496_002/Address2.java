package com.example.user.cs496_002;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Address2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address2);
        Intent intent = getIntent();

        final int position = intent.getIntExtra("position", 1);
        final String name = intent.getStringExtra("name");
        final String number = intent.getStringExtra("number");
        final String email = intent.getStringExtra("email");
        final String link = intent.getStringExtra("link");

        TextView nameTextView = findViewById(R.id.textView1);
        TextView numberTextView = findViewById(R.id.textView2);
        TextView emailTextView = findViewById(R.id.textView3);

        Button deletebtn = (Button) findViewById(R.id.button2);
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApp = (MyApplication) getApplication();
                String id = myApp.id;
                JSONArray usr = new JSONArray();
                JSONObject user = null;
                try {
                    user = new JSONObject("{id:"+id+"}");
                    user.accumulate("name", name);
                    user.accumulate("number", number);
                    user.accumulate("email", email);
                    user.accumulate("link", link);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                usr.put(user);
                myApp.adapter.deleteItem(name, number, email, link);
                Log.i("test","dafsadfasdf");
                myApp.adapter.notifyDataSetChanged();
                Log.i("adfasdfsafd","afdasdfasdfsafd");
                NetworkTask deletecontact = new NetworkTask("api/deletecontact","delete", null, usr);
                deletecontact.execute();
                Intent intent = new Intent(Address2.this, TabActivity.class);
                startActivity(intent);
            }
        });

        nameTextView.setText(name);
        numberTextView.setText(number);
        emailTextView.setText(email);
    }

}