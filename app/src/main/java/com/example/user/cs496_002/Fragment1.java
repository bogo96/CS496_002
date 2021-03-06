package com.example.user.cs496_002;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment1 extends Fragment {
    String str;
    ArrayList<Contact> ContactList;
    JSONArray jsonarray;
    AccessToken token;
    String id="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment1, container, false);

        ListView listview = view.findViewById(R.id.listview);
        //final ListViewAdapter adapter = new ListViewAdapter();

        jsonarray = new JSONArray();

        MyApplication myApp = (MyApplication) getActivity().getApplication();
        ContactList = myApp.getContactList();
        token  = myApp.token;

        listview.setAdapter(myApp.adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getActivity(), Address2.class);
                MyApplication myApp = (MyApplication) getActivity().getApplication();
                ListViewItem item = (ListViewItem) myApp.adapter.getItem(position);
                intent.putExtra("position", position);
                intent.putExtra("name", item.getName());
                intent.putExtra("number", item.getNumber());
                intent.putExtra("email", item.getEmail());
                intent.putExtra("link", item.getLink());
                startActivity(intent);
            }
        });

        /*for(int i=0; i<ContactList.size(); i++){
            Contact c = ContactList.get(i);
            adapter.addItem(c.name, c.number, c.email, c.link);
        }*/


        return view;
    }
}