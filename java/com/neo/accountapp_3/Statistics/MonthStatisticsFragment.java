package com.neo.accountapp_3.Statistics;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.neo.accountapp_3.Adapter.MStatisticsRecyclerAdapter;
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

public class MonthStatisticsFragment extends Fragment {
    int num;
    View view;

    String usekindstab;

    SimpleDateFormat dateformat;
    ArrayList<ArrayList<ArrayList<String>>> ConditionListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트
    String MonthCategorytab = "1"; //

    AccountBook oAccountBook;
    UseKindsList oUseKindsList;
    WhoList oWhoList;
    PaymentKindsList oPaymentKindsList;
    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

    String couplekey = "";
    String Standarddate = ""; //처음 입력된 기준이 되는 날짜
    String Selectdate = ""; //선택된 날짜
    Context oContext;

    Calendar calendar;
    String Getfirstday;
    String Getfinalday;

    PieChart pieChart;

    public MonthStatisticsFragment() {
        dateformat = new SimpleDateFormat("yyyy/MM/dd");
    }

    //프래그먼트를 띄우면서 데이터를 넘김
    public static MonthStatisticsFragment newInstance(int number, String usekindstab, String MonthCategorytab, String couplekey, String Standarddate) {
        MonthStatisticsFragment monthStatisticsFragment = new MonthStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        bundle.putString("usekindstab", usekindstab);
        bundle.putString("MonthCategorytab", MonthCategorytab);
        bundle.putString("couplekey", couplekey);
        bundle.putString("Standarddate", Standarddate);


        monthStatisticsFragment.setArguments(bundle);
        return monthStatisticsFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


            //프래그먼트 해당 번호를 넘겨 받음
            num = getArguments().getInt("number");
            usekindstab = getArguments().getString("usekindstab");
            MonthCategorytab = getArguments().getString("MonthCategorytab");
            couplekey = getArguments().getString("couplekey");
            Standarddate = getArguments().getString("Standarddate");

            Log.d("num", String.valueOf(num)); //프래그먼트 위치
            Log.d("usekindstab", String.valueOf(usekindstab)); //사용 카테고리 탭
            Log.d("MonthCategorytab", String.valueOf(MonthCategorytab)); //사용 카테고리 탭

            Log.d("onCreat 실행여부", "");
            //TextView fragmenttext = (TextView)findViewById(R.id.fragmenttext);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.monthstatisticsfragment, container, false);
        oContext = view.getContext();
        oWhoList = new WhoList(oContext);
        oUseKindsList = new UseKindsList(oContext);
        oPaymentKindsList = new PaymentKindsList(oContext);
        oAccountBook = new AccountBook(oContext);
        AccountSelectlist = oAccountBook.AccountRead();


        //해당 날짜를 입력해주어서 리스트를 보여준다.
        calendar = Calendar.getInstance();
        String[] StandarddateArray = Standarddate.split("/");
        Selectdate = StandarddateArray[0]+"/"+(num+1)+"/"+StandarddateArray[2]; //선택한 월로 입력
        Log.d("원래 기준 날짜",Standarddate);
        Log.d("수정된 날짜",Selectdate);
        Log.d("수정할 월", String.valueOf(num+1));

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

        try {
           // MakemonthconditionRCV("2022/02/01", "2022/02/28");
            MakemonthconditionRCV(Getfirstday, Getfinalday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Makecirclechart(usekindstab, MonthCategorytab);

        Log.d("onCreateView 실행여부", "");
        return view;
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancdState){
        super.onViewCreated(view,savedInstancdState);
        data_t = (TextView) view.findViewById(R.id.tvName2);
        data_t.setText("Page " + frag_num);
    }*/
    @Override
    public void onStop() {
        super.onStop();
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
        if(MonthCategorytab.equals("1")){ //주체 통계
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

        }else if(MonthCategorytab.equals("2")){ //사용 카테고리 통계
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

        RecyclerView RecyclerView = (RecyclerView)view.findViewById(R.id.MonthstatisticsRCV);
        LinearLayoutManager linearManager = new LinearLayoutManager(getActivity());
        MStatisticsRecyclerAdapter Adapter = new MStatisticsRecyclerAdapter(getActivity()); //내가만든 어댑터 선언
        RecyclerView.setLayoutManager(linearManager); //리사이클러뷰 + 리니어 매니저 = 만들 형식

        Adapter.setneeddata(couplekey, usekindstab, mindate, maxdate); //필요한 데이터 넘김
        Adapter.setlisttype(Integer.parseInt(MonthCategorytab)); //1 유저, 2 사용, 3 결재
        Adapter.setRecycleList(ConditionListBox); //arraylist 연결
        RecyclerView.setAdapter(Adapter); //리사이클러뷰 위치에 어답터 세팅
    }


    public void Makecirclechart(String usekindstab, String MonthCategorytab){
/*        pieChart = view.findViewById(R.id.piechart);

        //샘플데이터
        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(508, "2016"));
        visitors.add(new PieEntry(600, "2017"));
        visitors.add(new PieEntry(750, "2018"));
        visitors.add(new PieEntry(600, "2019"));
        visitors.add(new PieEntry(670, "2020"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Visitors");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Visitors");
        pieChart.animate();*/

        pieChart = (PieChart)view.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList yValues = new ArrayList();

        if(!ConditionListBox.get(0).get(0).get(0).equals("-")){

            for (int i = 0; i < ConditionListBox.size(); i++){//D/카테고리 조건 리스트 완성: [[[1], [0, kim, root@naver.com, -95000]], [[0], [0, k2, root2@naver.com, -13800]]]
                String name;
                int price;
                if(MonthCategorytab.equals("1")){
                    name = ConditionListBox.get(i).get(1).get(1);
                    if(usekindstab.equals("1")){
                        price = Integer.parseInt(ConditionListBox.get(i).get(1).get(3))*-1;
                    }else{
                        price = Integer.parseInt(ConditionListBox.get(i).get(1).get(3));
                    }
                }else if(MonthCategorytab.equals("2")){ //D/카테고리 조건 리스트 완성: [[[0], [0, 0, 월급, 165000]], [[3], [0, 0, 용돈, 12000]]]
                    name = ConditionListBox.get(i).get(1).get(2);
                    if(usekindstab.equals("1")){
                        price = Integer.parseInt(ConditionListBox.get(i).get(1).get(3))*-1;
                    }else{
                        price = Integer.parseInt(ConditionListBox.get(i).get(1).get(3));
                    }
                }else{ //D/보여줄 리스트: [[[0], [0, 1, 2, qq11, 150000]], [[4], [0, 1, 2, ttt, 15000]], [[5], [0, 1, 2, hhh, 12000]]]
                    name = ConditionListBox.get(i).get(1).get(3);
                    if(usekindstab.equals("1")){
                        price = Integer.parseInt(ConditionListBox.get(i).get(1).get(4))*-1;
                    }else{
                        price = Integer.parseInt(ConditionListBox.get(i).get(1).get(4));
                    }
                }

                yValues.add(new PieEntry(price,name));
            }
        }
        Description description = new Description();
        description.setText(""); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
    }
}