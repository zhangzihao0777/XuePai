package com.wangyi.UIview.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wangyi.UIview.BaseActivity;
import com.wangyi.reader.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhang on 2016/9/7.
 */
@ContentView(R.layout.activity_get_xue_pai)
public class HowToGetPaiActivity extends BaseActivity {
    @ViewInject(R.id.how_to_get_pai_content)
    private TextView content;
    @ViewInject(R.id.get_pai_back_btn)
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SpannableStringBuilder builder = new SpannableStringBuilder(content.getText().toString());
        ForegroundColorSpan blue = new ForegroundColorSpan(Color.BLUE);
        ForegroundColorSpan green = new ForegroundColorSpan(Color.GREEN);
        ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan yellow = new ForegroundColorSpan(Color.YELLOW);
        ForegroundColorSpan yellows = new ForegroundColorSpan(Color.YELLOW);


        builder.setSpan(blue, 23, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(green, 27, 30, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(red, 31, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(yellows, 17, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(yellow, 57, 60, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(builder);
    }
}
