package com.wangyi.UIview.activity;

import android.content.Intent;
import android.os.CountDownTimer;

import com.wangyi.UIview.BaseActivity;
import com.wangyi.reader.R;

import org.xutils.view.annotation.ContentView;

/**
 * Created by zhang on 2016/9/11.
 */
@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {

    private CountDownTimer timer = new CountDownTimer(1000, 3000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(WelcomeActivity.this, ItOneActivity.class);
            startActivity(intent);
            finish();
        }
    }.start();

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        finish();
    }
}
