package com.renyuzhuo.chat.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    EditText feedback;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedback = (EditText) findViewById(R.id.feedback);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(feedback.getText().toString())){
                    Map map = new HashMap();
                    map.put("myId", ChatApplication.getUserId());
                    map.put("token", ChatApplication.getToken());
                    map.put("message", feedback.getText().toString());
                    NetOption.postDataToUrl(Global.FEEDBACK_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("res").equals("success")) {
                                            ToastUtil.showToast(FeedbackActivity.this, getResources().getString(R.string.reedback_success));
                                            finish();
                                            Anim.out(FeedbackActivity.this);
                                        }
                                    }catch (Exception e){

                                    }
                                }
                            };
                        }
                    });
                }
            }
        });

    }
}
