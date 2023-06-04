package com.neo.accountapp_3.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.neo.accountapp_3.PaymentCategory.PaymentkindslistManagement;
import com.neo.accountapp_3.PeopleCategory.WholistManagement;
import com.neo.accountapp_3.R;
import com.neo.accountapp_3.UseCategory.UsekindslistManagement;

public class AccountKindsSetlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_kinds_setlist);


        //툴바 설정
        Toolbar appbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존제목 지우기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        //인물 카테고리 관리
        Button whocategorybtn = (Button)findViewById(R.id.whocategorybtn);
        whocategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WholistManagement.class);
                startActivity(intent);
            }
        });

        //사용 카테고리 관리
        Button usekindcategorybtn = (Button)findViewById(R.id.usekindcategorybtn);
        usekindcategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UsekindslistManagement.class);
                startActivity(intent);
            }
        });

        //결재 카테고리 관리
        Button paymentkindcategorybtn = (Button)findViewById(R.id.paymentkindcategorybtn);
        paymentkindcategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaymentkindslistManagement.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}