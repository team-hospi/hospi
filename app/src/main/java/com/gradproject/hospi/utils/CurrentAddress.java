package com.gradproject.hospi.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CurrentAddress implements MapReverseGeoCoder.ReverseGeoCodingResultListener {
    public static MapReverseGeoCoder.ReverseGeoCodingResultListener reverseGeoCodingResultListener;
    public static final String NO_LOCATION_INFORMATION = "위치정보 없음";


    // 현재 주소 얻기
    public static String getCurrentAddress(Context context, double latitude, double longitude) {
        /*
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder("07d563b8fc089510a0c926182cf35b1f", mapPoint, reverseGeoCodingResultListener, activity);
        reverseGeoCoder.startFindingAddress();
        return reverseGeoCoder.findAddressForMapPointSync("07d563b8fc089510a0c926182cf35b1f", mapPoint);
*/

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제 - 지오코더 서비스 사용불가
            return NO_LOCATION_INFORMATION;
        } catch (IllegalArgumentException illegalArgumentException) {
            // 잘못된 GPS 좌표
            return NO_LOCATION_INFORMATION;
        }

        if (addresses == null || addresses.size() == 0) {
            // 주소 미발견
            return NO_LOCATION_INFORMATION;
        }

        Address address = addresses.get(0);
        String str = address.getAddressLine(0).toString()+"\n"; // 주소값 구함

        // 주소 맨 앞 대한민국 제외 (읍, 면, 동)까지만 표시
        String strArr[] = str.split(" ");
        String addr = "";

        for(int i=1; i<strArr.length; i++){
            if(strArr[i].endsWith("읍") || strArr[i].endsWith("면") || strArr[i].endsWith("동")){
                addr += strArr[i];
                break;
            }else{
                addr += strArr[i] + " ";
            }
        }

        return addr;

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }
}
