package com.neo.accountapp_3.Statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Adapter.TStatisticsRecyclerAdapter;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;

/**
 * 전체 가계부 목록에 대해서 결재의 통계를 낸다.
 * wholist로 구별하는게 아니라
 * 카드결재를 누구걸로 했느냐로 계산한다.
 * paymentcategorylist 에 who를 기준으로 계산
 * */
public class TotalStatisticsActivity extends AppCompatActivity {
    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    String couplekey = "";
    String myid = "";
    String otherid = "";

    RadioButton mybtn, otherbtn, withbtn;
    RadioButton.OnClickListener clickListener;
    TextView totalpricetxt, capitaltxt, liabilitytxt;

    WhoList oWhoList;
    PaymentKindsList oPaymentKindsList;

    ArrayList<ArrayList<ArrayList<String>>> WhoConditionListBox = new ArrayList<>();; //카테고리 조건에 맞게 넣어주는 리스트
    String usertab = "";

    AccountBook oAccountBook;
    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;
    int TotalPrice = 0; //합계
    int CapitalPrice = 0; //자본
    int LiabilityPrice = 0; //부채


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_statistics);

        oWhoList = new WhoList(this);
        oPaymentKindsList = new PaymentKindsList(this);
        oAccountBook = new AccountBook(this);
        AccountSelectlist = oAccountBook.AccountRead();

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totalpricetxt = (TextView) findViewById(R.id.totalpricetxt);
        capitaltxt = (TextView) findViewById(R.id.capitaltxt);
        liabilitytxt = (TextView) findViewById(R.id.liabilitytxt);

        GlobalClass = (GlobalClass)getApplication(); //글로벌 클래스 선언
        couplekey = GlobalClass.getcouplecode();
        myid = GlobalClass.getmyid();
        otherid = GlobalClass.getotherid();
        Log.d("메인 커플키 ",GlobalClass.getcouplecode());


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

        mybtn = (RadioButton) findViewById(R.id.mybtn);
        mybtn.setText(WhoConditionListBox.get(0).get(1).get(1));
        otherbtn = (RadioButton) findViewById(R.id.otherbtn);
        otherbtn.setText(WhoConditionListBox.get(1).get(1).get(1));
        withbtn = (RadioButton) findViewById(R.id.withbtn);
        withbtn.setText(WhoConditionListBox.get(2).get(1).get(1));

        //인물 선택
        mybtn = findViewById(R.id.mybtn);
        otherbtn = findViewById(R.id.otherbtn);
        withbtn = findViewById(R.id.withbtn);

        usertab = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
        //인물 체크 처리
        mybtn.setChecked(true);
        /*
        if(usertab.equals("0")){
            mybtn.setChecked(true);
        }else if(usertab.equals("1")){
            otherbtn.setChecked(true);
        }else{
            withbtn.setChecked(true);
        }*/

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.mybtn:

                        usertab = WhoConditionListBox.get(0).get(0).get(0);//유저 탭
                        Log.d("나",usertab);
                        MakeTotalCapital();
                        MakePaymentCategoryRCView();
                        break;
                    case R.id.otherbtn:

                        usertab = WhoConditionListBox.get(1).get(0).get(0);//유저 탭
                        Log.d("상대",usertab);
                        MakeTotalCapital();
                        MakePaymentCategoryRCView();
                        break;
                    case R.id.withbtn:

                        usertab = WhoConditionListBox.get(2).get(0).get(0);//유저 탭
                        Log.d("우리",usertab);
                        MakeTotalCapital();
                        MakePaymentCategoryRCView();
                        break;
                }
            }
        };
        mybtn.setOnClickListener(clickListener);
        otherbtn.setOnClickListener(clickListener);
        withbtn.setOnClickListener(clickListener);


        //통합 금액을 계산
        MakeTotalCapital();

        MakePaymentCategoryRCView();


    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //전체 가계부 목록을 전체 자본을 계산한다.
    public void MakeTotalCapital(){

        CapitalPrice = 0; //자본 초기화

        //가계부 목록을 불러와서 계산
        for (int i = 0; i < AccountSelectlist.size(); i++){
            if(AccountSelectlist.get(i).get(1).get(0).equals(couplekey)){ //커플키 조건

                ArrayList<ArrayList<String>> paymentcategoryinfo = oPaymentKindsList.Getoneinfo(AccountSelectlist.get(i).get(1).get(6));
                if(!paymentcategoryinfo.isEmpty()) { //카테고리가 있을때만 계산 함/
                    if (paymentcategoryinfo.get(1).get(1).equals(usertab)) { //유저 조건

                        Log.d("각 금액", AccountSelectlist.get(i).get(1).get(2));

                        CapitalPrice += Integer.parseInt(AccountSelectlist.get(i).get(1).get(2));

                        Log.d("전체", String.valueOf(CapitalPrice));
                    }
                }
            }
        }

        //Log.d("CapitalPrice", String.valueOf(CapitalPrice));
        TotalPrice = CapitalPrice - LiabilityPrice;
        totalpricetxt.setText(String.valueOf(TotalPrice)); //합계
        capitaltxt.setText(String.valueOf(CapitalPrice)); //자본
        liabilitytxt.setText(String.valueOf(LiabilityPrice)); //부채
    }

    //결재 대분류 카테고리를 리사이클러뷰로 생성한다.
    public void MakePaymentCategoryRCView(){
        ArrayList<ArrayList<ArrayList<String>>> TotalStatistcslist = new ArrayList<>();


        //다 만들어서 보내면 어떨까?
        // 0 1 2 3 -> 해당 가격 입력
        // 0 1 2 3 -> 해당 결재 리스트

        String[] Paytablist = oPaymentKindsList.PaymentCategoryTab; //결재 탭 모록을 만든다.
        ArrayList<ArrayList<String>> PaymentTabCategorylistbox = new ArrayList<>(); //대분류 탭의 이름과 합친 금액을 저장
        ArrayList<ArrayList<ArrayList<ArrayList<String>>>> PaymentInnerCategorylistbox = new ArrayList<>(); //대분류 탭의 밑에 카테고리 리스트와 합친 금액을 저장

        for (int pt = 0; pt < Paytablist.length; pt++){
            ArrayList<String> paymenttab = new ArrayList<>();
            paymenttab.add(Paytablist[pt]);

            int EachPayPrice = 0;
            //현금에 해당한 금액을 저장한다.
            for (int a = 0; a < AccountSelectlist.size(); a++){
                if(AccountSelectlist.get(a).get(1).get(0).equals(couplekey)) { //커플키 조건
                    //결재 카테고리 연결
                    ArrayList<ArrayList<String>> paymentcategoryinfo = oPaymentKindsList.Getoneinfo(AccountSelectlist.get(a).get(1).get(6));
                    if(!paymentcategoryinfo.isEmpty()){ //카테고리가 있을때만 계산 함/
                        if (paymentcategoryinfo.get(1).get(1).equals(usertab)) { //유저 조건

                            //결재 탭 모아서 가격 계산
                            if (paymentcategoryinfo.get(1).get(2).equals(String.valueOf(pt))) {

                                //현금 은행 신용카드 대출 별로 가격을 모을때 쓴다.
                                //여기서 각 항목별로 금액을 더해주면 되겠다.
                                Log.d("체크!!!!!", String.valueOf(AccountSelectlist.get(a).get(1).get(2)));

                                //다 더한다.
                                EachPayPrice += Integer.parseInt(AccountSelectlist.get(a).get(1).get(2));

                            }
                        }
                    }
                }
            }
            paymenttab.add(String.valueOf(EachPayPrice)); //탭에 해당하는 카테고리의 가계부의 각 가격을 합친 리스트
            PaymentTabCategorylistbox.add(paymenttab);

            //리스트 만들때 조건 - 커플키, 유저, 카테고리 탭

            //결재 전체 카테고리 리스트를 가져와서 탭에 해당된 결재 카테고리 리스트를 가져온다.

            ArrayList<ArrayList<ArrayList<String>>> PaymentInnerCategorylist = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<String>>> paymentcategoryall = oPaymentKindsList.AlllistRead();
            for (int pl = 0; pl < paymentcategoryall.size(); pl++){
                if(paymentcategoryall.get(pl).get(1).get(0).equals(couplekey)) { //커플키 조건
                    if(paymentcategoryall.get(pl).get(1).get(1).equals(usertab)) { //유저 조건
                        if (paymentcategoryall.get(pl).get(1).get(2).equals(String.valueOf(pt))) { //결재 카테고리 탭 조건


                            //paymentcategoryall.get(pl).get(0).get(0) 키 값
                            int EachPayinnerPrice = 0;
                            //카테고리에 따른 가격을 합쳐서 추가한다.
                            for (int a = 0; a < AccountSelectlist.size(); a++){
                                if(AccountSelectlist.get(a).get(1).get(0).equals(couplekey)) { //커플키 조건

                                    //결재 키값이 맞게 비교
                                    if(AccountSelectlist.get(a).get(1).get(6).equals(paymentcategoryall.get(pl).get(0).get(0))){

                                        EachPayinnerPrice += Integer.parseInt(AccountSelectlist.get(a).get(1).get(2));
                                    }
                                }
                            }
                            paymentcategoryall.get(pl).get(1).add(String.valueOf(EachPayinnerPrice));

                            //여기다 합친 값을 추가해보자.
                            PaymentInnerCategorylist.add(paymentcategoryall.get(pl));

                        }
                    }
                }
            }
            Log.d("PaymentInnerCategorylist", String.valueOf(PaymentInnerCategorylist));//탭에 해당하는 카테고리 리스트
            PaymentInnerCategorylistbox.add(PaymentInnerCategorylist);
        }

        Log.d("PaymentTabCategorylistbox", String.valueOf(PaymentTabCategorylistbox)); //결재 카테고리 대분류 이름과 합친 가격
        Log.d("PaymentInnerCategorylistbox", String.valueOf(PaymentInnerCategorylistbox)); //결재 대분류 밑에 카테고리 리스트와 그 리스트의 합친 가격


        RecyclerView PaytabRCV = (RecyclerView)findViewById(R.id.PaytabRCV);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        TStatisticsRecyclerAdapter Adapter = new TStatisticsRecyclerAdapter(this); //내가만든 어댑터 선언
        PaytabRCV.setLayoutManager(linearManager); //리사이클러뷰 + 리니어 매니저 = 만들 형식

        //Adapter.setneeddata(couplekey, usertab); //필요한 데이터 넘김
        Adapter.setneeddata(PaymentInnerCategorylistbox); //필요한 데이터 넘김
        Adapter.setRecycleList(PaymentTabCategorylistbox); //arraylist 연결
        PaytabRCV.setAdapter(Adapter); //리사이클러뷰 위치에 어답터 세팅
    }

}