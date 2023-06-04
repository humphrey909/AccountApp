package com.neo.accountapp_3.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.WhoList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MStatisticsRecyclerAdapter extends RecyclerView.Adapter<MStatisticsRecyclerAdapter.ViewHolder> {

    Context oContext;

    PaymentKindsList oPaymentKindsList;
    UseKindsList oUseKindsList;
    WhoList oWhoList;
    AccountBook oAccountBook;

    private ArrayList<ArrayList<ArrayList<String>>> list;

    String couplekey = "";
    String usekindstab = "";
    String mindate = "";
    String maxdate = "";

    int listType = 0;
    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;
    SimpleDateFormat dateformat;

    public MStatisticsRecyclerAdapter(Context context){
        oContext = context;

        oPaymentKindsList = new PaymentKindsList(context);
        oUseKindsList = new UseKindsList(context);
        oWhoList = new WhoList(context);
        oAccountBook = new AccountBook(context);

        dateformat = new SimpleDateFormat("yyyy/MM/dd");

    }
    public void setlisttype(int type){
        this.listType = type;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public MStatisticsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mstatistics_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    @Override
    public void onBindViewHolder(@NonNull MStatisticsRecyclerAdapter.ViewHolder holder, int position) {

        //Log.d("해당 위치", String.valueOf(position));
        //Log.d("해당 리스트", String.valueOf(payloads));
        try {
            holder.onBind(list.get(position), position);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ArrayList<ArrayList<String>>> list){
        this.list = list;
    }

    public void setneeddata(String couplekey ,String usekindstab, String mindate, String maxdate){
        this.couplekey = couplekey;
        this.usekindstab = usekindstab;
        this.mindate = mindate;
        this.maxdate = maxdate;
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameview;
        TextView priceview;

        int position;

        RecyclerView PaytabinnerRCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            PaytabinnerRCV = (RecyclerView) itemView.findViewById(R.id.PaytabinnerRCV);

            nameview = (TextView) itemView.findViewById(R.id.nameview);
            priceview = (TextView) itemView.findViewById(R.id.priceview);

            //리스트 중 아이템 하나 클릭시 작동
            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    //클릭시 상태 저장
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){


                        //Log.e("클릭된 포지션 저장 ", String.valueOf(prePosition));
                    }
                }
            });

        }

        //리스트를 한번에 다 뿌려서 보여주는 것.
        @SuppressLint("SetTextI18n")
        void onBind(ArrayList<ArrayList<String>> item, int position) throws ParseException {
            this.position = position;

            if(item.get(0).get(0).equals("-")){
                nameview.setText(item.get(1).get(0));
                priceview.setText(" ");
            }else {

                if (listType == 1) { //인물 카테고리
                    nameview.setText(item.get(1).get(1));
                    priceview.setText(item.get(1).get(3) + "원");
                } else if (listType == 2) { //사용 카테고리
                    nameview.setText(item.get(1).get(2));
                    priceview.setText(item.get(1).get(3) + "원");
                } else { //결재 카테고리
                    String whocatename = oWhoList.Getoneinfo(item.get(1).get(1)).get(1).get(1); //인물 카테고리에서 이름 가져옴
                    String paycatetabname = oPaymentKindsList.PaymentCategoryTab[Integer.parseInt(item.get(1).get(2))]; //결재 탭 이름 가져옴
                    String nameviewtext = whocatename + ">" + paycatetabname + ">" + item.get(1).get(3); //등록할 이름 만듬
                    nameview.setText(nameviewtext); //분류 해서 적을 것
                    priceview.setText(item.get(1).get(4) + "원");
                }
            }
        }



        //안쓴다
        //결재 대분류 카테고리에 사용한 가격을 계산하여 보여준다.
        /*
        public void MakePaymentCapital(int listType, int position) throws ParseException {

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


            AccountSelectlist = oAccountBook.AccountRead();

            int EachPayPrice = 0;
            //ConditionPriceListBox.clear();
            //조건에 맞게 리스트를 만든다.
            for (int i = 0; i < AccountSelectlist.size(); i++){
                if(AccountSelectlist.get(i).get(1).get(0).equals(couplekey)) { //커플키 조건

                    //한달만 기간 정할 것
                    //가계부 날짜 파싱
                    Date Selectdate = dateformat.parse(AccountSelectlist.get(i).get(1).get(1));
                    if(Selectdate.after(date1) && Selectdate.before(date2) || Selectdate.equals(date1) || Selectdate.equals(date2)) {

                        //사용 카테고리 조건 - 입금 지출 조건 주기
                        ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(AccountSelectlist.get(i).get(1).get(5));
                        if(!usecategoryinfo.isEmpty()) { //카테고리가 있을때만 계산 함
                            if (usecategoryinfo.get(1).get(1).equals(usekindstab)) { //사용 카테고리 조건


                                if(listType == 1){ //인물 카테고리
                                    //인물 카테고리 조건
                                    if (AccountSelectlist.get(i).get(1).get(3).equals(list.get(position).get(0).get(0))) { //인물 카테고리 조건

                                        //해당 조건에 맞게 계산
                                        Log.d("인물 체크!!!!!", String.valueOf(AccountSelectlist.get(i).get(1).get(2)));
                                        EachPayPrice += Integer.parseInt(AccountSelectlist.get(i).get(1).get(2));
                                    }
                                }else if(listType == 2){ //사용 카테고리

                                    if (AccountSelectlist.get(i).get(1).get(5).equals(list.get(position).get(0).get(0))) { //인물 카테고리 조건

                                        //해당 조건에 맞게 계산
                                        Log.d("사용 체크!!!!!", String.valueOf(AccountSelectlist.get(i).get(1).get(2)));
                                        EachPayPrice += Integer.parseInt(AccountSelectlist.get(i).get(1).get(2));
                                    }
                                }else{ //결재 카테고리

                                    if (AccountSelectlist.get(i).get(1).get(6).equals(list.get(position).get(0).get(0))) { //인물 카테고리 조건

                                        //해당 조건에 맞게 계산
                                        Log.d("결재 체크!!!!!", String.valueOf(AccountSelectlist.get(i).get(1).get(2)));
                                        EachPayPrice += Integer.parseInt(AccountSelectlist.get(i).get(1).get(2));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            priceview.setText(EachPayPrice+"원");

        }
         */
    }
}


