package com.neo.accountapp_3;

import android.content.Context;
import android.util.Log;

import com.neo.accountapp_3.db.User;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Join{
    User oUser;
   //Context mContext;

    public Join(Context mContext){
        oUser  = new User(mContext);
    }

    //코드번호 랜덤으로 설정
    public String couplecodeRandom(int len) {

        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for(int i=0;i<len;i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
            if(!numStr.contains(ran)) {
                //중복된 값이 없으면 numStr에 append
                numStr += ran;
            }else {
                //생성된 난수가 중복되면 루틴을 다시 실행한다
                i-=1;
            }
        }
        return numStr;
    }

    //회원가입시 arraylist 하나 추가
    public String joinsuccess(String email, String pw, String name, String oauth) {



        String newcode = couplecodeRandom(6); // 커플인증 코드
        String couplechk = "-";  //커플 여부 // - 지훈수정
        String oauthsocial = oauth;  //소셜로그인 업체

        String key = email;
        //String[] Joindata = {pw, name, newcode, couplechk}; //데이터를 array로 만들어서 넘기자

        ArrayList<String> Joindata = new ArrayList<String>();
        Joindata.add(pw);
        Joindata.add(name);
        Joindata.add(newcode);
        Joindata.add(couplechk);
        Joindata.add(oauthsocial);

Log.d("회원가입 데이터!!!", String.valueOf(Joindata));



       String result = oUser.UserWrite(key, Joindata);

        return result;
    }

    //이메일 형식 체크 메서드
    public boolean isEmail(String email){
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()){
            returnValue = true;
        }
        return returnValue;
    }

    //유저 id 중복 체크
    public int ChkEmailExitence(String writeemail){
        ArrayList<ArrayList<ArrayList<String>>> TotalUserlist = oUser.UserRead();

        int chkexitence = -1; //중복안됨
        for (int i = 0; i < TotalUserlist.size(); i++){

            if(TotalUserlist.get(i).get(0).get(0).equals(writeemail)){
                chkexitence = 1; //중복됨
            }
        }

        return chkexitence;
    }
}
