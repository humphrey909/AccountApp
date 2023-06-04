package com.neo.accountapp_3.Adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.R;

import java.util.ArrayList;

public class TStatisticsRecyclerAdapter extends RecyclerView.Adapter<TStatisticsRecyclerAdapter.ViewHolder> {

    Context oContext;

    PaymentKindsList oPaymentKindsList;
    AccountBook oAccountBook;

    ArrayList<ArrayList<String>> PaymentTabCategorylistbox;
    ArrayList<ArrayList<ArrayList<ArrayList<String>>>> PaymentInnerCategorylistbox;

    String couplekey = "";
    String usertab = "";
    ArrayList<ArrayList<ArrayList<String>>> ConditionInnerListBox = new ArrayList<>(); //카테고리 조건에 맞게 넣어주는 리스트

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    ArrayList<ArrayList<ArrayList<String>>> AccountSelectlist;

    public TStatisticsRecyclerAdapter(Context context){
        oContext = context;

        oPaymentKindsList = new PaymentKindsList(context);
        oAccountBook = new AccountBook(context);

    }
    //뷰 하나를 만드는 곳 = 뷰홀더 = 뷰 하나를 가지고 있는 것.
    @NonNull
    @Override
    public TStatisticsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    //D/PaymentTabCategorylistbox: [[현금, 0], [은행, 0], [신용카드, -15000], [대출, 0]]
    //D/PaymentInnerCategorylistbox: [[], [], [[[0], [0, 1, 2, qq, -15000]], [[2], [0, 1, 2, q23, 0]], [[3], [0, 1, 2, t54, 0]], [[4], [0, 1, 2, ttt, 0]], [[5], [0, 1, 2, hhh, 0]]], []]

    //컨텐츠를 채워넣는 곳
    @Override
    public void onBindViewHolder(@NonNull TStatisticsRecyclerAdapter.ViewHolder holder, int position) {

        //Log.d("해당 위치", String.valueOf(position));
        //Log.d("해당 리스트", String.valueOf(payloads));
        holder.onBind(PaymentTabCategorylistbox.get(position), position);
    }

    //arraylist 를 가져옴.
    public void setRecycleList(ArrayList<ArrayList<String>> list){
        this.PaymentTabCategorylistbox = list;
    }

    //public void setneeddata(String couplekey ,String usertab){
    public void setneeddata(ArrayList<ArrayList<ArrayList<ArrayList<String>>>> PaymentInnerCategorylistbox){
       // this.couplekey = couplekey;
       // this.usertab = usertab;
        this.PaymentInnerCategorylistbox = PaymentInnerCategorylistbox;
    }

    //화면에 몇개 그려져야하는지 갯수
    @Override
    public int getItemCount() {
        return PaymentTabCategorylistbox.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView paynameview;
        TextView paypriceview;

        //String data;
        int position;

        RecyclerView PaytabinnerRCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            PaytabinnerRCV = (RecyclerView) itemView.findViewById(R.id.PaytabinnerRCV);

            paynameview = (TextView) itemView.findViewById(R.id.paynameview);
            paypriceview = (TextView) itemView.findViewById(R.id.paypriceview);

            //리스트 중 아이템 하나 클릭시 작동
            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {

                    //클릭시 상태 저장
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){

                        if (selectedItems.get(position)) {
                            // 펼쳐진 Item을 클릭 시
                            selectedItems.delete(position);
                        } else {
                            // 직전의 클릭됐던 Item의 클릭상태를 지움
                            selectedItems.delete(prePosition);
                            // 클릭한 Item의 position을 저장
                            selectedItems.put(position, true);
                        }
                        // 해당 포지션의 변화를 알림
                        Log.e("클릭된 포지션 저장 ", String.valueOf(selectedItems));
                        Log.e("포지션 변화1 ", String.valueOf(prePosition));
                        Log.e("포지션 변화2 ", String.valueOf(position));
                        //MakePaymentInnerRCView(); //위치마다 리스트를 생성한다.

                        if (prePosition != -1) notifyItemChanged(prePosition);
                        notifyItemChanged(position);
                        // 클릭된 position 저장
                        prePosition = position;

                        //Log.e("클릭된 포지션 저장 ", String.valueOf(prePosition));
                    }
                }
            });

        }

        //리스트를 한번에 다 뿌려서 보여주는 것.
        void onBind(ArrayList<String> info, int position){
            //this.data = info.get(0);
            this.position = position;

            //결재 카테고리의 가격을 입력
            //MakePaymentCapital(position);

            paynameview.setText(info.get(0));
            paypriceview.setText(info.get(1)+"원");

            changeVisibility(selectedItems.get(position), position);
        }

        /**
         * 클릭된 Item의 상태 변경
         * @param isExpanded Item을 펼칠 것인지 여부
         */
        private void changeVisibility(final boolean isExpanded, int position) {

            Log.d("position", String.valueOf(position));
            Log.d("position", String.valueOf(isExpanded));
            int height = 0;
            if(isExpanded){ //true일때만 클릭을 했을 때를 말함.
                Log.d("이너 리스트 가져옴", String.valueOf(PaymentInnerCategorylistbox.get(position)));
                Log.d("이너 리스트 가져옴2", String.valueOf(PaymentInnerCategorylistbox.get(position).size()));

                // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
                //int dpValue = 150;
                int dpValue = 120;
                //float d = oContext.getResources().getDisplayMetrics().density;
                float d = PaymentInnerCategorylistbox.get(position).size();
                Log.d("d가 무엇인가", String.valueOf(d));
                height = (int) (dpValue * d);

            }



            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            //true면 0 -> 높이, false면 높이 -> 0
            //true 인 값만 애니메이션을 통해 높이만큼 슬라이딩을 하겠다. false는 슬라이딩 닫겠다.
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);

            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    //Log.d("구분선", "------------------------------------------------------------------------------------------------------------");

                    //true일때만 리스트를 생성한다.
                    if(isExpanded){
                        MakePaymentInnerRCView(position); //위치마다 리스트를 생성한다.
                    }

                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();
                    // imageView의 높이 변경
                    PaytabinnerRCV.getLayoutParams().height = value;
                    PaytabinnerRCV.requestLayout();
                    // imageView가 실제로 사라지게하는 부분
                    PaytabinnerRCV.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }

        //결재 대분류 카테고리에 소분류 리스트를 리사이클러뷰로 생성한다.
        public void MakePaymentInnerRCView(int position){

            Log.d("이너 리사이클러뷰 값_" + position, String.valueOf(PaymentInnerCategorylistbox.get(position)));

            RecyclerView PaytabinnerRCV = (RecyclerView) itemView.findViewById(R.id.PaytabinnerRCV);
            LinearLayoutManager linearManager = new LinearLayoutManager(oContext);
            TStatisticsRecyclerAdapterinner Adapter = new TStatisticsRecyclerAdapterinner(oContext); //내가만든 어댑터 선언
            PaytabinnerRCV.setLayoutManager(linearManager); //리사이클러뷰 + 리니어 매니저 = 만들 형식
            Adapter.setneeddata(couplekey, usertab); //필요한 데이터 넘김
            Adapter.setRecycleList(PaymentInnerCategorylistbox.get(position)); //arraylist 연결
            PaytabinnerRCV.setAdapter(Adapter); //리사이클러뷰 위치에 어답터 세팅
        }
    }
}


