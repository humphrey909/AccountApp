package com.neo.accountapp_3.sociallogin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.neo.accountapp_3.CodeShareActivity;
import com.neo.accountapp_3.Join;
import com.neo.accountapp_3.db.CoupleConnectList;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.db.Session;
import com.neo.accountapp_3.db.User;
import com.neo.accountapp_3.mainActivity;
import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//네이버 로그인 토큰 받기
public class RequestApiTask extends AsyncTask<Void, Void, String> {
    private final Context mContext;
    private final OAuthLogin mOAuthLoginModule;
    Join oJoin;
    User oUser;
    CoupleConnectList oCoupleConnectList;
    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    Session oSession; //자동로그인을 위한 db

    public RequestApiTask(Context mContext, OAuthLogin mOAuthLoginModule, GlobalClass globalClass) {
        this.mContext = mContext;
        this.mOAuthLoginModule = mOAuthLoginModule;
        oJoin = new Join(mContext);
        oUser = new User(mContext);
        oCoupleConnectList = new CoupleConnectList(mContext);
        oSession = new Session(mContext);

        GlobalClass = globalClass;//글로벌 클래스 선언
        //GlobalClass = (GlobalClass) getApplication(); //글로벌 클래스 선언
        GlobalClass.Init(); // 초기화

    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = "https://openapi.naver.com/v1/nid/me";
        String at = mOAuthLoginModule.getAccessToken(mContext);
        Log.d("at",at);
        return mOAuthLoginModule.requestApi(mContext, at, url);
    }

    //사용자 정보 가져오기
    protected void onPostExecute(String content) {
        try {
            JSONObject loginResult = new JSONObject(content);
            if (loginResult.getString("resultcode").equals("00")){
                JSONObject response = loginResult.getJSONObject("response");
                String id = response.getString("id");
                String email = response.getString("email");
                String name = response.getString("name");
                Toast.makeText(mContext, "id : "+id +" email : "+email, Toast.LENGTH_SHORT).show();


                Log.d("response", String.valueOf(response));
                Log.d("id",id);
                Log.d("email",email);
                Log.d("name",name);

                //소셜 로그인 id 여부 체크 - 해당 id가 있는지 찾는다.
                // 있으면 그 회원으로 로그인 진행, 없으면 회원가입후 로그인 진행
                if(oJoin.ChkEmailExitence(email) == -1) { //존재하지 않음
                    //네이버 소셜 로그인시 회원가입 진행을 할 것.
                    String joinchk = oJoin.joinsuccess(email, "-", name, "naver");

                    Log.d("joinchk",joinchk);
                }else{ //존재
                    Log.d("joinchk","로그인을 진행합니다. ");

                }
                oSession.Save(email);


                    //들어오자마자 로그인이 있는지 없는지 확인해야함.
                    //자동 로그인 하기
                    //ArrayList<ArrayList<String>> Sessionlist = oSession.Getoneinfo("0");
                    //Log.d("Sessionlist", String.valueOf(Sessionlist));
                    //Log.d("Sessionlist", String.valueOf(Sessionlist.get(1).get(0)));

                   // if(!Sessionlist.isEmpty()){ //값이 있다.
                        //Log.d("Sessionvalue", "1");
                        //값이 있으니 자동 로그인 처리 한다.
                        ArrayList<ArrayList<String>> Myinfo = oUser.GetMyinfo(email);

                        String couplekey = Myinfo.get(1).get(3);
                        String myid = Myinfo.get(0).get(0);

                        if(couplekey.equals("-")){ //커플 연결 안됨
                            Intent intent = new Intent(mContext, CodeShareActivity.class);
                            intent.putExtra("Myid",myid);

                            mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }else{ //이미 커플연결 됨

                            Intent intent = new Intent(mContext, mainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//액티비티 스택제거

                            Log.d("login 커플키",couplekey);
                            GlobalClass.setcouplecode(couplekey); //글로벌 변수에 커플 key 세팅
                            GlobalClass.setmyid(myid); //글로벌 변수에 내 id 세팅

                            ArrayList<ArrayList<String>> Coupleinfo = oCoupleConnectList.GetOneinfo(couplekey);
                            //상대 아이디 찾기
                            String Otherid = "";
                            for (int i = 0; i < 2; i++){
                                Coupleinfo.get(1).get(i);
                                if(!Coupleinfo.get(1).get(i).equals(myid)){
                                    Otherid = Coupleinfo.get(1).get(i);
                                }
                            }
                            GlobalClass.setotherid(Otherid); //글로벌 변수에 상대 id 세팅
                            mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        Toast toast = Toast.makeText(mContext, "네이버 로그인", Toast.LENGTH_SHORT);
                        toast.show();
                    //}else{ //값이 없으면
                    //    Log.d("Sessionvalue", "2"); //로그인 진행
                    //}
//
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
