package com.neo.accountapp_3.PeopleCategory;

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

import com.neo.accountapp_3.Adapter.CategoryRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;

public class WholistManagement extends AppCompatActivity {
    WhoList oWhoList;

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트
    String couplekey = "";
    String myid = "";
    String otherid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholist_managemnet);

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

                Intent intent = new Intent(getApplicationContext(), WhoAdd.class);
                intent.putExtra("type", 1);
                startActivity(intent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //who 리스트 만듬
    public void MakeGridRecyclerview() {
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

        ConditionListBox.clear();
        for (int i = 0; i < sortlist_alert.size(); i++){
            ArrayList<ArrayList<String>> whoone = oWhoList.Getoneinfo(String.valueOf(sortlist_alert.get(i)));
            Log.d("하나의 가계부", String.valueOf(whoone));

            ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
            eachlist.addAll(whoone);
            ConditionListBox.add(eachlist);
        }
        Log.d("Sort list total", String.valueOf(ConditionListBox));

        Log.d("사용 카테고리 조건 리스트", String.valueOf(ConditionListBox));


        RecyclerView WHORecyclerView = (RecyclerView)findViewById(R.id.whoRecyclerView); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(getApplicationContext());
        CategoryRecyclerAdapter WHOAdapter = new CategoryRecyclerAdapter(this); //내가만든 어댑터 선언
        WHORecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 그리드 매니저 = 만들 형식

        WHOAdapter.setlisttype(7);
        WHOAdapter.setRecycleList(ConditionListBox); //arraylist 연결
        WHORecyclerView.setAdapter(WHOAdapter); //리사이클러뷰 위치에 어답터 세팅

    }

    @Override
    protected void onStart() {
        super.onStart();

        MakeGridRecyclerview();
    }
}