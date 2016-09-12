package com.wangyi.UIview.widget.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wangyi.reader.R;

public class FragmentIndicator extends LinearLayout implements OnClickListener {
    private int mDefaultIndicator = 0;
    private static int mCurIndicator;

    private static View[] mIndicators;
    private OnIndicateListener mOnIndicateListener;

    private static final String TAG_ICON_0 = "icon_tag_0";
    private static final String TAG_ICON_1 = "icon_tag_1";
    private static final String TAG_ICON_2 = "icon_tag_2";

    public FragmentIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCurIndicator = mDefaultIndicator;
        setOrientation(LinearLayout.HORIZONTAL);
        init();
    }

    private void init() {
        mIndicators = new View[5];

        mIndicators[0] = createIndicator(R.drawable.shoye_lv, TAG_ICON_0);
        mIndicators[0].setBackgroundResource(R.color.basebottombar_color);
        mIndicators[0].setTag(Integer.valueOf(0));
        mIndicators[0].setOnClickListener(this);
        addView(mIndicators[0]);

        mIndicators[1] = createIndicator(R.drawable.quanbukecheng_hui, TAG_ICON_1);
        mIndicators[1].setBackgroundResource(R.color.basebottombar_color);
        mIndicators[1].setTag(Integer.valueOf(1));
        mIndicators[1].setOnClickListener(this);
        addView(mIndicators[1]);

        mIndicators[2] = createIndicator(R.drawable.wo_hui, TAG_ICON_2);
        mIndicators[2].setBackgroundResource(R.color.basebottombar_color);
        mIndicators[2].setTag(Integer.valueOf(2));
        mIndicators[2].setOnClickListener(this);
        addView(mIndicators[2]);
    }

    private View createIndicator(int iconResID, String iconTag) {
        LinearLayout view = new LinearLayout(getContext());
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setPadding(0, 10, 0, 10);

        ImageView iconView = new ImageView(getContext());
        iconView.setTag(iconTag);
        iconView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
        iconView.setImageResource(iconResID);
        view.addView(iconView);
        return view;
    }

    public static void setIndicator(int which) {
        // clear previous status.
        ImageView prevIcon;
        switch (mCurIndicator) {
            case 0:
                prevIcon = (ImageView) mIndicators[mCurIndicator]
                        .findViewWithTag(TAG_ICON_0);
                prevIcon.setImageResource(R.drawable.shouye_hui);
                break;
            case 1:
                prevIcon = (ImageView) mIndicators[mCurIndicator]
                        .findViewWithTag(TAG_ICON_1);
                prevIcon.setImageResource(R.drawable.quanbukecheng_hui);
                break;
            case 2:
                prevIcon = (ImageView) mIndicators[mCurIndicator]
                        .findViewWithTag(TAG_ICON_2);
                prevIcon.setImageResource(R.drawable.wo_hui);
                break;
        }

        // UpdateService current status.
        ImageView currIcon;
        switch (which) {
            case 0:
                currIcon = (ImageView) mIndicators[which]
                        .findViewWithTag(TAG_ICON_0);
                currIcon.setImageResource(R.drawable.shoye_lv);
                break;
            case 1:
                currIcon = (ImageView) mIndicators[which]
                        .findViewWithTag(TAG_ICON_1);
                currIcon.setImageResource(R.drawable.quanbukecheng_lv);
                break;
            case 2:
                currIcon = (ImageView) mIndicators[which]
                        .findViewWithTag(TAG_ICON_2);
                currIcon.setImageResource(R.drawable.wo_lv);
                break;
        }

        mCurIndicator = which;
    }

    @Override
    public void onClick(View v) {
        if (mOnIndicateListener != null) {
            int tag = (Integer) v.getTag();
            switch (tag) {
                case 0:
                    if (mCurIndicator != 0) {
                        mOnIndicateListener.onIndicate(v, 0);
                        setIndicator(0);
                    }
                    break;
                case 1:
                    if (mCurIndicator != 1) {
                        mOnIndicateListener.onIndicate(v, 1);
                        setIndicator(1);
                    }
                    break;
                case 2:
                    if (mCurIndicator != 2) {
                        mOnIndicateListener.onIndicate(v, 2);
                        setIndicator(2);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setOnIndicateListener(OnIndicateListener listener) {
        mOnIndicateListener = listener;
    }

    public interface OnIndicateListener {
        public void onIndicate(View v, int which);
    }

}
