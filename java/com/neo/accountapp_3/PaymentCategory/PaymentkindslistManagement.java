package com.neo.accountapp_3.PaymentCategory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

public class PaymentkindslistManagement extends AppCompatActivity {
    PaymentKindsList oPaymentKindsList;
    WhoList oWhoList;

    RadioButton mybtn, otherbtn, withbtn;
    RadioButton cashbtn, bankbtn, creditbtn, loanbtn;
    Button.OnClickListener clickListener;

    String usertab;
    String paymenttab;

    com.neo.accountapp_3.db.GlobalClass GlobalClass;
    String couplekey;
    String myid = "";
    String otherid = "";

    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트
    ArrayList<ArrayList<ArrayList<String>>> WhoConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentkindslist_managemnet);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 사용 class 불러옴
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();

        oPaymentKindsList = new PaymentKindsList(this);
        oWhoList = new WhoList(this);

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
        otherbtn = (RadioButton) findViewById(R.id.otherbtn);
        withbtn = (RadioButton) findViewById(R.id.withbtn);

        mybtn.setText(WhoConditionListBox.get(0).get(1).get(1));
        otherbtn.setText(WhoConditionListBox.get(1).get(1).get(1));
        withbtn.setText(WhoConditionListBox.get(2).get(1).get(1));

        //인물 선택
        mybtn = findViewById(R.id.mybtn);
        otherbtn = findViewById(R.id.otherbtn);
        withbtn = findViewById(R.id.withbtn);

        clickListener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.mybtn:

                        usertab = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
                        Log.d("나",usertab);
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                    case R.id.otherbtn:
                        usertab = WhoConditionListBox.get(1).get(0).get(0);//유저 탭
                        Log.d("상대",usertab);
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                    case R.id.withbtn:
                        usertab = WhoConditionListBox.get(2).get(0).get(0);//유저 탭
                        Log.d("우리",usertab);
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                }
            }
        };
        mybtn.setOnClickListener(clickListener);
        otherbtn.setOnClickListener(clickListener);
        withbtn.setOnClickListener(clickListener);



        //대분류 선택
        cashbtn = findViewById(R.id.cashbtn);
        bankbtn = findViewById(R.id.bankbtn);
        creditbtn = findViewById(R.id.creditbtn);
        loanbtn = findViewById(R.id.loanbtn);

        clickListener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cashbtn:
                        Log.d("","현금");
                        paymenttab = "0"; //품목 탭 입력
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                    case R.id.bankbtn:
                        Log.d("","은행");
                        paymenttab = "1"; //품목 탭 입력
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                    case R.id.creditbtn:
                        Log.d("","신용카드");
                        paymenttab = "2"; //품목 탭 입력
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                    case R.id.loanbtn:
                        Log.d("","대출");
                        paymenttab = "3"; //품목 탭 입력
                        MakeRecyclerview(usertab, paymenttab);

                        break;
                }
            }
        };
        cashbtn.setOnClickListener(clickListener);
        bankbtn.setOnClickListener(clickListener);
        creditbtn.setOnClickListener(clickListener);
        loanbtn.setOnClickListener(clickListener);
    }

    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.objectselect, menu);
        return true;
    }
    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.addtab: //카테고리 추가

                Intent intent = new Intent(getApplicationContext(), PaymentKindsAdd.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void MakeRecyclerview(String whotab, String paymenttab) {
        ArrayList<ArrayList<ArrayList<String>>> Selectlist = oPaymentKindsList.AlllistRead();
        Log.d("사용 카테고리 리스트", String.valueOf(Selectlist));

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

        RecyclerView WHORecyclerView = (RecyclerView)findViewById(R.id.whoRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext());
        CategoryRecyclerAdapter WHOAdapter = new CategoryRecyclerAdapter(this); //내가만든 어댑터 선언
        WHORecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        WHOAdapter.setlisttype(9);
        WHOAdapter.setRecycleList(ConditionListBox); //arraylist 연결
        WHORecyclerView.setAdapter(WHOAdapter); //리사이클러뷰 위치에 어답터 세팅

    }


    @SuppressLint("ResourceType")
    @Override
    protected void onStart() {
        super.onStart();

        //기본값 정하기
        usertab = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
        Log.d("usertab?",usertab);
        paymenttab = "0";
        MakeRecyclerview(usertab, paymenttab);

        //인물 체크 처리
        mybtn.setChecked(true);

        //사용 체크 처리
        if(paymenttab.equals("0")){
            cashbtn.setChecked(true);
        }else if(paymenttab.equals("1")){
            bankbtn.setChecked(true);
        }else if(paymenttab.equals("2")){
            creditbtn.setChecked(true);
        }else{
            loanbtn.setChecked(true);
        }
    }
}