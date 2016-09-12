package com.wangyi.UIview.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.wangyi.UIview.adapter.viewholder.DownloadItemVH;
import com.wangyi.define.DownloadState;
import com.wangyi.define.bean.DownloadInfo;
import com.wangyi.function.DownloadManagerFunc;
import com.wangyi.reader.R;
import com.wangyi.utils.ItOneUtils;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

public class DownloadListAdapter extends BaseAdapter {
    private boolean selectState = false;
    private boolean allSelectState = false;
    private LayoutInflater inflater;
    private DownloadManagerFunc downloadManager;
    private Context contxt;
    private int listSize;
    private DownloadItemVH holder;

    public DownloadListAdapter(Context context) {
        downloadManager = DownloadManagerFunc.getInstance();
        inflater = LayoutInflater.from(context);
        if (downloadManager == null) listSize = 0;
        else listSize = downloadManager.getDownloadListCount();
        this.contxt = context;
    }

    public void allSelect() {
        for (int i = 0; i < listSize; i++) {
            //allSelectState = !allSelectState;
            downloadManager.getDownloadInfo(i).setSelect(true);
        }
    }

    public void clearSelect() {
        for (int i = 0; i < listSize; i++) {
            downloadManager.getDownloadInfo(i).setSelect(false);
        }
    }

    public void delect() {
        try {
            for (int pos = 0; pos < listSize; pos++) {
                if (downloadManager.getDownloadInfo(pos).isSelect()) {
                    Log.d("aa", "pos:" + pos);
                    listSize--;
                    downloadManager.removeDownload(getItem(pos));
                    pos--;
                }
                Intent intent = new Intent("refresh");
                contxt.sendBroadcast(intent);
            }
            clearSelect();
            if (downloadManager == null) listSize = 0;
            else listSize = downloadManager.getDownloadListCount();

        } catch (DbException e) {
        }
    }

    public void setEditMode() {
        selectState = !selectState;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listSize;
    }

    @Override
    public DownloadInfo getItem(int pos) {
        // TODO Auto-generated method stub
        return downloadManager.getDownloadInfo(pos);
    }

    @Override
    public long getItemId(int pos) {
        // TODO Auto-generated method stub
        return pos;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        DownloadItemVH holder = null;
        final DownloadInfo downloadInfo = downloadManager.getDownloadInfo(pos);

        if (null == view) {
            view = inflater.inflate(R.layout.download_ing_listitem, parent, false);
            holder = new DownloadItemVH(view, downloadInfo, downloadManager);
            view.setTag(holder);
            holder.refresh();
        } else {
            holder = (DownloadItemVH) view.getTag();
            holder.update(downloadInfo);
        }
        if (downloadInfo.isSelect()) {
            holder.selectBtn.setChecked(true);
        } else {
            holder.selectBtn.setChecked(false);
        }
        final DownloadItemVH finalHolder = holder;
        holder.clickDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalHolder.progress.getText().equals("100%")) {
                    File file = new File(downloadInfo.getFileSavePath());
                    Uri uri = Uri.parse(file.getAbsolutePath());
                    if (searchLoacl(uri)) {
                        Intent intent = new Intent(contxt, MuPDFActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        contxt.startActivity(intent);
                    } else {
                        ItOneUtils.showToast(x.app(), "文件不存在");
                    }
                } else {
                    ItOneUtils.showToast(x.app(), "拼命下载中...");
                }
            }
        });


        if (downloadInfo.getState().value() < DownloadState.FINISHED.value()) {
            try {
                downloadManager.startDownload(
                        downloadInfo.getUrl(),
                        downloadInfo.getLabel(),
                        downloadInfo.getFileSavePath(),
                        downloadInfo.isAutoResume(),
                        downloadInfo.isAutoRename(),
                        holder);
                this.holder = holder;
            } catch (DbException ex) {
                ItOneUtils.showToast(x.app(), "下载失败");
            }
        }

        if (selectState) {
            holder.stopBtn.setVisibility(View.GONE);
            holder.selectBtn.setVisibility(View.VISIBLE);
        } else {
            holder.selectBtn.setVisibility(View.GONE);
            holder.stopBtn.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private boolean searchLoacl(Uri uri) {
        try {
            File f = new File(String.valueOf(uri));
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    public void ref() {

        this.holder.refresh();
    }
}
