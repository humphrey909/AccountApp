package com.neo.accountapp_3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Account.AccountEdit;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.db.WhoList;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    Context oContext;

    private ArrayList<ArrayList<ArrayList<String>>> oAccountbook;

    int listType = 0;

    public MyRecyclerAdapter(Context context){
        oContext = context;
    }
    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(listType == 1){ //main
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview, parent, false);
        }else if(listType == 2){ //검색
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview, parent, false);
        }else{ //알림
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_recyclerview, parent, false);
        }

        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(oAccountbook.get(position));
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ArrayList<ArrayList<String>>> list){
        this.oAccountbook = list;
    }

    public void setlisttype(int type){
        this.listType = type;
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return oAccountbook.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateview;
        TextView ddayview;
        TextView nameview;
        TextView explainview;
        TextView priceview;
        WhoList oWhoList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            oWhoList = new WhoList(oContext);

            dateview = (TextView) itemView.findViewById(R.id.dateview);
            ddayview = (TextView) itemView.findViewById(R.id.ddayview); //오늘 날짜에서 등록 날짜를 빼서 몇일인지 구분한다. 시간은...??? 
            nameview = (TextView) itemView.findViewById(R.id.nameview);
            explainview = (TextView) itemView.findViewById(R.id.explainview);
            priceview = (TextView) itemView.findViewById(R.id.priceview);

            //리스트 중 아이템 하나 클릭시 작동
            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){

                       // if(listType == 1) { //main
                            Log.d("Recyclerveiw", String.valueOf(position));
                            Intent intent = new Intent(view.getContext(), AccountEdit.class);
                            intent.putExtra("accountkey", oAccountbook.get(position).get(0).get(0));
                            intent.putExtra("accesstype", "1"); //메인 알람 검색 일 때
                            view.getContext().startActivity(intent);
                       // }else{ //검색

                       // }

                    }
                }
            });
        }
        void onBind(ArrayList<ArrayList<String>> item){

            if(listType == 1) { //main
                dateview.setText(item.get(1).get(1));
                nameview.setText(oWhoList.Getoneinfo(item.get(1).get(3)).get(1).get(1));
                explainview.setText(item.get(1).get(4));
                priceview.setText(item.get(1).get(2));
            }else if(listType == 2){ //검색
                dateview.setText(item.get(1).get(1));
                nameview.setText(oWhoList.Getoneinfo(item.get(1).get(3)).get(1).get(1));
                explainview.setText(item.get(1).get(4));
                priceview.setText(item.get(1).get(2));
            }else{ //알림
                dateview.setText(item.get(1).get(1));
                nameview.setText(oWhoList.Getoneinfo(item.get(1).get(3)).get(1).get(1));
                explainview.setText(item.get(1).get(4));
                priceview.setText(item.get(1).get(2));
                ddayview.setText(item.get(1).get(11)+item.get(1).get(12));
            }

        }
    }
}


