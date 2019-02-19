package com.valuebiz.servicemarket.http;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * 프로젝트명    : toitdoit
 * 패키지명      : com.valuebiz.toitdoit.http
 * 작성 및 소유자 : hucloud(huttchang@gmail.com)
 * 최초 생성일   : 2018. 9. 10.
 */
public interface PushKey {

    @FormUrlEncoded
    @POST("v1/apis/my/push/token/1")
    Call<Void> addRestrationKey(
            @Header("Authorization") String requestToken,
            @Field("type") int androidType,
            @Field("token") String pushToken);
}
