package com.neo.accountapp_3;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.sdk.user.UserApiClient;
import com.neo.accountapp_3.Account.AccountAdd;
import com.neo.accountapp_3.Adapter.MyRecyclerAdapter;
import com.neo.accountapp_3.Map.AccountTotalMap;
import com.neo.accountapp_3.Setting.AccountKindsSetlist;
import com.neo.accountapp_3.Setting.ProfilesetManagement;
import com.neo.accountapp_3.Setting.PwManagement;
import com.neo.accountapp_3.Sort.RegdateComparator;
import com.neo.accountapp_3.Sort.RegdateSort;
import com.neo.accountapp_3.Statistics.MonthStatisticsActivityPager;
import com.neo.accountapp_3.Statistics.SearchActivity;
import com.neo.accountapp_3.Statistics.TotalStatisticsActivity;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.CoupleConnectList;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.Session;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.User;
import com.neo.accountapp_3.db.WhoList;
import com.neo.accountapp_3.sociallogin.DeleteTokenTask;
import com.nhn.android.naverlogin.OAuthLogin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class mainActivity extends AppCompatActivity {

    WhoList oWhoList;
    UseKindsList oUseKindsList;
    PaymentKindsList oPaymentKindsList;
    Session oSession; //자동로그인을 위한 db

    int FrameIdx;

    AccountBook oAccountBook; //이렇게 arraylist를 불러와서 넘겨줄것
    CoupleConnectList oCoupleConnectList;
    User oUser;

    String couplekey = ""; //커플 키 - 로그아웃 하기 전까지 전체 고정키이다.
    String myid = ""; //나의 id - 로그아웃 하기 전까지 전체 고정키이다.
    String otherid = ""; //상대 id - 로그아웃 하기 전까지 전체 고정키이다.
    ArrayList<ArrayList<String>> Coupleinfo; //커플 정보
    ArrayList<ArrayList<String>> MyinfoList; //내정보
    ArrayList<ArrayList<String>> OtherinfoList; //상대정보

    ArrayList<ArrayList<ArrayList<String>>> AccountbookTotal;

    int selectyear;
    int selectmonth;
    int selectday;
    String selectdayofweek = ""; //요일

    int todayyear;
    int todaymonth;
    int todaydayOfMonth;
    String todaydayofweek = ""; //요일


    //각 실행을 하고 마지막에 연결된 메소드 오버라이딩할때 return해주는 값
    final static int REQUEST_TAKE_PHOTO = 1; //카메라열기
    final static int OPEN_GALLERY = 2; //사진첩 열기

    ImageView iv_photo;

    //카메라 열고 임시 경로
    private String mCurrentPhotoPath;

    //저장시 파일 이름
    private String SavePicturePath; //경로
    private String SavePictureName; //이름


    //광고 이미지 리스트
    int[] Adverimg = new int[] {R.drawable.adver0, R.drawable.adver1, R.drawable.adver2, R.drawable.adver3, R.drawable.adver4, R.drawable.adver5, R.drawable.adver6, R.drawable.adver7, R.drawable.adver8};

    ArrayList<ArrayList<ArrayList<String>>> AccountLIst_EachBox = new ArrayList<>();

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스

    private android.icu.util.Calendar Calendar;

    Handler AdverHandler, AlertHandler, LoadingHandler;
    Thread adverthread, alertthread, loadingthread;
    int AdverChk, AlertChk = 0;

    ImageView adverbox;

    java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        //main을 띄우고 로딩을 띄우는데 메인 로딩 페이지를 같이 띄우고 로딩만 사라져야함.
        //이놈을 핸들러 스레드 처리해야겟다.
        //Intent intent = new Intent(this, LoadingActivity.class);
        //startActivity(intent);



        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언

        oUser = new User(this);
        oCoupleConnectList = new CoupleConnectList(this);
        oAccountBook = new AccountBook(this);
        oWhoList = new WhoList(this);
        oUseKindsList = new UseKindsList(this);
        oPaymentKindsList = new PaymentKindsList(this);
        oSession = new Session(this);

        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());


        Log.d("TAG", "onCreate");

        try {
            changeView(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //밑에 메뉴 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        //핸들러 생성
        MakeHandler();

        //스레드 실행
        Log.d("스레드 여부", "스레드 실행");
        MakeAdverThread();
        MakeAlertThread();
    }

    //frame마다 필요함 기능을 연결해준다.
    public void Functionbinding(int idx) throws ParseException {

        //커플 정보 가져옴.
        Coupleinfo = oCoupleConnectList.GetOneinfo(couplekey);
        MyinfoList = oUser.GetMyinfo(myid);
        OtherinfoList = oUser.GetMyinfo(otherid);

        Log.d("커플 정보 arrylist", String.valueOf(Coupleinfo));
        Log.d("나의 정보 arrylist", String.valueOf(MyinfoList));
        Log.d("상대 정보 arrylist", String.valueOf(OtherinfoList));

        //오늘 날짜 구하기
        LocalDate nowdate = LocalDate.now();
        Log.d("오늘날짜", String.valueOf(nowdate)); //오늘 날짜
        todayyear = nowdate.getYear();
        todaymonth = nowdate.getMonth().getValue();
        todaydayOfMonth = nowdate.getDayOfMonth();

        switch (idx) {
            case 0 : //main
                //이미지 공간 생성여부
                adverbox = (ImageView)findViewById(R.id.adverbox);
                adverbox.setVisibility(View.VISIBLE);

                //디데이를 구한다.
                int dday = 0;
                if(!Coupleinfo.get(1).get(3).equals("-")){
                    dday = getDDay();
                    Log.d("디데이", String.valueOf(dday));
                }


                //내 이름, 상대 이름 표시
                TextView myname = (TextView)findViewById(R.id.myname);
                TextView othername = (TextView)findViewById(R.id.othername);
                myname.setText(MyinfoList.get(1).get(1));
                othername.setText(OtherinfoList.get(1).get(1));

                TextView lovetimeer = (TextView)findViewById(R.id.lovetimeer);
                lovetimeer.setText("사랑한지 "+dday+"일째");

                //이미지 썸네일 부분
                iv_photo = (ImageView)findViewById(R.id.mainimagebox);

                //이미지 경로 찾아서 바로 보여주기
                String filepath;
                if(SavePictureName == null){ //내 데이터 사용
                    //filepath = "/storage/emulated/0/DCIM/Camera/firstman.jpg";
                    filepath = Coupleinfo.get(1).get(2);
                    Log.d("데이터에 저장된 사진 이름", Coupleinfo.get(1).get(2));
                }else{ //저장된 경로 사용
                    filepath = SavePicturePath+SavePictureName;
                    Log.d("저장한 사진 이름",SavePictureName);
                }

                File imgFile = new  File(filepath);
                if(imgFile.exists()){ //사진이 경로에 없으면 기본값을 띄움.
                    iv_photo.setImageURI(Uri.parse(imgFile.getAbsolutePath()));
                }


                //퍼미션 설정 - 카메라 사용, 외부저장소
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면

                    //카메라 권한
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "카메라 권한 설정 완료");
                    } else {
                        Log.d("TAG", "카메라 권한 설정 요청");
                        ActivityCompat.requestPermissions(mainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    //외부 저장소 사용 권한
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Log.d("TAG","외부 저장소 사용을 위해 읽기/쓰기 필요");
                        }

                        requestPermissions(new String[]
                                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                2);  //마지막 인자는 체크해야될 권한 갯수

                    } else {
                        Log.d("TAG","외부 저장소 권한 승인되었음\"");
                    }
                }

                //앨범 실행
                FloatingActionButton albumbtn = findViewById(R.id.albumbtn);
                albumbtn.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(View view) {

                        //갤러리 열기

                        Intent intent = new Intent();
                        //기기 기본 갤러리 접근
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent,OPEN_GALLERY);

                    }
                });

                //카메라 실행 - 카메라로 찍고 사진을 등록
                FloatingActionButton camerabtn = findViewById(R.id.camerabtn);
                camerabtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        captureCamera();
                    }
                });


                break ;
            case 1 : //account
                //이미지 공간 생성여부
                adverbox = (ImageView)findViewById(R.id.adverbox);
                adverbox.setVisibility(View.GONE);

                //오늘날짜 요일구하기
                LocalDate date_ = LocalDate.of(nowdate.getYear(), nowdate.getMonth().getValue(), nowdate.getDayOfMonth());
                DayOfWeek dayOfWeek = date_.getDayOfWeek();
                int dayOfWeekNumber = dayOfWeek.getValue();
                Log.d("요일", String.valueOf(dayOfWeekNumber));
                Log.d("요일_korea", DayofweekKorea(dayOfWeekNumber));
                todaydayofweek = DayofweekKorea(dayOfWeekNumber);

                MakeCalRCV(todayyear, todaymonth, todaydayOfMonth);

                //툴바 설정
                Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
                setSupportActionBar(appbar);
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
                getSupportActionBar().setTitle("");


                //날짜 선택시 이벤트
                CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);//캘린더뷰
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() // 날짜 선택 이벤트
                {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
                    {
                        //요일구하기
                        LocalDate date_ = LocalDate.of(year, month+1, dayOfMonth);
                        DayOfWeek dayOfWeek = date_.getDayOfWeek();
                        int dayOfWeekNumber = dayOfWeek.getValue();
                        Log.d("요일", String.valueOf(dayOfWeekNumber));
                        Log.d("요일_korea", DayofweekKorea(dayOfWeekNumber));

                        //날짜 선택
                        selectyear = year;
                        selectmonth = month+1;
                        selectday = dayOfMonth;
                        selectdayofweek = DayofweekKorea(dayOfWeekNumber);

                        String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                        Log.d("선택한 날짜",date);
                        //whenDate.setText(date); // 선택한 날짜로 설정

                        MakeCalRCV_ck(view, selectyear, selectmonth, selectday);
                    }
                });

                //가계부 추가 설정
                FloatingActionButton fab = findViewById(R.id.addbtn);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("선택 년", String.valueOf(selectyear));
                        Log.d("선택 월", String.valueOf(selectmonth));
                        Log.d("선택 일", String.valueOf(selectday));
                        Log.d("선택 요일", selectdayofweek);

                        Log.d("오늘 년", String.valueOf(todayyear));
                        Log.d("오늘 월", String.valueOf(todaymonth));
                        Log.d("오늘 일", String.valueOf(todaydayOfMonth));
                        Log.d("오늘 요일", todaydayofweek);


                        Intent intent = new Intent(getApplicationContext(), AccountAdd.class);
                        if(selectyear == 0){ //선택한 날짜가 없으면 오늘 년을 보낸다
                            intent.putExtra("year", todayyear);
                        }else{
                            intent.putExtra("year", selectyear);
                        }

                        if(selectmonth == 0){//선택한 날짜가 없으면 오늘 달을 보낸다
                            intent.putExtra("month", todaymonth);
                        }else{
                            intent.putExtra("month", selectmonth);
                        }

                        if(selectday == 0){//선택한 날짜가 없으면 오늘 일을 보낸다
                            intent.putExtra("day", todaydayOfMonth);
                        }else{
                            intent.putExtra("day", selectday);
                        }

                        if(selectdayofweek.equals("")){//선택한 날짜가 없으면 오늘 일을 보낸다
                            intent.putExtra("dayofweek", todaydayofweek);
                        }else{
                            intent.putExtra("dayofweek", selectdayofweek);
                        }
                        startActivity(intent);
                    }
                });

                //전체 통계 버튼
                ImageButton statisticstotalbtn = (ImageButton)findViewById(R.id.statisticstotalbtn);
                statisticstotalbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), TotalStatisticsActivity.class);
                        startActivity(intent);
                    }
                });

                //검색 버튼
                ImageButton searchbtn = (ImageButton)findViewById(R.id.searchbtn);
                searchbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);
                    }
                });

                break ;
            case 2 : //alert
                //이미지 공간 생성여부
                adverbox = (ImageView)findViewById(R.id.adverbox);
                adverbox.setVisibility(View.VISIBLE);

                MakeCalRCV_alert();

                break ;
            case 3 : //setting
                //이미지 공간 생성여부
                adverbox = (ImageView)findViewById(R.id.adverbox);
                adverbox.setVisibility(View.VISIBLE);

                Button profileinfobtn = (Button)findViewById(R.id.profileinfobtn);
                profileinfobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfilesetManagement.class);
                        startActivity(intent);
                    }
                });
                Button accountsetbtn = (Button)findViewById(R.id.accountsetbtn);
                accountsetbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AccountKindsSetlist.class);
                        startActivity(intent);
                    }
                });
                Button logoutbtn = (Button)findViewById(R.id.logoutbtn);
                logoutbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oSession.Init();

                        //네이버 로그아웃
                        OAuthLogin naveroauthlogin = GlobalClass.getnaveroauth();
                        naveroauthlogin.logout(v.getContext());
                        //Toast.makeText(mainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        new DeleteTokenTask(v.getContext(), naveroauthlogin).execute();


                        //카카오 로그아웃
                        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                return null;
                            }
                        });

                        //구글 로그아웃
                        GoogleSignInClient googleoauthlogin = GlobalClass.getgoogleoauth();
                        googleoauthlogin.signOut();


                        //로그인창으로 이동
                        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거
                        Toast toast = Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT);
                        toast.show();
                        startActivity(intent);
                    }
                });
                Button pwchangebtn = (Button)findViewById(R.id.pwchangebtn);
                pwchangebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PwManagement.class);
                        startActivity(intent);
                    }
                });

                break ;
        }
    }

    //하단 메뉴 바 클릭시 이동
    //int funchk0, funchk1, funchk2, funchk3 = 0;
    int[] funtionchk = {0,0,0,0}; //한번만 클릭하도록 설정
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId())
            {
                case R.id.maintab:

                    try {
                        //한번만 클릭하도록 설정
                        if(funtionchk[0] == 0){
                            Log.d("통과? ", String.valueOf(0));
                            changeView(0);
                            FrameIdx = 0;

                            //상태 변경
                            for (int i = 0; i < funtionchk.length; i++){
                                if(i == 0){
                                    funtionchk[i] = 1;
                                }else{
                                    funtionchk[i] = 0;
                                }
                            }
                            Log.d("클릭 값 ", String.valueOf(funtionchk[0])+ String.valueOf(funtionchk[1]) +String.valueOf(funtionchk[2]) + String.valueOf(funtionchk[3]));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.accounttab:

                    try {
                        //한번만 클릭하도록 설정
                        if(funtionchk[1] == 0){
                            Log.d("통과? ", String.valueOf(1));
                            changeView(1);
                            FrameIdx = 1;

                            //상태 변경
                            for (int i = 0; i < funtionchk.length; i++){
                                if(i == 1){
                                    funtionchk[i] = 1;
                                }else{
                                    funtionchk[i] = 0;
                                }
                            }
                            Log.d("클릭 값 ", String.valueOf(funtionchk[0])+ String.valueOf(funtionchk[1]) +String.valueOf(funtionchk[2]) + String.valueOf(funtionchk[3]));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.alerttab:

                    try {
                        //한번만 클릭하도록 설정
                        if(funtionchk[2] == 0){
                            Log.d("통과? ", String.valueOf(2));
                            changeView(2);
                            FrameIdx = 2;

                            //상태 변경
                            for (int i = 0; i < funtionchk.length; i++){
                                if(i == 2){
                                    funtionchk[i] = 1;
                                }else{
                                    funtionchk[i] = 0;
                                }
                            }
                            Log.d("클릭 값 ", String.valueOf(funtionchk[0])+ String.valueOf(funtionchk[1]) +String.valueOf(funtionchk[2]) + String.valueOf(funtionchk[3]));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.settingtab:

                    try {
                        //한번만 클릭하도록 설정
                        if(funtionchk[3] == 0){
                            Log.d("통과? ", String.valueOf(3));
                            changeView(3);
                            FrameIdx = 3;

                            //상태 변경
                            for (int i = 0; i < funtionchk.length; i++){
                                if(i == 3){
                                    funtionchk[i] = 1;
                                }else{
                                    funtionchk[i] = 0;
                                }
                            }
                            Log.d("클릭 값 ", String.valueOf(funtionchk[0])+ String.valueOf(funtionchk[1]) +String.valueOf(funtionchk[2]) + String.valueOf(funtionchk[3]));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    }


    //메뉴에 따라 화면 변경 - onCreate
    private void changeView(int index) throws ParseException {
        // LayoutInflater 초기화.
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FrameLayout frame = (FrameLayout) findViewById(R.id.framebox) ;
        if (frame.getChildCount() > 0) {
            // FrameLayout에서 뷰 삭제.
            frame.removeViewAt(0);
        }

        // XML에 작성된 레이아웃을 View 객체로 변환.
        View view = null ;
        switch (index) {
            case 0 :
                view = inflater.inflate(R.layout.frammain, frame, false) ;
                break ;
            case 1 :
                view = inflater.inflate(R.layout.framaccount, frame, false) ;
                break ;
            case 2 :
                view = inflater.inflate(R.layout.framalert, frame, false) ;
                break ;
            case 3 :
                view = inflater.inflate(R.layout.framsetting, frame, false) ;
                break ;
        }

        // FrameLayout에 뷰 추가.
        if (view != null) {
            frame.addView(view) ;

            //해당 버튼 기능들을 연결해준다.
            Functionbinding(index);
        }
    }

    //메뉴에 따라 화면 변경 - onStart
    private void changeView_onstart(int index) throws ParseException {

        if (index == 0) { //main

        }else if(index == 1){ //account

            //편집, 추가 후 다시 activity로 돌아올때 전에 선택했던 날짜의 리스트가 새로고침 되어야한다.
            int year_;
            int month_;
            int day_;

            if(selectyear == 0){ //선택한 날짜가 없으면 오늘 년을 보낸다
                year_ = todayyear;
            }else{
                year_ = selectyear;
            }

            if(selectmonth == 0){//선택한 날짜가 없으면 오늘 달을 보낸다
                month_ = todaymonth;
            }else{
                month_ = selectmonth;
            }

            if(selectday == 0){//선택한 날짜가 없으면 오늘 일을 보낸다
                day_ = todaydayOfMonth;
            }else{
                day_ = selectday;
            }

            Log.d("onstart - 날짜", year_+"/"+month_+"/"+day_);

            MakeCalRCV(year_, month_, day_);

        }else if(index == 2){ //alert
            MakeCalRCV_alert();
        }else{ //setting

        }
    }
    //상단 actionbar 메뉴 icon 구성
    //account 일때만 적용이 됨
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.accountviewmenu, menu);
        return true;
    }

    //상단 actionbar 메뉴 클릭시 이동
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT);
        switch (item.getItemId()) {
            case R.id.maptab: //맵 지도 보기
                /*
                Log.d("선택 년", String.valueOf(selectyear));
                Log.d("선택 월", String.valueOf(selectmonth));
                Log.d("선택 일", String.valueOf(selectday));
                Log.d("선택 요일", selectdayofweek);

                Log.d("오늘 년", String.valueOf(todayyear));
                Log.d("오늘 월", String.valueOf(todaymonth));
                Log.d("오늘 일", String.valueOf(todaydayOfMonth));
                Log.d("오늘 요일", todaydayofweek);

                Intent intent1 = new Intent(getApplicationContext(), MonthStatisticsActivityPager.class);
                String Selectdays_ = "";
                if(selectyear == 0 && selectmonth == 0 && selectday == 0){ //선택한 날짜가 없으면 오늘 년을 보낸다
                    Selectdays_ = todayyear+"/"+todaymonth+"/"+todaydayOfMonth;
                    intent1.putExtra("Selectdate", Selectdays_);
                }else{
                    Selectdays_ = selectyear+"/"+selectmonth+"/"+selectday;
                    intent1.putExtra("Selectdate", Selectdays_);
                }
                startActivity(intent1);
                */


                Intent intent1 = new Intent(getApplicationContext(), AccountTotalMap.class);
                startActivity(intent1);
                break;
            case R.id.statisticsmonthtab: //월별 통계 보기

                Log.d("선택 년", String.valueOf(selectyear));
                Log.d("선택 월", String.valueOf(selectmonth));
                Log.d("선택 일", String.valueOf(selectday));
                Log.d("선택 요일", selectdayofweek);

                Log.d("오늘 년", String.valueOf(todayyear));
                Log.d("오늘 월", String.valueOf(todaymonth));
                Log.d("오늘 일", String.valueOf(todaydayOfMonth));
                Log.d("오늘 요일", todaydayofweek);

                Intent intentmonth = new Intent(getApplicationContext(), MonthStatisticsActivityPager.class);
                String Selectdays = "";
                if(selectyear == 0 && selectmonth == 0 && selectday == 0){ //선택한 날짜가 없으면 오늘 년을 보낸다
                    Selectdays = todayyear+"/"+todaymonth+"/"+todaydayOfMonth;
                    intentmonth.putExtra("Selectdate", Selectdays);
                }else{
                    Selectdays = selectyear+"/"+selectmonth+"/"+selectday;
                    intentmonth.putExtra("Selectdate", Selectdays);
                }
                startActivity(intentmonth);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //권한에 대한 응답이 있을때 작동하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("TAG", "onRequestPermissionsResult");
        int length = permissions.length;
        for (int i = 0; i < length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // 동의
                Log.d("MainActivity","권한 허용 : " + permissions[i]);
            }
        }
    }

    //카메라, 앨범을 들렸다 나오면 이게 실행 = 썸네일 만드는 부분
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("썸네일을 열다", "true");
        Log.d("bitmap1", String.valueOf(requestCode)); //클릭시 모드를 던져줌
        Log.d("bitmap2", String.valueOf(resultCode)); //0 접근해서 아무것도 안함 뒤로가기시, -1 사진을 클릭함
        try {
            //after capture
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: { //사진 촬영 후 사진 가져옴
                    if (resultCode == RESULT_OK) {

                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));

                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

//                            //사진해상도가 너무 높으면 비트맵으로 로딩
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inSampleSize = 8; //8분의 1크기로 비트맵 객체 생성
//                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                            Bitmap rotatedBitmap = null;
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }

                            //Rotate한 bitmap을 ImageView에 저장
                            iv_photo.setImageBitmap(rotatedBitmap);
                            saveImg();
                        }
                    }
                    break;
                }
                case OPEN_GALLERY: { //갤러리 열기 -> 선택한 사진 정보가 필요함.
                    if (resultCode == RESULT_OK) {
                        try{
                            //사진을 비티맵으로 가져와서 썸네일로 넣어줌
                            /*
                            InputStream is = getContentResolver().openInputStream(intent.getData());
                            Bitmap bm = BitmapFactory.decodeStream(is);
                            is.close();
                            iv_photo.setImageBitmap(bm);*/


                            // 절대 경로를 저장할 변수 선언
                            //String img_path;

                            //선택한이미지 썸네일 설정
                            iv_photo.setImageURI(intent.getData());

                            Log.d("사진에서 선택한 사진 경로", intent.getData().getPath());

                            //선택한 이미지 경로를 나눠서 데이터를 db에 저장
                            String[] patharray = intent.getData().getPath().split(":");
                            String[] patharray2 = patharray[1].split("/");

                            SavePicturePath = "/storage/emulated/0/"+patharray2[0]+"/"+patharray2[1]+"/";
                            SavePictureName = patharray2[2];

                            Log.d("사진에서 선택한 사진 경로 자세히", SavePicturePath+SavePictureName);

                            //선택한 사진 경로를 저장한다.
                            oCoupleConnectList.Editcoupledata(couplekey, SavePicturePath+SavePictureName, "coupleimg");

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    break;
                }
            }

        } catch (Exception e) {
            Log.w("TAG", "onActivityResult Error !", e);
        }
    }

    //카메라에 맞게 이미지 로테이션
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    //카메라를 열면서 내부 캐시에 저장.
    private void captureCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // 인텐트를 처리 할 카메라 액티비티가 있는지 확인
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // 촬영한 사진을 저장할 파일 생성
            File photoFile = null;

            try {
                //임시로 사용할 파일이므로 경로는 캐시폴더로
                File tempDir = getCacheDir();

                //임시촬영파일 세팅
                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String imageFileName = "Capture_" + timeStamp + "_"; //ex) Capture_20201206_

                File tempImage = File.createTempFile(
                        imageFileName,  /* 파일이름 */
                        ".jpg",         /* 파일형식 */
                        tempDir      /* 경로 */
                );

                // ACTION_VIEW 인텐트를 사용할 경로 (임시파일의 경로)
                mCurrentPhotoPath = tempImage.getAbsolutePath();

                photoFile = tempImage;

            } catch (IOException e) {
                //에러 로그는 이렇게 관리하는 편이 좋다.
                Log.w("TAG", "파일 생성 에러!", e);
            }

            //파일이 정상적으로 생성되었다면 계속 진행
            if (photoFile != null) {
                //Uri 가져오기
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                //인텐트에 Uri담기
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //인텐트 실행
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //이미지파일을 외부경로에 저장한다.
    private void saveImg() {

        try {
            //외부에 저장할 파일 경로
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File storageDir = new File(String.valueOf(path)+"/Camera"); //예) /storage/emulated/0/DCIM/Camera/Img20220114_5384915997394979020.jpg
            Log.d("외부경로", String.valueOf(path));

            //내부저장소 저장할 파일 경로
            //File storageDir = new File(getFilesDir() + "/capture");

            if (!storageDir.exists()) //폴더가 없으면 생성.
                storageDir.mkdirs();

            //촬영파일 이름 세팅
            @SuppressLint("SimpleDateFormat")
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Random random = new Random();
            random.setSeed(System.nanoTime());
            String nannum = Long.toString(random.nextLong());
            nannum = nannum.replace("-", "");
            String filename = "Img"+timeStamp + "_"+ nannum + ".jpg";

            //선택한 사진의 경로를 변수에 담는다.
            SavePicturePath = String.valueOf(path)+"/Camera/";
            SavePictureName = filename;

            //선택한 사진 경로를 저장한다.
            oCoupleConnectList.Editcoupledata(couplekey, SavePicturePath+SavePictureName, "coupleimg");
            //oCoupleConnectList.Couplelist.get(0).set(4, SavePicturePath+SavePictureName);

            // 기존에 있다면 삭제
            File file = new File(storageDir, filename);
            boolean deleted = file.delete();
            Log.w("TAG", "Delete Dup Check : " + deleted);
            FileOutputStream output = null;

            try {
                //이미지 파일 저장
                output = new FileOutputStream(file);
                BitmapDrawable drawable = (BitmapDrawable) iv_photo.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output); //해상도에 맞추어 Compress
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.e("TAG", "Captured Saved");
            Toast.makeText(this, "Capture Saved ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.w("TAG", "Capture Saving Error!", e);
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //여기서 메인 가계부 알림 설정 중 어디 페이지인지 알아야 한다.
        Log.d("페이지 명!!!!!!!", String.valueOf(FrameIdx));
        try {
            changeView_onstart(FrameIdx);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //커플 리스트 확인
         ArrayList<ArrayList<ArrayList<String>>> CoupleList = oCoupleConnectList.CoupleListRead();
         Log.d("Couple list", String.valueOf(CoupleList));

        //유저 리스트 확인
        ArrayList<ArrayList<ArrayList<String>>> UserList = oUser.UserRead();
         Log.d("User List", String.valueOf(UserList));

        ArrayList<ArrayList<ArrayList<String>>> AccountRead = oAccountBook.AccountRead();
        Log.d("Account List", String.valueOf(AccountRead));

        //Toast toast = Toast.makeText(getApplicationContext(),"onstart", Toast.LENGTH_SHORT);
        //toast.show();

        Log.d("TAG", "onStart");

        //광고 변경 : 가계부 아닌 곳에서만 실행
        //if(FrameIdx != 1){
        //    changeAdver();
       // }


        //핸들러 스레드 둘다 creat에서 처음에 만들고, onstart와 on stop에서는 변수를 바꿔주면서 멈추고 재실행을 반복한다.
       // AdverChk = 0;
        //AlertChk = 0;

        //Log.d("스레드 상태", AdverChk+ "|" + AlertChk);

        //Toast.makeText(GlobalClass, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast toast = Toast.makeText(getApplicationContext(),"onresume", Toast.LENGTH_SHORT);
        //toast.show();

        Log.d("TAG", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast toast = Toast.makeText(getApplicationContext(),"onpause", Toast.LENGTH_SHORT);
        //toast.show();

        Log.d("TAG", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast toast = Toast.makeText(getApplicationContext(),"onstop", Toast.LENGTH_SHORT);
        //toast.show();

        //Log.d("스레드 여부", "스레드 멈춤");
        //AdverChk = -1;
        //AlertChk = -1;

        //thread.interrupt();
        //Log.d("스레드 상태", AdverChk+ "|" + AlertChk);
        Log.d("TAG", "onStop");
        //Toast.makeText(GlobalClass, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast toast = Toast.makeText(getApplicationContext(),"onrestart", Toast.LENGTH_SHORT);
        //toast.show();

        Log.d("TAG", "onRestart");
        //AdverChk = 1;
        //AlertChk = 1;

        //Log.d("스레드 상태", AdverChk+ "|" + AlertChk);
        //Toast.makeText(GlobalClass, "onRestart", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast toast = Toast.makeText(getApplicationContext(),"ondestroy", Toast.LENGTH_SHORT);
        //toast.show();

        Log.d("TAG", "onDestroy");
    }
    //광고 변경
    private void changeAdver() {

        adverbox = (ImageView)findViewById(R.id.adverbox);
        int imageId = (int)(Math.random() * Adverimg.length);
        //Log.d("랜덤 숫자 ", String.valueOf(Math.random()));
        //Log.d("랜덤 숫자 ", String.valueOf(imageId));
        adverbox.setBackgroundResource(Adverimg[imageId]);
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

    private int getDDay(){
        //오늘 날짜 - 날짜정보로 변경
        Calendar = Calendar.getInstance();
        Calendar.set(todayyear, todaymonth-1, todaydayOfMonth);
        long today_millis = Calendar.getTimeInMillis()/(24*60*60*1000);
        Log.d("today_millis", String.valueOf(today_millis));


        //커플 날짜
        //Log.d("커플날짜", Coupleinfo.get(1).get(3));
        String[] Couplesplit = Coupleinfo.get(1).get(3).split("/");
        Calendar.set(Integer.parseInt(Couplesplit[0]), Integer.parseInt(Couplesplit[1])-1, Integer.parseInt(Couplesplit[2]));

        long coupleday_millis = Calendar.getTimeInMillis()/(24*60*60*1000);
        Log.d("coupleday_millis", String.valueOf(coupleday_millis));

        long d_day = today_millis - coupleday_millis;

        return (int) d_day;
    }


    public void MakeCalRCV(int year, int month, int dayofmonth){

        AccountbookTotal = oAccountBook.AccountRead();

        //큰 숫자가 위로가도록 정렬처리한다.
        ArrayList<Integer> sortlist_sd = new ArrayList<Integer>();
        for (int i = 0; i < AccountbookTotal.size(); i++){
            if(AccountbookTotal.get(i).get(1).get(0).equals(couplekey)) { //커플 고유번호 지정
                    //날짜 조건 연결
                    String[] datearray = AccountbookTotal.get(i).get(1).get(1).split("/");
                    if(datearray[0].equals(String.valueOf(year)) && datearray[1].equals(String.valueOf(month)) && datearray[2].equals(String.valueOf(dayofmonth))) {

                        sortlist_sd.add(Integer.parseInt(AccountbookTotal.get(i).get(0).get(0))); //키만 저장한다
                        Log.d("Sort list each", String.valueOf(AccountbookTotal.get(i).get(0).get(0)));

                    }
            }
        }
        Log.d("Sort list!!1", String.valueOf(sortlist_sd));
        Collections.sort(sortlist_sd, Collections.reverseOrder()); //내림차순 : 큰숫자가 위로
        Log.d("Sort list!!2", String.valueOf(sortlist_sd));


        AccountLIst_EachBox.clear();
        for (int i = 0; i < sortlist_sd.size(); i++){
            //sortlist_sd.get(i)
            //Log.d("정렬 값 하나씩", String.valueOf(sortlist_sd.get(i)));

            ArrayList<ArrayList<String>> accountone = oAccountBook.Getoneinfo(String.valueOf(sortlist_sd.get(i)));
            //Log.d("정렬 값 하나씩2", String.valueOf(accountone));
            ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
            AccountList_each.addAll(accountone);
            AccountLIst_EachBox.add(AccountList_each);
        }

        Log.d("Sort list total", String.valueOf(AccountLIst_EachBox));

        //가계부 리스트를 받아와서 리사이클러뷰를 만든다.
        RecyclerView ACCOUNTRecyclerView = (RecyclerView) findViewById(R.id.calendarRecyclerView);
        LinearLayoutManager linearManager_ACCOUNT = new LinearLayoutManager(this);
        ACCOUNTRecyclerView.setLayoutManager(linearManager_ACCOUNT);
        MyRecyclerAdapter ACCOUNTAdapter = new MyRecyclerAdapter(this);
        ACCOUNTAdapter.setlisttype(1);
        ACCOUNTAdapter.setRecycleList(AccountLIst_EachBox);
        ACCOUNTRecyclerView.setAdapter(ACCOUNTAdapter);

    }

    //달력 클릭시
    public void MakeCalRCV_ck(CalendarView view, int year, int month, int dayofmonth){
        AccountbookTotal = oAccountBook.AccountRead();

        //큰 숫자가 위로가도록 정렬처리한다.
        ArrayList<Integer> sortlist_sd = new ArrayList<Integer>();
        for (int i = 0; i < AccountbookTotal.size(); i++){
            if(AccountbookTotal.get(i).get(1).get(0).equals(couplekey)) { //커플 고유번호 지정
                    //날짜 조건 연결
                    String[] datearray = AccountbookTotal.get(i).get(1).get(1).split("/");
                    if(datearray[0].equals(String.valueOf(year)) && datearray[1].equals(String.valueOf(month)) && datearray[2].equals(String.valueOf(dayofmonth))) {

                        sortlist_sd.add(Integer.parseInt(AccountbookTotal.get(i).get(0).get(0))); //키만 저장한다
                        Log.d("Sort list each", String.valueOf(AccountbookTotal.get(i).get(0).get(0)));
                    }
            }
        }
        Log.d("Sort list!!1", String.valueOf(sortlist_sd));
        Collections.sort(sortlist_sd, Collections.reverseOrder()); //내림차순 : 큰숫자가 위로
        Log.d("Sort list!!2", String.valueOf(sortlist_sd));


        AccountLIst_EachBox.clear();
        for (int i = 0; i < sortlist_sd.size(); i++){
            //sortlist_sd.get(i)
            //Log.d("정렬 값 하나씩", String.valueOf(sortlist_sd.get(i)));

            ArrayList<ArrayList<String>> accountone = oAccountBook.Getoneinfo(String.valueOf(sortlist_sd.get(i)));
            //Log.d("정렬 값 하나씩2", String.valueOf(accountone));
            ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
            AccountList_each.addAll(accountone);
            AccountLIst_EachBox.add(AccountList_each);
        }

        Log.d("Sort list total", String.valueOf(AccountLIst_EachBox));

        //가계부 리스트를 받아와서 리사이클러뷰를 만든다.
        RecyclerView ACCOUNTRecyclerView = (RecyclerView) findViewById(R.id.calendarRecyclerView);
        LinearLayoutManager linearManager_ACCOUNT = new LinearLayoutManager(view.getContext());
        ACCOUNTRecyclerView.setLayoutManager(linearManager_ACCOUNT);
        MyRecyclerAdapter ACCOUNTAdapter = new MyRecyclerAdapter(view.getContext());
        ACCOUNTAdapter.setlisttype(1);
        ACCOUNTAdapter.setRecycleList(AccountLIst_EachBox);
        ACCOUNTRecyclerView.setAdapter(ACCOUNTAdapter);
    }

    public void MakeCalRCV_alert() throws ParseException {

        String currenttime = Makecurrenttime();//현재시간 불러오기

        AccountbookTotal = oAccountBook.AccountRead(); // ArrayList<ArrayList<ArrayList<String>>>

        //등록 날짜로 정렬한다.
        ArrayList<RegdateSort> sortlist_alert = new ArrayList<>();
        for (int i = 0; i < AccountbookTotal.size(); i++){
            if (AccountbookTotal.get(i).get(1).get(0).equals(couplekey)) { //커플 고유번호 조건
                String key = AccountbookTotal.get(i).get(0).get(0);
                String regdate = AccountbookTotal.get(i).get(1).get(7);
                sortlist_alert.add(new RegdateSort(key, regdate));
            }
        }

        //Log.d("원본 리스트", String.valueOf(sortlist_alert));

        //Collections.sort(sortlist_alert, new RegdateComparator()); 사용 x
        //Log.d("등록날짜 정렬 리스트 오름차순 ", String.valueOf(sortlist_alert));

        //미래의 시간이 앞으로 오도록
        Collections.sort(sortlist_alert, new RegdateComparator().reversed());
        //Log.d("등록날짜 정렬 리스트 내림차순 ", String.valueOf(sortlist_alert));


        //정렬된 list 순서대로 보여주며, 현재시간과 등록시간을 비교하여 d-day를 보여준다.
        AccountLIst_EachBox.clear();
        for (int i = 0; i < sortlist_alert.size(); i++){
            ArrayList<ArrayList<String>> accountone = oAccountBook.Getoneinfo(String.valueOf(sortlist_alert.get(i).key));
            //Log.d("하나의 가계부", String.valueOf(accountone));

            ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();

            String regtime = accountone.get(1).get(7); //등록 시간
            //Log.d("현재 오리진널 날짜",currenttime);
            //Log.d("등록 오리진널 날짜",regtime);

            Date dt1 = timeFormat.parse(currenttime); //현재시간
            Date dt2 = timeFormat.parse(regtime); //등록시간
            //Log.d("현재 시간 - 날짜 파싱", String.valueOf(dt1));
            //Log.d("등록 시간 - 날짜 파싱", String.valueOf(dt2));
            //dateFormat

            long diff = dt1.getTime() - dt2.getTime();
            long diffsec = diff / 1000; // 초 구하기
            long diffmin = diff / (60000); //분 구하기
            long diffhour = diff /  (3600000); // 시간 구하기
            long diffdays = diffsec /  (24*60*60); //일 구하기

            //Log.d("현재시간 - 가계부 등록시간 = 뺀값", String.valueOf(diff));
            //Log.d("현재시간 - 가계부 등록시간 = 몇초전", String.valueOf(diffsec));
            //Log.d("현재시간 - 가계부 등록시간 = 몇분전", String.valueOf(diffmin));
            //Log.d("현재시간 - 가계부 등록시간 = 몇시간전", String.valueOf(diffhour));
            //Log.d("현재시간 - 가계부 등록시간 = 몇일전", String.valueOf(diffdays));


            //맨뒤에 추가해서 넣어줄것
            // 60초까지는 몇초전으로 진행
            // 60초가 넘어가면 1분 전으로 진행
            // 60분이 넘어가면 1시간 전으로 진행
            // 24시간이 넘어가면 1day전으로 진행
            if(diffsec < 60){
                accountone.get(1).add(String.valueOf(diffsec));
                accountone.get(1).add("초 전");
            }else{ //60초 넘으면
                if(diffmin < 60){
                    accountone.get(1).add(String.valueOf(diffmin));
                    accountone.get(1).add("분 전");
                }else{ //60분 넘으면
                    if(diffhour < 24){
                        accountone.get(1).add(String.valueOf(diffhour));
                        accountone.get(1).add("시간 전");
                    }else{ //12시간이 넘으면
                        accountone.get(1).add(String.valueOf(diffdays));
                        accountone.get(1).add("일 전");
                    }
                }
            }

            AccountList_each.addAll(accountone);

            //Log.d("바뀐 데이터 확인~~1", String.valueOf(accountone));
            //Log.d("바뀐 데이터 확인~~2", String.valueOf(AccountList_each));
            AccountLIst_EachBox.add(AccountList_each);
        }

        //Log.d("Sort list total", String.valueOf(AccountLIst_EachBox));

        //가계부 리스트를 받아와서 리사이클러뷰를 만든다.
        RecyclerView ARTRecyclerView = (RecyclerView) findViewById(R.id.alertRecyclerView);
        LinearLayoutManager lineaManager_ART = new LinearLayoutManager(this);
        ARTRecyclerView.setLayoutManager(lineaManager_ART);
        MyRecyclerAdapter ARTAdapter = new MyRecyclerAdapter(this);
        ARTAdapter.setlisttype(3);
        ARTAdapter.setRecycleList(AccountLIst_EachBox);
        ARTRecyclerView.setAdapter(ARTAdapter);
    }

  public void MakeHandler(){

      //광고 이미지 변경 핸들러
      AdverHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
              // TODO : process device message.

              if(msg.what == 1){

                  //Log.d("화면 frame 값", String.valueOf(FrameIdx));
                  if(FrameIdx != 1) { //달력보기가 아닐때만 입력함

                      //Log.d("광고 이미지 랜덤 값", String.valueOf(msg.obj));
                      //Log.d("광고 이미지 키", String.valueOf(msg.arg1));
                      adverbox = (ImageView) findViewById(R.id.adverbox);
                      adverbox.setBackgroundResource(Adverimg[Integer.parseInt(String.valueOf(msg.obj))]);
                      //Log.d("mainpage", String.valueOf(msg.obj));
                  }
              }
          }
      } ;


      //알림 리사이클러뷰 리셋 핸들러
      AlertHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
              // TODO : process device message.

              if(msg.what == 1){
                  if(FrameIdx == 2) { //알림보기에서만 처리

                     // Log.d("알림 스레드 처리2 ", String.valueOf(msg.what));
                      //Log.d("알림 핸들러 키", String.valueOf(msg.arg1));
                      //Log.d("알림 핸들러 핸들러 ", String.valueOf(msg.obj));

                      try {
                          MakeCalRCV_alert();
                      } catch (ParseException e) {
                          e.printStackTrace();
                      }
                  }
              }
          }
      } ;

      //로딩페이지 리셋 핸들러
      /*
      LoadingHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
              // TODO : process device message.

              if(msg.what == 1){
                  if(FrameIdx == 2) { //알림보기에서만 처리

                      // Log.d("알림 스레드 처리2 ", String.valueOf(msg.what));
                      //Log.d("알림 핸들러 키", String.valueOf(msg.arg1));
                      Log.d("알림 핸들러 핸들러 ", String.valueOf(msg.obj));

                     // try {
                          //MakeCalRCV_alert();
                     // } catch (ParseException e) {
                     //     e.printStackTrace();
                     // }
                  }
              }
          }
      } ;

       */
  }

    //광고 이미지 변경을 위한 스레드를 작동한다. - 메인에서 스레들 계속 돌고 stop이 되면 멈춤 처리.
    public void MakeAdverThread(){
//thread.interrupt();
        adverthread = new Thread(){
            public void run(){
                    int seconds = 0;
                    while(true){
                        try{
                            seconds++;

                            int imageId = (int)(Math.random() * Adverimg.length-1);

                            //메서드로 획득한 메시지 객체에 보내고자 하는 데이터를 채우는 것
                            //메시지의 target이 핸들러 자신으로 지정된 Message 객체 리턴
                            Message msg = AdverHandler.obtainMessage();
                            msg.what = 1;
                            msg.obj = imageId;
                            AdverHandler.sendMessage(msg);

                            //Log.d("","광고 스레드 진행" + seconds);
                            Thread.sleep(5000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        //스레드 멈춤 안됨 수정바람
                        //if(AdverChk == -1){
                        //    Thread.interrupted();

                        //}else if(AdverChk == 1){
                            //alertthread.start();
                        //}
                    }
            }
        };
        adverthread.start();

        //adverthread.isInturrupted();
    }

    //알림 리스트 스레드 - 메인에서 스레들 계속 돌고 stop이 되면 멈춤 처리.
    public void MakeAlertThread(){

        alertthread = new Thread(){
            public void run(){

                    int seconds = 0;
                    while(true){
                        try{
                            seconds++;

                            //메서드로 획득한 메시지 객체에 보내고자 하는 데이터를 채우는 것
                            //메시지의 target이 핸들러 자신으로 지정된 Message 객체 리턴
                            Message msg = AlertHandler.obtainMessage();
                            msg.what = 1;
                            msg.obj = "알림 스레드 발동 "+seconds;
                            //Log.d("","!!");

                            // 메서드를 사용하여 메시지 객체를 수신 스레드에 보내는 것

                            AlertHandler.sendMessage(msg);


                            Thread.sleep(10000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        //스레드 멈춤 안됨 수정바람
                        //if(AlertChk == -1){
                        //    Thread.interrupted();
                        //}else if(AlertChk == 1){
                            //alertthread.start();
                        //}
                    }
            }
        };
        alertthread.start();
    }

    //알림 리스트 스레드 - 메인에서 스레들 계속 돌고 stop이 되면 멈춤 처리.
    /*
    public void MakeLoadingThread(){

        loadingthread = new Thread(){
            public void run(){

                int seconds = 0;
                while(true){
                    try{
                        seconds++;

                        //메서드로 획득한 메시지 객체에 보내고자 하는 데이터를 채우는 것
                        //메시지의 target이 핸들러 자신으로 지정된 Message 객체 리턴
                        Message msg = LoadingHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = "로딩 스레드 발동 "+seconds;
                        //Log.d("","!!");

                        // 메서드를 사용하여 메시지 객체를 수신 스레드에 보내는 것

                        LoadingHandler.sendMessage(msg);


                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //스레드 멈춤 안됨 수정바람
                  //  if(AlertChk == -1){
                   //     Thread.interrupted();
                   // }else if(AlertChk == 1){
                        //alertthread.start();
                   // }
                }
            }
        };
        loadingthread.start();
    }

     */

    //현재시간을 생성한다.
    public String Makecurrenttime(){

        Date todaydate = new Date();
        //Log.d("test 현재 시간", String.valueOf(todaydate));
        String todaytime = timeFormat.format(todaydate);
        //Log.d("test 현재 시간 변환", String.valueOf(todaytime));
        return todaytime;
    }
}
