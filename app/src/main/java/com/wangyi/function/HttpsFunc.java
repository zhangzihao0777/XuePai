package com.wangyi.function;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.wangyi.define.EventName;
import com.wangyi.define.bean.BookData;
import com.wangyi.define.bean.Homework;
import com.wangyi.define.bean.StringListItem;
import com.wangyi.define.bean.UserInfo;
import com.wangyi.define.bean.UserPlus;
import com.wangyi.define.bean.UserRank;
import com.wangyi.function.funchelp.Function;
import com.wangyi.utils.HttpsUtils;
import com.wangyi.utils.ImagePicker;
import com.wangyi.utils.ItOneUtils;
import com.wangyi.utils.PreferencesReader;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpsFunc implements Function {
    public static String host = "http://119.29.229.214:3000";

    private static final HttpsFunc INSTANCE = new HttpsFunc();

    private Handler handler = null;
    private Context context;
    private String bookName;

    private void HttpsFunc() {
    }

    public static HttpsFunc getInstance() {
        return INSTANCE;
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    public HttpsFunc connect(Handler handler) {
        if (this.handler != handler)
            this.handler = handler;
        return INSTANCE;
    }

    public void disconnect() {
        this.handler = null;
    }

    public void update(String version) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("version", version);
        HttpsUtils.Post(host + "/app/update", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                if (!result.equals(EventName.Https.ERROR)) {
                    handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
                }
            }

        });
    }

    public void Login(final String userID, final String passWords) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (userID.equals("") || passWords.equals("")) {
            ItOneUtils.showToast(context, "账号密码不能为空");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        map.put("id", userID);
        map.put("passWords", passWords);
        HttpsUtils.Post(host + "/users/login", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "账号或密码错误");
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FAULT);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                if (result.equals(EventName.Https.OK)) {
                    ItOneUtils.showToast(context, "登陆成功");
                    visitUserInfo();
                    visitUserElseInfo();
                    visitUserRank();
                    UserManagerFunc.getInstance().setLoginStatus(true);
                    PreferencesReader.saveUser(userID, passWords);
                } else if (result.equals(EventName.Https.ERROR)) {
                    ItOneUtils.showToast(context, "用户名或密码错误");
                    if (handler != null) handler.sendEmptyMessage(EventName.UI.FAULT);
                }
            }

        });
    }

    public void commitForm(UserInfo user, final String formkind) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo", new Gson().toJson(user));
        if (user.picture.equals("true"))
            map.put("file", new File(ImagePicker.SAVE_PATH));
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        HttpsUtils.UpLoadFile(host + "/users/" + formkind, map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(x.app(), "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                if (result.equals(EventName.Https.OK)) {
                    ItOneUtils.showToast(x.app(), "成功");
                    if (formkind.equals("modify")) {
                        visitUserInfo();
                    }
                } else if (result.equals(EventName.Https.ERROR) && formkind.equals("login")) {
                    ItOneUtils.showToast(x.app(), "用户名已经存在");
                }
            }

        });
    }

    public void searchBookByUser() {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (!UserManagerFunc.getInstance().isLogin()) return;
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        map.put("uid", UserManagerFunc.getInstance().getUserInfo().id);
        HttpsUtils.Post(host + "/books/userbooks", map, new Callback.CommonCallback<List<BookData>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<BookData> result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void searchBookByName(String bookName) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }

        if (!UserManagerFunc.getInstance().isLogin()) {
            ItOneUtils.showToast(context, "请先登录");
            return;
        }

        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        String university = UserManagerFunc.getInstance().getUserInfo().university;

        Map<String, String> map = new HashMap<>();
        map.put("bookName", bookName);
        map.put("fromUniversity", university);
        HttpsUtils.Post(host + "/books/search", map, new Callback.CommonCallback<List<BookData>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<BookData> result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void searchBooksBySubject(String subject, String university, String category, int start) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }

        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        map.put("subject", subject.equals("全部") ? "*" : subject);
        map.put("category", category);
        map.put("fromUniversity", university);
        map.put("start", start + "");
        HttpsUtils.Post(host + "/books/booklist", map, new Callback.CommonCallback<List<BookData>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);

            }

            @Override
            public void onSuccess(List<BookData> result) {
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void visitUserInfo() {
        HttpsUtils.Get(host + "/users/userbaseinfo", null, new Callback.CommonCallback<UserInfo>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                // TODO Auto-generated method stub
                if (handler != null) ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(UserInfo result) {
                // TODO Auto-generated method stub
                if (result != null) {
                    UserManagerFunc.getInstance().setUserInfo(result);
                    if (handler != null) handler.sendEmptyMessage(EventName.UI.SUCCESS);
                }
            }

        });
    }

    private void visitUserElseInfo() {
        HttpsUtils.Get(host + "/users/userelseinfo", null, new Callback.CommonCallback<UserPlus>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                // TODO Auto-generated method stub
                if (handler != null) ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(UserPlus result) {
                // TODO Auto-generated method stub
                if (result != null) {
                    UserManagerFunc.getInstance().setUserPlus(result);
                    if (handler != null) handler.sendEmptyMessage(EventName.UI.SUCCESS);
                }
            }

        });
    }

    private void visitUserRank() {
        HttpsUtils.Get(host + "/users/getrank", null, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                // TODO Auto-generated method stub
                if (handler != null) ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                if (result != null) {
                    UserManagerFunc.getInstance().setRank(Integer.parseInt(result));
                    if (handler != null) handler.sendEmptyMessage(EventName.UI.SUCCESS);
                }
            }

        });
    }

    public void getRankByOrder() {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        handler.sendEmptyMessage(EventName.UI.START);
        HttpsUtils.Get(host + "/users/usersbyorder", null, new Callback.CommonCallback<List<UserRank>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<UserRank> result) {
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void download(final String id, final String uid, final String bookName) throws DbException {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        final Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("uid", uid);
        HttpsUtils.Post(host + "/books/download", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                Log.d("HttpsFunc", "arg0:" + arg0);
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                Log.d("HttpsFunc", "finish");
            }

            @Override
            public void onSuccess(String result) {

                // TODO Auto-generated method stub
                String newBookName = bookName.replace(":", "").replace("：", "");
                try {
                    DownloadManagerFunc.getInstance().startDownload(
                            host + result, newBookName,
                            BookManagerFunc.FILEPATH + newBookName, true, false, null);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void commitIdea(String advice) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) {
            handler.sendEmptyMessage(EventName.UI.START);
        }
        Map<String, String> map = new HashMap<>();
        String id = UserManagerFunc.getInstance().isLogin() ?
                UserManagerFunc.getInstance().getUserInfo().id : null;
        map.put("id", id);
        map.put("advice", advice);
        HttpsUtils.Post(host + "/advice", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                ItOneUtils.showToast(context, "提交成功");
                if (handler != null) handler.sendEmptyMessage(EventName.UI.SUCCESS);
            }

        });
    }

    public void getMessage(String date) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        String id = UserManagerFunc.getInstance().isLogin() ?
                UserManagerFunc.getInstance().getUserInfo().id : null;
        map.put("id", id);
        map.put("date", date);
        HttpsUtils.Post(host + "/message/getMessage", map, new Callback.CommonCallback<List<com.wangyi.define.bean.Message>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<com.wangyi.define.bean.Message> result) {
                // TODO Auto-generated method stub
                MessageFunc.getInstance().addMessages(result);
            }

        });
    }

    public void sendMessage(String category, String message, String picUrl) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        String id = UserManagerFunc.getInstance().isLogin() ?
                UserManagerFunc.getInstance().getUserInfo().id : null;
        Map<String, String> jsonOB = new HashMap<>();
        jsonOB.put("category", category);
        jsonOB.put("message", message);
        jsonOB.put("uid", id);
        jsonOB.put("picUrl", picUrl);
        String json = new Gson().toJson(jsonOB);
        Map<String, Object> map = new HashMap<>();
        map.put("message", json);
        if (picUrl.equals("true"))
            map.put("file", new File(ImagePicker.SAVE_PATH_MESSAGE));
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        HttpsUtils.UpLoadFile(host + "/message/send", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(x.app(), "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                if (result.equals(EventName.Https.OK)) {
                    ItOneUtils.showToast(x.app(), "成功");
                } else if (result.equals(EventName.Https.ERROR)) {
                    ItOneUtils.showToast(x.app(), "发送消息失败");
                }
            }

        });
    }

    public void sendHomework(int courseNo, String message, String fdate, String picUrl) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        String id = UserManagerFunc.getInstance().isLogin() ?
                UserManagerFunc.getInstance().getUserInfo().id : null;
        Map<String, String> jsonOB = new HashMap<>();
        jsonOB.put("courseNo", courseNo + "");
        jsonOB.put("message", message);
        jsonOB.put("uid", id);
        jsonOB.put("fdate", fdate);
        jsonOB.put("picUrl", picUrl);
        String json = new Gson().toJson(jsonOB);
        Map<String, Object> map = new HashMap<>();
        map.put("homework", json);
        if (picUrl.equals("true"))
            map.put("file", new File(ImagePicker.SAVE_PATH_HOMEWORK));
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        HttpsUtils.UpLoadFile(host + "/message/send", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(x.app(), "服务器抽风中...");
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                if (result.equals(EventName.Https.OK)) {
                    ItOneUtils.showToast(x.app(), "成功");
                } else if (result.equals(EventName.Https.ERROR)) {
                    ItOneUtils.showToast(x.app(), "发送消息失败");
                }
            }

        });
    }

    public void getHomework() {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        String id = UserManagerFunc.getInstance().isLogin() ?
                UserManagerFunc.getInstance().getUserInfo().id : null;
        map.put("id", id);
        HttpsUtils.Post(host + "/homework/getHomework", map, new Callback.CommonCallback<List<Homework>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<Homework> result) {
                // TODO Auto-generated method stub
                HomeworkFunc.getInstance().addHomeworks(result);
            }

        });
    }

    public void getUniversityList() {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        HttpsUtils.Get(host + "/base/university", null, new Callback.CommonCallback<List<StringListItem>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<StringListItem> result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void getFacultyList(String fromUniversity) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        map.put("fromUniversity", fromUniversity);
        HttpsUtils.Post(host + "/base/faculty", map, new Callback.CommonCallback<List<StringListItem>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, ex.toString());
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<StringListItem> result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void getClassList(String fromUniversity, String fromFaculty) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        map.put("fromUniversity", fromUniversity);
        map.put("fromFaculty", fromFaculty);
        HttpsUtils.Post(host + "/base/class", map, new Callback.CommonCallback<List<StringListItem>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, ex.toString());
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<StringListItem> result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void getCourseList(String fromUniversity) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        if (handler != null) handler.sendEmptyMessage(EventName.UI.START);
        Map<String, String> map = new HashMap<>();
        map.put("fromUniversity", fromUniversity);
        HttpsUtils.Post(host + "/base/course", map, new Callback.CommonCallback<List<StringListItem>>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
                ItOneUtils.showToast(context, ex.toString());
            }

            @Override
            public void onFinished() {
                if (handler != null) handler.sendEmptyMessage(EventName.UI.FINISH);
            }

            @Override
            public void onSuccess(List<StringListItem> result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS, result).sendToTarget();
            }

        });
    }

    public void SMS(String mob) {
        if (!isNetworkConnected()) {
            ItOneUtils.showToast(context, "网络未连接");
            return;
        }
        ItOneUtils.showToast(context, "发送短信");
        Map<String, String> map = new HashMap<>();
        map.put("mob", mob);
        HttpsUtils.Post(host + "/base/sms", map, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable ex, boolean isCheck) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                handler.obtainMessage(EventName.UI.SUCCESS + 7, result).sendToTarget();
            }

        });
    }

    public boolean isNetworkConnected() {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
