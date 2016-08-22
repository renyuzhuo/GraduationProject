package com.renyuzhuo.chat.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.model.Message;

import org.joda.time.DateTime;

public class EmitTestActivity extends AppCompatActivity {

    Button button;
    EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emit_test);
        button = (Button) findViewById(R.id.send_message);
        number = (EditText) findViewById(R.id.num);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(number.getText().toString())) {
                    Message message = new Message();
                    message.setId(--ChatBaseDetailActivity.messageId);
                    message.setFromuser(ChatApplication.getUserId());
                    message.setTouser(0);
                    message.setMessage("测试消息");
                    message.setType(1);
                    message.setPath("");
                    message.setToken(ChatApplication.getToken());
                    message.setTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                    message.setFromstate("sending");
//                MessageSQL.insertIntoMessage(message);
                    for (int i = 0; i < Integer.valueOf(number.getText().toString()); i++) {
                        message.setMessage("测试消息:" + i);
                        ChatService.sendMessage(message);
                    }
                }
            }
        });
    }
}
