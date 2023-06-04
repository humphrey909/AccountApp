package com.neo.accountapp_3.db;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends SharedModel implements Serializable {

    String dbname = "";
    CoupleConnectList oCoupleConnectList;

    //어떻게 사용할거야?
    //데이터를 가져올때 사용할거야. User라는 db에 데이터를 가공하고 array만들고 해서 가져올때 쓸거야.
    public User(Context mContext){
        this.oContext = mContext; //부모클래스에 전달

        dbname = "User"; //db name 지정

        Field = new String[]{
                "pw", "name", "couplecode", "couplechk", "oauth"
        };

    }

    //몇번째 필드인지 알려줌
    public int GetFildnum(String Fieldname){
        int Fieldnum = 0;
        for (int i = 0; i<Field.length; i++){
            if(Field[i].equals(Fieldname)){
                Fieldnum = i;
            }
        }
        return Fieldnum;
    }



    //해당 코드가 있는지 찾음
    //이미 커플인 유저 제외
    public String SearchCoupleCode(String writecode, String mycode){
        ArrayList<ArrayList<ArrayList<String>>> Userlist = UserRead(); //전체 유저리스트
        Log.d("list", String.valueOf(Userlist));

        String Selectkey = "-1";
        for (int i = 0; i < Userlist.size(); i++){
            String Couplecode = Userlist.get(i).get(1).get(2); //커플코드만 가져옴
            String Couplechk = Userlist.get(i).get(1).get(3); //커플코드만 가져옴

            //내 코드 입력시 노노
            if(Couplechk.equals("-") && !writecode.equals(mycode)) { //커플 체크가 -일때만 가져오기
                if (Couplecode.equals(writecode)) {
                    Selectkey = Userlist.get(i).get(0).get(0);
                }
            }
        }

        //같은 코드가 없으면 0, 있으면 상대방 회원 id
        return Selectkey;
    }


    //커플 연결!
    public String SetCoupleSave(String Myid, String Otherid){

        //커플리스트에 커플 등록하기
        oCoupleConnectList = new CoupleConnectList(oContext);
        String couplekey = oCoupleConnectList.SetCoupleConnect(Myid, Otherid);

        //두커플 다 - 3번째 변수를 0 -> 커플키 로 변경
        DoEditOne(dbname, Myid, GetFildnum("couplechk"), couplekey); //내id
        DoEditOne(dbname, Otherid, GetFildnum("couplechk"), couplekey); //상대id

        return couplekey;
    }


    //유저 추가하기
    public String UserWrite(String key, ArrayList<String> Joindata) {
        //User라는 데이터베이스에 값을 추가할 것. Userdb가 없으면 만들고 putString을 하고, 있으면 찾아서 putString 할것
        //찾을 db 이름, 저장할 데이터들,

        //String result = DoAdd(dbname, key, Joindata); //저장하고 그 key값을 던져줌
        String result = DoAdd_json(dbname, key, Joindata); //저장하고 그 key값을 던져줌

        return result;
    }

    //해당 데이터를 수정한다.
    public int EditUser(String key, String name, String order){

       int result =  DoEditOne(dbname, key, GetFildnum(order), name);
       Log.d("User", key+"수정완료");

        return result;
    }

    //데이터 전부 가져오기
    public ArrayList<ArrayList<ArrayList<String>>> UserRead(){
        //User라는 데이터베이스에 값을 추가할 것. Userdb가 없으면 만들고 putString을 하고, 있으면 찾아서 putString 할것
        //찾을 db 이름, 저장할 데이터들,

        //Map<String, ?> Userlist = DoReadAll(dbname);
        ArrayList<ArrayList<ArrayList<String>>> Userlist = DoReadAll(dbname);

        return Userlist;
    }

    //데이터 하나 가져오기
    public  ArrayList<ArrayList<String>> GetMyinfo(String key){

        // result = DoReadOne(dbname, key);
        ArrayList<ArrayList<String>> result = DoReadOne(dbname, key);
        return result;
    }

    //db초기화
    public int Init(){
        //User라는 데이터베이스에 값을 추가할 것. Userdb가 없으면 만들고 putString을 하고, 있으면 찾아서 putString 할것
        //찾을 db 이름, 저장할 데이터들,

        int Userlist = DoDeleteAll(dbname);

        return Userlist;
    }

    //데이터 하나 삭제
    public int Deleteone(String key){
        String[] keyarr = {key};
        DoDeleteArray(dbname, keyarr);


        return 1;
    }

}
