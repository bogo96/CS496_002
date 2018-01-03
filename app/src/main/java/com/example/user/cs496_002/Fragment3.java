package com.example.user.cs496_002;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

public class Fragment3 extends Fragment{

    private MapView mapView = null;

    private GoogleMap googleMap;
    JSONArray jsonList ;
    JSONArray dbArray;
    JSONObject dbMarker,temp;
    Geocoder geocoder;
    Address AddrAddress;
    List<Address> listAddress;



    private boolean mLocationPermissionGranted;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab_fragment3, container, false);

        final MyApplication myApp = (MyApplication)getActivity().getApplication();
        final ToggleButton want = rootView.findViewById(R.id.want);
        final ToggleButton went = rootView.findViewById(R.id.went);
        final Button search = rootView.findViewById(R.id.button3);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                Log.i("ready", "ready");
                googleMap = mMap;

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 8080);
                }
                mMap.setMyLocationEnabled(true);


                NetworkTask getDBimg = new NetworkTask("api/maps/"+myApp.id, "get",null, null);
                getDBimg.execute();
                String result = null;
                try {
                    result = getDBimg.get();
                    dbArray = new JSONArray(result);
                    for (int i=0; i< dbArray.length(); i++){
                        dbMarker = (JSONObject) dbArray.get(i);
                        MarkerOptions dboptioins = new MarkerOptions();
                        LatLng latLng = new LatLng(Double.parseDouble(dbMarker.getString("latitude")),Double.parseDouble(dbMarker.getString("longitude")));
                        dboptioins.position(latLng);
                        dboptioins.title(dbMarker.getString("title"));
                        Log.i("get",dbMarker.getString("title"));
                        Marker marker = googleMap.addMarker(dboptioins);
                        marker.setTag(dbMarker.getString("Tag"));
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(Float.parseFloat(dbMarker.getString("Tag"))));
                        myApp.markerList.add(marker);
                    }
                    ShowAllMarkers(myApp.markerList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

                    public void onMapClick(LatLng latLng){
                        MarkerOptions markeroptions = new MarkerOptions();
                        float color = ChooseMarkerType();
                        markeroptions.title(writeTitle());
                        MyApplication myApp = (MyApplication) getActivity().getApplication();
                        Log.i("make","before choose");
                        markeroptions.position(latLng);
                        Log.i("make","finish title");
                        Marker marker = googleMap.addMarker(markeroptions);
                        myApp.markerList.add(marker);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        MyApplication myApp = (MyApplication) getActivity().getApplication();
                        DeleteMarker(marker);
                        return false;
                    }
                });

                want.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                        Log.i("want","IN");

                        MyApplication myApp = (MyApplication)getActivity().getApplication();
                        ClearAllMarkers(myApp.markerList);
                        if(check){
                            Log.i("want","check");
                            for(int i=0; i< myApp.markerList.size(); i++){
                                if (myApp.markerList.get(i).getTag().equals("60.0")){
                                    Log.i("want","check - if");
                                    myApp.markerList.get(i).setVisible(true);
                                }
                            }
                        }
                        if(went.isChecked()){
                            Log.i("want","else");
                            for(int i=0; i< myApp.markerList.size(); i++){
                                if (myApp.markerList.get(i).getTag().equals("240.0")){
                                    Log.i("want","else - if");
                                    myApp.markerList.get(i).setVisible(true);
                                }
                            }
                        }
                    }
                });

                went.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                        Log.i("want","IN");

                        MyApplication myApp = (MyApplication)getActivity().getApplication();
                        ClearAllMarkers(myApp.markerList);
                        if(check){
                            Log.i("want","check");
                            for(int i=0; i< myApp.markerList.size(); i++){
                                if (myApp.markerList.get(i).getTag().equals("240.0")){
                                    Log.i("want","check - if");
                                    myApp.markerList.get(i).setVisible(true);
                                }
                            }
                        }
                        if(want.isChecked()){
                            Log.i("want","else");
                            for(int i=0; i< myApp.markerList.size(); i++){
                                if (myApp.markerList.get(i).getTag().equals("60.0")){
                                    Log.i("want","else - if");
                                    myApp.markerList.get(i).setVisible(true);
                                }
                            }
                        }
                    }
                });

                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchPlace();
                    }
                });

                LatLng sydney = new LatLng(37.56, 126.97);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



            }
        });


        return rootView;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 8080: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //call function

    }

    public void ShowAllMarkers(ArrayList<Marker> markers){

        for (int i=0; i<markers.size(); i++){
            markers.get(i).showInfoWindow();
//            googleMap.showinfowindo(markers.get(i));
        }
    }

    public void ClearAllMarkers(ArrayList<Marker> markers){

        for (int i=0; i<markers.size(); i++){
            markers.get(i).setVisible(false);
//            googleMap.showinfowindo(markers.get(i));
        }
    }
    public String writeTitle(){
        Log.i("make","IN title");
        final MyApplication myApp = (MyApplication)getActivity().getApplication();
        AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
        final String[] title = new String[1];
        popup.setTitle("장소에 대해서");
        popup.setMessage("메모해주세요");
        final EditText ask = new EditText(getActivity());
        popup.setView(ask);
        popup.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("make","IN title onclick");
                title[0] =  ask.getText().toString();
                myApp.markerList.get(myApp.markerList.size()-1).setTitle(title[0]);
//                if (title[0].length()==
//                    Toast.makeText(getActivity(),"꼭 써주세요",Toast.LENGTH_SHORT);
//                    title[0] = writeTitle();
//                }
            }
        });
        popup.show();
        Log.i("make","OUT title");
        return title[0];
    }

    public String searchPlace(){
        Log.i("make","IN title");
        final MyApplication myApp = (MyApplication)getActivity().getApplication();
        AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
        final String[] title = new String[1];
        popup.setTitle("찾을 장소를");
        popup.setMessage("입력해주세요");
        final EditText ask = new EditText(getActivity());
        popup.setView(ask);
        popup.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("make","IN title onclick");
                title[0] =  ask.getText().toString();
                geocoder = new Geocoder(getActivity());
                try{
                    listAddress = geocoder.getFromLocationName(title[0], 5);
                    if(listAddress.size()>0){
                        AddrAddress = listAddress.get(0);
                        LatLng latLng = new LatLng(AddrAddress.getLatitude() , AddrAddress.getLongitude() );
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                //myApp.markerList.get(myApp.markerList.size()-1).setTitle(title[0]);
//                if (title[0].length()==
//                    Toast.makeText(getActivity(),"꼭 써주세요",Toast.LENGTH_SHORT);
//                    title[0] = writeTitle();
//                }
            }
        });
        popup.show();
        Log.i("make","OUT title");
        return title[0];
    }

    public float ChooseMarkerType(){
        Log.i("make","IN choose");
        final float[] color = {HUE_RED};
        final MyApplication myApp = (MyApplication)getActivity().getApplication();
        AlertDialog.Builder del_btn = new AlertDialog.Builder(getActivity());
        del_btn.setMessage("선택하세요").setCancelable(false).setPositiveButton("갔던 곳",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        color[0] = HUE_BLUE;
                        myApp.markerList.get(myApp.markerList.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(color[0]));
                        myApp.markerList.get(myApp.markerList.size()-1).setTag(Float.toString(color[0]));
                        Post();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("가고 싶은 곳", new DialogInterface.OnClickListener() {
            @Override
                public void onClick(DialogInterface dialogInterface, int j) {
                    color[0] = HUE_YELLOW;
                    myApp.markerList.get(myApp.markerList.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(color[0]));
                    myApp.markerList.get(myApp.markerList.size()-1).setTag(Float.toString(color[0]));
                    ShowAllMarkers(myApp.markerList);
                    Post();
                    dialogInterface.dismiss();
                }
            });
        del_btn.show();
        Log.i("make","OUT choose");
        return color[0];
    }

    public void DeleteMarker(final Marker delete){
        final MyApplication myApp = (MyApplication) getActivity().getApplication();
        final int[] select = {-1};
        AlertDialog.Builder del_btn = new AlertDialog.Builder(getActivity());
        del_btn.setTitle("원하시는 기능을 선택해주세요");
        del_btn.setMessage(delete.getTitle()).setCancelable(false).setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        for (int i=0; i< myApp.markerList.size(); i++){
                            if ( myApp.markerList.get(i).getPosition().longitude == delete.getPosition().longitude &&
                                    myApp.markerList.get(i).getPosition().latitude == delete.getPosition().latitude){
                                Log.i("delete","hi");
                                select[0]=i;
                                break;
                            }
                        }
                        Delete(select[0]);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                    }
                }).setNeutralButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                        if(delete.getTag().equals("60.0")){
                            delete.setTag("240.0");
                            delete.setIcon(BitmapDescriptorFactory.defaultMarker(HUE_BLUE));
                        }else{
                            delete.setTag("60.0");
                            delete.setIcon(BitmapDescriptorFactory.defaultMarker(HUE_YELLOW));
                        }
                        Update(delete);
                        ClearAllMarkers(myApp.markerList);
                        ShowAllMarkers(myApp.markerList);
                    }
                });
                del_btn.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    public void Update(Marker marker){
        MyApplication myApp = (MyApplication)getActivity().getApplication();
        JSONArray json = new JSONArray();
        JSONObject info = new JSONObject();
        try {
            info = new JSONObject();
            info.accumulate("latitude", Double.toString(marker.getPosition().latitude));
            info.accumulate("longitude", Double.toString(marker.getPosition().longitude));
            info.accumulate("title", marker.getTitle());
            info.accumulate("Tag", marker.getTag());
            info.accumulate("id",myApp.id);
            json.put(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkTask postDBimg = new NetworkTask("api/maps","update", null, json);
        postDBimg.execute();
        String result = "";
        try {
            result = postDBimg.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void Delete(int i){
        MyApplication  myApp = (MyApplication)getActivity().getApplication();
        JSONArray user = new JSONArray();
        JSONObject info = new JSONObject();
        Marker marker = myApp.markerList.get(i);
        try {
            info.accumulate("id", myApp.id);
            info.accumulate("longitude", Double.toString(marker.getPosition().longitude));
            info.accumulate("latitude", Double.toString(marker.getPosition().latitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user.put(info);
        NetworkTask deleteDBimg = new NetworkTask("api/maps", "delete", null, user);
        deleteDBimg.execute();
        Log.i("delete",Integer.toString(myApp.markerList.size()));
        myApp.markerList.get(i).remove();
        myApp.markerList.remove(i);
        Log.i("delete",Integer.toString(myApp.markerList.size()));
        ShowAllMarkers(myApp.markerList);
    }

    public void Post(){
        MyApplication myApp = (MyApplication) getActivity().getApplication();
        Log.i("onResume","onResume");
        Log.i("make","start post");
        jsonList= new JSONArray();
        Marker marker = myApp.markerList.get(myApp.markerList.size()-1);
        try {
            temp = new JSONObject();
            temp.accumulate("latitude", Double.toString(marker.getPosition().latitude));
            temp.accumulate("longitude", Double.toString(marker.getPosition().longitude));
            temp.accumulate("title", marker.getTitle());
            temp.accumulate("Tag", marker.getTag());
            temp.accumulate("id",myApp.id);
            jsonList.put(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkTask postDBimg = new NetworkTask("api/maps","post", null, jsonList);
        postDBimg.execute();
        String result = "";
        try {
            result = postDBimg.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
