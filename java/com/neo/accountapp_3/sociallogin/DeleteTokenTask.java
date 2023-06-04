package com.neo.accountapp_3.sociallogin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;

//네이버 로그아웃 토큰 삭제
public class DeleteTokenTask extends AsyncTask<Void, Void, Boolean> {
    private final Context mContext;
    private final OAuthLogin mOAuthLoginModule;
    public DeleteTokenTask(Context mContext, OAuthLogin mOAuthLoginModule) {
        this.mContext = mContext;
        this.mOAuthLoginModule = mOAuthLoginModule;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext);

        if (!isSuccessDeleteToken) {
            // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
            // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
            Log.d("delete", "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext));
            Log.d("delete", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext));
        }

        return isSuccessDeleteToken;
    }

    protected void onPostExecute(boolean isSuccessDeleteToken) {
        //Log.d("isSuccessDeleteToken", String.valueOf(isSuccessDeleteToken));
    }
}
