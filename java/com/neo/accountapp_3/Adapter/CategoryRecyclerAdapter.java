package com.neo.accountapp_3.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.Account.AccountAdd2;
import com.neo.accountapp_3.Account.AccountAdd3;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.PaymentCategory.PaymentKindsAdd;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.UseCategory.UseKindsAdd;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.PeopleCategory.WhoAdd;
import com.neo.accountapp_3.mainActivity;

import java.util.ArrayList;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    Context oContext;
    Activity oActivity;
    int RESULTCODE;

    AccountBook oAccountBook;
    UseKindsList oUseKindsList;

    ArrayList<ArrayList<ArrayList<String>>> list;

    //가계부 추가하기 - 1. 가계부 추가하기 첫번째 페이지 2. 가계부 추가하기 두번째 페이지 3. 추가하기 세번째 페이지 (grid) - Button
    //가계부 수정하기 (Select) - 4. who 수정, 5. usekind 수정, 6. payment 수정 (grid) - Button
    //카테고리 관리하기 - 7. who 카테고리 리스트, 8. usekind 카테고리 리스트, 9. payment 카테고리 리스트 (linear) - Textview
    int listType = 0;

    String couplekey = "";
    int selectyear;
    int selectmonth;
    int selectday;
    String selectdayofweek;
    int price; //금액

    String who; //누가
    String explain; //상세설명
    String selectusekindstab; //선택한 사용 카테고리 탭

    String usecategorykey; //품목
    String selectwhotab; //선택한 인물 카테고리 탭
    String selectpaymenttab; //선택한 결재 카테고리 탭

    String placename, place_x, place_y;

    public CategoryRecyclerAdapter(Context context){
        oContext = context;
        oAccountBook = new AccountBook(oContext);
        oUseKindsList = new UseKindsList(oContext);
    }
    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ArrayList<ArrayList<String>>> list){
        this.list = list;
    }
    public void setlisttype(int type){
        this.listType = type;
    }

    //add1 activity
    public void setNeedData(String couplekey, int selectyear, int selectmonth, int selectday, String selectdayofweek, int price){
        this.couplekey = couplekey;
        this.selectyear = selectyear;
        this.selectmonth = selectmonth;
        this.selectday = selectday;
        this.selectdayofweek = selectdayofweek;
        this.price = price;
    }

    //add2 activity
    public void setNeedData2(String couplekey, int selectyear, int selectmonth, int selectday, String selectdayofweek, int price, String who, String explain, String usekindstab, String placename, String place_x, String place_y){
        this.couplekey = couplekey;
        this.selectyear = selectyear;
        this.selectmonth = selectmonth;
        this.selectday = selectday;
        this.selectdayofweek = selectdayofweek;
        this.price = price;
        this.who = who;
        this.explain = explain;
        this.selectusekindstab = usekindstab;
        this.placename = placename;
        this.place_x = place_x;
        this.place_y = place_y;
        Log.d("가계부 내용이다", String.valueOf(explain));

    }

    //add3 activity
    public void setNeedData3(String couplekey, int selectyear, int selectmonth, int selectday, String selectdayofweek, int price, String who, String explain, String usecategorykey, String whotab, String paymenttab, String placename, String place_x, String place_y){
        this.couplekey = couplekey;
        this.selectyear = selectyear;
        this.selectmonth = selectmonth;
        this.selectday = selectday;
        this.selectdayofweek = selectdayofweek;
        this.price = price;
        this.who = who;
        this.explain = explain;
        this.usecategorykey = usecategorykey;
        this.selectwhotab = whotab;
        this.selectpaymenttab = paymenttab;
        this.placename = placename;
        this.place_x = place_x;
        this.place_y = place_y;
    }

    //who category select activity, //use category select activity
    public void setNeedData4(Activity activity, int RESULTCODE){
        this.oActivity = activity;
        this.RESULTCODE = RESULTCODE;

    }

    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoreyrecyclerbuttonrow, parent, false);
        
        return new ViewHolder(view);
    }

    //컨텐츠를 채워넣는 곳
    //위에서 만든 뷰홀더가 holder로 받아온다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button rowname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowname = (Button) itemView.findViewById(R.id.rowname);

            //리스트 중 아이템 하나 클릭시 작동
            rowname.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        Log.d("Recyclerveiw", String.valueOf(position)); //선택한 숫자를 불러옴

                        Intent intent = null;
                        if(listType == 1){ //가계부 추가1 리스트 버튼
                            intent = new Intent(view.getContext(), AccountAdd2.class);

                            //intent.putExtra("couplekey", couplekey);
                            intent.putExtra("selectyear", selectyear);
                            intent.putExtra("selectmonth", selectmonth);
                            intent.putExtra("selectday", selectday);
                            intent.putExtra("selectdayofweek", selectdayofweek);
                            intent.putExtra("price", price);
                            intent.putExtra("who", list.get(position).get(0).get(0));

                            oContext.startActivity(intent);

                        }else if(listType == 2){ //가계부 추가2 리스트 버튼
                            intent = new Intent(view.getContext(), AccountAdd3.class);

                           // intent.putExtra("couplekey", couplekey);
                            intent.putExtra("selectyear", selectyear);
                            intent.putExtra("selectmonth", selectmonth);
                            intent.putExtra("selectday", selectday);
                            intent.putExtra("selectdayofweek", selectdayofweek);
                            intent.putExtra("price", price);
                            intent.putExtra("who", who);
                            if(explain == null){
                                explain = "빈값입니다.";
                            }
                            intent.putExtra("explain", explain);
                            intent.putExtra("usecategorykey", list.get(position).get(0).get(0)); //사용 카테고리 키 값

                            intent.putExtra("placename", placename);
                            intent.putExtra("place_x", place_x);
                            intent.putExtra("place_y", place_y);

                            oContext.startActivity(intent);
                        }else if(listType == 3){ //가계부 추가3 리스트 버튼

                            //저장할때 ->  사용카테코리 키와, 결재카테고리 키를 찾아서 저장해야한다.
                            //현재 그냥 나열된 숫자를 넣어준다. 이러면 오류가 생길 것이다. 바꿔주라.!!
                            Log.d("couplekey", String.valueOf(couplekey)); //년
                            Log.d("selectyear", String.valueOf(selectyear)); //년
                            Log.d("selectmonth", String.valueOf(selectmonth)); //월
                            Log.d("selectday", String.valueOf(selectday)); //일
                            Log.d("selectdayofweek", String.valueOf(selectdayofweek)); //일
                            Log.d("price", String.valueOf(price)); //금액
                            Log.d("who", String.valueOf(who)); //누구
                            Log.d("explain", String.valueOf(explain)); //
                            Log.d("usecategorykey", String.valueOf(usecategorykey)); //
                            Log.d("paymentcategorykey", String.valueOf(list.get(position).get(0).get(0))); //

                            Log.d("placename", String.valueOf(placename)); //
                            Log.d("place_x", String.valueOf(place_x)); //
                            Log.d("place_y", String.valueOf(place_y)); //

                            //ChangeValueUsecategorykey를 풀어서 입금인지 지출인지 결정해서 + - 값을 입혀준다.
                            ArrayList<ArrayList<String>> usecategoryinfo = oUseKindsList.Getoneinfo(usecategorykey);

                            if( usecategoryinfo.get(1).get(1).equals("1")) { // 1이면 지출 -
                                price = price*-1;
                            }else{ // 2 이면 입금 +
                                price = price;
                            }

                            String Accountkey = oAccountBook.SaveAccountobject(couplekey, selectyear, selectmonth, selectday, selectdayofweek, price, who, explain, usecategorykey, list.get(position).get(0).get(0), placename, place_x, place_y);
                            Log.d("저장한 가계부 이름", Accountkey);

                            intent = new Intent(view.getContext(), mainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            oContext.startActivity(intent);
                        }else if(listType == 4){//가계부 수정 - 인물 카테고리 재선택 리스트 버튼
                            //선택한값을 가지고 edit으로 넘겨줄 것.
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("whoselect", list.get(position).get(0).get(0));
                            oActivity.setResult(RESULTCODE, resultIntent);
                            oActivity.finish();

                        }else if(listType == 5){//가계부 수정 - 사용 카테고리 재선택 리스트 버튼
                            //선택한값을 가지고 edit으로 넘겨줄 것.
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("usecategorykey", list.get(position).get(0).get(0));
                            oActivity.setResult(RESULTCODE, resultIntent);
                            oActivity.finish();

                        }else if(listType == 6){
                            //선택한값을 가지고 edit으로 넘겨줄 것.
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("paymentcategorykey", list.get(position).get(0).get(0));
                            oActivity.setResult(RESULTCODE, resultIntent);
                            oActivity.finish();

                        }else if(listType == 7){ //7. who 카테고리 리스트
                            //if(position != 0){
                            if(list.get(position).get(1).get(2).equals("-")){
                                intent = new Intent(view.getContext(), WhoAdd.class);
                                intent.putExtra("type", 2);
                                intent.putExtra("who", list.get(position).get(0).get(0));
                                intent.putExtra("position", position);
                                oContext.startActivity(intent);
                            }else{
                                Toast.makeText(oContext, "프로필 관리에서 수정이 가능합니다.", Toast.LENGTH_SHORT).show();
                            }

                        }else if(listType == 8){ //8. usekind 사용 카테고리 리스트
                            intent = new Intent(view.getContext(), UseKindsAdd.class);
                            intent.putExtra("type", 2);
                            intent.putExtra("usekey", list.get(position).get(0).get(0));
                            oContext.startActivity(intent);
                        }else if(listType == 9){ //9. payment 결재방법 카테고리 리스트
                            intent = new Intent(view.getContext(), PaymentKindsAdd.class);
                            intent.putExtra("type", 2);
                            //인물, 결재 대분류, 해당 키

                            //정해진 키값 하나만 넘기면 됨. 그걸로 정보 가져올 수 있으니까. 어떻게 고유 키를 넘길까?
                            intent.putExtra("paymentkey", list.get(position).get(0).get(0));
                            Log.d("고유키",list.get(position).get(0).get(0)); //
                            Log.d("이름",list.get(position).get(1).get(3)); //

                            oContext.startActivity(intent);
                        }
                    }
                }
            });

        }
        void onBind(ArrayList<ArrayList<String>> item){
            if(listType == 1){ //가계부 추가1 리스트 버튼
                rowname.setText(item.get(1).get(1));
            }else if(listType == 2){ //가계부 추가2 리스트 버튼
                rowname.setText(item.get(1).get(2));
            }else if(listType == 3){//가계부 추가3 리스트 버튼
                rowname.setText(item.get(1).get(3));
            }else if(listType == 4){//가계부 수정 - 인물 카테고리 재선택 리스트 버튼
                rowname.setText(item.get(1).get(1));

            }else if(listType == 5){//가계부 수정 - 사용 카테고리 재선택 리스트 버튼
                rowname.setText(item.get(1).get(2));

            }else if(listType == 6){//가계부 수정 - 결재 카테고리 재선택 리스트 버튼
                rowname.setText(item.get(1).get(3));

            }else if(listType == 7){
                rowname.setText(item.get(1).get(1));

            }else if(listType == 8){
                rowname.setText(item.get(1).get(2));

            }else if(listType == 9){
                rowname.setText(item.get(1).get(3));

            }


        }
    }
}
