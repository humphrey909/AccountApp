package com.neo.accountapp_3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.R;

import java.util.ArrayList;

public class TStatisticsRecyclerAdapterinner extends RecyclerView.Adapter<TStatisticsRecyclerAdapterinner.ViewHolder> {

    Context oContext;

    PaymentKindsList oPaymentKindsList;
    AccountBook oAccountBook;

    ArrayList<ArrayList<ArrayList<String>>> PaymentInnerCategorylist;

    int listType = 0;

    String couplekey = "";
    String usertab = "";

    TStatisticsRecyclerAdapterinner(Context context){
        oContext = context;

        oPaymentKindsList = new PaymentKindsList(context);
        oAccountBook = new AccountBook(context);
    }

    public void setneeddata(String couplekey ,String usertab){
        this.couplekey = couplekey;
        this.usertab = usertab;
    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public TStatisticsRecyclerAdapterinner.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //if(listType == 1){ //main
           // view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview, parent, false);
        //}else if(listType == 2){ //검색
           // view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview, parent, false);
        //}else{ //알림
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_recyclerview, parent, false);
       // }

        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull TStatisticsRecyclerAdapterinner.ViewHolder holder, int position) {
        holder.onBind(PaymentInnerCategorylist.get(position));
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ArrayList<ArrayList<String>>> list){
        this.PaymentInnerCategorylist = list;
    }

    public void setlisttype(int type){
        this.listType = type;
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return PaymentInnerCategorylist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView paynameview;
        TextView paypriceview;

        ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            paynameview = (TextView) itemView.findViewById(R.id.paynameview);
            paypriceview = (TextView) itemView.findViewById(R.id.paypriceview);

            //리스트 중 아이템 하나 클릭시 작동
            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){

                    }
                }
            });
        }
        void onBind(ArrayList<ArrayList<String>> item){
            paynameview.setText(item.get(1).get(3));
            paypriceview.setText(item.get(1).get(4) +"원");
            //MakePaymentCapital(item);
        }
    }
}


