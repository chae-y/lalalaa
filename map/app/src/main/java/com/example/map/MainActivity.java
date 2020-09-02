package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    String [] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0);
        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                return;
            }
        }
        init();
    }

    public void init(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fragmentManager.findFragmentById(R.id.map);

        MapReadyCallback callback1 = new MapReadyCallback();
        mapFragment.getMapAsync(callback1);

    }

    //구글 지도 사용준비가 완료되면 동작하는 콜백
    class MapReadyCallback implements OnMapReadyCallback{
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //Log.d("test123","구글지도 사용준비 완료");
            getMyLocation();
        }
    }

    //현재 위치를 측정하는 매서드
    public void getMyLocation(){
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //권한 확인 작업
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                return;
            }
        }
        //이전에 측정했던 값을 가져온다.
        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location1 !=null) {
            setMyLocation(location1);
        }else{
            if(location2 != null){
                setMyLocation(location2);
            }
        }
        //새롭게 측정한다.
        GetMyLocationListener listener = new GetMyLocationListener();

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10f,listener);
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)==true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10f,listener);
        }

    }

    public void setMyLocation(Location location){
        Log.d("test123","위도 : "+location.getLatitude());
        Log.d("test123","경도 : "+location.getLongitude());

        //위도와 경도값을 관리하는 객체
        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
    }
    //현재 위치 측정이 성공하면 반응하는 리스너
    class GetMyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(@NonNull Location location) {
            setMyLocation(location);
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}