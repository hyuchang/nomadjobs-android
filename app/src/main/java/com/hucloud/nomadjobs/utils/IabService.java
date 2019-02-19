package com.hucloud.nomadjobs.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 프로젝트명    : toitdoit
 * 패키지명      : com.android.vending.billing
 * 작성 및 소유자 : hucloud(huttchang@gmail.com)
 * 최초 생성일   : 28/09/2018
 */
public class IabService {

    /**
     * AIDL
     */
    private IInAppBillingService mIabService;
    private ServiceConnection mServiceConn = null;
    private ArrayList<String> mSkuList = new ArrayList();

    private Context mContext;
    private String mBillingType = "inapp";
    private static final int INAPP_API_VERSION = 3;
    public static final int INAPP_ERROR_EMPTY_SKUS = 3_1;
    public static final int INAPP_SUCCESS_OPEN_DIALOG = 1_1;

    private IabService(){
    }

    public IabService(Context context){
        this.mContext = context;
        Log.d("iabService","start IabService");
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIabService = IInAppBillingService.Stub.asInterface(service);
                Log.d("iabService", "onServiceConnected");

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIabService = null;
                Log.d("iabService", "onServiceDisconnected");

            }
        };
    }

    public ServiceConnection getServiceConnection(){
        return this.mServiceConn;
    }

    public boolean isClose(){
        return mServiceConn == null;
    }

    public void close(){
        this.mServiceConn = null;
    }

    public void addRequestItemId(String itemId){
        this.mSkuList.add(itemId);
    }

    public String getBillingType() {
        return mBillingType;
    }

    public void setBillingType(String mBillingType) {
        this.mBillingType = mBillingType;
    }

    public int getInAppSKUList(){
        if ( mSkuList.isEmpty()) {
            throw new IllegalArgumentException("Empty SKU, You can set Sku ex) addRequestItemId");
        }
        final Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", mSkuList);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    Bundle skuDetails = mIabService.getSkuDetails(INAPP_API_VERSION, mContext.getPackageName(), mBillingType, querySkus);
                    System.out.println( skuDetails.getStringArrayList("DETAILS_LIST") );

                    if ( skuDetails.getInt("RESPONSE_CODE") == 0 ) {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                        for (String thisResponse : responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");

                            System.out.println( sku + " : " + price );
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        return INAPP_SUCCESS_OPEN_DIALOG;
    }

}
