package com.neo.accountapp_3.Map;

import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.neo.accountapp_3.R;

import java.util.Vector;

public class MapMarkFragment extends Fragment implements OnMapReadyCallback, Overlay.OnClickListener{
    int num;
    View view;
    String placename, place_x, place_y;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private Geocoder geocoder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    Context oContext;

    MapActivityPager mapactivitypager;

    public MapMarkFragment() {

    }

    // 메인 액티비티 위에 올린다.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mapactivitypager = (MapActivityPager) getActivity();

        Log.d("맵 보기","onAttach");
        // Toast.makeText(this.getContext(), "onAttach", Toast.LENGTH_LONG).show();
    }

    //프래그먼트를 띄우면서 데이터를 넘김
    public static MapMarkFragment newInstance(int number, String placename, String place_x, String place_y) {
        MapMarkFragment mapmarkfragment = new MapMarkFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        bundle.putString("placename", placename);
        bundle.putString("place_x", place_x);
        bundle.putString("place_y", place_y);

        mapmarkfragment.setArguments(bundle);
        return mapmarkfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //프래그먼트 해당 번호를 넘겨 받음
            num = getArguments().getInt("number");
            placename = getArguments().getString("placename");
            place_x = getArguments().getString("place_x");
            place_y = getArguments().getString("place_y");

            Log.d("맵 보기","onCreate");
            Log.d("num x", String.valueOf(num));
            Log.d("placename x", placename);
            Log.d("place_x x", place_x);
            Log.d("place_y x", place_y);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("맵 보기","onCreateView");


        view = inflater.inflate(R.layout.fragment_map_mark, container, false);
        oContext = container.getContext();

        //지도 사용권한을 받아 온다.
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);



        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment= (MapFragment) fragmentManager.findFragmentById(R.id.mapview);


        Log.d("맵 보기 mapFragment", String.valueOf(mapFragment));
        //if(mapFragment==null){
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.mapview, mapFragment).commit();
        //}
        //getMapAsync를 호출하여 비동기로 onMapReady콜백 메서드 호출
        //onMapReady에서 NaverMap객체를 받음
        mapFragment.getMapAsync(this);

        return view;
    }

    //맵 실행
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        geocoder = new Geocoder(oContext);
        //네이버 맵에 locationSource를 셋하면 위치 추적 기능을 사용 할 수 있다
        naverMap.setLocationSource(locationSource);
        //위치 추적 모드 지정 가능 내 위치로 이동
//        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //현재위치 버튼 사용가능 여부 설정
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        //averMap.setLocationTrackingMode(LocationTrackingMode.None);

        // LatLng initialPosition = new LatLng(37.506855, 127.066242);

        Log.d("place_x x!!!", place_x);
        Log.d("place_y x!!!", place_y);
        LatLng initialPosition = new LatLng(Double.parseDouble(place_y), Double.parseDouble(place_x));
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(initialPosition);
        naverMap.moveCamera(cameraUpdate);


        // 지도상에 마커 표시 - 클릭시 정보 보이게 할 것.
        Marker marker = new Marker();
        marker.setPosition(new LatLng(Double.parseDouble(place_y), Double.parseDouble(place_x)));

        //Log.d("naverMap1", String.valueOf(naverMap));
        marker.setMap(naverMap);
        marker.setCaptionText(placename);
        //marker.setCaptionColor(Color.BLUE);
        //marker.setCaptionHaloColor(Color.rgb(200, 255, 200));
        marker.setCaptionTextSize(15);
        marker.setHideCollidedSymbols(true);
        marker.setOnClickListener(this);




        // 마커들 위치 정의 (대충 1km 간격 동서남북 방향으로 만개씩, 총 4만개)
        //1km 간격으로 바둑판 형식으로 마커가 생긴다.
        //그럼 여기서 데이터를 받아 온 다음에 마커를 생성하면 되겠네
        //스팟의 정보를 여기서 전부 받아 온 다음에 한번에 마커로 생성해주자
        //밑에 for문에 전체 스팟 리스트로 사이즈를 주고 만들면 될거 같은데?

        //지도에 마킹을 한다. 여러개 마킹 가능하게 함.
        /*
        markersPosition = new Vector<LatLng>();
        markersPosition.add(new LatLng(
                initialPosition.latitude,
                initialPosition.longitude
        ));*/



        /*
        markersPosition = new Vector<LatLng>();
        for (int x = 0; x < 100; ++x) {
            for (int y = 0; y < 100; ++y) {
                markersPosition.add(new LatLng(
                        initialPosition.latitude - (REFERANCE_LAT * x),
                        initialPosition.longitude + (REFERANCE_LNG * y)
                ));
                markersPosition.add(new LatLng(
                        initialPosition.latitude + (REFERANCE_LAT * x),
                        initialPosition.longitude - (REFERANCE_LNG * y)
                ));
                markersPosition.add(new LatLng(
                        initialPosition.latitude + (REFERANCE_LAT * x),
                        initialPosition.longitude + (REFERANCE_LNG * y)
                ));
                markersPosition.add(new LatLng(
                        initialPosition.latitude - (REFERANCE_LAT * x),
                        initialPosition.longitude - (REFERANCE_LNG * y)
                ));
            }
        }*/

        // 카메라 이동 되면 호출 되는 이벤트
        // 마우스로 움직일때마다 반응
        /*
        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int reason, boolean animated) {

                Log.d("onCameraChange","카메라 이동");

                freeActiveMarkers();

                // 정의된 마커위치들중 가시거리 내에있는것들만 마커 생성
                LatLng currentPosition = getCurrentPosition(naverMap);
                for (LatLng markerPosition: markersPosition) {
                    if (!withinSightMarker(currentPosition, markerPosition))
                        continue;

                    Log.d("you!", String.valueOf(markerPosition));
                    Marker marker = new Marker();
                    marker.setPosition(markerPosition);

                    //Log.d("naverMap2", String.valueOf(naverMap));
                    marker.setMap(naverMap);
                    activeMarkers.add(marker);

                }
            }
        });*/

/*
        // 버튼 클릭시 지도에서 검색
        Button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                String str = editText.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                // 마커 생성
                Marker marker = new Marker();
                marker.setPosition(point);
                // 마커 추가
                marker.setMap(naverMap);

                // 해당 좌표로 화면 줌
//                naverMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(point);
                naverMap.moveCamera(cameraUpdate);
            }
        });
*/


        // 현재 위치 버튼 안보이게 설정
        // UiSettings uiSettings = naverMap.getUiSettings();
        //uiSettings.setLocationButtonEnabled(false);

        // 지도 유형 위성사진으로 설정
        //naverMap.setMapType(NaverMap.MapType.Satellite);
    }
    // 마커 정보 저장시킬 변수들 선언
    private Vector<LatLng> markersPosition;
    private Vector<Marker> activeMarkers;

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
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if (overlay instanceof Marker) {
            Toast.makeText(this.getContext(), "마커가 선택되었습니다", Toast.LENGTH_LONG).show();

            //activity에서 메서드 처리
            mapactivitypager.ReturnResult(placename, place_x, place_y);
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }
}