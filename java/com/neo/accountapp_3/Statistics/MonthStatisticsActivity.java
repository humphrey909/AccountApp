package com.neo.accountapp_3.Statistics;

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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Adapter.MStatisticsRecyclerAdapter;
import com.neo.accountapp_3.CunstomMenuDialog;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.Sort.PriceComparator;
import com.neo.accountapp_3.Sort.PriceSort;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.WhoList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * 월별 가계부 목록에 대해서 결재의 통계를 낸다.
 * wholist로 구별하는게 아니라
 * 카드결재를 누구걸로 했느냐로 계산한다.
 * paymentcategorylist 에 who를 기준으로 계산
 * */
public class MonthStatisticsActivity extends AppCompatActivity {

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    String couplekey = "";
    String myid = "";
    String otherid = "";

    Button inputbtn, outputbtn;
    Button.OnClickListener clickListener;
    String usekindstab = "";

    AccountBook oAccountBook;
    UseKindsList oUseKindsList;
    WhoList oWhoList;
    PaymentKindsList oPaymentKindsList;

    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트

    SimpleDateFormat dateformat;
    Calendar calendar;
    Date todayorigin;
    String today;

    String Getfirstday;
    String Getfinalday;

    int MonthCategorytab = 1; //

    TextView inputpricetxt, outputpricetxt, restpricetxt, monthstatisticTitle;
    int RestPrice = 0;
    int InputPrice = 0;
    int OutpricePrice = 0;

    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

    int selectyear = 0;
    int selectmonth = 0;
    int selectday = 0;
    String selectdayofweek = ""; //요일

    String Selectdate = ""; //달력에서 넘어온 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_statistics);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
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
        String[] datesplit = Selectdate.split("/");

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
        monthstatisticTitle.setText(datesplit[1]+"월별 통계");

        //오늘 날짜
        //todayorigin = new Date();
        dateformat = new SimpleDateFormat("yyyy/MM/dd");
        //today = dateformat.format(todayorigin); //오늘 날짜

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
                        Log.d("입금",usekindstab);

                        try {
                            MakemonthconditionRCV(Getfirstday, Getfinalday);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.outputbtn:
                        
                        usekindstab = "1";//사용 카테고리 탭
                        Log.d("지출",usekindstab);

                        try {
                            MakemonthconditionRCV(Getfirstday, Getfinalday);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        inputbtn.setOnClickListener(clickListener);
        outputbtn.setOnClickListener(clickListener);

        //기본설정
        usekindstab = "1";//사용 카테고리 탭
        try {
            MakemonthconditionRCV(Getfirstday, Getfinalday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //월에 지출액을 계산한다.
        try {
            MakeMonthCapital(Getfirstday, Getfinalday);
        } catch (ParseException e) {
            e.printStackTrace();
        }


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
                        MonthCategorytab = 1; //주체
                        Log.d("주체", String.valueOf(MonthCategorytab));
                        MakemonthconditionRCV(Getfirstday, Getfinalday);
                    }

                    @Override
                    public void usecatebtnCK() throws ParseException {
                        MonthCategorytab = 2; //사용 카테고리
                        Log.d("사용 카테고리", String.valueOf(MonthCategorytab));
                        MakemonthconditionRCV(Getfirstday, Getfinalday);
                    }

                    @Override
                    public void pricecatebtnCK() throws ParseException {
                        MonthCategorytab = 3; //결재 카테고리
                        Log.d("결재 카테고리", String.valueOf(MonthCategorytab));
                        MakemonthconditionRCV(Getfirstday, Getfinalday);
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


    //세가지의 조건 별로 분류한다.
    //주체 별, 사용 카테고리 별, 결재 카테고리 별
    public void MakemonthconditionRCV(String mindate, String maxdate) throws ParseException {
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

        ConditionListBox.clear();
        ArrayList<ArrayList<ArrayList<String>>> Selectlist;

        //카테고리에 따라서 월 통계를 만든다.
        if(MonthCategorytab == 1){ //주체 통계
            Selectlist = oWhoList.WholistRead();
            ArrayList<ArrayList<ArrayList<String>>> Calculatebox = new ArrayList<>();

            for (int i = 0; i < Selectlist.size(); i++) {
                if (Selectlist.get(i).get(1).get(0).equals(couplekey)) { //커플키 조건

                    ArrayList<ArrayList<String>> eachlist = new ArrayList<>();

                        //가격을 인물 카테고리에 맞게 계산한다.
                        int pricecalculate = 0;
                        ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist = oAccountBook.AccountRead(); //가계부 전체
                        for (int aidx = 0; aidx < AccountSelectlist.size(); aidx++) {


                            //가계부 날짜 파싱
                            Date Selectdate = dateformat.parse(AccountSelectlist.get(aidx).get(1).get(1));
                            Log.d("가계부 날짜 값", String.valueOf(Selectdate));

                            //가계부 날짜가 최소 최대 날짜 사이에 있을때만 리스트를 생성
                            if(Selectdate.after(date1) && Selectdate.before(date2) || Selectdate.equals(date1) || Selectdate.equals(date2)) {


                                //사용 카테고리 조건 - 입금 지출 조건 주기
                                ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(AccountSelectlist.get(aidx).get(1).get(5));
                                if (!usecategoryinfo.isEmpty()) { //카테고리가 있을때만 계산 함
                                    if (usecategoryinfo.get(1).get(1).equals(usekindstab)) { //입금 지출 구분

                                        //인물 카테고리의 키 값을 매칭하여 가계부리스트를 전부 가져와서 계산
                                        if (AccountSelectlist.get(aidx).get(1).get(3).equals(Selectlist.get(i).get(0).get(0))) {
                                            pricecalculate += Integer.parseInt(AccountSelectlist.get(aidx).get(1).get(2));
                                        }
                                    }
                                }
                            }
                        }
                        Selectlist.get(i).get(1).add(String.valueOf(pricecalculate)); //가격 계산한 값 추가

                        eachlist.addAll(Selectlist.get(i));
                        Log.d("변경된 값", String.valueOf(eachlist));
                        Calculatebox.add(eachlist);
                }
            }


            //ConditionListBox 가격 정렬 시작
            //계산된 금액으로 정렬한다.
            ArrayList<PriceSort> sortlist = new ArrayList<>();
            for (int s = 0; s < Calculatebox.size(); s++){
                String key = Calculatebox.get(s).get(0).get(0);
                int price = Integer.parseInt(Calculatebox.get(s).get(1).get(3));
                sortlist.add(new PriceSort(key, price));
            }

            Log.d("원본 리스트", String.valueOf(sortlist));

            //가격 정렬 - 입금 출금 다르게 설정
            if(usekindstab.equals("0")) { //입금
                //입금일때는 내림차순
                Collections.sort(sortlist, new PriceComparator().reversed());
                Log.d("가격 정렬 리스트 내림차순 ", String.valueOf(sortlist));
            }else{ //지출
                //지출일때는 오름차순
                Collections.sort(sortlist, new PriceComparator());
                Log.d("가격 정렬 리스트 오름차순 ", String.valueOf(sortlist));
            }


            //정렬된 리스트로 가계부를 다시 불러온다. 가격합계가 0인 가계부는 불러오지 않는다.
            for (int aidx = 0; aidx < sortlist.size(); aidx++){
                if(sortlist.get(aidx).price != 0) {
                    ArrayList<ArrayList<String>> accountone = oWhoList.Getoneinfo(String.valueOf(sortlist.get(aidx).key));
                    Log.d("하나의 객체", String.valueOf(accountone));
                    Log.d("", String.valueOf(sortlist.get(aidx).key));
                    Log.d("", String.valueOf(sortlist.get(aidx).price));

                    accountone.get(1).add(String.valueOf(sortlist.get(aidx).price));

                    ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
                    AccountList_each.addAll(accountone);
                    ConditionListBox.add(AccountList_each);
                }
            }

            //값이 없는 경우
            if(ConditionListBox.isEmpty()){
                ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
                ArrayList<String> listin1 = new ArrayList<>();
                ArrayList<String> listin2 = new ArrayList<>();
                listin1.add("-");
                listin2.add("데이터가 존재하지 않습니다. ");
                AccountList_each.add(listin1);
                AccountList_each.add(listin2);
                ConditionListBox.add(AccountList_each);
                Log.d("값 ", "값이 없습니다.");
            }

            Log.d("보여줄 리스트", String.valueOf(ConditionListBox));

        }else if(MonthCategorytab == 2){ //사용 카테고리 통계
            Selectlist = oUseKindsList.AlllistRead();

            ArrayList<ArrayList<ArrayList<String>>> Calculatebox = new ArrayList<>();

            for (int i = 0; i < Selectlist.size(); i++) {

                ArrayList<ArrayList<String>> eachlist = new ArrayList<>();

                if(Selectlist.get(i).get(1).get(1).equals(usekindstab)) { //사용 카테고리 조건

                    //가격을 사용 카테고리에 맞게 계산한다.
                    int pricecalculate = 0;
                    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist = oAccountBook.AccountRead(); //가계부 전체

                    for (int aidx = 0; aidx < AccountSelectlist.size(); aidx++){

                        //가계부 날짜 파싱
                        Date Selectdate = dateformat.parse(AccountSelectlist.get(aidx).get(1).get(1));
                        Log.d("가계부 날짜 값", String.valueOf(Selectdate));

                        //가계부 날짜가 최소 최대 날짜 사이에 있을때만 리스트를 생성
                        if(Selectdate.after(date1) && Selectdate.before(date2) || Selectdate.equals(date1) || Selectdate.equals(date2)) {

                            //사용 카테고리의 키 값을 매칭하여 가계부리스트를 전부 가져와서 계산
                            if (AccountSelectlist.get(aidx).get(1).get(5).equals(Selectlist.get(i).get(0).get(0))) {
                                pricecalculate += Integer.parseInt(AccountSelectlist.get(aidx).get(1).get(2));
                            }

                        }
                    }
                    Selectlist.get(i).get(1).add(String.valueOf(pricecalculate)); //가격 계산한 값 추가

                    eachlist.addAll(Selectlist.get(i));
                    Calculatebox.add(eachlist);
                }
            }

            //ConditionListBox 가격 정렬 시작
            //계산된 금액으로 정렬한다.
            ArrayList<PriceSort> sortlist = new ArrayList<>();
            for (int s = 0; s < Calculatebox.size(); s++){
                String key = Calculatebox.get(s).get(0).get(0);
                int price = Integer.parseInt(Calculatebox.get(s).get(1).get(3));
                sortlist.add(new PriceSort(key, price));
            }


            Log.d("원본 리스트", String.valueOf(sortlist));

            //가격 정렬 - 입금 출금 다르게 설정
            if(usekindstab.equals("0")) { //입금
                //입금일때는 내림차순
                Collections.sort(sortlist, new PriceComparator().reversed());
                Log.d("가격 정렬 리스트 내림차순 ", String.valueOf(sortlist));
            }else{ //지출
                //지출일때는 오름차순
                Collections.sort(sortlist, new PriceComparator());
                Log.d("가격 정렬 리스트 오름차순 ", String.valueOf(sortlist));
            }


            //정렬된 리스트로 가계부를 다시 불러온다. 가격합계가 0인 가계부는 불러오지 않는다.
            for (int aidx = 0; aidx < sortlist.size(); aidx++){
                if(sortlist.get(aidx).price != 0){
                    ArrayList<ArrayList<String>> accountone = oUseKindsList.Getoneinfo(String.valueOf(sortlist.get(aidx).key));
                    Log.d("하나의 객체", String.valueOf(accountone));
                    Log.d("", String.valueOf(sortlist.get(aidx).key));
                    Log.d("", String.valueOf(sortlist.get(aidx).price));

                    accountone.get(1).add(String.valueOf(sortlist.get(aidx).price));

                    ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
                    AccountList_each.addAll(accountone);
                    ConditionListBox.add(AccountList_each);
                }
            }

            //값이 없는 경우
            if(ConditionListBox.isEmpty()){
                ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
                ArrayList<String> listin1 = new ArrayList<>();
                ArrayList<String> listin2 = new ArrayList<>();
                listin1.add("-");
                listin2.add("데이터가 존재하지 않습니다. ");
                AccountList_each.add(listin1);
                AccountList_each.add(listin2);
                ConditionListBox.add(AccountList_each);
                Log.d("값 ", "값이 없습니다.");
            }

            Log.d("보여줄 리스트", String.valueOf(ConditionListBox));
        }else{ //결재 카테고리 통계
            Selectlist = oPaymentKindsList.AlllistRead();

            ArrayList<ArrayList<ArrayList<String>>> Calculatebox = new ArrayList<>();

            for (int i = 0; i < Selectlist.size(); i++) {
                if (Selectlist.get(i).get(1).get(0).equals(couplekey)) { //커플키 조건

                    ArrayList<ArrayList<String>> eachlist = new ArrayList<>();


                    //가격을 결재 카테고리에 맞게 계산한다.
                    int pricecalculate = 0;
                    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist = oAccountBook.AccountRead(); //가계부 전체
                    for (int aidx = 0; aidx < AccountSelectlist.size(); aidx++) {

                        //가계부 날짜 파싱
                        Date Selectdate = dateformat.parse(AccountSelectlist.get(aidx).get(1).get(1));
                        Log.d("가계부 날짜 값", String.valueOf(Selectdate));

                        //가계부 날짜가 최소 최대 날짜 사이에 있을때만 리스트를 생성
                        if(Selectdate.after(date1) && Selectdate.before(date2) || Selectdate.equals(date1) || Selectdate.equals(date2)) {

                            //사용 카테고리 조건 - 입금 지출 조건 주기
                            ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(AccountSelectlist.get(aidx).get(1).get(5));
                            if (!usecategoryinfo.isEmpty()) { //카테고리가 있을때만 계산 함
                                if (usecategoryinfo.get(1).get(1).equals(usekindstab)) { //입금 제출 구분

                                    //결재 카테고리의 키 값을 매칭하여 가계부리스트를 전부 가져와서 계산
                                    if (AccountSelectlist.get(aidx).get(1).get(6).equals(Selectlist.get(i).get(0).get(0))) {
                                        pricecalculate += Integer.parseInt(AccountSelectlist.get(aidx).get(1).get(2));
                                    }
                                }
                            }
                        }
                    }
                    Selectlist.get(i).get(1).add(String.valueOf(pricecalculate)); //가격 계산한 값 추가

                    eachlist.addAll(Selectlist.get(i));
                    Calculatebox.add(eachlist);
                }
            }

            //ConditionListBox 가격 정렬 시작
            //계산된 금액으로 정렬한다.
            ArrayList<PriceSort> sortlist = new ArrayList<>();
            for (int s = 0; s < Calculatebox.size(); s++){
                String key = Calculatebox.get(s).get(0).get(0);
                int price = Integer.parseInt(Calculatebox.get(s).get(1).get(4));
                sortlist.add(new PriceSort(key, price));
            }


            Log.d("원본 리스트", String.valueOf(sortlist));

            //가격 정렬 - 입금 출금 다르게 설정
            if(usekindstab.equals("0")) { //입금
                //입금일때는 내림차순
                Collections.sort(sortlist, new PriceComparator().reversed());
                Log.d("가격 정렬 리스트 내림차순 ", String.valueOf(sortlist));
            }else{ //지출
                //지출일때는 오름차순
                Collections.sort(sortlist, new PriceComparator());
                Log.d("가격 정렬 리스트 오름차순 ", String.valueOf(sortlist));
            }


            //정렬된 리스트로 가계부를 다시 불러온다. 가격합계가 0인 가계부는 불러오지 않는다.
            for (int aidx = 0; aidx < sortlist.size(); aidx++){
                if(sortlist.get(aidx).price != 0){
                    ArrayList<ArrayList<String>> accountone = oPaymentKindsList.Getoneinfo(String.valueOf(sortlist.get(aidx).key));
                    Log.d("하나의 객체", String.valueOf(accountone));
                    Log.d("", String.valueOf(sortlist.get(aidx).key));
                    Log.d("", String.valueOf(sortlist.get(aidx).price));

                    accountone.get(1).add(String.valueOf(sortlist.get(aidx).price));

                    ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
                    AccountList_each.addAll(accountone);
                    ConditionListBox.add(AccountList_each);
                }
            }
            //값이 없는 경우
            if(ConditionListBox.isEmpty()){
                ArrayList<ArrayList<String>> AccountList_each = new ArrayList<>();
                ArrayList<String> listin1 = new ArrayList<>();
                ArrayList<String> listin2 = new ArrayList<>();
                listin1.add("-");
                listin2.add("데이터가 존재하지 않습니다. ");
                AccountList_each.add(listin1);
                AccountList_each.add(listin2);
                ConditionListBox.add(AccountList_each);
                Log.d("값 ", "값이 없습니다.");
            }

            Log.d("보여줄 리스트", String.valueOf(ConditionListBox));
        }

        Log.d("카테고리 조건 리스트 완성", String.valueOf(ConditionListBox));

        RecyclerView RecyclerView = (RecyclerView)findViewById(R.id.MonthstatisticsRCV);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        MStatisticsRecyclerAdapter Adapter = new MStatisticsRecyclerAdapter(this); //내가만든 어댑터 선언
        RecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 리니어 매니저 = 만들 형식

         Adapter.setneeddata(couplekey, usekindstab, mindate, maxdate); //필요한 데이터 넘김
        Adapter.setlisttype(MonthCategorytab); //1 유저, 2 사용, 3 결재
        Adapter.setRecycleList(ConditionListBox); //arraylist 연결
        RecyclerView.setAdapter(Adapter); //리사이클러뷰 위치에 어답터 세팅
    }
}