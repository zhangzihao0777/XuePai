package com.wangyi.UIview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.expanx.LinearExpanxURVAdapter;
import com.wangyi.UIview.adapter.template.ExpandDataItem;
import com.wangyi.UIview.adapter.viewholder.BookCategory;
import com.wangyi.UIview.adapter.viewholder.BookSubCategory;
import com.wangyi.define.bean.BookData;
import com.wangyi.reader.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eason on 5/11/16.
 */
public class WatchUserAdapter extends LinearExpanxURVAdapter<ExpandDataItem<BookData>, BookCategory, BookSubCategory> {
    OnSubItemClickListener onSubItemClickListener = null;
    public WatchUserAdapter(Context context,OnSubItemClickListener onSubItemClickListener) {
        super(context, EXPANDABLE_ITEMS, true);
        this.onSubItemClickListener = onSubItemClickListener;
    }

    @Override
    protected BookCategory iniCustomParentHolder(View parentview) {
        return new BookCategory(parentview);
    }

    @Override
    protected BookSubCategory iniCustomChildHolder(View childview) {
        return new BookSubCategory(childview,onSubItemClickListener);
    }

    @Override
    protected int getLayoutResParent() {
        return R.layout.exp_wu_father;
    }

    @Override
    protected int getLayoutResChild() {
        return R.layout.exp_wu_child;
    }

    @Override
    protected List<ExpandDataItem<BookData>> getChildrenByPath(String path, int depth, int position) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder newFooterHolder(View view) {
        return new UltimateRecyclerviewViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder newHeaderHolder(View view) {
        return new UltimateRecyclerviewViewHolder(view);
    }

    public static List<ExpandDataItem<BookData>> getPreCodeMenu(BookData[] bookData) {
        List<ExpandDataItem<BookData>> e = new ArrayList<>();
        final List<ExpandDataItem<BookData>> ppt = new ArrayList<>();
        final List<ExpandDataItem<BookData>> note = new ArrayList<>();
        final List<ExpandDataItem<BookData>> review = new ArrayList<>();

        for(BookData book:bookData){
            if(book.category.equals("ppt")){
                ppt.add(new ExpandDataItem(book.bookName,"下载量"+book.downloadNumber,book));
            }else if(book.category.equals("笔记")){
                note.add(new ExpandDataItem(book.bookName,"下载量"+book.downloadNumber,book));
            }else if(book.category.equals("课本")){
                review.add(new ExpandDataItem(book.bookName,"下载量"+book.downloadNumber,book));
            }
        }

        e.add(new ExpandDataItem("我的ppt", ppt));
        e.add(new ExpandDataItem("我的笔记", note));
        e.add(new ExpandDataItem("我的课本", review));
        return e;
    }

    public void removeAll(){
        removeAllInternal(getSet());
    }

    public interface OnSubItemClickListener{
        void onClick(BookData book);
    }
}
