package com.example.user.cs496_002;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
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
import java.util.ArrayList;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

public class Fragment_temp extends Fragment{

    //    final MyApplication myApp = (MyApplication) getActivity().getApplication();
    private MapView mapView = null;
    private GoogleMap googleMap;
    private Marker currentMarker;
    //    String locationProvider;
//    LocationManager locationManager;
//    private LatLng DEFAULT_LOCATION;
    private boolean mLocationPermissionGranted;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment3, container, false);



        mapView = (MapView) rootView.findViewById(R.id.map);
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
                googleMap = mMap;

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 8080);
                }
                mMap.setMyLocationEnabled(true);

//                locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
//                locationProvider = locationManager.getBestProvider(new Criteria(), true);
//                Location location = locationManager.getLastKnownLocation(locationProvider);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(37.56, 126.97);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });

//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
//            public void onMapClick(LatLng latLng){
//                MyApplication myApp = (MyApplication) getActivity().getApplication();
//                float color = ChooseMarkerType();
//                Marker marker = googleMap.addMarker(new MarkerOptions());
//                marker.setPosition(latLng);
//                marker.setTitle(writeTitle());
//                marker.setTag(String.valueOf(color));
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
//                myApp.markerList.add(marker);
//                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동
//                ShowAllMarkers(myApp.markerList); //마커 생성
//
//                //post2DB
//            }
//        });
//
//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                MyApplication myApp = (MyApplication) getActivity().getApplication();
//                int select = DeleteMarker(marker);
//                //deletemarker
//                if(select>0){
//                    myApp.markerList.remove(select);
//                    ShowAllMarkers(myApp.markerList); //마커 생성
//                }
//                return false;
//            }
//        });


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
    public String writeTitle(){
        AlertDialog.Builder popup = new AlertDialog.Builder(getActivity());
        final String[] title = new String[1];
        popup.setTitle("장소에 대해서");
        popup.setMessage("메모해주세요");
        final EditText ask = new EditText(getActivity());
        popup.setView(ask);
        popup.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    title[0] =  ask.getText().toString();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        popup.show();
        return title[0];
    }

    public float ChooseMarkerType(){
        final String items[] = { "갔던곳 ", "가고 싶은 곳"};
        final float[] color = {HUE_RED};
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("선택하세요");
        ab.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                switch (whichButton){
                    case 1:
                        color[0] = HUE_GREEN;
                    case 2:
                        color[0] = HUE_YELLOW;
                }

            }
        }).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
                    }
                });
        ab.show();
        return color[0];
    }

    public int DeleteMarker(final Marker delete){
        final MyApplication myApp = (MyApplication) getActivity().getApplication();
        final int[] select = {-1};
        AlertDialog.Builder del_btn = new AlertDialog.Builder(getActivity());
        del_btn.setMessage("마커를 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        for (int i=0; i< myApp.markerList.size(); i++){
                            if ( myApp.markerList.get(i).getTitle().equals(delete.getTitle()) && myApp.markerList.get(i).getPosition() == delete.getPosition() ){
                                select[0]=i;
                                break;
                            }
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
            }
        });
        del_btn.show();
        return select[0];
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
