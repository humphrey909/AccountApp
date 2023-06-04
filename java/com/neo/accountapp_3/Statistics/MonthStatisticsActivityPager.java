package com.neo.accountapp_3.Statistics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.neo.accountapp_3.CunstomMenuDialog;
import com.neo.accountapp_3.Adapter.MonthStatisticsViewPagerAdapter;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.WhoList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MonthStatisticsActivityPager extends AppCompatActivity {
    private ViewPager2 viewPager2;
    int fragmentPosition = -1;
    ArrayList<Fragment> fragmentslistbox;

    String MonthCategorytab = "1"; //
    String Selectdate = ""; //달력에서 넘어온 날짜
    String[] SelectDateArray;

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    String couplekey = "";
    String myid = "";
    String otherid = "";

    TextView inputpricetxt, outputpricetxt, restpricetxt, monthstatisticTitle;
    int RestPrice = 0;
    int InputPrice = 0;
    int OutpricePrice = 0;

    SimpleDateFormat dateformat;
    Calendar calendar;
    String Getfirstday;
    String Getfinalday;

    RadioButton inputbtn, outputbtn;
    RadioButton.OnClickListener clickListener;
    String usekindstab = "1";

    AccountBook oAccountBook;
    UseKindsList oUseKindsList;
    WhoList oWhoList;
    PaymentKindsList oPaymentKindsList;

    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthstatisticsactivitypager);

        GlobalClass = (com.neo.accountapp_3.db.GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();

        oWhoList = new WhoList(this);
        oUseKindsList = new UseKindsList(this);
        oPaymentKindsList = new PaymentKindsList(this);
        oAccountBook = new AccountBook(this);
        AccountSelectlist = oAccountBook.AccountRead();

        //데이터 받음 - 선택되거나 오늘 날짜
        Intent intent = getIntent();
        Selectdate = intent.getExtras().getString("Selectdate");
        Log.d("intent Selectdate", String.valueOf(Selectdate));
        SelectDateArray = Selectdate.split("/");

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputpricetxt = (TextView) findViewById(R.id.inputpricetxt);
        outputpricetxt = (TextView) findViewById(R.id.outputpricetxt);
        restpricetxt = (TextView) findViewById(R.id.restpricetxt);
        monthstatisticTitle = (TextView) findViewById(R.id.monthstatisticTitle);

        //넘어온 달만 제목에 작성한다.
        monthstatisticTitle.setText(SelectDateArray[0]+"년 "+SelectDateArray[1]+"월별 통계");

        //오늘 날짜
        //todayorigin = new Date();
        dateformat = new SimpleDateFormat("yyyy/MM/dd");
        //today = dateformat.format(todayorigin); //오늘 날짜

        //선택한 날짜를 최소 최대 날짜로 데이터를 만든다.
        Makemonthterm(Selectdate);

        Log.d("이번달 처음 일", Getfirstday);
        Log.d("이번달 마지막 일", Getfinalday);

        //사용탭 선택
        inputbtn = findViewById(R.id.inputbtn);
        outputbtn = findViewById(R.id.outputbtn);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.inputbtn:

                        usekindstab = "0";//사용 카테고리 탭
                        fragmentmake(); //프래그먼트 재생성
                        break;
                    case R.id.outputbtn:

                        usekindstab = "1";//사용 카테고리 탭
                        fragmentmake(); //프래그먼트 재생성
                        break;
                }
            }
        };
        inputbtn.setOnClickListener(clickListener);
        outputbtn.setOnClickListener(clickListener);

        //기본설정
        usekindstab = "1";//사용 카테고리 탭
        if(usekindstab.equals("0")){
            inputbtn.setChecked(true);
        }else{
            outputbtn.setChecked(true);
        }


        //월에 지출액을 계산한다.
        try {
            MakeMonthCapital(Getfirstday, Getfinalday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fragmentmake(); //프래그먼트 재생성

        //final float pageMargin= getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        //final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);

        //페이지 애니메이션 효과를 주기위해
       // viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        /*viewPager2.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float myOffset = position * -(2 * pageOffset + pageMargin);
                if (viewPager2.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(viewPager2) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.setTranslationX(-myOffset);
                    } else {
                        page.setTranslationX(myOffset);
                    }
                } else {
                    page.setTranslationY(myOffset);
                }
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.changetab, menu);
        return true;
    }
    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.changebtn: //설정 변경

                CunstomMenuDialog octDialog = new CunstomMenuDialog(this, new CunstomMenuDialog.CustomDialogClickListener() {
                    @Override
                    public void peoplecatebtnCK() throws ParseException {
                        MonthCategorytab = "1"; //주체
                        fragmentmake();
                        Log.d("주체", String.valueOf(MonthCategorytab));
                        //MakemonthconditionRCV(Getfirstday, Getfinalday);
                    }

                    @Override
                    public void usecatebtnCK() throws ParseException {
                        MonthCategorytab = "2"; //사용 카테고리
                        fragmentmake();
                        Log.d("사용 카테고리", String.valueOf(MonthCategorytab));
                        //MakemonthconditionRCV(Getfirstday, Getfinalday);
                    }

                    @Override
                    public void pricecatebtnCK() throws ParseException {
                        MonthCategorytab = "3"; //결재 카테고리
                        fragmentmake();
                        Log.d("결재 카테고리", String.valueOf(MonthCategorytab));
                        //MakemonthconditionRCV(Getfirstday, Getfinalday);
                    }
                });
                octDialog.setCanceledOnTouchOutside(true);
                octDialog.setCancelable(true);
                octDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                octDialog.show();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //월 가계부 목록을 월 지출을 계산한다.

    //월 전체 지출 : 월에 쓴 전체 지출 금액
    //월 전체 입금 : 월에 쓴 전체 입금 금액
    //잔액 : 입금 - 지출
    public void MakeMonthCapital(String mindate, String maxdate) throws ParseException {

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

        InputPrice = 0; //입금 초기화
        OutpricePrice = 0; //지출 초기화

        //가계부 목록을 불러와서 계산
        for (int i = 0; i < AccountSelectlist.size(); i++){
            if(AccountSelectlist.get(i).get(1).get(0).equals(couplekey)){ //커플키 조건

                //한달만 기간 정할 것
                //가계부 날짜 파싱
                Date Selectdate = dateformat.parse(AccountSelectlist.get(i).get(1).get(1));

                //가계부 날짜가 최소 최대 날짜 사이에 있을때만 리스트를 생성
                if(Selectdate.after(date1) && Selectdate.before(date2) || Selectdate.equals(date1) || Selectdate.equals(date2)) {

                    ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(AccountSelectlist.get(i).get(1).get(5));
                    if(!usecategoryinfo.isEmpty()) { //카테고리가 있을때만 계산 함/
                        if (usecategoryinfo.get(1).get(1).equals("0")) { //사용 카테고리 조건 - 입금

                            Log.d("각 금액 - 입금", AccountSelectlist.get(i).get(1).get(2));

                            InputPrice += Integer.parseInt(AccountSelectlist.get(i).get(1).get(2));

                            Log.d("전체", String.valueOf(InputPrice));
                        }else if(usecategoryinfo.get(1).get(1).equals("1")){//사용 카테고리 조건 - 지출
                            Log.d("각 금액 - 지출", AccountSelectlist.get(i).get(1).get(2));

                            OutpricePrice += Integer.parseInt(AccountSelectlist.get(i).get(1).get(2));

                            Log.d("전체", String.valueOf(OutpricePrice));

                        }
                    }
                }
            }
        }

        Log.d("입금 월 금액", String.valueOf(InputPrice));
        Log.d("지출 월 금액", String.valueOf(OutpricePrice));
        Log.d("RestPrice", String.valueOf(InputPrice - OutpricePrice*-1));

        //Log.d("InputPrice", String.valueOf(InputPrice));
        RestPrice = InputPrice - OutpricePrice*-1;
        inputpricetxt.setText(String.valueOf(InputPrice)); //합계
        outputpricetxt.setText(String.valueOf(OutpricePrice)); //자본
        restpricetxt.setText(String.valueOf(RestPrice)); //부채
    }

    /*public void fragmentdatasend(String usekindstab){

        //프래그먼트로 값을 전달한다.
        Bundle bundle = new Bundle();
        bundle.putString("usekindstab", usekindstab);
        fragmentslistbox.get(Integer.parseInt(SelectDateArray[1])-1).setArguments(bundle);
    }*/



    public void fragmentmake(){

        //프래그먼트를 arraylist로 구성하는 부분분
        fragmentslistbox = new ArrayList<>();
        for (int i = 0; i < 12; i++){
            fragmentslistbox.add(MonthStatisticsFragment.newInstance(i, usekindstab, MonthCategorytab, couplekey, Selectdate));
        }

        Log.d(" ArrayList<Fragment>", String.valueOf(fragmentslistbox));

        //뷰페이지 위치 불러오고
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager2_container);

        //뷰페이지 위치에 퓨페이지어뎁터 연결
        MonthStatisticsViewPagerAdapter monthStatisticsViewPagerAdapter = new MonthStatisticsViewPagerAdapter(this, fragmentslistbox);
        viewPager2.setAdapter(monthStatisticsViewPagerAdapter);

        //처음에 위치할 위치를 지정해주는 부분이다.
        int targetposition = 0;
        if(fragmentPosition == -1){
            targetposition = Integer.parseInt(SelectDateArray[1])-1;
        }else{
            targetposition = fragmentPosition;
        }
        viewPager2.setCurrentItem(targetposition, false); //처음 포지션을 정해줄 수 있음.
        //viewPager2.setOffscreenPageLimit(12); // 상태를 유지할 페이지의 최대 갯수를 정함. 지정되지 않은 position 은 다시 oncreat 함.

        //프래그먼트로 값을 전달한다. - 기본 설정
        //Bundle bundle = new Bundle();
        //bundle.putString("usekindstab", usekindstab);
        //fragmentslistbox.get(Integer.parseInt(SelectDateArray[1])-1).setArguments(bundle);

        //스크롤시 호출되튼 콜백 기능
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if(positionOffsetPixels  == 0){
                    viewPager2.setCurrentItem(position);
                    Log.d("onPageScrolled", String.valueOf(position));
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                fragmentPosition = position;
                Log.d("onPageSelected", String.valueOf(position)); //선택한 프래그먼트 위치를 가져온다.

                //앱바의 이름을 변경한다.
                monthstatisticTitle.setText(SelectDateArray[0]+"년 "+(fragmentPosition+1)+"월별 통계");

                //선택된 달의 기간을 만든다.
                Selectdate = SelectDateArray[0]+"/"+(fragmentPosition+1)+"/"+SelectDateArray[2]; //선택한 월로 입력
                Makemonthterm(Selectdate);

                //입금 지출 잔액을 월에 맞게 다시 계산함
                try {
                    MakeMonthCapital(Getfirstday, Getfinalday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //그 달의 기간을 만든다. 최대 최소 날짜를 생성한다.
    public void Makemonthterm(String Selectdate){ //선택된 날짜
        calendar = Calendar.getInstance();

        //선택해서 넘어온 날짜 파싱 한고 달력에 입력해준다.
        Date Selectdate_pars = null;
        try {
            Selectdate_pars = dateformat.parse(Selectdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(Selectdate_pars);

        //이번달 최소 최대 날짜 불러오기
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Getfirstday = dateformat.format(calendar.getTime()); //첫번째 일

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); //이번달 마지막 날짜 지정
        Getfinalday = dateformat.format(calendar.getTime()); //마지막 일
    }
}