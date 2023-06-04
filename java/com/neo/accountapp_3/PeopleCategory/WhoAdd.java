package com.neo.accountapp_3.PeopleCategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;

public class WhoAdd extends AppCompatActivity {
    WhoList oWhoList;
    String couplekey;
    String myid = "";
    String otherid = "";

    com.neo.accountapp_3.db.GlobalClass GlobalClass;
    String whokey;
    int addtype; // 1.추가버튼 2.리스트에서 클릭

    EditText whoedit;

    ArrayList<ArrayList<String>> whoinfo;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_add);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 사용 class 불러옴
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();

        oWhoList = new WhoList(this);

        whoedit = (EditText)findViewById(R.id.whoedit);

        //데이터 받음
        Intent intent = getIntent();
        addtype = intent.getExtras().getInt("type"); //1 = 새로추가
        if(addtype == 2){ //수정 모드
            whokey = intent.getExtras().getString("who");
            position = intent.getExtras().getInt("position");

            Log.d("who key",whokey);
            Log.d("who info", String.valueOf(oWhoList.Getoneinfo(whokey)));
            whoinfo = oWhoList.Getoneinfo(whokey);
            whoedit.setText(whoinfo.get(1).get(1));

        }

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: //뒤로가기
                finish();
                break;

            case R.id.savetab: //추가하기

                String whoname = whoedit.getText().toString();

                if(!whoname.equals("")) { //사용 탭과 텍스트가 체크가 됐을때만

                    if (addtype == 1) { //새로저장
                        oWhoList.SaveCategory(couplekey, whoname);
                        Log.d("WhoAdd", "인원 저장");
                    } else { //수정

                        //내 아이디나 상대 아이다라면 다르게 설정 할 것
                        if(whoinfo.get(1).get(2).equals(otherid) || whoinfo.get(1).get(2).equals(myid)){
                            Log.d("what", "2");
                            oWhoList.EditCategory(whokey, couplekey, whoinfo.get(1).get(2), whoname);
                        }else{
                            Log.d("what", "3");
                            oWhoList.EditCategory(whokey, couplekey, "-", whoname);
                        }

                        Log.d("WhoAdd", "인원 수정");
                    }


                    finish(); //내 액티비티 삭제
                }

                break;
            case R.id.deletetab: //삭제하기
                if(position != 2){
                    oWhoList.Deleteone(whokey);
                    finish(); //내 액티비티 삭제
                }else{
                    Toast.makeText(GlobalClass, "삭제할 수 없습니다. ", Toast.LENGTH_SHORT).show();
                }

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

        if(addtype == 1) { //새로저장
            menuInflater.inflate(R.menu.objectadd, menu);
        }else{
            menuInflater.inflate(R.menu.accounteditmenu, menu);
        }

        return true;
    }
}