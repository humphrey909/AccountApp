package com.neo.accountapp_3.db;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class WhoList  extends SharedModel {

    String dbname = "";
    CoupleConnectList oCoupleConnectList;
    User oUser;


    //기본 세팅으로 세개의 값이 들어 가야함.
    public WhoList(Context mContext){
        this.oContext = mContext; //부모클래스에 전달

        dbname = "WhoCategorey"; //db name 지정

        //고유키 - 커플고유키, 카테고리이름
        Field = new String[]{
                "couplekey", "name", "userkey"
        };
    }

    //인물 카테고리 객체를 등록한다.
    public String SaveCategory(String Couplekey, String name){
        ArrayList<String> savedata = new ArrayList<>();
        savedata.add(Couplekey); //커플 고유번호
        savedata.add(name); //이름
        savedata.add("-"); //연결 user key

        ArrayList<ArrayList<ArrayList<String>>> alllist = DoReadAll(dbname);

        int maxkey = 0;
        //값이 아무것도 없을때 처리를 해줘야돼.
        if(alllist.isEmpty()) {
            maxkey = alllist.size();
        }else{
            //키값을 만들어야한다. 전체 리스트를 가져와서 가장 큰 숫자를 찾고 +1을 한다.
            ArrayList<Integer> sortdata = new ArrayList<>();
            for (int i = 0; i < alllist.size(); i++){
                String eachkey = alllist.get(i).get(0).get(0);
                sortdata.add(Integer.parseInt(eachkey));
            }
            maxkey = Collections.max(sortdata)+1; //키값으로 쓰임
        }


        //Log.d("alllist list sort", String.valueOf(sortdata));
        //Log.d("alllist list sort2", String.valueOf(maxkey));

        String key = String.valueOf(maxkey);
        String getkey = DoAdd_json(dbname, key, savedata); //저장하고 그 key값을 던져줌

        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("SaveWhoCategory", getkey+"저장완료");
        return getkey;
    }

    //인물 카테고리 기본 데이터를 저장한다.
    public int SaveBasicCategory(String Couplekey){
        if(!Couplekey.equals("")) {

            //커플키로 데이터를 가져옴
            oCoupleConnectList = new CoupleConnectList(oContext);
            oUser = new User(oContext);
            ArrayList<ArrayList<String>> Coupleinfo = oCoupleConnectList.GetOneinfo(Couplekey);
            ArrayList<ArrayList<String>> myinfo = oUser.GetMyinfo(Coupleinfo.get(1).get(0)); //내정보
            ArrayList<ArrayList<String>> otherinfo = oUser.GetMyinfo(Coupleinfo.get(1).get(1)); //상대정보

            //기본으로 넣을 데이터 array로 만듬 : 인물 카테고리 명칭들
            String[] BasicArr = {myinfo.get(1).get(1), otherinfo.get(1).get(1), "우리"};

            for (int i = 0; i < BasicArr.length; i++){
                String DataAlias = BasicArr[i];

                ArrayList<String> savedata = new ArrayList<>();
                savedata.clear();//초기화
                savedata.add(Couplekey); //커플 고유번호
                savedata.add(DataAlias); //데이터 이름
                if(i == 0){ //내 id
                    savedata.add(myinfo.get(0).get(0)); //연결 유저 key
                }else if(i == 1){ //상대 id
                    savedata.add(otherinfo.get(0).get(0)); //연결 유저 key
                }else{ //우리는 패스
                    savedata.add("-"); //연결 유저 key
                }


                ArrayList<ArrayList<ArrayList<String>>> alllist = DoReadAll(dbname);

                int maxkey = 0;
                //값이 아무것도 없을때 처리를 해줘야돼.
                if(alllist.isEmpty()) {
                    maxkey = alllist.size();
                }else{
                    //키값을 만들어야한다. 전체 리스트를 가져와서 가장 큰 숫자를 찾고 +1을 한다.
                    ArrayList<Integer> sortdata = new ArrayList<>();
                    for (int j = 0; j < alllist.size(); j++){
                        String eachkey = alllist.get(j).get(0).get(0);
                        sortdata.add(Integer.parseInt(eachkey));
                    }
                    maxkey = Collections.max(sortdata)+1; //키값으로 쓰임
                }

                String key = String.valueOf(maxkey);

                String getkey = DoAdd_json(dbname, key, savedata); //저장하고 그 key값을 던져줌

                Log.d("SaveWhoCategory", getkey + "저장완료");
            }


            return 1;
        }else{
            return 0;
        }
    }

    //데이터 전부 가져오기
    public ArrayList<ArrayList<ArrayList<String>>> WholistRead(){

        ArrayList<ArrayList<ArrayList<String>>> Accountlist = DoReadAll(dbname);

        return Accountlist;
    }
    //데이터 하나 가져오기
    public  ArrayList<ArrayList<String>> Getoneinfo(String key){

        ArrayList<ArrayList<String>> result = DoReadOne(dbname, key);
        return result;
    }

    //db초기화
    public int Init(){
        //User라는 데이터베이스에 값을 추가할 것. Userdb가 없으면 만들고 putString을 하고, 있으면 찾아서 putString 할것
        //찾을 db 이름, 저장할 데이터들,

        int Accountlist = DoDeleteAll(dbname);

        return Accountlist;
    }

    //하나만 삭제
    public int Deleteone(String key){
        String[] keyarr = {key};
        DoDeleteArray(dbname, keyarr);

        return 1;
    }

    //해당 데이터를 수정한다.
    public String EditCategory(String whokey, String Couplekey, String userkey, String name){
        Log.d("커플 키", Couplekey); //데이터 변경 전값을 가져와야함
        Log.d("인물 카테고리 키", String.valueOf(whokey)); //데이터 변경 전값을 가져와야함

        ArrayList<String> whodata = new ArrayList<>();
        whodata.add(Couplekey); //커플 고유번호
        whodata.add(name); //인물 카테고리키
        whodata.add(userkey); //유저 키

        //키값이 이미 존재하면 덮어 씌움
        String key = whokey; //키는 리스트에서 가장 큰 숫자
        String GetWhokey = DoAdd_json(dbname, key, whodata); //저장하고 그 key값을 던져줌

        //Log.d("저장할 값들", String.valueOf(accountdata));
        Log.d("TAG", GetWhokey+"수정완료");


        return GetWhokey;
    }


}
