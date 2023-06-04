package com.neo.accountapp_3.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.neo.accountapp_3.Account.AccountEdit;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.GlobalClass;

import java.util.ArrayList;
import java.util.Vector;

public class AccountTotalMap extends AppCompatActivity implements OnMapReadyCallback, Overlay.OnClickListener{
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private Geocoder geocoder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    ArrayList<ArrayList<ArrayList<String>>> locationlist = new ArrayList<ArrayList<ArrayList<String>>>();

    // 마커 정보 저장시킬 변수들 선언
    private Vector<LatLng> markersPosition;
    private Vector<Marker> activeMarkers;

    AccountBook oAccountBook;
    ArrayList<ArrayList<ArrayList<String>>> AccountTotallist;

    String couplekey = ""; //커플 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String myid = ""; //나의 id - 로그아웃 하기 전까지 전체 고정키이다.
    String otherid = ""; //상대 id - 로그아웃 하기 전까지 전체 고정키이다.
    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_map);

        context = this;

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());

        oAccountBook = new AccountBook(this);
        AccountTotallist = oAccountBook.AccountRead();

        //맵을 띄울 공간 불러옴
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment= (MapFragment) fragmentManager.findFragmentById(R.id.mapview);


        Log.d("맵 보기 mapFragment", String.valueOf(mapFragment));
        mapFragment = MapFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.mapview, mapFragment).commit();
        //getMapAsync를 호출하여 비동기로 onMapReady콜백 메서드 호출
        //onMapReady에서 NaverMap객체를 받음
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("log","onStart");
        //지도를 재시작한다.
        //onMapReady(naverMap);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        geocoder = new Geocoder(this);
        //네이버 맵에 locationSource를 셋하면 위치 추적 기능을 사용 할 수 있다
        naverMap.setLocationSource(locationSource);
        //위치 추적 모드 지정 가능 내 위치로 이동
        //naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //현재위치 버튼 사용가능 여부 설정
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        naverMap.setMapType(NaverMap.MapType.Basic);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false);
        naverMap.setIndoorEnabled(true);
        //averMap.setLocationTrackingMode(LocationTrackingMode.None);



        //Log.d("place_x x!!!", place_x);
        //Log.d("place_y x!!!", place_y);
        //LatLng initialPosition = new LatLng(Double.parseDouble(place_y), Double.parseDouble(place_x));

        //초기 위치를 정한다.
        //LatLng initialPosition = new LatLng(37.506855, 127.066242);
        //CameraUpdate cameraUpdate = CameraUpdate.scrollTo(initialPosition);
        //naverMap.moveCamera(cameraUpdate);

        //카메라 변경 이벤트
        /*
        naverMap.addOnCameraChangeListener((reason, animated) -> {
            Log.i("NaverMap", "카메라 변경 - reson: " + reason + ", animated: " + animated);
        });

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(37.5666102, 126.9783881))
                .animate(CameraAnimation.Easing, 2000)
                .reason(1000);

        naverMap.moveCamera(cameraUpdate);*/

        //카메라 이동 콜백
        /*
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(37.5666102, 126.9783881))
                .animate(CameraAnimation.Easing, 2000)
                .finishCallback(() -> {
                    Toast.makeText(getApplication(), "카메라 이동 완료", Toast.LENGTH_SHORT).show();
                })
                .cancelCallback(() -> {
                    Toast.makeText(getApplication(), "카메라 이동 취소", Toast.LENGTH_SHORT).show();
                });

        naverMap.moveCamera(cameraUpdate);
*/



        //CameraPosition cameraPosition =
        //        new CameraPosition(new LatLng(37.5666102, 126.9783881), 16);

        /*
        NaverMapOptions options = new NaverMapOptions()
                .camera(new CameraPosition(new LatLng(35.1798159, 129.0750222), 1))
                .mapType(NaverMap.MapType.Terrain);

        MapFragment mapFragment = MapFragment.newInstance(options);*/

        //초기값 정해주는 부분!!!
        //naverMap.moveCamera();
        //CameraPosition cameraPosition = naverMap.getCameraPosition();
        //new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

        //지도에 표시할 마커들을 생성한다.
        MakePlaceMarker();
    }



    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("log","onRestart");
        //onMapReady(naverMap);
    }

    public void MakePlaceMarker(){
        freeActiveMarkers(); //마커 삭제

        //가계부 전체 리스트 중 커플에 맞는 것만 가져옴
        for (int i = 0; i < AccountTotallist.size(); i++){
            ArrayList<String> locationone = new ArrayList<>();
            if(AccountTotallist.get(i).get(1).get(0).equals(couplekey)) {
                if(!AccountTotallist.get(i).get(1).get(8).equals("")){
                    locationlist.add(AccountTotallist.get(i));
                }
            }
        }
        Log.d("locationlist!!!", String.valueOf(locationlist));

        // 지도상에 마커 표시 - 클릭시 정보 보이게 할 것.
        for (int i = 0; i < locationlist.size(); i++){

            Marker marker = new Marker();
            marker.setPosition(new LatLng(Double.parseDouble(locationlist.get(i).get(1).get(10)), Double.parseDouble(locationlist.get(i).get(1).get(9))));

            marker.setMap(naverMap);
            activeMarkers.add(marker);

            //marker.setCaptionText(placename);
            //marker.setCaptionColor(Color.BLUE);
            //marker.setCaptionHaloColor(Color.rgb(200, 255, 200));

            marker.setCaptionText(locationlist.get(i).get(1).get(4));
            marker.setCaptionTextSize(15);
            marker.setHideCollidedSymbols(true);
            marker.setOnClickListener(this);

            InfoWindow infoWindow = new InfoWindow();

            marker.setTag(locationlist.get(i).get(1).get(8)); //태그 선언 :  장소이름
            int finalI = i;

            //마커를 클릭하여 응답하게 한다.
            marker.setOnClickListener(overlay -> {
                Marker marker_ = (Marker)overlay;
                if (marker_.getInfoWindow() == null) {
                    // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                    infoWindow.open(marker_);



                } else {
                    // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                    infoWindow.close();


                    //클릭시 수정탭으로 이동한다.
                    //가계부 정보로 이동할 것.
                    Log.d("클릭!","클릭");
                    Log.d("", String.valueOf(finalI));
                    Log.d("클릭! 키",locationlist.get(finalI).get(0).get(0));

                    Intent intent = new Intent(this, AccountEdit.class);
                    intent.putExtra("accountkey", locationlist.get(finalI).get(0).get(0));
                    intent.putExtra("accesstype", "2"); // 지도
                    startActivity(intent);
                }
                return true;
            });

            //위에 알림창을 열어 놓을 때 사용하는 메서드
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    // 정보 창이 열린 마커의 tag를 텍스트로 노출하도록 반환
                    return (CharSequence)infoWindow.getMarker().getTag();
                }
            });
            infoWindow.open(marker);

        }

        Log.d("activeMarkers", String.valueOf(activeMarkers));
    }



    //전체 마커를 클릭함으로 동일한 반응을 하게 됨
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        //Log.d("","클릭1");

        return false;
    }

    // 지도상에 표시되고있는 마커들 지도에서 삭제
    private void freeActiveMarkers() {
        if (activeMarkers == null) {
            activeMarkers = new Vector<Marker>();
            return;
        }
        for (Marker activeMarker: activeMarkers) {
            activeMarker.setMap(null);
        }
        activeMarkers = new Vector<Marker>();
    }

    // 현재 카메라가 보고있는 위치
    public LatLng getCurrentPosition(NaverMap naverMap) {
        CameraPosition cameraPosition = naverMap.getCameraPosition();
        return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }

    // 선택한 마커의 위치가 가시거리(카메라가 보고있는 위치 반경 3km 내)에 있는지 확인
    public final static double REFERANCE_LAT = 1 / 109.958489129649955;
    public final static double REFERANCE_LNG = 1 / 88.74;
    public final static double REFERANCE_LAT_X3 = 3 / 109.958489129649955;
    public final static double REFERANCE_LNG_X3 = 3 / 88.74;

    public boolean withinSightMarker(LatLng currentPosition, LatLng markerPosition) {
        boolean withinSightMarkerLat = Math.abs(currentPosition.latitude - markerPosition.latitude) <= REFERANCE_LAT_X3;
        boolean withinSightMarkerLng = Math.abs(currentPosition.longitude - markerPosition.longitude) <= REFERANCE_LNG_X3;
        return withinSightMarkerLat && withinSightMarkerLng;
    }
}