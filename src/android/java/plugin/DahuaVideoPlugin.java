package com.sdr.xitang.plugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.sdr.xitang.DahuaMainActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by HeYongFeng on 2016/10/11.
 */
public class DahuaVideoPlugin extends CordovaPlugin {

    private Activity mActivity;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        mActivity = cordova.getActivity();
        if (action.equals("startDahuaVideo")){
            Log.e("携带的参数是",args.toString());
            Intent intent = new Intent(mActivity, DahuaMainActivity.class);
            mActivity.startActivity(intent);
            return true;
        }
        return false;
    }
}
