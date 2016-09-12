package com.wangyi.UIview.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wangyi.UIview.BaseFragment;
import com.wangyi.define.EventName;
import com.wangyi.function.HttpsFunc;
import com.wangyi.reader.R;
import com.wangyi.utils.ItOneUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by eason on 5/24/16.
 */
@ContentView(R.layout.fragment_register2)
public class RegisterFragment2 extends BaseFragment {
    @ViewInject(R.id.tel)
    private TextView tel;
    @ViewInject(R.id.checknum)
    private EditText checknum;
    @ViewInject(R.id.info)
    private  TextView info;

    private int time = 60;
    private String id;

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            id = getMessage();
            tel.setText("您的手机号："+id);
            run.run();
        }else{
            time = 60;
            handler.removeCallbacks(run);
        }
    }

    private Handler handler = new Handler();
    private Runnable run = new Runnable(){

        @Override
        public void run() {
            if(time == 0){
                info.setText("重新发送");
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = 60;
                        info.setOnClickListener(null);
                        HttpsFunc.getInstance().connect(handler).SMS(id);
                        run.run();
                    }
                });
            }else{
                time--;
                SpannableString str = new SpannableString(time+"秒后重新发送");
                str.setSpan(new ForegroundColorSpan(Color.parseColor("#69B2F0")),0, (time+"").length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                info.setText(str);
                handler.postDelayed(this,1000);
            }
        }
    };

    @Event(R.id.commit)
    private void onCommitClick(View view){
        sendMessage(2+3,id);
    }

    public boolean check(String ckn){
        return ckn.equals(checknum.getText().toString());
    }
}
