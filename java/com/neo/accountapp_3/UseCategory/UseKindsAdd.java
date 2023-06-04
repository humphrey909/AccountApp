package com.neo.accountapp_3.UseCategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.UseKindsList;

import java.util.ArrayList;

public class UseKindsAdd extends AppCompatActivity {

    UseKindsList oUseKindsList;
    String couplekey;

    com.neo.accountapp_3.db.GlobalClass GlobalClass;
    String usekindskey;
    ArrayList<ArrayList<String>>  usekindsinfo;
    int addtype; // 1.추가버튼 2.리스트에서 클릭

    EditText nameedit;

    RadioButton input_pricebtn, output_pricebtn;
    RadioButton.OnClickListener clickListener;

    String usekindstab = null; //품목 탭

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_kinds_add);

        oUseKindsList = new UseKindsList(this);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 사용 class 불러옴
        couplekey = GlobalClass.getcouplecode();

        nameedit = (EditText)findViewById(R.id.nameedit);

        input_pricebtn = findViewById(R.id.input_pricebtn);
        output_pricebtn = findViewById(R.id.output_pricebtn);

        //데이터 받음
        Intent intent = getIntent();
        addtype = intent.getExtras().getInt("type");
        if(addtype == 2){ //수정
            usekindskey = intent.getExtras().getString("usekey");
            usekindsinfo = oUseKindsList.Getoneinfo(usekindskey);

            Log.d("who key",usekindskey);
            //Log.d("who key", String.valueOf(oWhoList.Getoneinfo(whokey)));
            nameedit.setText(usekindsinfo.get(1).get(2));

            //기본값 설정
            usekindstab = usekindsinfo.get(1).get(1);

            //usekindstab에 맞는 사용 카테고리 탭 체크설정
            if(usekindstab.equals("0")){
                input_pricebtn.setChecked(true);
            }else{
                output_pricebtn.setChecked(true);
            }
        }


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clickListener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.input_pricebtn:
                        Log.d("","입금");
                        usekindstab = "0"; //품목 탭 입력
                        //changeView(0);
                        break;
                    case R.id.output_pricebtn:
                        Log.d("","지출");
                        usekindstab = "1"; //품목 탭 입력
                        //changeView(1);
                        break;
                }
            }
        };
        input_pricebtn.setOnClickListener(clickListener);
        output_pricebtn.setOnClickListener(clickListener);
    }


    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: //뒤로가기
                finish();
                break;

            case R.id.savetab: //추가하기
                String nameedit =  this.nameedit.getText().toString();

                if(usekindstab != null && !nameedit.equals("")){ //사용 탭과 텍스트가 체크가 됐을때만

                    if(addtype == 1){ //새로저장

                        oUseKindsList.SaveCategory(couplekey, usekindstab, nameedit);
                        Log.d("WhoAdd","인원 저장");
                    }else{ //수정
                        oUseKindsList.EditCategory(usekindskey, couplekey, usekindstab, nameedit);
                        Log.d("WhoAdd","인원 수정");
                    }


                    finish(); //내 액티비티 삭제
                }else{
                    Toast.makeText(this, "빈칸이 존재합니다. ", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.deletetab: //삭제하기
                oUseKindsList.Deleteone(usekindskey);
                finish(); //내 액티비티 삭제
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