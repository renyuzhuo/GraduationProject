package com.renyuzhuo.chat.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.SoftkeyboardUtil;
import com.renyuzhuo.chat.util.TextUtils;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterAlidayuActivity extends BaseRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

        initFindViewById();
        initOnClickListener();
    }

    @Override
    void sendCode() {
        LogUtil.log("正在发送，请稍后");
        ToastUtil.showToast(context, getResources().getString(R.string.sendding));
        SoftkeyboardUtil.hideSoftKeyboard(context, phoneNumberEditText);

        Map<String, String> map = new HashMap<>();
        map.put("phoneNumber", phoneNumber);
        NetOption.postDataToUrl(Global.CODE_URL, map, new NetOptionResponse() {
            @Override
            public Response.Listener<JSONObject> success() {
                return new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, getBaseContext().getResources().getText(R.string.get_indetity_code_success), Toast.LENGTH_SHORT).show();
                        sendIdentifyCodeButton.setVisibility(View.GONE);
                        phoneNumberEditText.setEnabled(false);
                        identifyButton.setVisibility(View.VISIBLE);
                    }
                };
            }
        });
    }

    private void initOnClickListener() {
        sendIdentifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberEditText.getText().toString();
                if (TextUtils.isMobilNumber(phoneNumber)) {
                    validate(phoneNumber);
                } else {
                    LogUtil.log("输入手机号错误");
                    ToastUtil.showToast(context, getResources().getString(R.string.err_phone_number));
                    SoftkeyboardUtil.hideSoftKeyboard(context, phoneNumberEditText);
                }
            }
        });

        identifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyingCode = identifyingCodeEditText.getText().toString();
                if (!android.text.TextUtils.isEmpty(identifyingCode)) {
                    Map<String, String> map = new HashMap();
                    map.put("phoneNumber", phoneNumber);
                    map.put("identifyingCode", identifyingCode);

                    NetOption.postDataToUrl(Global.IDENTIFYINGCODE, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.i("RESULOT", response.getString("res"));

                                        Toast.makeText(context, getBaseContext().getResources().getText(R.string.confirm_identity_code_success), Toast.LENGTH_SHORT).show();
                                        identifyingCodeEditText.setVisibility(View.GONE);
                                        identifyButton.setVisibility(View.GONE);
                                        passwordInputEditText.setVisibility(View.VISIBLE);
                                        passwordConfirmEditText.setVisibility(View.VISIBLE);
                                        registerButton.setVisibility(View.VISIBLE);
                                        nickname.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                        }
                    });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInput = passwordInputEditText.getText().toString();
                passwordconfirm = passwordConfirmEditText.getText().toString();
                if (passwordInput.length() == 0) {
                    LogUtil.log("密码不可为空");
                    ToastUtil.showToast(context, getResources().getString(R.string.not_null_password));
                    SoftkeyboardUtil.hideSoftKeyboard(context, phoneNumberEditText);
                } else if (passwordInput.equals(passwordconfirm)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("username", phoneNumber);
                    map.put("password", passwordconfirm);
                    String nicknameStr;
                    if (nickname.getText() == null || nickname.getText().toString().length() == 0) {
                        nicknameStr = "";
                    } else {
                        nicknameStr = nickname.getText().toString();
                    }
                    map.put("nickname", nicknameStr);

                    NetOption.postDataToUrl(Global.REGISTER_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int t = response.getInt("id");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                        }
                    });
                    Toast.makeText(context, getBaseContext().getResources().getText(R.string.register_success), Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                } else {
                    LogUtil.log("密码输入不一致");
                    ToastUtil.showToast(context, getResources().getString(R.string.not_same_password));
                    SoftkeyboardUtil.hideSoftKeyboard(context, phoneNumberEditText);
                }
            }
        });

    }

}
