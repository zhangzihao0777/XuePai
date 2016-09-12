package com.wangyi.UIview.adapter.template;

import android.util.Log;
import android.view.View;

import com.wangyi.define.bean.DownloadInfo;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;

/**
 * Created by maxchanglove on 2016/2/24.
 */
public class DownloadViewHolder {

    protected DownloadInfo downloadInfo;

    public DownloadViewHolder(View view, DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
        Log.d("111111111111111111111", "this.downloadInfo.getFileLength():" + this.downloadInfo.getFileLength());
        Log.d("111111111111111111111", this.downloadInfo.getFileSavePath());
        x.view().inject(this, view);
    }

    public final DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void update(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public void onWaiting() {

    }

    public void onStarted() {

    }

    public void onLoading(long total, long current) {

    }

    public void onSuccess(File result) {
    }

    public void onError(Throwable ex, boolean isOnCallback) {
    }

    public void onCancelled(Callback.CancelledException cex) {
    }
}