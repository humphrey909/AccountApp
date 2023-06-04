package com.neo.accountapp_3.Account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Adapter.CategoryRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.Map.MapActivityPager;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.WhoList;
import com.neo.accountapp_3.mainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class AccountAdd2 extends AppCompatActivity {

    WhoList oWhoList;
    UseKindsList oUseKindsList;

    String couplekey = "";

    int selectyear;//년
    int selectmonth;//월
    int selectday;//일
    String selectdayofweek;//요일
    int price; //금액

    String who; //누가
    String usekindstab; //품목 탭
    String explain; //상세설명
    EditText explaintxt;
    TextView locationtxt;

    RadioButton input_pricebtn, output_pricebtn;
    Button locationbtn;

    Button.OnClickListener clickListener;

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트

    String placename="";
    String place_x="";
    String place_y="";

    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE4 = 4;

    @SuppressLint("StaticFieldLeak")
    public static Activity acc2activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add2);

        acc2activity = this;

        oWhoList = new WhoList(this);
        oUseKindsList = new UseKindsList(this);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());

        //데이터 받음
        Intent intent = getIntent();
        selectyear = intent.getExtras().getInt("selectyear");
        selectmonth = intent.getExtras().getInt("selectmonth");
        selectday = intent.getExtras().getInt("selectday");
        selectdayofweek = intent.getExtras().getString("selectdayofweek");

        price = intent.getExtras().getInt("price");
        who = intent.getExtras().getString("who");

        Log.d("selectyear", String.valueOf(selectyear)); //년
        Log.d("selectmonth", String.valueOf(selectmonth)); //월
        Log.d("selectday", String.valueOf(selectday)); //일
        Log.d("price", String.valueOf(price)); //금액
        Log.d("who", who); //누구


        TextView whotxt = (TextView) findViewById(R.id.whotxt);
        ArrayList<ArrayList<String>> whoobject = oWhoList.Getoneinfo(who);
        whotxt.setText(whoobject.get(1).get(1));

        TextView pricetxt = (TextView) findViewById(R.id.pricetxt);
        pricetxt.setText(String.valueOf(price));

        locationtxt = (TextView) findViewById(R.id.locationtxt);


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usekindstab = "1"; //품목 탭 입력
        MakeGridRecyclerview(usekindstab); //기본 지출

        //클릭시 fram 목록 변경
        input_pricebtn = findViewById(R.id.input_pricebtn);
        output_pricebtn = findViewById(R.id.output_pricebtn);
        locationbtn = findViewById(R.id.locationbtn);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.input_pricebtn:
                        Log.d("","입금");

                        usekindstab = "0"; //품목 탭 입력
                        MakeGridRecyclerview(usekindstab);
                        //changeView(0);
                        break;
                    case R.id.output_pricebtn:
                        Log.d("","지출");

                        usekindstab = "1"; //품목 탭 입력
                        MakeGridRecyclerview(usekindstab);
                        //changeView(1);
                        break;
                    case R.id.locationbtn:
                        Log.d("","지도");

                        //마커 선택시 값을 가져오도록 개발한다.
                        Intent intent = new Intent(getApplicationContext(), MapActivityPager.class);
                        startActivityForResult(intent, REQUESTCODE);

                        //지도 선택후 결과값 받아오게 함.
                        //Intent intent = new Intent(getApplicationContext(), PlaceSearchlistpage_x.class);
                        //startActivityForResult(intent, REQUESTCODE);
                        break;
                }
            }
        };
        input_pricebtn.setOnClickListener(clickListener);
        output_pricebtn.setOnClickListener(clickListener);
        locationbtn.setOnClickListener(clickListener);


        explaintxt = (EditText)findViewById(R.id.explaintxt);
        explaintxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                explain = s.toString();

                MakeGridRecyclerview(usekindstab);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });
    }


    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.accountadd2menu, menu);
        return true;
    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.closetab: //창 닫기

                Intent intent = new Intent(getApplicationContext(), mainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //카테고리 리스트를 만듬
    public void MakeGridRecyclerview(String ustab) {
        ArrayList<ArrayList<ArrayList<String>>> Selectlist = oUseKindsList.AlllistRead();
        //Log.d("사용 카테고리 리스트", String.valueOf(Selectlist));

        //고유값이 가장 큰값이 마지막에 오도록 정렬한다.
        ArrayList<Integer> sortlist = new ArrayList<>();
        for (int i = 0; i < Selectlist.size(); i++){
            if (Selectlist.get(i).get(1).get(0).equals(couplekey)) { //커플 고유번호 조건
                String key = Selectlist.get(i).get(0).get(0);
                sortlist.add(Integer.parseInt(key));
            }
        }

        Log.d("원본 리스트", String.valueOf(sortlist));
        Collections.sort(sortlist);
        Log.d("리스트 오름차순 ", String.valueOf(sortlist));

        ConditionListBox.clear();
        //조건에 맞게 리스트를 만든다.
        for (int i = 0; i < sortlist.size(); i++){
            ArrayList<ArrayList<String>> Usecateinfo = oUseKindsList.Getoneinfo(String.valueOf(sortlist.get(i)));

            if(Usecateinfo.get(1).get(0).equals(couplekey)){ //커플키 조건
                if(Usecateinfo.get(1).get(1).equals(ustab)){ //사용 카테고리 조건
                    ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
                    eachlist.addAll(Usecateinfo);
                    ConditionListBox.add(eachlist);
                }
            }
        }

        Log.d("사용 카테고리 조건 리스트", String.valueOf(ConditionListBox));

        RecyclerView recyclerview = (RecyclerView)findViewById(R.id.UsekindsRecyclerView); //리사이클러뷰 위치 선언
        GridLayoutManager GridlayoutManager = new GridLayoutManager(getApplicationContext(), 3); //그리드 매니저 선언
        CategoryRecyclerAdapter CategroryAdapter = new CategoryRecyclerAdapter(this); //내가만든 어댑터 선언
        recyclerview.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        CategroryAdapter.setlisttype(2); //arraylist 연결
        CategroryAdapter.setRecycleList(ConditionListBox); //arraylist 연결

        CategroryAdapter.setNeedData2(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price, who, explain, ustab, placename, place_x, place_y); //필요한 데이터 넘기자
        recyclerview.setAdapter(CategroryAdapter); //리사이클러뷰 위치에 어답터 세팅
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(usekindstab.equals("0")){
            input_pricebtn.setChecked(true);
        }else{
            output_pricebtn.setChecked(true);
        }
    }


    //지도 검색 activity 띄워서 선정한 위도 경도 위치와 이름을 받아 온다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        //who = 1
        //usekinds = 2
        //paymentkinds = 3
        Log.d("요청", String.valueOf(requestCode));
        Log.d("결과", String.valueOf(resultCode));

        if(requestCode == REQUESTCODE){

            if (resultCode == RESULTCODE4) { //인물 카테고리 선택 후 결과
                placename = resultIntent.getStringExtra("placename");
                place_x = resultIntent.getStringExtra("place_x");
                place_y = resultIntent.getStringExtra("place_y");
                Log.d("placename ", placename);
                Log.d("place_x ", place_x);
                Log.d("place_y ", place_y);

                locationtxt.setText(placename);

                MakeGridRecyclerview(usekindstab);
            }
        }
    }
}