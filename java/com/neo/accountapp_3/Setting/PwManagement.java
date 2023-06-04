package com.neo.accountapp_3.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.CoupleConnectList;
import com.neo.accountapp_3.db.User;

import java.util.ArrayList;

public class PwManagement extends AppCompatActivity {

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    String couplekey = ""; //커플 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String myid = ""; //나의 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String otherid = ""; //상대 키 - 로그아웃 하기 전까지 전체 고정키이다.
    ArrayList<ArrayList<String>> coupleinfo;
    ArrayList<ArrayList<String>> myinfo;

    User oUser;
    CoupleConnectList oCoupleConnectList;
    EditText currentpwedit;
    EditText newpwedit;
    EditText newpw2edit;

    int currentpwchk = 0; //현재 패스워드가 맞는지 체크
    int newpwchk = 0; //새로운 패스워드가 맞는지 체크

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_management);

        oUser = new User(this);
        oCoupleConnectList = new CoupleConnectList(this);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());

        //기본 가입 정보들 가져옴
        coupleinfo = oCoupleConnectList.GetOneinfo(couplekey); //커플 정보
        myinfo = oUser.GetMyinfo(myid); //나의 정보



        //현재 패스워드가 맞는지 분별
        currentpwedit = (EditText)findViewById(R.id.currentpwedit);
        currentpwedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                Log.d("현재 패스워드 ", s.toString());
                Log.d("my 패스워드 ", myinfo.get(1).get(0));

                if(myinfo.get(1).get(0).equals(s.toString())){
                    currentpwchk = 1;
                    Log.d("현재 비밀번호 체크 ", "올바른 패스워드 입니다. ");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });


        //비밀번호가 위에 비밀번호와 맞는지 분별
        newpw2edit = (EditText)findViewById(R.id.newpw2edit);
        newpw2edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때

                EditText newpwedit = (EditText)findViewById(R.id.newpwedit);//처음 패스워드
                String pw1 = newpwedit.getText().toString();
                String pw = s.toString(); //두번째 패스워드
                Log.d("처음입력한 패스워드", pw1);
                Log.d("두번째 패스워드", pw);


                if(pw1.equals(pw)){ // 패스워드가 맞음
                    newpwchk = 1;
                    Log.d("패스워드 여부", "패스워드가 맞음");
                }else{ //패스워드가 맞지 않음
                    newpwchk = 0;
                    Log.d("패스워드 여부", "패스워드가 맞지않음");
                }
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
        menuInflater.inflate(R.menu.objectadd, menu);
        return true;
    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.savetab: //패스워드 변경

                if(currentpwchk == 1){
                    if(newpwchk == 1){

                        oUser.EditUser(myinfo.get(0).get(0), newpw2edit.getText().toString(), "pw");
                        Toast.makeText(this, "변경 완료 ", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(this, "새로운 비밀번호가 맞지 않습니다. ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "현재 비밀번호가 맞지 않습니다. ", Toast.LENGTH_SHORT).show();
                }


                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}