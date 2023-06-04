package com.neo.accountapp_3;

import android.content.Context;
import android.util.Log;

import com.neo.accountapp_3.db.SharedModel;
import com.neo.accountapp_3.db.User;

import java.util.ArrayList;

public class Login {

    User oUser;
    ArrayList<String> Myinfoarr;
    String Myid;

    //인스턴스 선언 되면서 context 저장
    Login(Context mContext){
        oUser  = new User(mContext);
    }

    //id와 pw를 받고 그 값이 맞는지 확인
    public int loingprocess(String idvar, String pwvar){
       // oUser = userclass;

        if(!idvar.equals("") || !pwvar.equals("")){

            //아이디는 중복이 없으니 아이디로 db에 값이 있는지 찾는다.
            //찾는 아이디가 있다면 pw의 값도 비교해서 맞는지 확인한다.
            int loginchk = 0; //로그인 체크 - id pw 모두 맞으면
            ArrayList<ArrayList<String>> Myinfo = oUser.GetMyinfo(idvar);
            Log.d("info", String.valueOf(Myinfo)); // -1 없음,

            if(Myinfo.size() != 0){ //id 존재 유
            //if(!Myinfo.equals("-1")){ //id 존재 유
                Myid = Myinfo.get(0).get(0); //key
                Myinfoarr = Myinfo.get(1);

               // Myinfoarr[1] // 이름
                //패스워드 비교
                if(Myinfoarr.get(0).equals(pwvar)){ //같음
                    Log.d("로그인여부 ", "로그인 성공 ");
                    loginchk = 1;
                }else{ //다름
                    Log.d("로그인여부 ", "pw가 맞지 않습니다. ");
                    loginchk = 0;
                }
            }else{
                Log.d("로그인여부 ", "id가 존재하지 않습니다. ");
                loginchk = 0;
            }

            return loginchk;
        }else{
            Log.d("로그인여부 ", "id와 pw를 입력해주세요 ");
            return 0;
        }
    }

}
