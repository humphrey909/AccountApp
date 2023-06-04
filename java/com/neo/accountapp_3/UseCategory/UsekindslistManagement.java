package com.neo.accountapp_3.UseCategory;

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
import android.widget.RadioButton;

import com.neo.accountapp_3.Adapter.CategoryRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.UseKindsList;

import java.util.ArrayList;
import java.util.Collections;

public class UsekindslistManagement extends AppCompatActivity {
    UseKindsList oUseKindsList;

    RadioButton input_pricebtn, output_pricebtn;
    RadioButton.OnClickListener clickListener;

    String usekindstab; //품목 탭

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트

    String couplekey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usekindslist_managemnet);

        oUseKindsList = new UseKindsList(this);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //기본 -> 지출
        usekindstab = "1"; //품목 탭 입력
        MakeRecyclerview(usekindstab); //기본 지출

        //연결하기 클릭시
        //클릭시 fram 목록 변경
        input_pricebtn = findViewById(R.id.input_pricebtn);
        output_pricebtn = findViewById(R.id.output_pricebtn);

        clickListener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.input_pricebtn:
                        Log.d("","입금");
                        usekindstab = "0"; //품목 탭 입력


                        //input_pricebtn.setPaintFlags(Button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                        //changeView(0);
                        MakeRecyclerview(usekindstab);
                        break;
                    case R.id.output_pricebtn:
                        Log.d("","지출");
                        usekindstab = "1"; //품목 탭 입력
                        //changeView(1);
                        MakeRecyclerview(usekindstab);
                        break;
                }
            }
        };
        input_pricebtn.setOnClickListener(clickListener);
        output_pricebtn.setOnClickListener(clickListener);
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
                //finish(); //내 액티비티 삭제

                //AccountAdd firstActivity = (AccountAdd)AccountAdd.activity_accountadd; //첫번째 액티비티 삭제
                //firstActivity.finish();

                Intent intent = new Intent(getApplicationContext(), UseKindsAdd.class);
                intent.putExtra("type", 1);
                //intent.putExtra("type", 1);
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //사용종류 리스트 만듬
    public void MakeRecyclerview(String ustab) {
        ArrayList<ArrayList<ArrayList<String>>> Selectlist  = oUseKindsList.AlllistRead();
        Log.d("사용종류 탭",usekindstab);

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

        RecyclerView WHORecyclerView = (RecyclerView)findViewById(R.id.UsekindsRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext());
        CategoryRecyclerAdapter WHOAdapter = new CategoryRecyclerAdapter(this); //내가만든 어댑터 선언
        WHORecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        WHOAdapter.setlisttype(8);
        WHOAdapter.setRecycleList(ConditionListBox); //arraylist 연결
        //WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        WHORecyclerView.setAdapter(WHOAdapter); //리사이클러뷰 위치에 어답터 세팅

    }

    @Override
    protected void onStart() {
        super.onStart();

        usekindstab = "1"; //품목 탭 입력
        MakeRecyclerview(usekindstab);

        if(usekindstab.equals("0")){
            input_pricebtn.setChecked(true);
        }else{
            output_pricebtn.setChecked(true);
        }
    }
}