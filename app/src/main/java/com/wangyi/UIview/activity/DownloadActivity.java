package com.wangyi.UIview.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wangyi.UIview.BaseActivity;
import com.wangyi.UIview.adapter.DownloadListAdapter;
import com.wangyi.reader.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.download_me)
public class DownloadActivity extends BaseActivity {
    @ViewInject(R.id.control)
    private RelativeLayout control;
    @ViewInject(R.id.downloadlist)
    private ListView downloadList;
    private RefreshDownLoad refreshDownLoad;
    private DownloadListAdapter dla;
    private int editTag = 0;

    private Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //接收广播
        refreshDownLoad = new RefreshDownLoad();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refresh");
        registerReceiver(refreshDownLoad, intentFilter);

        dla = new DownloadListAdapter(this);
        downloadList.setAdapter(dla);

        run.run();

        control.setVisibility(View.GONE);
    }

    private Runnable run = new Runnable() {

        @Override
        public void run() {
            dla.notifyDataSetChanged();
            handler.postDelayed(this, 1000);
        }
    };

    @Event(R.id.back)
    private void onBackClick(View view) {
        handler.removeCallbacks(run);
        DownloadActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(run);
    }

    @Event(R.id.edit)
    private void onEditClick(View view) {
        if (editTag == 0) {
            ((Button) view).setText("完成");
            control.setVisibility(View.VISIBLE);
            dla.setEditMode();
            dla.notifyDataSetChanged();
            editTag = 1;
        } else {
            ((Button) view).setText("编辑");
            control.setVisibility(View.GONE);
            dla.setEditMode();
            dla.clearSelect();
            dla.notifyDataSetChanged();
            editTag = 0;
        }
    }

    @Event(R.id.allselect)
    private void onAllSelectClick(View view) {
        dla.allSelect();
        dla.notifyDataSetChanged();
    }

    @Event(R.id.delete)
    private void onDeleteClick(View view) {
        dla.delect();
        dla.notifyDataSetChanged();
    }

    class RefreshDownLoad extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            downloadList.setAdapter(dla);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refreshDownLoad);
    }
}
