package com.neo.accountapp_3.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.CoupleConnectList;
import com.neo.accountapp_3.db.User;
import com.neo.accountapp_3.db.WhoList;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class ProfilesetManagement extends AppCompatActivity {

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    String couplekey = ""; //커플 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String myid = ""; //나의 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String otherid = ""; //상대 키 - 로그아웃 하기 전까지 전체 고정키이다.
    ArrayList<ArrayList<String>> coupleinfo;
    ArrayList<ArrayList<String>> myinfo;
    ArrayList<ArrayList<String>> otherinfo;

    User oUser;
    CoupleConnectList oCoupleConnectList;
    WhoList oWhoList;

    private android.icu.util.Calendar Calendar;
    private int Calendaryear;
    private int Calendarmonth;
    private int Calendarday;
    private String dayofweektxt = "";

    String ChangeValueDate = "";

    EditText aliasedit;
    TextView myidinfoedit;
    TextView otheridinfoedit;
    TextView lovedateedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesetting);

        oUser = new User(this);
        oCoupleConnectList = new CoupleConnectList(this);
        oWhoList = new WhoList(this);

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
        otherinfo = oUser.GetMyinfo(otherid); //상대 정보

        //기본 데이터 보여주기
        aliasedit = (EditText) findViewById(R.id.aliasedit); //사용자 별명
        myidinfoedit = (TextView) findViewById(R.id.myidinfoedit); //id 정보
        otheridinfoedit = (TextView) findViewById(R.id.otheridinfoedit); //id 정보
        lovedateedit = (TextView) findViewById(R.id.lovedateedit); //사귄날짜
        aliasedit.setText(myinfo.get(1).get(1));

        //내 정보 소셜로그인 정보 보이기
        if(!myinfo.get(1).get(4).equals("-")){
            myidinfoedit.setText(myinfo.get(0).get(0)+"("+myinfo.get(1).get(4)+")");
        }else{
            myidinfoedit.setText(myinfo.get(0).get(0));
        }

        //상대 정보 소셜로그인 정보 보이기
        if(!otherinfo.get(1).get(4).equals("-")){
            otheridinfoedit.setText(otherinfo.get(0).get(0)+"("+otherinfo.get(1).get(4)+")");
        }else{
            otheridinfoedit.setText(otherinfo.get(0).get(0));
        }


        //날짜 수정클릭시 달력 처음날짜 지정하기
        Calendar = Calendar.getInstance();

        if(coupleinfo.get(1).get(3).equals("-")){ //빈값
            //오늘 날짜 구하기
            LocalDate nowdate = LocalDate.now();
            Log.d("오늘날짜", String.valueOf(nowdate)); //오늘 날짜
            int todayyear = nowdate.getYear();
            int todaymonth = nowdate.getMonth().getValue();
            int todaydayOfMonth = nowdate.getDayOfMonth();
            Log.d("오늘 날짜 !!!", String.valueOf(todayyear));
            Log.d("오늘 날짜 !!!", String.valueOf(todaymonth));
            Log.d("오늘 날짜 !!!", String.valueOf(todaydayOfMonth));

            Calendar.set(todayyear, todaymonth-1, todaydayOfMonth);
        }else{ //날짜 지정됨

            //받은 날짜 자르기
             String[] Datesplit = ((String) coupleinfo.get(1).get(3)).split("/");
            String Datemake = Datesplit[0] + "년 " + Datesplit[1] + "월 " + Datesplit[2] + "일(" + Datesplit[3] + ")";
            lovedateedit.setText(Datemake);

            Calendar.set(Integer.parseInt(Datesplit[0]), Integer.parseInt(Datesplit[1])-1, Integer.parseInt(Datesplit[2]));
        }

        //입력된 날짜 설정함
        Calendaryear = Calendar.get(Calendar.YEAR);
        Calendarmonth = Calendar.get(Calendar.MONTH);
        Calendarday = Calendar.get(Calendar.DAY_OF_MONTH);

        //날짜 수정
        lovedateedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    //날짜 클릭시 설정
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //요일구하기
                        LocalDate date_ = LocalDate.of(year, month+1, dayOfMonth);
                        DayOfWeek dayOfWeek = date_.getDayOfWeek();
                        int dayOfWeekNumber = dayOfWeek.getValue();
                        dayofweektxt = DayofweekKorea(dayOfWeekNumber);

                        String Datemake_save = year + "/" + (month+1) + "/" + dayOfMonth + "/" + dayofweektxt; //저장하기위한 형태로 변형함
                        ChangeValueDate = Datemake_save;

                        String Datemake = year + "년 " + (month+1) + "월 " + dayOfMonth + "일(" + dayofweektxt + ")";
                        lovedateedit.setText(Datemake);

                        Calendar.set(year, (month), dayOfMonth);
                        Calendaryear = Calendar.get(Calendar.YEAR);
                        Calendarmonth = Calendar.get(Calendar.MONTH);
                        Calendarday = Calendar.get(Calendar.DAY_OF_MONTH);
                    }
                }, Calendaryear, Calendarmonth, Calendarday);

                //달력 열기
                if (lovedateedit.isClickable()) {
                    datePickerDialog.show();
                }
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
            case R.id.savetab: //프로필 저장

                //내 별명을 변경한다.
                oUser.EditUser(myinfo.get(0).get(0), String.valueOf(aliasedit.getText()), "name");

                //wholist 에서 내 키를 찾는다.
                ArrayList<ArrayList<String>> myinfo = null;
                ArrayList<ArrayList<ArrayList<String>>> wholist = oWhoList.WholistRead();
                for (int i = 0; i < wholist.size(); i++){
                    if(wholist.get(i).get(1).get(0).equals(couplekey) && wholist.get(i).get(1).get(2).equals(myid)){ //커플키, myid 매칭
                        myinfo = wholist.get(i);
                    }
                }
                Log.d("프로필 수정", String.valueOf(myinfo));

                //나의 인물 카테고리 이름을 변경한다.
                oWhoList.EditCategory(myinfo.get(0).get(0), couplekey, myinfo.get(1).get(2), String.valueOf(aliasedit.getText()));

                //사귄 날짜 수정.
                if(!ChangeValueDate.equals("")){
                    oCoupleConnectList.Editcoupledata(couplekey, ChangeValueDate, "coupledays");
                }
                finish();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private String DayofweekKorea(int dayOfWeek){

        String korDayOfWeek = "";
        switch (dayOfWeek) {
            case 1:
                korDayOfWeek = "월";
                break;
            case 2:
                korDayOfWeek = "화";
                break;
            case 3:
                korDayOfWeek = "수";
                break;
            case 4:
                korDayOfWeek = "목";
                break;
            case 5:
                korDayOfWeek = "금";
                break;
            case 6:
                korDayOfWeek = "토";
                break;
            case 7:
                korDayOfWeek = "일";
                break;
        }

        return korDayOfWeek;
    }
}