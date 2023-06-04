package com.neo.accountapp_3;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.Objects;

public class CunstomMenuDialog extends Dialog {
    private Context context;
    private CustomDialogClickListener customDialogClickListener;

    ImageButton peoplecatebtn, usecatebtn, pricecatebtn;

    int MonthCategorytab;

    public CunstomMenuDialog(@NonNull Context context, CustomDialogClickListener customDialogClickListener) {
        super(context);
        this.context = context;
        this.customDialogClickListener = customDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cunstommenudialog);


        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        peoplecatebtn = (ImageButton)findViewById(R.id.peoplecatebtn);
        usecatebtn = (ImageButton)findViewById(R.id.usecatebtn);
        pricecatebtn = (ImageButton)findViewById(R.id.pricecatebtn);

        peoplecatebtn.setOnClickListener(v -> {
            // 저장버튼 클릭
            try {
                this.customDialogClickListener.peoplecatebtnCK();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dismiss();
        });
        usecatebtn.setOnClickListener(v -> {
            // 취소버튼 클릭
            try {
                this.customDialogClickListener.usecatebtnCK();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dismiss();
        });
        pricecatebtn.setOnClickListener(v -> {
            // 취소버튼 클릭
            try {
                this.customDialogClickListener.pricecatebtnCK();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dismiss();
        });
/*
        peoplecatebtn = (ImageButton)findViewById(R.id.peoplecatebtn);
        usecatebtn = (ImageButton)findViewById(R.id.usecatebtn);
        pricecatebtn = (ImageButton)findViewById(R.id.pricecatebtn);

        clickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.peoplecatebtn:
                        MonthCategorytab = 1;
                        Log.d("", String.valueOf(MonthCategorytab));


                        dismiss();
                        break;
                    case R.id.usecatebtn:
                        MonthCategorytab = 2;
                        Log.d("", String.valueOf(MonthCategorytab));
                        dismiss();
                        break;

                    case R.id.pricecatebtn:
                        MonthCategorytab = 3;
                        Log.d("", String.valueOf(MonthCategorytab));
                        dismiss();
                        break;
                }
            }
        };
        peoplecatebtn.setOnClickListener(clickListener);
        usecatebtn.setOnClickListener(clickListener);
        pricecatebtn.setOnClickListener(clickListener);
        */
    }

    public interface CustomDialogClickListener {
        void peoplecatebtnCK() throws ParseException;
        void usecatebtnCK() throws ParseException;
        void pricecatebtnCK() throws ParseException;
    }
}
