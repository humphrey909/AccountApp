package com.neo.accountapp_3.UseCategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.neo.accountapp_3.db.UseKindsList;

import java.util.ArrayList;
import java.util.Collections;

public class UseKindsSelect extends AppCompatActivity {

    UseKindsList oUseKindsList;

    RadioButton input_pricebtn, output_pricebtn;
    RadioButton.OnClickListener clickListener;

    String couplekey = "";

    String usekindstab; //품목 탭
    String usekinds; //품목

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트


    //선택한 결과를 보낼때 쓰임
    public int RESULTCODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_kinds_select);

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

        input_pricebtn = findViewById(R.id.input_pricebtn);
        output_pricebtn = findViewById(R.id.output_pricebtn);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.input_pricebtn:
                        Log.d("","입금");
                        usekindstab = "0"; //품목 탭 입력
                        MakeGridRecyclerview(usekindstab);
                        break;
                    case R.id.output_pricebtn:
                        Log.d("","지출");
                        usekindstab = "1"; //품목 탭 입력
                        MakeGridRecyclerview(usekindstab);
                        break;
                }
            }
        };
        input_pricebtn.setOnClickListener(clickListener);
        output_pricebtn.setOnClickListener(clickListener);


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

        CategroryAdapter.setlisttype(5); //arraylist 연결
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

                Intent intent = new Intent(getApplicationContext(), UseKindsAdd.class);
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

        //기본값 입력
        usekindstab = "1"; //품목 탭 입력
        MakeGridRecyclerview(usekindstab); //기본 지출

        if(usekindstab.equals("0")){
            input_pricebtn.setChecked(true);
        }else{
            output_pricebtn.setChecked(true);
        }
    }
}