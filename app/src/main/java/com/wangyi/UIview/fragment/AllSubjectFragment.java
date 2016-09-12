package com.wangyi.UIview.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.wangyi.UIview.BaseFragment;
import com.wangyi.UIview.activity.SearchActivity;
import com.wangyi.UIview.activity.WatchBook;
import com.wangyi.UIview.adapter.CategoryAdapter;
import com.wangyi.UIview.widget.container.LessonListLayout;
import com.wangyi.define.EventName;
import com.wangyi.define.bean.BookData;
import com.wangyi.define.bean.UserInfo;
import com.wangyi.function.BookManagerFunc;
import com.wangyi.function.HttpsFunc;
import com.wangyi.function.UserManagerFunc;
import com.wangyi.reader.R;
import com.wangyi.utils.ItOneUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.fragment_allsubject)
public class AllSubjectFragment extends BaseFragment {
    public String[] keywords = {"工科数学", "大学物理", "大学英语", "俄语", "离散数学",
            "西方建筑史", "茶文化欣赏", "音乐鉴赏", "思修", "马哲", "形式与政治",
            "交流技巧", "数字逻辑", "matlab", "博弈论", "西方文学", "欧洲史",
            "西方美术史", "人机交互"};
    @ViewInject(R.id.lessons)
    private LessonListLayout lessons;
    @ViewInject(R.id.pager)
    private ViewPager mViewPager;
    @ViewInject(R.id.tabs_strip)
    private PagerSlidingTabStrip tabsStrip;
    @ViewInject(R.id.measure)
    private RelativeLayout measure;
    private ArrayList<String> bookNames_one, bookNames_two, bookNames_three, bookNames_four;
    private CategoryAdapter adapter;
    private int current = 0;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EventName.UI.FINISH:
                    break;
                case EventName.UI.SUCCESS:
                    ArrayList<BookData> book = (ArrayList<BookData>) msg.obj;
                    if (book.size() != 0) {
                        Log.d("adsadfasdfasdf", book.get(0).category);

                        switch (book.get(0).category) {
                            case "课本":
                                if (!bookNames_one.get(0).equals(book.get(0).bookName)) {
                                    adapter.getAdapter(current).insert(book);
                                    if (bookNames_one.get(0).equals("")) {
                                        bookNames_one.add(0, book.get(0).bookName);
                                    } else {
                                        bookNames_one.add(book.get(0).bookName);
                                    }
                                }
                                break;
                            case "作业":
                                if (!bookNames_two.get(0).equals(book.get(0).bookName)) {
                                    adapter.getAdapter(current).insert(book);
                                    if (bookNames_two.get(0).equals("")) {
                                        bookNames_two.add(0, book.get(0).bookName);
                                    } else {
                                        bookNames_two.add(book.get(0).bookName);
                                    }
                                }
                                break;
                            case "ppt":
                                if (!bookNames_three.get(0).equals(book.get(0).bookName)) {
                                    adapter.getAdapter(current).insert(book);
                                    if (bookNames_three.get(0).equals("")) {
                                        bookNames_three.add(0, book.get(0).bookName);
                                    } else {
                                        bookNames_three.add(book.get(0).bookName);
                                    }
                                }
                                break;
                            case "笔记":
                                if (!bookNames_four.get(0).equals(book.get(0).bookName)) {
                                    adapter.getAdapter(current).insert(book);
                                    if (bookNames_four.get(0).equals("")) {
                                        bookNames_four.add(0, book.get(0).bookName);
                                    } else {
                                        bookNames_four.add(book.get(0).bookName);
                                    }
                                }
                                break;
                        }
                    }
                    if (book.size() != 0)
                        adapter.increaseStart(current, book.size());
                    else {
                        ItOneUtils.showToast(getContext(), "已經加載全部資源");
                        adapter.getAdapter(current).getCustomLoadMoreView().setVisibility(View.GONE);
                    }
                    break;
                case EventName.UI.START:
                    break;
            }
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bookNames_one = new ArrayList<>();
        bookNames_three = new ArrayList<>();
        bookNames_two = new ArrayList<>();
        bookNames_four = new ArrayList<>();
        bookNames_one.add("");
        bookNames_two.add("");
        bookNames_three.add("");
        bookNames_four.add("");
        initCategoryBar();
    }

    private void initCategoryBar() {
        adapter = new CategoryAdapter(getContext(), new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View clickedView, int position) {
                if (position > adapter.getAdapter(current).getItemCount() || position < 0) return;
                Intent intent = new Intent(AllSubjectFragment.this.getActivity().getApplicationContext(), WatchBook.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("book", adapter.getAdapter(current).getItemData(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerView parent, View clickedView, int position) {

            }
        }, handler);

        mViewPager.setAdapter(adapter);
        tabsStrip.setViewPager(mViewPager);
        tabsStrip.setIndicatorColorResource(R.color.accent_color);
        tabsStrip.setTextColorResource(R.color.tabtext_normal_color);
        tabsStrip.setShouldExpand(true);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(4);

        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                UserInfo user = UserManagerFunc.getInstance().getUserInfo();
                if (user != null && adapter != null && adapter.getAdapter(current).isEmpty())
                    HttpsFunc.getInstance().connect(handler).searchBooksBySubject("全部", user.university, adapter.getCategory(current), adapter.getStart(current));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initLesson() {
        lessons.addLessons(keywords, new OnClickListener() {

            @Override
            public void onClick(final View view) {
                // TODO Auto-generated method stub
                if (lessons.obj != null) {
                    ((TextView) lessons.obj).setBackground(null);
                }
                view.setBackgroundResource(R.drawable.bg_subjecttab);
                lessons.obj = view;
                String subject = ((TextView) view).getText().toString();
                UserInfo user = UserManagerFunc.getInstance().getUserInfo();
                if (user != null)
                    HttpsFunc.getInstance().connect(handler).searchBooksBySubject(subject, user.university, adapter.getCategory(current), 0);
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            UserInfo user = UserManagerFunc.getInstance().getUserInfo();
            if (user != null && adapter != null)
                HttpsFunc.getInstance().connect(handler).searchBooksBySubject("*", user.university, adapter.getCategory(current), 0);
        } else {

            BookManagerFunc.getInstance().connect(handler).clear();
        }
    }

    private int getWindowHeight() {
        return measure.getHeight();
    }

    @Event(R.id.search)
    private void onSearchClick(View view) {
        Intent intent = new Intent(x.app(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
