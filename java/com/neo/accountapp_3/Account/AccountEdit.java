package com.neo.accountapp_3.Account;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.neo.accountapp_3.Map.AccountTotalMap;
import com.neo.accountapp_3.Map.MapActivityPager;
import com.neo.accountapp_3.PaymentCategory.PaymentKindsSelect;
import com.neo.accountapp_3.PeopleCategory.WhoSelect;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.UseCategory.UseKindsSelect;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.WhoList;
import com.neo.accountapp_3.mainActivity;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class AccountEdit extends AppCompatActivity {

    AccountBook oAccountBook;

    String accountkey; //가계부 id
    ArrayList<ArrayList<String>> acountinfo; //가계부 값들
    ArrayList<String> acountvalue; //가계부 값들

    WhoList oWhoList;
    UseKindsList oUseKindsList;
    PaymentKindsList oPaymentKindsList;


    private Calendar Calendar;
    private int Calendaryear;
    private int Calendarmonth;
    private int Calendarday;
    private String dayofweektxt = "";

    //view 부분에 해당된 변수
    TextView datetxt;
    TextView whotxt;
    EditText pricetxt;
    TextView locationtxt;
    String UseKindsvalue;
    TextView usekindstxt;
    EditText Explaintxt;
    String Paymentkindsvalue;
    TextView paymentkindstxt;

    //변경될 데이터를 형식대로 저장하기 위한 변수
    String StaticValueCoupleKey;
    String ChangeValueDate;
    String ChangeValueWho;
    int ChangeValuePrice;
    String ChangeValueUsecategorykey; //사용 카테고리 키
    String ChangeValuePaymentcategorykey; //결재 카테고리 키

    //콤마 찍기 위한 변수
    int price; //금액
    private String Decimalresult="";
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    //select activity를 요청하고 선택한 값을 받아올 때 사용
    public int REQUESTCODE = 100;// 100 101 102
    public int RESULTCODE1 = 1;
    public int RESULTCODE2 = 2;
    public int RESULTCODE3 = 3;
    public int RESULTCODE4 = 4;

    String ChangeValuePlaceName = "";
    String ChangeValuePlace_X = "";
    String ChangeValuePlace_Y = "";

    String accesstype;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        oAccountBook = new AccountBook(this);
        oWhoList = new WhoList(this);
        oUseKindsList = new UseKindsList(this);
        oPaymentKindsList = new PaymentKindsList(this);

        Intent intent = getIntent();
        accountkey = intent.getExtras().getString("accountkey"); //선택한 가계부 키값
        accesstype = intent.getExtras().getString("accesstype"); //타입 설정
        acountinfo =  oAccountBook.Getoneinfo(accountkey); //선택한 가계부 정보
        acountvalue =  acountinfo.get(1); //선택한 가계부 내용



        Log.d("edit 가계부 키", String.valueOf(accountkey));
        Log.d("edit 가계부 내용들", String.valueOf(acountvalue));

        //커플고유키, 날짜, 가격, 구매한 사람, 내역, 사용종류 탭, 사용종류, 결재종류탭, 결재종류

        //커플고유키, 날짜, 가격, 구매한 사람, 내역, 사용 카테고리 키, 결재 카테고리 키
        Log.d("", (String) acountvalue.get(0));
        Log.d("", (String) acountvalue.get(1));
        Log.d("", (String) acountvalue.get(2));
        Log.d("", (String) acountvalue.get(3));
        Log.d("", (String) acountvalue.get(4));

        Log.d("", (String) acountvalue.get(5));//사용 카테고리 키
        Log.d("", (String) acountvalue.get(6));//결재 카테고리 키

        //Log.d("", (String) acountvalue.get(7));
        //Log.d("", (String) acountvalue.get(8));

        //변경될 데이터를 형식대로 저장하기 위한 변수
        StaticValueCoupleKey = acountvalue.get(0);
        ChangeValueDate = acountvalue.get(1);
        ChangeValueWho = acountvalue.get(3);

        ChangeValuePrice = Integer.parseInt(acountvalue.get(2));

        ChangeValueUsecategorykey = acountvalue.get(5);//사용 카테고리 키
        ChangeValuePaymentcategorykey = acountvalue.get(6);//결재 카테고리 키

        ChangeValuePlaceName = acountvalue.get(8);
        ChangeValuePlace_X = acountvalue.get(9);
        ChangeValuePlace_Y = acountvalue.get(10);


        //받은 날짜 자르기
        String[] Datesplit = ((String) acountvalue.get(1)).split("/");
        String Datemake = Datesplit[0] + "년 " + Datesplit[1] + "월 " + Datesplit[2] + "일(" + Datesplit[3] + ")";

        //보여지는 텍스트 부분에 입력
        datetxt = (TextView) findViewById(R.id.datetxt);
        datetxt.setText(Datemake);

        whotxt = (TextView) findViewById(R.id.whotxt);
        whotxt.setText(oWhoList.Getoneinfo((String) acountvalue.get(3)).get(1).get(1));

        pricetxt = (EditText) findViewById(R.id.pricetxt);
        locationtxt = (TextView) findViewById(R.id.locationtxt);
        locationtxt.setText(acountvalue.get(8));

        //지출일때만 -붙일 것
        ArrayList<ArrayList<String>> usecategoryinfo_ = oUseKindsList.Getoneinfo(ChangeValueUsecategorykey);
        String pricepars;
        if(usecategoryinfo_.get(1).get(1).equals("1")) { // 지출이면 - 붙여서 부호 없애줌
            pricepars = String.valueOf(Integer.parseInt(acountvalue.get(2))*-1);
        }else{ //입금이면 그대로
            pricepars = String.valueOf(Integer.parseInt(acountvalue.get(2)));
        }
        String Decimalvalue = decimalFormat.format(Double.parseDouble(pricepars.replaceAll(",",""))); //콤마 붙여주는 부분
        pricetxt.setText((String) Decimalvalue);

        usekindstxt = (TextView) findViewById(R.id.usekindstxt);
        ArrayList< ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo((String) acountvalue.get(5));
        UseKindsvalue = oUseKindsList.GetCategorytab(Integer.parseInt(usecategoryinfo.get(1).get(1))) + ">" + usecategoryinfo.get(1).get(2);
        usekindstxt.setText(UseKindsvalue);

        Explaintxt = (EditText) findViewById(R.id.explaintxt);
        Explaintxt.setText((String) acountvalue.get(4));

        paymentkindstxt = (TextView) findViewById(R.id.paymentkindstxt);

        ArrayList< ArrayList<String>> paymentcategoryinfo = oPaymentKindsList.Getoneinfo((String) acountvalue.get(6));
        if(paymentcategoryinfo.isEmpty()){ //값이 없다면..??
            Paymentkindsvalue = "카테고리 재 선택";
        }else{
            Paymentkindsvalue = oWhoList.Getoneinfo((String) paymentcategoryinfo.get(1).get(1)).get(1).get(1) + ">" + oPaymentKindsList.GetCategorytab(Integer.parseInt(paymentcategoryinfo.get(1).get(2))) + ">" + paymentcategoryinfo.get(1).get(3);
        }
        paymentkindstxt.setText(Paymentkindsvalue);

        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //날짜 수정클릭시 달력 처음날짜 지정하기
        Calendar = Calendar.getInstance();
        Calendar.set(Integer.parseInt(Datesplit[0]), Integer.parseInt(Datesplit[1])-1, Integer.parseInt(Datesplit[2]));
        Calendaryear = Calendar.get(Calendar.YEAR);
        Calendarmonth = Calendar.get(Calendar.MONTH);
        Calendarday = Calendar.get(Calendar.DAY_OF_MONTH);

        //날짜 수정
        datetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        //요일구하기
                        LocalDate date_ = LocalDate.of(year, month+1, dayOfMonth);
                        DayOfWeek dayOfWeek = date_.getDayOfWeek();
                        int dayOfWeekNumber = dayOfWeek.getValue();
                        dayofweektxt = DayofweekKorea(dayOfWeekNumber);

                        //저장하기위한 형태로 변형함
                        String Datemake_save = year + "/" + (month+1) + "/" + dayOfMonth + "/" + dayofweektxt;
                        ChangeValueDate = Datemake_save;

                        //보여주기 위한 형태로 변형
                        String Datemake = year + "년 " + (month+1) + "월 " + dayOfMonth + "일(" + dayofweektxt + ")";
                        datetxt.setText(Datemake);

                        //마지막에 입력한 날짜를 저장해준다. 다시 달력을 열었을때 값을 고정시키기 위함.
                        Calendar.set(year, (month), dayOfMonth);
                        Calendaryear = Calendar.get(Calendar.YEAR);
                        Calendarmonth = Calendar.get(Calendar.MONTH);
                        Calendarday = Calendar.get(Calendar.DAY_OF_MONTH);
                    }
                }, Calendaryear, Calendarmonth, Calendarday);

                //달력 열기
                if (datetxt.isClickable()) {
                   datePickerDialog.show();
                }
            }
        });

        //구매한 사람 변경
        whotxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("","구매한사람");
                Intent intent = new Intent(getApplicationContext(), WhoSelect.class);
                startActivityForResult(intent, REQUESTCODE);
            }
        });

        //구매한 사람 변경
        usekindstxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("","사용 종류!!!");
                Intent intent = new Intent(getApplicationContext(), UseKindsSelect.class);
                startActivityForResult(intent, REQUESTCODE);
            }
        });

        //구매한 사람 변경
        paymentkindstxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("","지불 종류!!!");
                Intent intent = new Intent(getApplicationContext(), PaymentKindsSelect.class);
                startActivityForResult(intent, REQUESTCODE);
            }
        });



        //가격 콤마 찍어주기
        pricetxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력난에 변화가 있을때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때

                Log.d("charsequence", String.valueOf(s)); //텍스트 그대로 가져옴
                if(s.toString().length() > 0){
                    //길이가 1개인데 -일때
                    //if(s.toString().equals("-")){
                        //pricetxt.setText("0");
                    //}else{
                        price = Integer.parseInt(s.toString().replaceAll(",",""));
                        ChangeValuePrice = price;
                   // }
                }



                //특수문자 제거, 공백제거, 숫자 수 제한 필요
                Log.d("결과", String.valueOf(Decimalresult)); //콤마 삭제

                //콤마 붙이기
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(Decimalresult)){ //빈값이 아닐때 변환된 값과 같지 않을때
                    Decimalresult = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",",""))); //콤마 붙여주는 부분
                    pricetxt.setText(Decimalresult); //변환된 값을 저장
                    pricetxt.setSelection(Decimalresult.length()); //숫자를 입력하면 그 숫자만큼 커서 위치를 설정
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //장소 변경
        locationtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("","위치 변경");
                Intent intent = new Intent(getApplicationContext(), MapActivityPager.class);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
    }

    //action tab 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.accounteditmenu, menu);
        return true;
    }

    //action tab 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //뒤로가기
                finish();
                break;
            case R.id.deletetab: //삭제
                finish();
                oAccountBook.Deleteone(accountkey);
                break;
            case R.id.savetab: //수정 완료


                Log.d("가계부 고유 키", String.valueOf(accountkey)); //데이터 변경 전값을 가져와야함

                Log.d("커플 키", StaticValueCoupleKey); //데이터 변경 전값을 가져와야함
                Log.d("변경된 날짜", ChangeValueDate); //데이터 변경 전값을 가져와야함
                Log.d("변경된 구매인", ChangeValueWho); //데이터 변경 전값을 가져와야함
                Log.d("변경된 가격", String.valueOf(ChangeValuePrice)); //데이터 변경 전값을 가져와야함
                //Log.d("변경된 설명", ChangeValueExplain); //데이터 변경 전값을 가져와야함
                //Log.d("변견된 가격", String.valueOf(Pricetxt.getText())); //그대로 저장가능
                Log.d("변경된 설명", String.valueOf(Explaintxt.getText())); //그대로 저장가능

                Log.d("변경된 사용 카테고리 키", String.valueOf(ChangeValueUsecategorykey)); //그대로 저장가능
                Log.d("변경된 결재 카테고리 키", String.valueOf(ChangeValuePaymentcategorykey)); //그대로 저장가능
                Log.d("변경된 ChangeValuePlaceName", ChangeValuePlaceName); //그대로 저장가능
                Log.d("변경된 ChangeValuePlace_X", ChangeValuePlace_X); //그대로 저장가능
                Log.d("변경된 ChangeValuePlace_Y", ChangeValuePlace_Y); //그대로 저장가능

                //Log.d("변경된 사용 종류 탭", ChangeValueUseKindsTab); //데이터 변경 전값을 가져와야함
                //Log.d("변경된 사용 종류", ChangeValueUseKinds); //데이터 변경 전값을 가져와야함
                //Log.d("변경된 지불 종류 탭", ChangeValuePaymentKindsTab); //데이터 변경 전값을 가져와야함
                //Log.d("변경된 지불 종류", ChangeValuePaymentKinds); //데이터 변경 전값을 가져와야함

                //ChangeValueUsecategorykey를 풀어서 입금인지 지출인지 결정해서 + - 값을 입혀준다.
                ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(ChangeValueUsecategorykey);
                Log.d("사용 카테고리 정보", String.valueOf(usecategoryinfo));
                Log.d("사용 카테고리 탭", String.valueOf(usecategoryinfo.get(1).get(1)));
                Log.d("가계부 금액", String.valueOf(ChangeValuePrice));
                if(usecategoryinfo.get(1).get(1).equals("1")) { // 1이면 지출 -
                    if(ChangeValuePrice > 0){
                        ChangeValuePrice = ChangeValuePrice *-1;
                    }
                }

                //가계부 수정 처리한다.
                oAccountBook.SetAccountEdit(accountkey,StaticValueCoupleKey,ChangeValueDate,ChangeValueWho,ChangeValuePrice,String.valueOf(Explaintxt.getText()),ChangeValueUsecategorykey,ChangeValuePaymentcategorykey, ChangeValuePlaceName, ChangeValuePlace_X, ChangeValuePlace_Y);
                finish(); //내 액티비티 삭제


                if(accesstype.equals("2")){ //지도에서 접근
                    //종료만 시키는 형태임.
                    //종료시키고 지도에서 띄웠다면. 그 지도페이지를 다시 띄운다.
                    Intent intent = new Intent(this, AccountTotalMap.class);
                    ((AccountTotalMap) AccountTotalMap.context).finish(); //기존 지도 삭제
                    startActivity(intent);
                }



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        //who = 1
        //usekinds = 2
        //paymentkinds = 3
        Log.d("요청", String.valueOf(requestCode));
        Log.d("결과", String.valueOf(resultCode));

        if(requestCode == REQUESTCODE){

            if (resultCode == RESULTCODE1) { //인물 카테고리 선택 후 결과
                String whoselect = resultIntent.getStringExtra("whoselect");
                Log.d("선택한 who는 무엇이냐 ", whoselect);
                ChangeValueWho = whoselect;

                //보여지는부분도 표시할 것
                //whotxt.setText(oWhoList.GetWhonametoidx((String) whoselect));
                whotxt.setText(oWhoList.Getoneinfo((String) whoselect).get(1).get(1));
            }else if (resultCode == RESULTCODE2) { //사용 카테고리 선택 후 결과

                String usecategorykey = resultIntent.getStringExtra("usecategorykey");
                ChangeValueUsecategorykey = usecategorykey;

                //String usekindstab = resultIntent.getStringExtra("usekindstab");
                //String usekinds = resultIntent.getStringExtra("usekinds");
                //ChangeValueUseKindsTab = usekindstab;
                //ChangeValueUseKinds = usekinds;

                Log.d("선택한 사용 카테고리 무엇이냐 ", usecategorykey);
                Log.d("선택한 사용 카테고리 무엇이냐 ", String.valueOf(oUseKindsList.Getoneinfo(usecategorykey).get(1).get(1)));
                Log.d("선택한 사용 카테고리 무엇이냐 ", String.valueOf(oUseKindsList.Getoneinfo(usecategorykey).get(1).get(2)));

                //보여지는부분도 표시할 것
                UseKindsvalue = oUseKindsList.GetCategorytab(Integer.parseInt(oUseKindsList.Getoneinfo(usecategorykey).get(1).get(1))) + ">" + oUseKindsList.Getoneinfo(usecategorykey).get(1).get(2);
                usekindstxt.setText(UseKindsvalue);
            }else if (resultCode == RESULTCODE3) { //결재 카테고리 선택 후 결과
                String paymentcategorykey = resultIntent.getStringExtra("paymentcategorykey");
                ChangeValuePaymentcategorykey = paymentcategorykey;
                //String paymentkindstab = resultIntent.getStringExtra("paymentkindstab");
                //String paymentkinds = resultIntent.getStringExtra("paymentkinds");
                //ChangeValuePaymentKindsTab = paymentkindstab;
                //ChangeValuePaymentKinds = paymentkinds;
                Log.d("선택한 결재 카테고리 는 무엇이냐 ",paymentcategorykey);
                Log.d("선택한 결재 카테고리 는 무엇이냐 ",String.valueOf(oPaymentKindsList.Getoneinfo(paymentcategorykey)));

                ArrayList< ArrayList<String>> paymentcategoryinfo = oPaymentKindsList.Getoneinfo(paymentcategorykey);

                //보여지는부분도 표시할 것
                Paymentkindsvalue = oWhoList.Getoneinfo((String) paymentcategoryinfo.get(1).get(1)).get(1).get(1) + ">" + oPaymentKindsList.GetCategorytab(Integer.parseInt(paymentcategoryinfo.get(1).get(2))) + ">" + paymentcategoryinfo.get(1).get(3);

                //Paymentkindsvalue = oWhoList.GetWhonametoidx((String) acountvalue.get(3)) + ">" + oPaymentKindsList.GetPaymentTabname((String) paymentkindstab) + ">" + oPaymentKindsList.GetPaymentname((String) paymentkindstab, (String) paymentkinds);
                paymentkindstxt.setText(Paymentkindsvalue);

            }else if (resultCode == RESULTCODE4) {
                ChangeValuePlaceName = resultIntent.getStringExtra("placename");
                ChangeValuePlace_X = resultIntent.getStringExtra("place_x");
                ChangeValuePlace_Y = resultIntent.getStringExtra("place_y");
                Log.d("ChangeValuePlaceName ", ChangeValuePlaceName);
                Log.d("ChangeValuePlace_X ", ChangeValuePlace_X);
                Log.d("ChangeValuePlace_Y ", ChangeValuePlace_Y);
                locationtxt.setText(ChangeValuePlaceName);

            }else{
                Log.d("결과","Failed");
            }
        }
    }
}