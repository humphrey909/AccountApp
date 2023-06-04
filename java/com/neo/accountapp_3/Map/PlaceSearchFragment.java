package com.neo.accountapp_3.Map;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.neo.accountapp_3.Adapter.PlaceSearchRecyclerAdapter;
import com.neo.accountapp_3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PlaceSearchFragment extends Fragment {
    int num;
    View view;

    final String BASE_URL = "https://dapi.kakao.com";
    final String REST_API_KEY = "KakaoAK 5b873369ed48cf216b1fd2e955e09459";
    String searchtext = "";
    RetrofitService retrofitService;
    Call<String> call;

    EditText searchedit;

    ArrayList<ArrayList<String>> ConditionListBox = new ArrayList<ArrayList<String>>(); //카테고리 조건에 맞게 넣어주는 리스트

    MapActivityPager mapactivitypager;

    // 메인 액티비티 위에 올린다.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mapactivitypager = (MapActivityPager) getActivity();

        Log.d("장소 검색","onAttach");
       // Toast.makeText(this.getContext(), "onAttach", Toast.LENGTH_LONG).show();
    }



    // 메인 액티비티에서 내려온다.
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("장소 검색","onDetach");
       // Toast.makeText(this.getContext(), "onDetach", Toast.LENGTH_LONG).show();
        //mapactivitypager = null;
    }

    public PlaceSearchFragment() {
        // Required empty public constructor

        RestapiStart();
    }

    //프래그먼트를 띄우면서 데이터를 넘김
    public static PlaceSearchFragment newInstance(int number, String searchtext) {
        PlaceSearchFragment placesearchfragment = new PlaceSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        bundle.putString("searchtext", searchtext);

        placesearchfragment.setArguments(bundle);
        return placesearchfragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //프래그먼트 해당 번호를 넘겨 받음
            num = getArguments().getInt("number");

            searchtext = getArguments().getString("searchtext");
            Log.d("onCreat 실행여부", "");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_place_search, container, false);

        //RequestActivity에서 전달한 번들 저장
        /*Bundle bundle = getArguments();
        String REST_SEARCH = bundle.getString("REST_SEARCH");
        if(REST_SEARCH != null){
            Log.d("REST_SEARCH", REST_SEARCH);
        }*/

        RestapiRequest(searchtext); //요청
        RestapiResponse(); //응답

        return view;
    }



    //레트로핏 라이브러리를 사용해 보낼 양식 만들어 놓음
    public void RestapiStart(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class); //restapi 전송시 필요한 정보 모아서 전송
    }

    //양식대로 restkey와 검색어를 보내고 결과값을 받음
    public void RestapiRequest(String searchtext){
        //rest api 실행 부분
        call = retrofitService.getSearchResult(REST_API_KEY, searchtext);
    }


    public void RestapiResponse(){
        //데이터 응답
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    //통신 성공
                    String resultlist = response.body();
                    Log.d("onResponse ? ","onResponse 성공" + resultlist);

                    //해당 리스트 json 파싱해서 arraylist로 변경하기
                    //응답받은 리스트 정보들을 가져온다.
                    try {
                        PlacedataParse(resultlist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //MakeRecyclerview(resultlist);
                }else{
                    //통신 실패
                    Log.d("onResponse ? ","onResponse 실패");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //통신 실패
                Log.d("onFailure",t.getMessage());
            }
        });
    }

    //가져온 장소 리스트를 json 파싱처리 시작!
    public void PlacedataParse(String resultlist) throws JSONException {
        ConditionListBox.clear(); //초기화
        JSONObject jObject = new JSONObject(resultlist);
        //Log.d("jObject", String.valueOf(jObject));
        //Log.d("documents", jObject.getString("documents"));
        //Log.d("meta", jObject.getString("meta"));

        if(!jObject.getString("documents").equals("[]")){
            JSONArray jsonArray = new JSONArray(jObject.getString("documents"));

            Log.d("jsonArray.length", String.valueOf(jsonArray.length()));

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d("jsonObject", i+"번 -> "+ jsonObject);

                ArrayList<String> eachlist = new ArrayList<>();
                Log.d("jsonObject address_name", i+"번 -> "+ jsonObject.get("address_name"));
                Log.d("jsonObject y", i+"번 -> "+ jsonObject.get("y"));
                Log.d("jsonObject x", i+"번 -> "+ jsonObject.get("x"));

                String addname = (String) jsonObject.get("address_name");
                String x = (String) jsonObject.get("x");
                String y = (String) jsonObject.get("y");
                eachlist.add(addname);
                eachlist.add(x);
                eachlist.add(y);

                ConditionListBox.add(eachlist);
            }

            Log.d("ConditionListBox!", String.valueOf(ConditionListBox));
            MakeRecyclerview();
        }
    }

    public void MakeRecyclerview(){


        Log.d("가계부 조건 리스트", String.valueOf(ConditionListBox));

        RecyclerView RecyclerView = (RecyclerView)view.findViewById(R.id.PlaceRCV); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity());
        PlaceSearchRecyclerAdapter Adapter = new PlaceSearchRecyclerAdapter(getActivity()); //내가만든 어댑터 선언
        RecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 리니어 매니저 = 만들 형식

        //Adapter.setlisttype(2); //arraylist 연결
        Adapter.setRecycleList(ConditionListBox); //arraylist 연결
        Adapter.setNeedData(mapactivitypager);
        RecyclerView.setAdapter(Adapter); //리사이클러뷰 위치에 어답터 세팅

    }
}