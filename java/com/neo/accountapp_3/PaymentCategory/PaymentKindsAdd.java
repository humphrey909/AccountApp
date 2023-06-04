package com.neo.accountapp_3.PaymentCategory;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;

public class PaymentKindsAdd extends AppCompatActivity {
    PaymentKindsList oPaymentKindsList;
    WhoList oWhoList;
    EditText nameedit;

    int addtype; // 1.추가버튼 2.리스트에서 클릭

    String couplekey;
    String myid = "";
    String otherid = "";

    String usertab = null;
    String paymenttab  = null;
    com.neo.accountapp_3.db.GlobalClass GlobalClass;

    RadioButton mybtn, otherbtn, withbtn;
    RadioButton cashbtn, bankbtn, creditbtn, loanbtn;
    Button.OnClickListener clickListener;

    String paymentkey; //고유키
    ArrayList<ArrayList<String>> paymentinfo;

    ArrayList<ArrayList<ArrayList<String>>> WhoConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_kinds_add);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 사용 class 불러옴
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();

        oPaymentKindsList = new PaymentKindsList(this);
        oWhoList = new WhoList(this);

        nameedit = (EditText)findViewById(R.id.nameedit);
        mybtn = (RadioButton) findViewById(R.id.mybtn);
        otherbtn = (RadioButton) findViewById(R.id.otherbtn);
        withbtn = (RadioButton) findViewById(R.id.withbtn);

        cashbtn = findViewById(R.id.cashbtn);
        bankbtn = findViewById(R.id.bankbtn);
        creditbtn = findViewById(R.id.creditbtn);
        loanbtn = findViewById(R.id.loanbtn);

        //데이터 받음
        Intent intent = getIntent();
        addtype = intent.getExtras().getInt("type");
        if(addtype == 2){ //데이터 수정
            paymentkey = intent.getExtras().getString("paymentkey");
            paymentinfo = oPaymentKindsList.Getoneinfo(paymentkey);

            //Log.d("who key",whokey);
            //Log.d("who key", String.valueOf(oWhoList.Getoneinfo(whokey)));
            nameedit.setText(paymentinfo.get(1).get(3));

            //기본값 설정
            usertab = paymentinfo.get(1).get(1);
            paymenttab = paymentinfo.get(1).get(2);

            //usekindstab에 맞는 사용 카테고리 탭 체크설정
            if(usertab.equals("0")){
                mybtn.setChecked(true);
            }else if(usertab.equals("1")){
                otherbtn.setChecked(true);
            }else{
                withbtn.setChecked(true);
                //output_pricebtn.setChecked(true);
            }

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



        mybtn.setText(WhoConditionListBox.get(0).get(1).get(1));
        otherbtn.setText(WhoConditionListBox.get(1).get(1).get(1));
        withbtn.setText(WhoConditionListBox.get(2).get(1).get(1));

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.mybtn:
                        usertab = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
                        Log.d("나",usertab);
                        break;
                    case R.id.otherbtn:
                        usertab = WhoConditionListBox.get(1).get(0).get(0);//유저 탭
                        Log.d("상대",usertab);
                        break;
                    case R.id.withbtn:
                        usertab = WhoConditionListBox.get(2).get(0).get(0);//유저 탭
                        Log.d("우리",usertab);
                        break;
                }
            }
        };
        mybtn.setOnClickListener(clickListener);
        otherbtn.setOnClickListener(clickListener);
        withbtn.setOnClickListener(clickListener);



        //대분류 선택
        clickListener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cashbtn:
                        paymenttab = "0"; //품목 탭 입력
                        Log.d("현금",paymenttab);
                        break;
                    case R.id.bankbtn:
                        paymenttab = "1"; //품목 탭 입력
                        Log.d("은행",paymenttab);
                        break;
                    case R.id.creditbtn:
                        paymenttab = "2"; //품목 탭 입력
                        Log.d("신용카드",paymenttab);
                        break;
                    case R.id.loanbtn:
                        paymenttab = "3"; //품목 탭 입력
                        Log.d("대출",paymenttab);
                        break;
                }
            }
        };
        cashbtn.setOnClickListener(clickListener);
        bankbtn.setOnClickListener(clickListener);
        creditbtn.setOnClickListener(clickListener);
        loanbtn.setOnClickListener(clickListener);
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
                //Log.d("save 유저탭",usertab);
                //Log.d("save 결재탭",paymenttab);
                //Log.d("save 이름탭",nameedit);
                if(usertab != null && paymenttab != null && !nameedit.equals("")){ //인원 탭과 결재 항목 탭, 텍스트가 체크가 됐을때만
                //if(usekindstab.equals("1")){ //입금
                    if(addtype == 1){ //새로저장
                        oPaymentKindsList.SaveCategory(couplekey, usertab, paymenttab, nameedit);
                        Log.d("WhoAdd","인원 저장");
                    }else{ //수정
                        oPaymentKindsList.EditCategory(paymentkey, couplekey, usertab, paymenttab, nameedit);
                        //oUseKindsInputList.SaveCategory(couplekey, nameedit);
                        //oWhoList.EditCategory(whokey, couplekey, nameedit);
                        Log.d("WhoAdd","인원 수정");
                    }

                    finish(); //내 액티비티 삭제
                }else{
                    Toast.makeText(this, "빈칸이 존재합니다. ", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.deletetab: //삭제하기
                oPaymentKindsList.Deleteone(paymentkey);
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