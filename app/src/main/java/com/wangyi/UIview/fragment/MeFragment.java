package com.wangyi.UIview.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangyi.UIview.BaseFragment;
import com.wangyi.UIview.activity.DownloadActivity;
import com.wangyi.UIview.activity.IdeaBackActivity;
import com.wangyi.UIview.activity.LoginActivity;
import com.wangyi.UIview.activity.SettingActivity;
import com.wangyi.UIview.activity.WatchUser;
import com.wangyi.define.EventName;
import com.wangyi.define.bean.UserInfo;
import com.wangyi.function.HttpsFunc;
import com.wangyi.function.UserManagerFunc;
import com.wangyi.reader.R;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.fragment_me)
public class MeFragment extends BaseFragment {
    @ViewInject(R.id.Login)
    private LinearLayout isLogin;
    @ViewInject(R.id.notLogin)
    private LinearLayout notLogin;
    @ViewInject(R.id.headPic)
    private ImageView pic;
    @ViewInject(R.id.userName)
    private TextView userName;
    @ViewInject(R.id.other)
    private TextView other;
    @ViewInject(R.id.back)
    private LinearLayout back;
    @ViewInject(R.id.loginNow)
    private LinearLayout loginNow;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == EventName.UI.SUCCESS) {
                UserInfo userInfo = UserManagerFunc.getInstance().getUserInfo();
                userName.setText(userInfo.userName);
                other.setText(userInfo.university + " " + userInfo.faculty + " ");

                ImageOptions options = new ImageOptions.Builder()
                        .setLoadingDrawableId(R.drawable.ic_me)
                        .setFailureDrawableId(R.drawable.ic_me)
                        .setUseMemCache(true)
                        .setCircular(true)
                        .setIgnoreGif(false)
                        .build();
                x.image().bind(pic, HttpsFunc.host + userInfo.picture + "headPic.jpg", options);

                notLogin.setVisibility(View.GONE);
                isLogin.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                loginNow.setVisibility(View.GONE);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (UserManagerFunc.getInstance().isLogin())
            handler.sendEmptyMessage(EventName.UI.SUCCESS);
        else {
            notLogin.setVisibility(View.VISIBLE);
            isLogin.setVisibility(View.GONE);
        }
        isLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getContext(), WatchUser.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (UserManagerFunc.getInstance().isLogin() && !isLogin.isShown())
                handler.sendEmptyMessage(EventName.UI.SUCCESS);
            else if (!UserManagerFunc.getInstance().isLogin()) {
                notLogin.setVisibility(View.VISIBLE);
                isLogin.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserManagerFunc.getInstance().isLogin() && !isLogin.isShown())
            handler.sendEmptyMessage(EventName.UI.SUCCESS);
        else if (!UserManagerFunc.getInstance().isLogin()) {
            notLogin.setVisibility(View.VISIBLE);
            isLogin.setVisibility(View.GONE);
        }
    }

    @Event(R.id.download)
    private void onDownloadClick(View view) {
        Intent intent = new Intent(x.app(), DownloadActivity.class);
        startActivity(intent);
    }

    @Event(R.id.ideaback)
    private void onIdeabackClick(View view) {
        Intent intent = new Intent(x.app(), IdeaBackActivity.class);
        startActivity(intent);
    }

    @Event(R.id.setting)
    private void onSettingClick(View view) {
        Intent intent = new Intent(x.app(), SettingActivity.class);
        startActivity(intent);
    }

    @Event(R.id.back)
    private void onBackClick(View view) {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("确认退出吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserManagerFunc.getInstance().clear();
                notLogin.setVisibility(View.VISIBLE);
                isLogin.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                loginNow.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Event(R.id.loginNow)
    private void onLoginClick(View view) {
        Intent intent = new Intent(x.app(), LoginActivity.class);
        startActivity(intent);
    }
}
