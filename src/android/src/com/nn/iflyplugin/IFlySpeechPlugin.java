package com.nn.iflyplugin;

import android.content.Context;
import android.speech.tts.Voice;
import android.util.Log;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class IFlySpeechPlugin extends CordovaPlugin {
    private static String TAG = "LPCORE";
    private Context         context;
    private CallbackContext callbackContext;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        context = cordova.getActivity();
        try {
            String appid = context.getString(getId("app_id", "string"));
            SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + appid);
        } catch (Exception e) {
            SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5cda7825");
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (action.equals("startListen")) {//开始听写
            boolean isShowDialog;
            try {
                isShowDialog = args.getBoolean(0);
            } catch (Exception e) {
                isShowDialog = true;
            }
            final boolean flags = args.getBoolean(1);
            if (isShowDialog) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initSpeech(context, flags);
                    }
                });
            }
            return true;
        }
        return false;
    }

    public void initSpeech(final Context context, boolean languages) {
        /*
         * 初始化语音识别
         */
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        if (languages) {
            mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        } else {
            mDialog.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        }
        //3.设置回调接口
        try {
            mDialog.setListener(new RecognizerDialogListener() {
                @Override
                public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                    if (!isLast) {
                        //解析语音
                        String result = parseVoice(recognizerResult.getResultString());
                        Log.d(TAG, result);
                        callbackContext.success(result);
                    }
                }

                @Override
                public void onError(SpeechError speechError) {
                    Log.d(TAG, "in error");
                }
            });
            //4.显示dialog，接收语音输入
            mDialog.show();
        } catch (Exception e) {
            Log.d(TAG, "e:" + e.getMessage());
        }
    }
    public String parseVoice(String resultString) {
        /*
         * 解析语音json
         */
        Gson  gson      = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuffer            sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }
    public class Voice {
        /*
         * 语音对象封装
         */
        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }


    private int getId(String idName, String type) {
        return context.getResources().getIdentifier(idName, type, context.getPackageName());
    }
}

