package com.neo.accountapp_3.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Adapter.CategoryRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.WhoList;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AccountAdd extends AppCompatActivity {

    WhoList oWhoList;
    String couplekey = "";
    String myid = "";
    String otherid = "";

    AccountBook oAccountBook;
    int selectyear;
    int selectmonth;
    int selectday;
    String selectdayofweek;
    int price; //금액

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    EditText pricetxt;
    private String result="";

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountadd);

        oWhoList = new WhoList(this);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();
        Log.d("메인 커플키 ",couplekey);
        Log.d("나의 id ",myid);
        Log.d("상대 id ",otherid);

        //데이터 받음
        Intent intent = getIntent();
        selectyear = intent.getExtras().getInt("year");
        selectmonth = intent.getExtras().getInt("month");
        selectday = intent.getExtras().getInt("day");
        selectdayofweek = intent.getExtras().getString("dayofweek");

        //Log.d("년", String.valueOf(selectyear));
        //Log.d("월", String.valueOf(selectmonth));
        //Log.d("일", String.valueOf(selectday));

        //선택한 날짜 설정
        TextView datetxt = (TextView) findViewById(R.id.datetxt);
        datetxt.setText(selectyear + "년 " + (selectmonth) + "월 " + selectday+"일("+selectdayofweek+")");

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //changeView(1); //기본

        //돈 콤마 찍어주기
        pricetxt = (EditText)findViewById(R.id.pricetxt);
        pricetxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                if(s.toString().length() > 0){
                    price = Integer.parseInt(s.toString().replaceAll(",","")); //값 넘길때 사용
                }



                //특수문자 제거, 공백제거, 숫자 수 제한 필요

                //콤마 붙이기
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)){ //빈값이 아닐때 변환된 값과 같지 않을때
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",","")));
                    pricetxt.setText(result); //변환된 값을 저장
                    pricetxt.setSelection(result.length()); //숫자를 입력하면 그 숫자만큼 커서 위치를 설정
                }

                //리사이클러뷰에서 바로 값을 넘겨주기 위해
                MakeGridRecyclerview();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        MakeGridRecyclerview();
        //MakeTablelist();
        //Functionbinding();

    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: //뒤로가기
                finish();
            break;

          default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void MakeGridRecyclerview() {
        ArrayList<ArrayList<ArrayList<String>>> Selectlist = oWhoList.WholistRead();
       // Log.d("유저 카테고리 리스트", String.valueOf(Selectlist));



       //내 id가 먼저 나오도록 변경한다.
        ArrayList<Integer> sortlist_alert = new ArrayList<Integer>();
        sortlist_alert.add(0); //내 id
        sortlist_alert.add(0); //상대 id
        for (int i = 0; i < Selectlist.size(); i++){
            if(Selectlist.get(i).get(1).get(0).equals(couplekey)) { //커플 고유번호 지정

                if(Selectlist.get(i).get(1).get(2).equals(myid)){ //내 id 라면? - 1번째
                    Log.d("Sort list each", String.valueOf(Selectlist.get(i).get(0).get(0)));
                    sortlist_alert.set(0,Integer.parseInt(Selectlist.get(i).get(0).get(0))); //키만 저장한다

                }else if(Selectlist.get(i).get(1).get(2).equals(otherid)){ //상대 id 라면? - 2번째
                    Log.d("Sort list each", String.valueOf(Selectlist.get(i).get(0).get(0)));
                    //sortlist_alert.add(0,1);
                    sortlist_alert.set(1,Integer.parseInt(Selectlist.get(i).get(0).get(0))); //키만 저장한다

                }else{
                    if(i >= 2){
                        sortlist_alert.add(Integer.parseInt(Selectlist.get(i).get(0).get(0))); //키만 저장한다
                    }
                }


            }
        }

        Log.d("Sort list!!1", String.valueOf(sortlist_alert));

        ConditionListBox.clear();
        for (int i = 0; i < sortlist_alert.size(); i++){
            ArrayList<ArrayList<String>> whoone = oWhoList.Getoneinfo(String.valueOf(sortlist_alert.get(i)));
            Log.d("하나의 가계부", String.valueOf(whoone));

            ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
            eachlist.addAll(whoone);
            ConditionListBox.add(eachlist);
        }
        Log.d("Sort list total", String.valueOf(ConditionListBox));

        Log.d("인물 카테고리 조건 리스트", String.valueOf(ConditionListBox));

        RecyclerView WHORecyclerView = (RecyclerView)findViewById(R.id.whoRecyclerView); //리사이클러뷰 위치 선언
        GridLayoutManager GridlayoutManager = new GridLayoutManager(getApplicationContext(), 3); //그리드 매니저 선언
        CategoryRecyclerAdapter WHOAdapter = new CategoryRecyclerAdapter(this); //내가만든 어댑터 선언
        WHORecyclerView.setLayoutManager(GridlayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        WHOAdapter.setlisttype(1); //arraylist 연결
        WHOAdapter.setRecycleList(ConditionListBox); //arraylist 연결
        WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        WHORecyclerView.setAdapter(WHOAdapter); //리사이클러뷰 위치에 어답터 세팅

    }
}