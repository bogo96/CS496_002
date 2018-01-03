package com.example.user.cs496_002;

/**
 * Created by user on 2017-12-30.
 */
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyApplication extends Application {
    public ArrayList<Contact> ContactList;
    public ArrayList<Origin> imageList;
    public ArrayList<Marker> markerList;
    public ListViewAdapter adapter;
    public AccessToken token;
    public String id, mapstring;
    public boolean fetchfinish;
    FBFriendsThread fbfriends;

    @Override
    public void onCreate(){
        super.onCreate();
        ContactList = new ArrayList<Contact>();
        imageList = new ArrayList<Origin>();
        markerList = new ArrayList<Marker>();
        adapter = new ListViewAdapter();
        fbfriends = new FBFriendsThread();

        token = AccessToken.getCurrentAccessToken();
        id="";
        fetchfinish = false;
    }

    public void loadData()
    {
        if(fetchfinish) return;
        myid();

        JSONArray usr = new JSONArray();
        try {
            JSONObject user = new JSONObject("{id:"+id+"}");
            usr.put(user);
            NetworkTask postuser = new NetworkTask("api/newuser","post", null, usr);
            postuser.execute();

            JSONObject result = new JSONObject(postuser.get());
            Log.i("result", result.toString());
            if(result.has("error")) return;
            int success = result.getInt("result");
            if(success==1){
                fetchAllContacts();

                JSONArray jsonarray = new JSONArray();
                for (int i = 0; i < ContactList.size(); i++) {
                    Contact c = ContactList.get(i);
                    //adapter.addItem(c.name, c.number, c.email, c.link);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("id", id);
                        jsonObject.accumulate("name", c.name);
                        jsonObject.accumulate("number", c.number);
                        jsonObject.accumulate("email", c.email);
                        jsonObject.accumulate("link", c.link);
                        jsonarray.put(jsonObject);
                    } catch (Exception e) {

                    }
                }
                // AsyncTask를 통해 HttpURLConnection 수행.
                NetworkTask addnewcontacts = new NetworkTask("api/addnewcontacts","post", null, jsonarray);
                addnewcontacts.execute();

                String s = addnewcontacts.get();
            }
            else{
                NetworkTask getcontacts = new NetworkTask("api/getallcontact/"+id, "get", null, null);
                getcontacts.execute();

                String s=  getcontacts.get();
                Log.i("asdf",s);
                JSONArray contactlist = new JSONArray(s);

                for(int i=0; i<contactlist.length(); i++){
                    JSONObject json = (JSONObject)contactlist.get(i);
                    String name = json.getString("name");
                    String number = json.getString("number");
                    String email = json.getString("email");
                    String link = json.getString("link");

                    Contact c = new Contact(name, number, email, link);
                    ContactList.add(c);
                    adapter.addItem(c.name, c.number, c.email, c.link);
                    adapter.notifyDataSetChanged();
                }

                NetworkTask getDBimg = new NetworkTask("api/maps/"+id, "get",null, null);
                getDBimg.execute();
                mapstring = getDBimg.get();
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fetchAllContacts(){
        fetchAllContactsFromInnerContact();
        fetchAllContactsFromFacebookFriends();
    }

    private void fetchAllContactsFromInnerContact(){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //ArrayList<Contact> contacts = new ArrayList<Contact>();

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" COLLATE LOCALIZED ASC";
        Cursor cursor = getApplicationContext().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortOrder
        );

        Log.i("CONTACT", "start");
        while(cursor.moveToNext()){
            String email = "";
            String number = cursor.getString(1).replaceAll("-","");
            String name = cursor.getString(2);
            if (number.length() == 10) {
                number = number.substring(0, 3) + "-"
                        + number.substring(3, 6) + "-"
                        + number.substring(6);
            } else if (number.length() > 8) {
                number = number.substring(0, 3) + "-"
                        + number.substring(3, 7) + "-"
                        + number.substring(7);
            }
            Cursor emailCursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Email.DATA},
                    "DISPLAY_NAME"+"='"+name+"'",
                    null, null);
            if(emailCursor.moveToFirst()){
                email = emailCursor.getString(0);
            }
            Contact c = new Contact(
                    name,
                    number,
                    "",
                    ""
            );

//            Log.i("CONTACT", c.name);
//            Log.i("CONTACT", c.number);
//            Log.i("CONTACT", c.email);

            ContactList.add(c);
            adapter.addItem(c.name, c.number, c.email, c.link);
            adapter.notifyDataSetChanged();
        }
    }

    public void fetchAllContactsFromFacebookFriends(){
        //FBFriendsThread fbfriends = new FBFriendsThread();
        fbfriends.start();
        try {
            fbfriends.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("fbfriends", "finish");
    }

    public class NextFriends extends AsyncTask<Void, Void, String> {

        private String url;

        public NextFriends(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            URL url = null;//url을 가져온다.
            HttpURLConnection con = null;

            try {
                url = new URL(this.url);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                StringBuffer buffer = null;
                InputStream stream = null;
                stream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String s = buffer.toString();
                JSONObject response = null;
                try {
                    response = new JSONObject(s);
                    JSONArray data = (JSONArray)response.get("data");
                    JSONObject paging = (JSONObject)response.get("paging");

                    Log.i("data", data.toString());
                    addList(data);
                    if(paging.has("next")){
                        String next = paging.getString("next");
                        return next;
                    }
                    else{
                        fetchfinish = true;
                        Log.i("fectchfinish", "finish");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("finish", "finish");
            return "";
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        }
    }

    public void addList(JSONArray data){
        for(int i=0; i<data.length(); i++){
            try {
                JSONObject ith = (JSONObject) data.get(i);
                JSONObject picture = (JSONObject) ith.get("picture");
                JSONObject picturedata = (JSONObject) picture.get("data");
                Contact c = new Contact(ith.getString("name"),"", "", picturedata.getString("url"));
                ContactList.add(c);
                adapter.addItem(c.name, c.number, c.email, c.link);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class myIdThread extends Thread{
        private static final String TAG = "LoadingThread";
        boolean finish;

        public myIdThread(){
            finish = false;
        }
        public void run(){
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                id = object.getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            //parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAndWait();
            Log.i("finish", "finish");
        }
    }

    public class FBFriendsThread extends Thread{
        private static final String TAG = "FBFriendsThread";
        String next = "";

        public FBFriendsThread(){
            next = "";
        }

        public void run(){
            if(id.compareTo("")!=0) {
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + id + "/taggable_friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONArray data = (JSONArray) response.getJSONObject().get("data");
                                    JSONObject paging = (JSONObject) response.getJSONObject().get("paging");

                                    addList(data);

                                    if (paging.has("next")) {
                                        next = paging.getString("next");

                                    } else {
                                        fetchfinish = true;
                                        Log.i("fetchfinish", "true");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAndWait();

                while(next.compareTo("")!=0){
                    NextFriends n = new NextFriends(next);
                    n.execute();
                    try {
                        next = n.get();
                        Log.i("??????","???????");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                Log.i("???","?????");
            }
        }
    }

    public void myid(){
        myIdThread idthread = new myIdThread();
        idthread.start();
        try {
            idthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("idididididid",this.id);
    }

    public ArrayList<Contact> getContactList(){return ContactList;}
}