package com.neo.accountapp_3.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.neo.accountapp_3.R;

import java.util.ArrayList;

public class MapActivityPager extends AppCompatActivity {
    EditText searchedit;
    TextView placeviewTitle;
    String REST_SEARCH = "";
    ArrayList<Fragment> fragmentslistbox;

    //선택한 결과를 보낼때 쓰임
    public int RESULTCODE4 = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pager);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeviewTitle = (TextView) findViewById(R.id.placeviewTitle);


        //검색 입력시 바로 검색 시작
        searchedit = (EditText)findViewById(R.id.searchedit);
        searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                Log.d("검색 text ", s.toString());
                REST_SEARCH= s.toString();

                fragmentmake();
            }
            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });


        fragmentmake();
    }

    public void fragmentmake(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.placepageview_container, PlaceSearchFragment.newInstance(0, REST_SEARCH)).commit();

        Log.d(" ArrayList<Fragment>", String.valueOf(fragmentslistbox));
    }


    // 인덱스를 통해 해당되는 프래그먼트를 띄운다.
    public void fragmentChange(int index, String placename, String place_x, String place_y){
        Log.d("index", String.valueOf(index));

        if(index == 2){ //지도 확인
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.placepageview_container, MapMarkFragment.newInstance(1, placename, place_x, place_y)).commit();
        }
    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //add2 page로 값을 리턴해주는 부분
    public void ReturnResult(String placename, String place_x, String place_y){
        Log.d("placename MapActivityPager", placename);
        Log.d("place_x MapActivityPager", place_x);
        Log.d("place_y MapActivityPager", place_y);


        //선택한값을 가지고 edit으로 넘겨줄 것.
        Intent resultIntent = new Intent();
        resultIntent.putExtra("placename", placename);
        resultIntent.putExtra("place_x", place_x);
        resultIntent.putExtra("place_y", place_y);
        this.setResult(RESULTCODE4, resultIntent);
        this.finish();
    }
}