package com.huawei.cordova.anyoffice;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import com.huawei.anyoffice.sdk.SDKContext;
import com.huawei.anyoffice.sdk.SDKContextOption;
import com.huawei.anyoffice.sdk.login.LoginAgent;
import com.huawei.anyoffice.sdk.login.LoginParam;
import com.huawei.anyoffice.sdk.network.NetStatusManager;
import com.huawei.cordova.anyoffice.util.FileUtil;
import com.huawei.svn.sdk.webview.SvnWebViewProxy;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * This class echoes a string called from JavaScript.
 */
public class MyAnyOffice extends CordovaPlugin {

    private static String TAG = "MyAnyOffice";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.i(TAG, "MyAnyOffice CordovaPlugin init begin");
        super.initialize(cordova, webView);
        System.loadLibrary("svnapi");
        System.loadLibrary("anyofficesdk");
        System.loadLibrary("tfcard");
        System.loadLibrary("jniapi");
        Log.i(TAG, "MyAnyOffice CordovaPlugin init end");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (("init").equals(action)) {
            this.init(args, callbackContext);
            return true;
        } else if ("login".equals(action)) {
            this.login(args, callbackContext);
            return true;
        } else if ("getNetStatus".equals(action)) {
            int res = NetStatusManager.getInstance().getNetStatus();
            callbackContext.success(res);
        }
        return false;
    }

    private void init(JSONArray param, CallbackContext callbackContext) {
        Activity activity = this.cordova.getActivity();
        boolean setLogParam = SDKContext.getInstance().setLogParam(
                FileUtil.getSDPath() + "/" + activity.getPackageName() + "/log",
                com.huawei.anyoffice.sdk.log.Log.LOG_TYPE_DEBUG);
        Log.i(TAG, "set log path " + setLogParam + "::" + FileUtil.getSDPath() + "/" + activity.getPackageName() + "/log");
        String workPath = FileUtil.getSDPath() + "/" + activity.getPackageName() + "/workPath";
        File mFile = new File(workPath);
        if (!mFile.exists()) {
            mFile.mkdir();
        }
        Log.i(TAG, "Begin to init sdk envirenment");
        SDKContextOption sdkCtxOpt = new SDKContextOption();
        sdkCtxOpt.context = activity;
        sdkCtxOpt.userNames = null;
        sdkCtxOpt.workPath = workPath;
        boolean inited = SDKContext.getInstance().init(sdkCtxOpt);
        Log.i(TAG, "SDKContext.getInstance().init:" + inited);
        if(inited) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, inited));
        } else {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, inited));
        }
    }

    public void login(JSONArray param, CallbackContext callbackContext) {
        try {
            String username = param.getString(0);
            String password = param.getString(1);
            String gateway = param.getString(2);
            int port = param.getInt(3);

            Activity activity = cordova.getActivity();
            LoginParam loginParam = new LoginParam();
            loginParam.setServiceType(activity.getPackageName());
            loginParam.setAutoLoginType(LoginParam.AutoLoginType.auto_login_enable);
            loginParam.setLoginBackground(true);
            loginParam.setInternetAddress(new InetSocketAddress(gateway, port));
            loginParam.setAuthGateway(false);
            LoginParam.UserInfo userInfo = loginParam.new UserInfo();
            userInfo.domain = gateway;
            userInfo.userName = username;
            userInfo.password = password;
            loginParam.setUserInfo(userInfo);
            loginParam.setUseSecureTransfer(true);

            Log.i(TAG, "Begin to login gateway");
            int logined = LoginAgent.getInstance().loginSync(activity, loginParam);
            Log.i(TAG, "LoginAgent.getInstance().loginSync:" + logined);
            SvnWebViewProxy.getInstance().setWebViewUseSVN((WebView) this.webView.getView());
            if(logined == 0) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, logined));
            } else {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, logined));
            }
        } catch (JSONException e) {
            callbackContext.error(-101);
        }
    }
}
