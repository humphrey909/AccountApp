package com.neo.accountapp_3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Map.MapActivityPager;
import com.neo.accountapp_3.R;

import java.util.ArrayList;

public class PlaceSearchRecyclerAdapter extends RecyclerView.Adapter<PlaceSearchRecyclerAdapter.ViewHolder> {

    Context oContext;
    MapActivityPager oMapActivityPager;
    private ArrayList<ArrayList<String>> Placelist;

    int listType = 0;

    public PlaceSearchRecyclerAdapter(Context context){
        oContext = context;
    }
    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public PlaceSearchRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placesearch_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull PlaceSearchRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(Placelist.get(position));
    }

    public void setNeedData(MapActivityPager oMapActivityPager){
        this.oMapActivityPager = oMapActivityPager;
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ArrayList<String>> list){
        this.Placelist = list;
    }

    public void setlisttype(int type){
        this.listType = type;
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return Placelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView placetitle;
        /*TextView ddayview;
        TextView nameview;
        TextView explainview;
        TextView priceview;
        WhoList oWhoList;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //oWhoList = new WhoList(oContext);

            placetitle = (TextView) itemView.findViewById(R.id.placetitle);
           // ddayview = (TextView) itemView.findViewById(R.id.ddayview); //오늘 날짜에서 등록 날짜를 빼서 몇일인지 구분한다. 시간은...???
            //nameview = (TextView) itemView.findViewById(R.id.nameview);
            //explainview = (TextView) itemView.findViewById(R.id.explainview);
            //priceview = (TextView) itemView.findViewById(R.id.priceview);

            //리스트 중 아이템 하나 클릭시 작동
            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){

                        // 두번 째 프래그먼트 호출
                        oMapActivityPager.fragmentChange(2, Placelist.get(position).get(0), Placelist.get(position).get(1), Placelist.get(position).get(2));

                        //((MapActivityPager)oActivity.getActivity()).replaceFragment(MapMarkFragment.newInstance(1));




                       // if(listType == 1) { //main

                        //Intent intent1 = new Intent(getApplicationContext(), PlaceSearchlistpage_x.class);
                        //startActivity(intent1);
/*
                        Intent resultIntent = new Intent();
                        //resultIntent.putExtra("whoselect", list.get(position).get(0).get(0));
                        oActivity.setResult(RESULTCODE, resultIntent);
                        oActivity.finish();
*/

                            //Log.d("Recyclerveiw", String.valueOf(position));
                            //Intent intent = new Intent(view.getContext(), MapMarkingview_x.class);
                            //intent.putExtra("placename", Placelist.get(position).get(0));
                            //intent.putExtra("place_x", Placelist.get(position).get(1));
                            //intent.putExtra("place_y", Placelist.get(position).get(2));
                            //oContext.startActivity(intent);


                       // }else{ //검색

                       // }

                    }
                }
            });
        }
        void onBind(ArrayList<String> item){
            placetitle.setText(item.get(0));
/*
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
                ddayview.setText(item.get(1).get(8)+item.get(1).get(9));
            }*/

        }
    }
}


