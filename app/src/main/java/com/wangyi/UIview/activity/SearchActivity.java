package com.wangyi.UIview.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.wangyi.UIview.adapter.SearchBookAdapter;
import com.wangyi.UIview.widget.dialog.LoadingDialog;
import com.wangyi.UIview.widget.view.SearchView;
import com.wangyi.UIview.widget.view.UltimateListView;
import com.wangyi.define.bean.BookData;
import com.wangyi.define.EventName;
import com.wangyi.function.HttpsFunc;
import com.wangyi.reader.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by eason on 5/20/16.
 */

@ContentView(R.layout.search)
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    @ViewInject(R.id.search)
    private SearchView search;
    @ViewInject(R.id.booklist)
    private UltimateRecyclerView bookList;
    @ViewInject(R.id.label)
    private TextView label;
    @ViewInject(R.id.search_en)
    private TextView en;
    @ViewInject(R.id.search_max)
    private TextView max;
    @ViewInject(R.id.search_mao)
    private TextView mao;
    @ViewInject(R.id.search_law)
    private TextView law;
    @ViewInject(R.id.search_history)
    private TextView his;
    @ViewInject(R.id.search_article)
    private TextView art;

    private SearchBookAdapter adapter;
    private LoadingDialog loading;
    private ArrayList<BookData> books;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventName.UI.FINISH:
                    loading.dismiss();
                    break;
                case EventName.UI.SUCCESS:
                    books = (ArrayList<BookData>) msg.obj;
                    adapter.removeAll();
                    adapter.insert(books);
                    label.setText("共找到" + books.size() + "个相关书籍");
                    break;
                case EventName.UI.START:
                    loading.show();
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        loading = new LoadingDialog(this);

        books = new ArrayList<BookData>();
        adapter = new SearchBookAdapter(books);

        en.setOnClickListener(this);
        max.setOnClickListener(this);
        mao.setOnClickListener(this);
        art.setOnClickListener(this);
        his.setOnClickListener(this);
        law.setOnClickListener(this);

        initBookList();
        initSearch();
    }

    public void initSearch() {
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) x.app().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    HttpsFunc.getInstance().connect(handler).searchBookByName(search.getText());
                    return true;
                }
                return false;
            }
        });
    }

    private void initBookList() {
        UltimateListView view = new UltimateListView(bookList, adapter, this);
        view.beforeFuncset();
        view.enableItemClick(new ItemTouchListenerAdapter(bookList.mRecyclerView,
                new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View clickedView, int position) {
                        Intent intent = new Intent(SearchActivity.this, WatchBook.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("book", books.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(RecyclerView recyclerView, View view, int i) {
                    }
                })
        );
        view.afterFuncset();
    }

    @Event(R.id.cancel)
    private void onCancelClick(View view) {
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_en:
                searchBook("英语");
                break;
            case R.id.search_max:
                searchBook("马克思");
                break;
            case R.id.search_mao:
                searchBook("毛概");
                break;
            case R.id.search_history:
                searchBook("近代史");
                break;
            case R.id.search_law:
                searchBook("法律");
                break;
            case R.id.search_article:
                searchBook("艺术");
                break;

        }
    }

    private void searchBook(String content) {
        final String searchContent = content;
        HttpsFunc.getInstance().connect(handler).searchBookByName(searchContent);
    }
}
