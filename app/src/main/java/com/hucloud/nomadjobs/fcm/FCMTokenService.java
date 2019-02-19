package com.hucloud.nomadjobs.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * 프로젝트명    : toitdoit
 * 패키지명      : com.valuebiz.toitdoit.fcm
 * 작성 및 소유자 : hucloud(huttchang@gmail.com)
 * 최초 생성일   : 2018. 9. 10.
 */
public class FCMTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        System.out.print("PUSHTOKEN : " + FirebaseInstanceId.getInstance().getToken());

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://olive.hutt.co.kr/")
//                .build();
//        PushKey settingNetwork = retrofit.create(PushKey.class);
//        settingNetwork.addRestrationKey(new Storage(this).getPrefStr("requestToken"), FirebaseInstanceId.getInstance().getToken()).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Log.d("FCMTokenService", "RefreshToken SendToServer - Success");
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.d("FCMTokenService", "RefreshToken SendToServer - Fail");
//            }
//        });

    }
}
