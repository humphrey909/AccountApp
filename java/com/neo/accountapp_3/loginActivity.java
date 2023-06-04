package com.neo.accountapp_3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.neo.accountapp_3.db.AccountBook;
import com.neo.accountapp_3.db.CoupleConnectList;
import com.neo.accountapp_3.db.GlobalClass;
import com.neo.accountapp_3.db.PaymentKindsList;
import com.neo.accountapp_3.db.Session;
import com.neo.accountapp_3.db.UseKindsList;
import com.neo.accountapp_3.db.User;
import com.neo.accountapp_3.db.WhoList;
import com.neo.accountapp_3.sociallogin.RequestApiTask;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class loginActivity extends AppCompatActivity {
    Join oJoin;
    Login ologin;
    User oUser; //전체 회원리스트 확인하는 용도로만 쓰임
    CoupleConnectList oCoupleConnectList; //잘못된 데이터 리셋용도
    AccountBook oAccountbook; //잘못된 데이터 리셋용도
    WhoList oWhoList; //잘못된 데이터 리셋용도
    UseKindsList oUseKindsList; //잘못된 데이터 리셋용도
    PaymentKindsList oPaymentKindsList; //잘못된 데이터 리셋용도


    Session oSession; //자동로그인을 위한 db


    String inputid = "";
    String inputpw = "";

    com.neo.accountapp_3.db.GlobalClass GlobalClass; //글로벌 클래스
    OAuthLogin mOAuthLoginModule; //네이버 로그인 api

    int RC_SIGN_IN = 1; //구글 로그인 api
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient; //구글 로그인 api
    private FirebaseAuth mAuth;

    Context mContext;
    Button naverlogin;
    Button kakaologin;
    Button googlelogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        mContext = getApplicationContext();



        //main을 띄우고 로딩을 띄우는데 메인 로딩 페이지를 같이 띄우고 로딩만 사라져야함.
        //이놈을 핸들러 스레드 처리해야겟다.
       // Intent intent2 = new Intent(this, LoadingActivity.class);
        //startActivity(intent2);
        //startLoading();

        GlobalClass = (GlobalClass) getApplication(); //글로벌 클래스 선언
        GlobalClass.Init(); // 초기화

        oJoin = new Join(this);
        ologin = new Login(this);
        oUser = new User(this);
        oCoupleConnectList = new CoupleConnectList(this);
        oAccountbook = new AccountBook(this);
        oWhoList = new WhoList(this);
        oUseKindsList = new UseKindsList(this);
        oPaymentKindsList = new PaymentKindsList(this);
        oSession = new Session(this);

        //initdelete(); //db 전체 초기화


        naverlogin = findViewById(R.id.naverlogin); //네이버 로그인
        kakaologin = findViewById(R.id.kakaologin); //카카오 로그인
        googlelogin = findViewById(R.id.googlelogin); //구글 로그인

        //네이버 로그인 api 키 정의
        mOAuthLoginModule = OAuthLogin.getInstance();
        GlobalClass.setnaveroauth(mOAuthLoginModule);
        mOAuthLoginModule.init(
                mContext
                ,getString(R.string.naver_client_id)
                ,getString(R.string.naver_client_secret)
                ,getString(R.string.naver_client_name)
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );

        //구글 로그인 api 키 정의
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GlobalClass.setgoogleoauth(mGoogleSignInClient);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();




        //들어오자마자 로그인이 있는지 없는지 확인해야함.
        //자동 로그인 하기
        ArrayList<ArrayList<String>> Sessionlist = oSession.Getoneinfo("0");
        Log.d("Sessionlist", String.valueOf(Sessionlist));
        //Log.d("Sessionlist", String.valueOf(Sessionlist.get(1).get(0)));

        if(!Sessionlist.isEmpty()){ //값이 있다.
            Log.d("Sessionvalue", "1");
            //값이 있으니 자동 로그인 처리 한다.
            ArrayList<ArrayList<String>> Myinfo = oUser.GetMyinfo(Sessionlist.get(1).get(0));

            String couplekey = Myinfo.get(1).get(3);
            String myid = Myinfo.get(0).get(0);

            if(couplekey.equals("-")){ //커플 연결 안됨
                Intent intent = new Intent(getApplicationContext(), CodeShareActivity.class);
                intent.putExtra("Myid",myid);

                startActivity(intent);
            }else{ //이미 커플연결 됨

                Intent intent = new Intent(getApplicationContext(), mainActivity.class);
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
                startActivity(intent);
            }
            Toast toast = Toast.makeText(getApplicationContext(), "자동 로그인", Toast.LENGTH_SHORT);
            toast.show();
        }else{ //값이 없으면
            Log.d("Sessionvalue", "2"); //로그인 진행
        }









        //네이버 로그인 버튼
        naverlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                @SuppressLint("HandlerLeak")
                OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        if (success) {
                            String accessToken = mOAuthLoginModule.getAccessToken(mContext); //사용자 액세스 토큰 값
                            String refreshToken = mOAuthLoginModule.getRefreshToken(mContext); //사용자 리프레시 토큰 값
                            long expiresAt = mOAuthLoginModule.getExpiresAt(mContext); //엑세스 토큰 만료 시간
                            String tokenType = mOAuthLoginModule.getTokenType(mContext); //토큰 타입, bearer로 고정

                            Log.i("LoginData1","accessToken : "+ accessToken);
                            Log.i("LoginData2","refreshToken : "+ refreshToken);
                            Log.i("LoginData3","expiresAt : "+ expiresAt);
                            Log.i("LoginData4","tokenType : "+ tokenType);


                            new RequestApiTask(mContext, mOAuthLoginModule, GlobalClass).execute();
                        } else {
                            String errorCode = mOAuthLoginModule
                                    .getLastErrorCode(mContext).getCode();
                            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                            Toast.makeText(mContext, "errorCode:" + errorCode
                                    + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                        }
                    };
                };
                mOAuthLoginModule.startOauthLoginActivity(loginActivity.this, mOAuthLoginHandler);
            }
        });

        //카카오톡 앱 - 로그인 여부 확인
        /*
        UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
            @Override
            public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {
                if(user != null){
                    Log.d("카카오톡 앱","로그인 되어있음!");
                }else{
                    Log.d("카카오톡 앱","로그인 안되어있음!");
                }
                updateKakaoLoginUi();
                return null;
            }
        });*/

        //카카오 로그인 후 정보를 가져옴.
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken OAuthToken, Throwable throwable) {
                Log.d("kakaolog", String.valueOf(OAuthToken));
                Log.d("kakaolog", String.valueOf(throwable));
                if(OAuthToken != null){
                    Log.d("kakaolog","2");
                    Log.d("kakaolog", String.valueOf(OAuthToken));
                }
                if(throwable != null){

                    Log.d("kakaolog","3");
                    Log.d("kakaolog", String.valueOf(throwable));
                }
                loginActivity.this.updateKakaoLoginUi();
                return null;
            }
        };


        //카카오 로그인 클릭
        kakaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //카카오톡 설치 여부로 앱으로 진행
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(loginActivity.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(loginActivity.this, callback);

                }else{ //웹으로 로그인 실행
                    UserApiClient.getInstance().loginWithKakaoAccount(loginActivity.this, callback);
                    Log.d("kakaoweb","실행");
                }
            }
        });
        //updateKakaoLoginUi();
        //getHashKey();

        //구글 로그인 클릭
        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });



        //임시로 만든 것. 로그아웃시 실행
        /*
        Button btn_logout;
        btn_logout = findViewById(R.id.naverlogout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //네이버 로그아웃
                mOAuthLoginModule.logout(mContext);
                Toast.makeText(loginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                new DeleteTokenTask(mContext, mOAuthLoginModule).execute();


                //카카오 로그아웃

                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });


                //구글 로그아웃
                mGoogleSignInClient.signOut();
            }
        });*/


        //수동 로그인 하기
        Button loginbtn = (Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText idtext = (EditText)findViewById(R.id.idtext);
                EditText pwtext = (EditText)findViewById(R.id.pwtext);
                inputid = idtext.getText().toString();
                inputpw = pwtext.getText().toString();

                CharSequence text;

                //로그인 처리

                int loginchk = ologin.loingprocess(inputid, inputpw);

                if(loginchk == 1){  //로그인 성공
                    text = "로그인 성공";
                }else{ //로그인 실패
                    text = "로그인 실패";
                }

                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();

                if(loginchk == 1){ //로그인되면 전송

                    ArrayList<ArrayList<String>> Myinfo = oUser.GetMyinfo(inputid);
                    String couplekey = Myinfo.get(1).get(3);
                    String myid = Myinfo.get(0).get(0);

                    oSession.Save(myid); //로그인 세션 저장

                    if(couplekey.equals("-")){ //커플 연결 안되어 있음
                        Intent intent = new Intent(getApplicationContext(), CodeShareActivity.class);
                        intent.putExtra("Myid",myid);

                        startActivity(intent);
                    }else{ //이미 커플연결 됨
                        Intent intent = new Intent(getApplicationContext(), mainActivity.class);
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

                        startActivity(intent);
                    }
                }
            }
        });


        Button joinbtn = (Button)findViewById(R.id.joinbtn);
        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), joinActivity.class);
                startActivity(intent);
                //ebView.clearCache(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        //전체 리스트 확인
        ArrayList<ArrayList<ArrayList<String>>> Userlist = oUser.UserRead();
        Log.d("유저 전체 리스트", String.valueOf(Userlist));

        ArrayList<ArrayList<ArrayList<String>>> Couplelist = oCoupleConnectList.CoupleListRead();
        Log.d("커플 전체 list", String.valueOf(Couplelist));

        //ArrayList<ArrayList<String>> Myinfo = oUser.GetMyinfo("root@naver.com");
        //Log.d("Myinfo", String.valueOf(Myinfo));

        //가계부 전체 리스트
        ArrayList<ArrayList<ArrayList<String>>> Accountinfo = oAccountbook.AccountRead();
        Log.d("Accountinfo", String.valueOf(Accountinfo));


        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("currentUser", String.valueOf(currentUser));
    }


    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent2= new Intent(getApplicationContext(), LoadingActivity.class);
                startActivity(intent2);  //Loagin화면을 띄운다.
                finish();   //현재 액티비티 종료
            }
        }, 2000); // 화면에 Logo 2초간 보이기
    }// startLoading Method..

    public void updateKakaoLoginUi() {
        // 카카오 UI 가져오는 메소드 (로그인 핵심 기능)
        UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
            @Override
            public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {
                Log.i("updateKakaoLoginUi", "id " + user);
                //Log.i("updateKakaoLoginUi", "id " + throwable);

                if (user != null) {
                    //Toast.makeText(GlobalClass, "dddddd", Toast.LENGTH_SHORT).show();
                    // 유저 정보가 정상 전달 되었을 경우

                    String id = String.valueOf(user.getId());
                    String nickname = user.getKakaoAccount().getProfile().getNickname();

                    Log.i("TAG", "id " + user.getId());
                    // 유저의 고유 아이디를 불러옵니다.
                    Log.i("TAG", "invoke: nickname=" + user.getKakaoAccount().getProfile().getNickname());


                    //아이디를 안가져오니 닉네임만 으로 랜덤 id를 만들어서 처리한다.
                    if(oJoin.ChkEmailExitence(id) == -1) { //존재하지 않음
                        //네이버 소셜 로그인시 회원가입 진행을 할 것.
                        String joinchk = oJoin.joinsuccess(id, "-", nickname, "kakao");

                        Log.d("joinchk",joinchk);
                    }else { //존재

                    }
                    oSession.Save(id);

                        ArrayList<ArrayList<String>> Myinfo = oUser.GetMyinfo(id);

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
                        Toast toast = Toast.makeText(mContext, "카카오 로그인", Toast.LENGTH_SHORT);
                        toast.show();



                    // 유저의 닉네임을 불러옵니다.
                    //Log.i("TAG", "userimage " + user.getKakaoAccount().getProfile().getProfileImageUrl());
                    // 유저의 이미지 URL을 불러옵니다.
                    // 이 부분에는 로그인이 정상적으로 되었을 경우 어떤 일을 수행할 지 적으면 됩니다.

                } if (throwable != null) {
                    // 로그인 시 오류 났을 때
                    // 키해시가 등록 안 되어 있으면 오류 납니다.
                    Log.w("TAG", "invoke: " + throwable.getLocalizedMessage());
                } return null;
            }
        });
    }
/*

//카카오 로그인 해시키를 받아온다. 한번만 사용
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

 */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { //구글 로그인 api
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("task", String.valueOf(task));
            Log.d("RC_SIGN_IN", String.valueOf(RC_SIGN_IN));
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
/*
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();

                Log.d("TAG", "handleSignInResult:personName "+personName);
                Log.d("TAG", "handleSignInResult:personGivenName "+personGivenName);
                Log.d("TAG", "handleSignInResult:personEmail "+personEmail);
                Log.d("TAG", "handleSignInResult:personId "+personId);
                Log.d("TAG", "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d("TAG", "handleSignInResult:personPhoto "+personPhoto);
                Log.d("TAG", "handleSignInResult:getIdToken "+account.getIdToken());*/

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    //파이어베이스를 통해 로그인한 구글 계정을 가져온다.
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String id = user.getUid();
                            String name = user.getDisplayName();
                            Uri photoUrl = user.getPhotoUrl();

                            Log.d("user", String.valueOf(user));
                            Log.d("id", String.valueOf(id));
                            Log.d("email", String.valueOf(email));
                            Log.d("name", String.valueOf(name));
                            Log.d("photoUrl", String.valueOf(photoUrl));

                            if(oJoin.ChkEmailExitence(email) == -1) { //존재하지 않음
                                //네이버 소셜 로그인시 회원가입 진행을 할 것.
                                String joinchk = oJoin.joinsuccess(email, "-", name, "google");

                                Log.d("joinchk",joinchk);
                            }else { //존재
                                Log.d("joinchk", "로그인을 진행합니다. ");
                            }
                            oSession.Save(email);

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
                                Toast toast = Toast.makeText(mContext, "구글 로그인", Toast.LENGTH_SHORT);
                                toast.show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });
    }

    //전체를 초기화한다.
    public void initdelete(){
        //가계부 전체 데이터에 강제로 시간 값 넣어줄때 사용함.
        /*ArrayList<ArrayList<ArrayList<String>>> tolist = oAccountbook.AccountRead();

        for (int i = 0; i < tolist.size(); i++){

            Log.d("각 데이터들",tolist.get(i).get(0).get(0));
            oAccountbook.dataedit_addtime(tolist.get(i).get(0).get(0));
        }*/

        //하나의 가계부의 시간을 변경함.
        //oAccountbook.dataedit_addtimeone("16","regtime", "2022-02-06 03:00:59");


        oUser.Init(); //db 초기화
        oCoupleConnectList.Init(); //db 초기화
        oAccountbook.Init(); //db 초기화
        oWhoList.Init(); //db 초기화
        oUseKindsList.Init(); //db 초기화
        oPaymentKindsList.Init(); //db 초기화
        oSession.Init(); //db 초기화


// ArrayList 준비
/*
        ArrayList<ArrayList<ArrayList<String>>> selectlist = oAccountbook.AccountRead();

        //가격 정렬
        ArrayList<PriceSort> pricesort = new ArrayList<>();
        for (int i = 0; i < selectlist.size(); i++){
            String key = selectlist.get(i).get(0).get(0);
            int price = Integer.parseInt(selectlist.get(i).get(1).get(2));
            pricesort.add(new PriceSort(key, price));
        }

        Log.d("원본 리스트", String.valueOf(pricesort));

        Collections.sort(pricesort, new PriceComparator());
        Log.d("가격 정렬 리스트 ", String.valueOf(pricesort));

        Collections.sort(pricesort, new PriceComparator().reversed());
        Log.d("가격 정렬 리스트 ", String.valueOf(pricesort));



        //날짜 정렬
        ArrayList<RegdateSort> regdatesort = new ArrayList<>();
        for (int i = 0; i < selectlist.size(); i++){
            String key = selectlist.get(i).get(0).get(0);
            String regdate = selectlist.get(i).get(1).get(7);
            regdatesort.add(new RegdateSort(key, regdate));
        }

        Log.d("원본 리스트", String.valueOf(regdatesort));

        Collections.sort(regdatesort, new RegdateComparator());
        Log.d("등록날짜 정렬 리스트 오름차순", String.valueOf(regdatesort));

        Collections.sort(regdatesort, new RegdateComparator().reversed());
        Log.d("등록날짜 정렬 리스트 내림차순", String.valueOf(regdatesort));
*/
    }
}


