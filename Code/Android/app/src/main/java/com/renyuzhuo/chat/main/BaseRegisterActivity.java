package com.renyuzhuo.chat.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.SoftkeyboardUtil;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseRegisterActivity extends AppCompatActivity {

    EditText phoneNumberEditText, identifyingCodeEditText, passwordInputEditText, passwordConfirmEditText;
    Button sendIdentifyCodeButton, identifyButton, registerButton;
    String phoneNumber, identifyingCode, passwordInput, passwordconfirm;

    Handler handler;
    Context context;

    EditText nickname;

    public BaseRegisterActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void initFindViewById() {
        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        identifyingCodeEditText = (EditText) findViewById(R.id.identifying_code_edit_text);
        passwordInputEditText = (EditText) findViewById(R.id.password_input_edit_text);
        passwordConfirmEditText = (EditText) findViewById(R.id.password_confirm_edit_text);

        sendIdentifyCodeButton = (Button) findViewById(R.id.send_identify_code_button);
        identifyButton = (Button) findViewById(R.id.identify_button);
        registerButton = (Button) findViewById(R.id.register_button);
        nickname = (EditText) findViewById(R.id.nickname);
    }

    boolean validate(String phone){
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        NetOption.postDataToUrl(Global.VALIDATE_URL, map, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("res").equals("success")) {
                                LogUtil.log("没有这个用户");
                                sendCode();
                            } else {
                                LogUtil.log("有这个用户");
                                ToastUtil.showToast(context, getResources().getString(R.string.exist_user));
                                SoftkeyboardUtil.hideSoftKeyboard(context, nickname);
                            }
                        } catch (JSONException e) {
                            LogUtil.log("验证是够有用户时出错");
                        }
                    }
                };
            }
        });
        return false;
    }

    abstract void sendCode();

}
