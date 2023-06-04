package com.neo.accountapp_3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.User;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;

public class CodeShareActivity extends AppCompatActivity {

    User oUser;
    WhoList oWhoList;
    UseKindsList oUseKindsList;
    PaymentKindsList oPaymentKindsList;

    String Myid = "";
    ArrayList<ArrayList<String>> Myinfo;

    ArrayList<String> Otherinfo;

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codeshare);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        GlobalClass.Init(); // 초기화

        oUser = new User(this);
        oWhoList = new WhoList(this);
        oUseKindsList = new UseKindsList(this);
        oPaymentKindsList = new PaymentKindsList(this);

        //이름, 코드 데이터를 가져옴
        Intent intent = getIntent();
        Myid = intent.getExtras().getString("Myid"); //내 id

        Myinfo = oUser.GetMyinfo(Myid); //내 정보
        Log.d("myid",Myid);
        Log.d("myinfo", String.valueOf(Myinfo));

        //내 이름 표시
        TextView myname = (TextView)findViewById(R.id.myname);
        myname.setText(Myinfo.get(1).get(1));

        //내 코드 표시
        TextView mycodetxt = (TextView)findViewById(R.id.mycodetxt);
        mycodetxt.setText(Myinfo.get(1).get(2));

        //연결하기 클릭시
        Button codewirtebtn = (Button)findViewById(R.id.codewirtebtn);
        codewirtebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //입력한 상대방 코드를 가져옴
                EditText othercodetxt = (EditText)findViewById(R.id.othercodetxt);
                String othercode = othercodetxt.getText().toString();

                //커플 연결 시도한다.
                //다른사람 커플 코드, CoupleConnect라는 db이름,
                //코드가 맞으면 -> 해당 유저에 커플연결 값을 1로 지정
                //              -> 커플db에 두개의 유저 id 저장

                //1. user db에 내가 적은 code 있는지 찾는다. 2. 가 값이 맞으면 두 아이디에 연결변수를 1로 지정한다. 3. 커플db에 두 아이디를 저장한다.
                //추가로 이미 커플이 된 유저는 선택이 안되게 처리한다.
                String Otherid = oUser.SearchCoupleCode(othercode, Myinfo.get(1).get(2));
                Log.d("!!!!!!!",Otherid);
                if(Otherid == "-1"){ //맞는 값이 없음
                    Toast.makeText(GlobalClass, "매칭될 회원이 없습니다. ", Toast.LENGTH_SHORT).show();
                    Log.d("CodeShare","매칭될 회원이 없습니다. ");
                }else{ //매칭된 id를 가져옴
                    Toast.makeText(GlobalClass, Otherid+"회원과 매칭됩니다.", Toast.LENGTH_SHORT).show();
                    Log.d("CodeShare",Otherid+"! 해당 회원가 매칭됩니다.  ");

                    //매칭된 id에 커플 변수에 1로 변경
                    //커플 db에 저장
                    String couplekey = oUser.SetCoupleSave(Myid, Otherid);
                    GlobalClass.setcouplecode(couplekey); //글로벌 변수에 세팅
                    GlobalClass.setmyid(Myid); //글로벌 변수에 내 id 세팅
                    GlobalClass.setotherid(Otherid); //글로벌 변수에 상대 id 세팅

                    //커플을 등록하면서 그 커플에 대한 카테고리 기본 리스트를 생성한다.
                    oWhoList.SaveBasicCategory(couplekey); //인물 카테고리 생성
                    oUseKindsList.SaveBasicCategory(couplekey); //사용 카테고리 생성

                    Log.d("커플연결 커플키",couplekey);
                    Intent intent = new Intent(getApplicationContext(), mainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거

                    startActivity(intent);
                }
            }
        });


    }
}