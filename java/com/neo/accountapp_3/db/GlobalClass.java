package com.neo.accountapp_3.db;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.kakao.sdk.common.KakaoSdk;
import com.neo.accountapp_3.R;
import com.nhn.android.naverlogin.OAuthLogin;

public class GlobalClass extends Application {
    public static String COUPLEKEY;
    public static String MYID;
    public static String OHTERID;
    OAuthLogin mOAuthLoginModule; //네이버 로그인 api
    GoogleSignInClient mGoogleSignInClient; //구글 로그인 api

    @Override   //오버라이딩 해서 onCreate()를 만들어 줍니다. 여느 클래스와 똑같이요 ㅎㅎ
    public void onCreate() {
        super.onCreate();
        COUPLEKEY = "";
        MYID = "";
        OHTERID = "";


        //카카오 로그인 api 실행 - 변순선언 없이 사용 가능
        KakaoSdk.init(this, getString(R.string.kakao_key));
    }

    @Override  //이건 선택사항인데 일단 추가해 줍니다. 데이터 공간 낭비를 방지하기 위해 추가하고 나중에 필요하면 선언하세요.
    public void onTerminate() {
        super.onTerminate();
        //instance = null;
    }

    // 초기화 함수입니다. 처음 선언을 해주면 안정적으로 초기화 되서 변수가 안정적입니다.
    public void Init() {
        COUPLEKEY = "";
        MYID = "";
        OHTERID = "";

    }

    //클래스를 선언한 뒤, 다른 액티비티에서 사용될 함수 입니다. 이건 verdiosn이라는 글로벌 변수에 flag값을 넣게다는 뜻입니다.
//다른 액티비티에서 선언 방법은 밑에 써드릴게요
    public void setcouplecode(String value){this.COUPLEKEY = value;}

    //이것은 저장된 값을 불러오는 함수입니다.
    public String getcouplecode(){return COUPLEKEY;}


    public void setmyid(String value){this.MYID = value;}

    //이것은 저장된 값을 불러오는 함수입니다.
    public String getmyid(){return MYID;}


    public void setotherid(String value){this.OHTERID = value;}

    //이것은 저장된 값을 불러오는 함수입니다.
    public String getotherid(){return OHTERID;}

    //네이버 로그인 api 글로벌 변수로 선언
    public void setnaveroauth(OAuthLogin mOAuthLoginModule){this.mOAuthLoginModule = mOAuthLoginModule; }
    public OAuthLogin getnaveroauth(){return mOAuthLoginModule; }

    //구글 로그인 api 글로벌 변수로 선언
    public void setgoogleoauth(GoogleSignInClient mGoogleSignInClient){this.mGoogleSignInClient = mGoogleSignInClient; }
    public GoogleSignInClient getgoogleoauth(){return mGoogleSignInClient; }
}
