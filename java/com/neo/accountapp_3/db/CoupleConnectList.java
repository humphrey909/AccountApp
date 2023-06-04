package com.neo.accountapp_3.db;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class CoupleConnectList extends SharedModel implements Serializable {

    String dbname = "";

    public CoupleConnectList(Context mContext){
        this.oContext = mContext; //부모클래스에 전달

        dbname = "CoupleConnect"; //db name 지정

        Field = new String[]{
                "coupleid1", "coupleid2", "coupleimg", "coupledays"
        };


        /*
        //커플 연결 리스트
        ArrayList<String> couple1 = new ArrayList<>();
        couple1.add("12"); //커플 연결 고유번호
        couple1.add("1"); //커플1 id
        couple1.add("김지훈"); //커플1 name
        couple1.add("2"); //커플2 id
        couple1.add("이상아"); //커플2 name
        couple1.add("/storage/emulated/0/DCIM/Camera/firstman.jpg"); //사진 경로
        couple1.add("20200105"); //사귄 날짜
        Couplelist.add(couple1);

         */
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

    //커플을 등록한다.
    public String SetCoupleConnect(String Myid, String Otherid){
        ArrayList<String> data = new ArrayList<String>();
        data.add(Myid);
        data.add(Otherid);
        data.add("img");
        data.add("-");

        ArrayList<ArrayList<ArrayList<String>>> Couplelist = DoReadAll(dbname);
        int maxkey = 0;
        //값이 아무것도 없을때 처리를 해줘야돼.
        if(Couplelist.isEmpty()){
            maxkey = Couplelist.size();
        }else{ //값이 있을때
            //키값을 만들어야한다. 전체 리스트를 가져와서 가장 큰 숫자를 찾고 +1을 한다.
            ArrayList<Integer> sortdata = new ArrayList<>();
            for (int i = 0; i < Couplelist.size(); i++){
                String eachkey = Couplelist.get(i).get(0).get(0);
                sortdata.add(Integer.parseInt(eachkey));
            }
            maxkey = Collections.max(sortdata)+1; //키값으로 쓰임
        }





        //Log.d("alllist list sort", String.valueOf(sortdata));
        //Log.d("alllist list sort2", String.valueOf(maxkey));

        String key = String.valueOf(maxkey);
        String couplekey = DoAdd_json(dbname, key, data);
        return couplekey;
    }

    //해당 데이터를 수정한다.
    public int Editcoupledata(String key, String name, String order){
        int result =  DoEditOne(dbname, key, GetFildnum(order), name);
        Log.d("CoupleConnect", key+"수정완료");
        return result;
    }

    //데이터 전부 가져오기
    public ArrayList<ArrayList<ArrayList<String>>> CoupleListRead(){
        //User라는 데이터베이스에 값을 추가할 것. Userdb가 없으면 만들고 putString을 하고, 있으면 찾아서 putString 할것
        //찾을 db 이름, 저장할 데이터들,

        ArrayList<ArrayList<ArrayList<String>>> CoupleList = DoReadAll(dbname);

        return CoupleList;
    }
    //데이터 하나 가져오기
    public ArrayList<ArrayList<String>> GetOneinfo(String key){

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
    public int Deleteone(String key){
        String[] keyarr = {key};
        DoDeleteArray(dbname, keyarr);


        return 1;
    }
}
