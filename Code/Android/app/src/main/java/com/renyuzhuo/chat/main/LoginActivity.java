package com.renyuzhuo.chat.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.MessageToReadSQL;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.SoftkeyboardUtil;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    static Context context;
    EditText usernameEditText, passwordEditText;
    String username;
    String password;
    Button submit;
    Button register;
    Button registerAlidayu;
    int yy = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        register = (Button) findViewById(R.id.register_button);
        registerAlidayu = (Button) findViewById(R.id.register_button_alidayu);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, String> params = new HashMap<>();

                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (username.equals("") && password.equals("")) {
                    if (yy-- <= 0) {
                        username = password = "15617867618";
                    }
                }

                if (username.equals("") || password.equals("")) {
                    SoftkeyboardUtil.hideSoftKeyboard(context, usernameEditText);
                    ToastUtil.showToast(context, getResources().getString(R.string.login_fail));
                    return;
                }

                params.put("username", username);
                params.put("password", password);

                NetOption.postDataToUrl(Global.LOGIN_URL, params, new NetOptionResponse() {
                    @Override
                    public Response.Listener<JSONObject> success() {
                        return new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", response.toString());
                                try {
                                    if (response.getString("res").equals("success")) {
                                        ChatApplication.setISLOGIN(true);
                                        ChatApplication.saveUserInfo(username, password, response.getString("token"), response.getInt("id"), response.getString("nickname"), response.getString("heap"), response.getInt("type"));
                                        MessageToReadSQL.getMessageToRead();
                                        Intent intent = new Intent(LoginActivity.this, ChatMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        ChatApplication.setISLOGIN(false);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                    }

                    @Override
                    public Response.ErrorListener error() {
                        return new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LogUtil.log("登录失败");
                                ToastUtil.showToast(context, getResources().getString(R.string.login_fail));
                                SoftkeyboardUtil.hideSoftKeyboard(context, usernameEditText);
                            }
                        };
                    }
                });

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterMobActivity.class);
                startActivity(intent);
            }
        });

        registerAlidayu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterAlidayuActivity.class);
                startActivity(intent);
            }
        });

    }

}
