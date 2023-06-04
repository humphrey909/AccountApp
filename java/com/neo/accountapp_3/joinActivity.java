package com.neo.accountapp_3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class joinActivity extends AppCompatActivity {
   // private Context mContext;

    Join oJoin;

    int emailchk;
    int pwchk;
   // User oUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinactivity);

        oJoin = new Join(this);

        //이메일 유효성 검사
        EditText emailedit = (EditText)findViewById(R.id.emailedit);
        emailedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                String email = s.toString();
                Log.d("입력한 이메일", email);

                if(oJoin.isEmail(email)){
                    //이메일 맞음!
                    emailchk = 1;
                    Log.d("이메일 여부", "이메일 형식에 맞음!");

                } else {
                    //이메일 아님!
                    emailchk = 0;
                    Log.d("이메일 여부", "이메일 형식에 맞지 않음!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });



        //두번째 패스워드 맞는지 체크

        EditText pwedit2 = (EditText)findViewById(R.id.pwedit2);
        pwedit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때

                EditText pwedit = (EditText)findViewById(R.id.pwedit);//처음 패스워드
                String pw1 = pwedit.getText().toString();
                String pw = s.toString(); //두번째 패스워드
                Log.d("처음입력한 패스워드", pw1);
                Log.d("두번째 패스워드", pw);


                if(pw1.equals(pw)){ // 패스워드가 맞음
                    pwchk = 1;
                    Log.d("패스워드 여부", "패스워드가 맞음");
                }else{ //패스워드가 맞지 않음
                    pwchk = 0;
                    Log.d("패스워드 여부", "패스워드가 맞지않음");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });



        //가입완료 클릭시
        Button joincomplete = (Button)findViewById(R.id.joincomplete);
        joincomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(getApplicationContext(), CodeShareActivity.class);
                // startActivity(intent);


                EditText emailedit = (EditText)findViewById(R.id.emailedit);
                EditText pwedit1 = (EditText)findViewById(R.id.pwedit);
                EditText pwedit2 = (EditText)findViewById(R.id.pwedit2);
                EditText nameedit = (EditText)findViewById(R.id.nameedit);
                String email = emailedit.getText().toString();
                String pw = pwedit2.getText().toString();
                String name = nameedit.getText().toString();

                if(!email.equals("") && !pwedit1.equals("") && !pwedit2.equals("") && !name.equals("")){
                    if(emailchk == 1 && pwchk == 1){
                        //중복여부 체크

                        if(oJoin.ChkEmailExitence(email) == -1){ //존재하지 않음
                            Toast.makeText(getApplicationContext(), "회원가입을 진행합니다. ", Toast.LENGTH_SHORT).show();

                            String joinchk = oJoin.joinsuccess(email, pw, name, "-");

                            if(!joinchk.equals("0")){
                                Toast.makeText(getApplicationContext(), "회원가입 완료 ", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }else{ //존재
                            Toast.makeText(getApplicationContext(), "존재하는 id 입니다. ", Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        if(emailchk == 0){
                            Toast.makeText(getApplicationContext(), "이메일 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        }else if(pwchk == 0){
                            Toast.makeText(getApplicationContext(), "패스워드가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "빈공간이 존재합니다.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



}