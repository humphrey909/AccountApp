package com.neo.accountapp_3.Statistics;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Adapter.MyRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.UseKindsList;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class SearchActivity extends AppCompatActivity{

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    String couplekey = "";
    String SearchUsetab = "";
    String Searchtext = "";

    String SearchMindate = "";
    String SearchMaxdate = "";
    int SearchMinprice = 0;
    int SearchMaxprice = 0;

    AccountBook oAccountBook;
    UseKindsList oUseKindsList;

    EditText searchedit;
    EditText minpriceedit;
    EditText maxpriceedit;

    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트

    RadioGroup useradio;
    RadioButton inputradio;
    RadioButton outputradio;

    RadioGroup termradio;
    RadioButton oneweek;
    RadioButton onemonth;
    RadioButton customterm;

    SimpleDateFormat dateformat;
    Date todayorigin;
    String today;
    Calendar calendar;

    Button mintermbtn, maxtermbtn;
    Button.OnClickListener clickListener;


    //DatePickerDialog 띄울때 필요함
    private int minCalyear;
    private int minCalmonth;
    private int minCalday;

    private int maxCalyear;
    private int maxCalmonth;
    private int maxCalday;

    private String Decimalresult="";
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oAccountBook = new AccountBook(this);
        oUseKindsList = new UseKindsList(this);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());

        //최소 최대 날짜 버튼 생성
        mintermbtn = findViewById(R.id.mintermbtn);
        maxtermbtn = findViewById(R.id.maxtermbtn);

        //최소 최대 금액 버튼 생성
        minpriceedit = (EditText)findViewById(R.id.minpriceedit);
        maxpriceedit = (EditText)findViewById(R.id.maxpriceedit);
        minpriceedit.setText("0");

        //오늘 날짜
        todayorigin = new Date();
        dateformat = new SimpleDateFormat("yyyy/MM/dd");
        today = dateformat.format(todayorigin);
        Log.d("오늘 날짜 입니까아!!!!!!!", String.valueOf(todayorigin));
        Log.d("오늘 날짜 입니까아!!!!!!!",today);


        calendar = Calendar.getInstance();

        calendar.setTime(todayorigin);
        calendar.add(Calendar.MONTH , -1);
        @SuppressLint("SimpleDateFormat")
        String beforeMonth = dateformat.format(calendar.getTime());
        //Log.d("한달전 날짜 입니까아!!!!!!!",beforeMonth);

        //날짜에 맞게 리사이클러뷰 만들기
        SearchUsetab = "1";
        SearchMindate = beforeMonth; //기본으로 한달전 선택
        SearchMaxdate = today;
        SearchMinprice = 0;
        SearchMaxprice = 0;

        //날짜에 맞게 날짜 보여주기
        mintermbtn.setText(SearchMindate);
        maxtermbtn.setText(SearchMaxdate);

        try {
            MakeRecyclerview(Searchtext, SearchUsetab ,SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //사용 카테고리 탭 - 라디오 버튼 설정
        inputradio = (RadioButton) findViewById(R.id.inputradio);
        outputradio = (RadioButton) findViewById(R.id.outputradio);
        outputradio.setChecked(true);
        inputradio.setOnClickListener(radioButtonClickListener);
        outputradio.setOnClickListener(radioButtonClickListener);

        //사용 카테고리 탭 - 라디오 그룹 설정
        useradio = (RadioGroup) findViewById(R.id.useradio);
        useradio.setOnCheckedChangeListener(radioGroupButtonChangeListener);


        //기간 탭 - 라디오 버튼 설정
        oneweek = (RadioButton) findViewById(R.id.oneweek);
        onemonth = (RadioButton) findViewById(R.id.onemonth);
        onemonth.setChecked(true);
        oneweek.setOnClickListener(radioButtonClickListener);
        onemonth.setOnClickListener(radioButtonClickListener);

        //기간 카테고리 탭 - 라디오 그룹 설정
        termradio = (RadioGroup) findViewById(R.id.termradio);
        termradio.setOnCheckedChangeListener(radioGroupButtonChangeListener);



        //검색 입력시 바로 검색 시작
        searchedit = (EditText)findViewById(R.id.searchedit);
        searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                Log.d("검색 text ", s.toString());
                Searchtext= s.toString();

                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //최소금액 입력
        minpriceedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때

                //Log.d("charsequence", String.valueOf(s)); //텍스트 그대로 가져옴
                if(s.toString().length() > 0){
                    SearchMinprice = Integer.parseInt(s.toString().replaceAll(",",""));
                    //ChangeValuePrice = price;
                    //Log.d("가격", String.valueOf(price)); //콤마 삭제
                }else{
                    SearchMinprice = 0;
                    minpriceedit.setText(String.valueOf(SearchMinprice)); //변환된 값을 저장
                }



                //특수문자 제거, 공백제거, 숫자 수 제한 필요
                Log.d("결과", String.valueOf(Decimalresult)); //콤마 삭제

                //콤마 붙이기
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(Decimalresult)){ //빈값이 아닐때 변환된 값과 같지 않을때
                    Decimalresult = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",",""))); //콤마 붙여주는 부분
                    minpriceedit.setText(Decimalresult); //변환된 값을 저장
                    minpriceedit.setSelection(Decimalresult.length()); //숫자를 입력하면 그 숫자만큼 커서 위치를 설정
                }

                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //최대금액 입력
        maxpriceedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때

                //Log.d("charsequence", String.valueOf(s)); //텍스트 그대로 가져옴
                if(s.toString().length() > 0){
                    SearchMaxprice = Integer.parseInt(s.toString().replaceAll(",",""));
                    //Log.d("가격", String.valueOf(price)); //콤마 삭제
                }else{
                    SearchMaxprice = 0;
                }



                //특수문자 제거, 공백제거, 숫자 수 제한 필요
                Log.d("결과", String.valueOf(Decimalresult)); //콤마 삭제

                //콤마 붙이기
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(Decimalresult)){ //빈값이 아닐때 변환된 값과 같지 않을때
                    Decimalresult = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",",""))); //콤마 붙여주는 부분
                    maxpriceedit.setText(Decimalresult); //변환된 값을 저장
                    maxpriceedit.setSelection(Decimalresult.length()); //숫자를 입력하면 그 숫자만큼 커서 위치를 설정
                }
                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });


        //지정된 최소 최대 날짜를 달력에 먼저 보여준다.
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateformat.parse(SearchMindate);
            date2 = dateformat.parse(SearchMaxdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //오늘 날짜를 지정.
        calendar.setTime(date1);

        //최소날짜 오늘날짜로 지정
        minCalyear = calendar.get(Calendar.YEAR);
        minCalmonth = calendar.get(Calendar.MONTH);
        minCalday = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(date2);

        //최대날짜 오늘날짜로 지정
        maxCalyear = calendar.get(Calendar.YEAR);
        maxCalmonth = calendar.get(Calendar.MONTH);
        maxCalday = calendar.get(Calendar.DAY_OF_MONTH);

        //최소 날짜 수정
        mintermbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectmindate = year + "/" + (month+1) + "/" + dayOfMonth;

                        //날짜 비교하기
                        Date date1 = null;
                        Date date2 = null;
                        try {
                            date1 = dateformat.parse(selectmindate);
                            date2 = dateformat.parse(SearchMaxdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(date1.after(date2) ) {
                            Toast.makeText(SearchActivity.this, "최대값보다 작게 선택하세요.", Toast.LENGTH_SHORT).show();

                        }else{ //최대값보다 작을때만 생성
                            //변경된 날짜 지정
                            calendar.set(year, (month), dayOfMonth);
                            minCalyear = calendar.get(Calendar.YEAR);
                            minCalmonth = calendar.get(Calendar.MONTH);
                            minCalday = calendar.get(Calendar.DAY_OF_MONTH);

                            SearchMindate = dateformat.format(calendar.getTime()); //최소날짜 저장
                            Log.d("최소 날짜", SearchMindate);
                            mintermbtn.setText(SearchMindate); //날짜 보여주기


                            //두값이 있으면 리스트 생성
                            if(!SearchMindate.equals("") && !SearchMaxdate.equals("")){
                                try {
                                    MakeRecyclerview(Searchtext, SearchUsetab ,SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }, minCalyear, minCalmonth, minCalday);

                //달력 열기
                datePickerDialog.show();
            }
        });


        //최대 날짜 수정
        maxtermbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectmaxdate = year + "/" + (month+1) + "/" + dayOfMonth;

                        //날짜 비교하기
                        Date date1 = null;
                        Date date2 = null;
                        try {
                            date1 = dateformat.parse(SearchMindate);
                            date2 = dateformat.parse(selectmaxdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(date2.before(date1)) {
                            Toast.makeText(SearchActivity.this, "최소값 보다 크게 선택하세요.", Toast.LENGTH_SHORT).show();
                        }else { //최소값보다 클때만 생성
                            //변경된 날짜 지정
                            calendar.set(year, (month), dayOfMonth);
                            maxCalyear = calendar.get(Calendar.YEAR);
                            maxCalmonth = calendar.get(Calendar.MONTH);
                            maxCalday = calendar.get(Calendar.DAY_OF_MONTH);

                            SearchMaxdate = dateformat.format(calendar.getTime()); //최소날짜 저장

                            Log.d("최대 날짜", SearchMaxdate);
                            maxtermbtn.setText(SearchMaxdate); //날짜 보여주기


                            //두값이 있으면 리스트 생성
                            if(!SearchMindate.equals("") && !SearchMaxdate.equals("")){
                                try {
                                    MakeRecyclerview(Searchtext, SearchUsetab ,SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, maxCalyear, maxCalmonth, maxCalday);

                //달력 열기
                datePickerDialog.show();
            }
        });
    }

    //사용 카테고리 탭 - 라디오 버튼 클릭 리스너
    RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){
        @Override public void onClick(View view) {
            //Toast.makeText(SearchActivity.this, "inputradio : "+inputradio.isChecked() + ", outputradio : " +outputradio.isChecked() , Toast.LENGTH_SHORT).show();
        }
    };

    //사용 카테고리 탭, 기간 탭 - 라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.inputradio){ //입금
                SearchUsetab = "0";
                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(SearchActivity.this, "라디오 그룹 버튼1 눌렸습니다.", Toast.LENGTH_SHORT).show();
            } else if(i == R.id.outputradio){ //출금
                SearchUsetab = "1";
                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(SearchActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();

            } else if(i == R.id.oneweek){ //한주

                calendar.setTime(todayorigin);
                calendar.add(Calendar.DATE, -7);
                @SuppressLint("SimpleDateFormat") String beforeWeek = dateformat.format(calendar.getTime());
                Log.d("한주전 날짜 입니까아!!!!!!!",beforeWeek);

                //한주전
                SearchMindate = beforeWeek;
                SearchMaxdate = today;

                //날짜에 맞게 날짜 보여주기
                mintermbtn.setText(SearchMindate);
                maxtermbtn.setText(SearchMaxdate);

                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(SearchActivity.this, "한주 전까지 보입니다.", Toast.LENGTH_SHORT).show();
            } else if(i == R.id.onemonth){ //한달

                calendar.setTime(todayorigin);
                calendar.add(Calendar.MONTH , -1);
                @SuppressLint("SimpleDateFormat") String beforeMonth = dateformat.format(calendar.getTime());
                Log.d("한달전 날짜 입니까아!!!!!!!",beforeMonth);

                //한달전
                SearchMindate = beforeMonth;
                SearchMaxdate = today;

                //날짜에 맞게 날짜 보여주기
                mintermbtn.setText(SearchMindate);
                maxtermbtn.setText(SearchMaxdate);

                try {
                    MakeRecyclerview(Searchtext, SearchUsetab, SearchMindate ,SearchMaxdate, SearchMinprice, SearchMaxprice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(SearchActivity.this, "한달 전까지 보입니다.", Toast.LENGTH_SHORT).show();
            }

        }
    };


    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: //뒤로가기
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void MakeRecyclerview(String searchtext, String usetab, String mindate, String maxdate, int minprice, int maxprice) throws ParseException {

        Log.d("최소 금액", String.valueOf(minprice));
        Log.d("최대 금액", String.valueOf(maxprice));

        Log.d("최소 날짜",mindate);
        Log.d("최대 날짜",maxdate);
        Date date1 = null;
        Date date2 = null;
        if(!mindate.equals("-") || !maxdate.equals("-")){
            //날짜 파싱
            date1 = dateformat.parse(mindate);
            date2 = dateformat.parse(maxdate);

            Log.d("date1", String.valueOf(date1));
            Log.d("date2", String.valueOf(date2));
        }

        ArrayList<ArrayList<ArrayList<String>>> Selectlist = oAccountBook.AccountRead();
        Log.d("가계부 전체 리스트", String.valueOf(Selectlist));

        ConditionListBox.clear();
        //조건에 맞게 리스트를 만든다.
        for (int i = 0; i < Selectlist.size(); i++){
            if(Selectlist.get(i).get(1).get(0).equals(couplekey)){ //커플키 조건

                if(Selectlist.get(i).get(1).get(4).contains(searchtext)){ //검색 데이터 포함 여부

                    ArrayList<ArrayList<String>> usekindsinfo = oUseKindsList.Getoneinfo(Selectlist.get(i).get(1).get(5));
                    if(usekindsinfo.get(1).get(1).contains(usetab)){ //사용 카테고리 조건

                        //가계부 날짜 파싱
                        Date Selectdate = dateformat.parse(Selectlist.get(i).get(1).get(1));

                        //가계부 날짜가 최소 최대 날짜 사이에 있을때만 리스트를 생성
                        if(Selectdate.after(date1) && Selectdate.before(date2) || Selectdate.equals(date1) || Selectdate.equals(date2)) {

                            int price = Integer.parseInt(Selectlist.get(i).get(1).get(2));

                            //ChangeValueUsecategorykey를 풀어서 입금인지 지출인지 결정해서 + - 값을 입혀준다.
                            ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(Selectlist.get(i).get(1).get(5));
                            if( usecategoryinfo.get(1).get(1).equals("1")) { // 1이면 지출
                                price = price * -1;
                            }

                            //최소 최대 금액은 바로 적용을 진행 - 최소금액보다 클때만
                            if (price >= minprice) {

                                //최대가격은 0보다 값이 클때만 적용한다.
                                if(maxprice > 0){

                                    //최대금액보다 작을때만
                                    if(price <= maxprice){
                                        ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
                                        eachlist.addAll(Selectlist.get(i));
                                        ConditionListBox.add(eachlist);
                                    }
                                }else{
                                    ArrayList<ArrayList<String>> eachlist = new ArrayList<>();
                                    eachlist.addAll(Selectlist.get(i));
                                    ConditionListBox.add(eachlist);
                                }
                            }


                        }
                    }
                }
            }
        }
        Log.d("가계부 조건 리스트", String.valueOf(ConditionListBox));

        RecyclerView RecyclerView = (RecyclerView)findViewById(R.id.searchlistbox); //리사이클러뷰 위치 선언
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        MyRecyclerAdapter Adapter = new MyRecyclerAdapter(this); //내가만든 어댑터 선언
        RecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 리니어 매니저 = 만들 형식

        Adapter.setlisttype(2); //arraylist 연결
        Adapter.setRecycleList(ConditionListBox); //arraylist 연결
        //WHOAdapter.setNeedData(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price); //필요한 데이터 넘기자
        RecyclerView.setAdapter(Adapter); //리사이클러뷰 위치에 어답터 세팅

    }
}