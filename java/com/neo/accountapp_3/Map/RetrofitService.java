package com.neo.accountapp_3.Map;

import javax.xml.transform.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    //해당 링크로 헤더와 파라미터를 전송한다.
    @GET("/v2/local/search/address.json?")
    Call<String> getSearchResult(
            @Header("Authorization") String authorization, //rest api 키
            @Query("query") String query //검색어
    );
}
