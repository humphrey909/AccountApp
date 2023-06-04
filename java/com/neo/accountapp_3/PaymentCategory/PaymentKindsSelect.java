package com.neo.accountapp_3.PaymentCategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.neo.accountapp_3.Adapter.CategoryRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;
import java.util.Collections;

public class PaymentKindsSelect extends AppCompatActivity {

    PaymentKindsList oPaymentKindsList;
    WhoList oWhoList;

    RadioButton mybtn, otherbtn ,withbtn;
    RadioButton cash, bank ,creditcard ,loan;

    RadioButton.OnClickListener clickListener;
    String couplekey = ""; //커플 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String myid = ""; //나의 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String otherid = ""; //상대 키 - 로그아웃 하기 전까지 전체 고정키이다.

    String paymentkindstab; //결재수단 목록 탭
    String paymentkinds; //결재수단

    String paymentwho; //누구의 결재 카테고리인지 선택

    //선택한 결과를 보낼때 쓰임
    public int RESULTCODE = 3;

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트
    ArrayList<ArrayList<ArrayList<String>>> WhoConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_kinds_select);

        oPaymentKindsList = new PaymentKindsList(this);
        oWhoList = new WhoList(this);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();

        Log.d("메인 커플키 ",GlobalClass.getcouplecode());

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //유저이름 가져오기 - 전체가져와서 0 1 2번째 가져오기
        ArrayList<ArrayList<ArrayList<String>>> Selectlist = oWhoList.WholistRead();
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

        WhoConditionListBox.clear();
        for (int i = 0; i < sortlist_alert.size(); i++){
            ArrayList<ArrayList<String>> whoone = oWhoList.Getoneinfo(String.valueOf(sortlist_alert.get(i)));
            Log.d("하나의 가계부", String.valueOf(whoone));

            ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
            eachlist.addAll(whoone);
            WhoConditionListBox.add(eachlist);
        }
        Log.d("Sort list total", String.valueOf(WhoConditionListBox));


        mybtn = (RadioButton) findViewById(R.id.mybtn);
        mybtn.setText(WhoConditionListBox.get(0).get(1).get(1));
        otherbtn = (RadioButton) findViewById(R.id.otherbtn);
        otherbtn.setText(WhoConditionListBox.get(1).get(1).get(1));
        withbtn = (RadioButton) findViewById(R.id.withbtn);
        withbtn.setText(WhoConditionListBox.get(2).get(1).get(1));



        mybtn = (RadioButton) findViewById(R.id.mybtn);
        otherbtn = (RadioButton) findViewById(R.id.otherbtn);
        withbtn = (RadioButton) findViewById(R.id.withbtn);

        //클릭시 유저에 맞게 리스트 불러옴
        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.mybtn:

                        paymentwho = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
                        Log.d("나",paymentwho);
                        MakeRecyclerview(paymentwho, paymentkindstab);
                        break;
                    case R.id.otherbtn:

                        paymentwho = WhoConditionListBox.get(1).get(0).get(0);//유저 탭
                        Log.d("상대",paymentwho);
                        MakeRecyclerview(paymentwho, paymentkindstab);
                        break;
                    case R.id.withbtn:

                        paymentwho = WhoConditionListBox.get(2).get(0).get(0);//유저 탭
                        Log.d("우리",paymentwho);
                        MakeRecyclerview(paymentwho, paymentkindstab);
                        break;
                }
            }
        };
        mybtn.setOnClickListener(clickListener);
        otherbtn.setOnClickListener(clickListener);
        withbtn.setOnClickListener(clickListener);

        //클릭시 fram 목록 변경
        cash = findViewById(R.id.cash);
        bank = findViewById(R.id.bank);
        creditcard = findViewById(R.id.creditcard);
        loan = findViewById(R.id.loan);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cash:
                        paymentkindstab = "0"; //결재수단 목록 탭 입력
                        MakeRecyclerview(paymentwho, paymentkindstab);

                        break;
                    case R.id.bank:
                        paymentkindstab = "1"; //결재수단 목록 탭 입력
                        MakeRecyclerview(paymentwho, paymentkindstab);

                        break;
                    case R.id.creditcard:
                        paymentkindstab = "2"; //결재수단 목록 탭 입력
                        MakeRecyclerview(paymentwho, paymentkindstab);

                        break;
                    case R.id.loan:
                        paymentkindstab = "3"; //결재수단 목록 탭 입력
                        MakeRecyclerview(paymentwho, paymentkindstab);

                        break;
                }
            }
        };
        cash.setOnClickListener(clickListener);
        bank.setOnClickListener(clickListener);
        creditcard.setOnClickListener(clickListener);
        loan.setOnClickListener(clickListener);


    }


    public void MakeRecyclerview(String whotab, String paymenttab) {
        ArrayList<ArrayList<ArrayList<String>>> Selectlist = oPaymentKindsList.AlllistRead();
        Log.d("결재 카테고리 전체 리스트", String.valueOf(Selectlist));


        //고유값이 가장 큰값이 마지막에 오도록 정렬한다.
        ArrayList<Integer> sortlist = new ArrayList<>();
        for (int i = 0; i < Selectlist.size(); i++){
            if (Selectlist.get(i).get(1).get(0).equals(couplekey)) { //커플 고유번호 조건
                if(Selectlist.get(i).get(1).get(1).equals(whotab)) {
                    if(Selectlist.get(i).get(1).get(2).equals(paymenttab)) {
                        String key = Selectlist.get(i).get(0).get(0);
                        sortlist.add(Integer.parseInt(key));
                    }
                }
            }
        }

        Log.d("원본 리스트", String.valueOf(sortlist));
        Collections.sort(sortlist);
        Log.d("리스트 오름차순 ", String.valueOf(sortlist));

        ConditionListBox.clear();
        //조건에 맞게 리스트를 만든다.
        for (int i = 0; i < sortlist.size(); i++){
            ArrayList<ArrayList<String>> Paymentcateinfo = oPaymentKindsList.Getoneinfo(String.valueOf(sortlist.get(i)));

            ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
            eachlist.addAll(Paymentcateinfo);
            ConditionListBox.add(eachlist);
        }

        Log.d("결재 카테고리 조건 리스트", String.valueOf(ConditionListBox));

        RecyclerView recyclerview = (RecyclerView)findViewById(R.id.PaymentkindsRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this); //그리드 매니저 선언
        CategoryRecyclerAdapter CategroryAdapter = new CategoryRecyclerAdapter(this); //내가만든 어댑터 선언
        recyclerview.setLayoutManager(LinearLayoutManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        CategroryAdapter.setlisttype(6); //arraylist 연결
        CategroryAdapter.setRecycleList(ConditionListBox); //arraylist 연결
        CategroryAdapter.setNeedData4(this, RESULTCODE); //필요한 데이터 넘기자
        recyclerview.setAdapter(CategroryAdapter); //리사이클러뷰 위치에 어답터 세팅

    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: //뒤로가기
                finish();
                break;

            case R.id.addtab: //창 닫기

                Intent intent = new Intent(getApplicationContext(), PaymentKindsAdd.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.objectselect, menu);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        //기본값 정하기
        paymentwho = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
        paymentkindstab = "0";
        MakeRecyclerview(paymentwho, paymentkindstab);

        //인물 체크 처리
        mybtn.setChecked(true);
        /*
        if(paymentwho.equals("0")){
            mybtn.setChecked(true);
        }else if(paymentwho.equals("1")){
            otherbtn.setChecked(true);
        }else{
            withbtn.setChecked(true);
        }*/

        //사용 체크 처리
        if(paymentkindstab.equals("0")){
            cash.setChecked(true);
        }else if(paymentkindstab.equals("1")){
            bank.setChecked(true);
        }else if(paymentkindstab.equals("2")){
            creditcard.setChecked(true);
        }else{
            loan.setChecked(true);
        }
    }
}