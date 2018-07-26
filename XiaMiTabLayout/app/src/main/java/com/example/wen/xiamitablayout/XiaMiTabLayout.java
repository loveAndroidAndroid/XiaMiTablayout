package com.example.wen.xiamitablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.IntEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaowen on 2018/5/8.
 */
public class XiaMiTabLayout extends HorizontalScrollView {

    //默认字体大小
    private final int DEFAULT_NORMAL_TEXT_SIZE_SP = ScreenUtils.sp2px(13);
    private int mNormalTextSize = DEFAULT_NORMAL_TEXT_SIZE_SP;
    //选中字体大小
    private final int DEFAULT_SELECT_TEXT_SIZE_SP = ScreenUtils.sp2px(30);
    private int mSelectTextSize = DEFAULT_SELECT_TEXT_SIZE_SP;
    //字体颜色
    private final int DEFAULT_NORMAL_TEXT_COLOR = Color.BLACK;
    private final int DEFAULT_SELECT_TEXT_COLOR = Color.RED;
    //关联的viewpager
    private ViewPager mViewPager;
    //第一个子View
    private LinearLayout mTabContainer;
    //Tab总数
    private int mTabCount;
    //当前选中的Tab
    private int mCurrentTabPosition;
    //数据源
    private List<String> mDataList;
    //利用估值器实现渐变
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private IntEvaluator intEvaluator = new IntEvaluator();

    public XiaMiTabLayout(Context context) {
        this(context, null);
    }

    public XiaMiTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XiaMiTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(context, attrs);
        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);
        mTabContainer = new LinearLayout(context);

        addView(mTabContainer, 0, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mDataList = new ArrayList<>();
    }

    private void initStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.XiaMiTabLayout, 0, 0);
        mNormalTextSize = typedArray.getDimensionPixelSize(R.styleable.XiaMiTabLayout_tab_normal_textSize, DEFAULT_NORMAL_TEXT_SIZE_SP);
        mSelectTextSize = typedArray.getDimensionPixelSize(R.styleable.XiaMiTabLayout_tab_select_textSize, DEFAULT_SELECT_TEXT_SIZE_SP);
        typedArray.recycle();
    }

    /**
     * 设置数据源
     */
    public void setDataList(List<String> dataList) {
        this.mDataList.clear();
        this.mDataList.addAll(dataList);
    }

    /**
     * 关联viewpager
     */
    public void setupWithViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        if (viewPager == null) {
            throw new IllegalArgumentException("viewpager not is null");
        }

        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter == null) {
            throw new IllegalArgumentException("pagerAdapter not is null");
        }
        this.mViewPager.addOnPageChangeListener(new TabPagerChanger());
        mTabCount = pagerAdapter.getCount();
        mCurrentTabPosition = viewPager.getCurrentItem();
        notifyDataSetChanged();
    }

    /**
     * 更新界面
     */
    public void notifyDataSetChanged() {
        mTabContainer.removeAllViews();
        for (int i = 0; i < mTabCount; i++) {
            final int currentPosition = i;
            TextView tabTextView = createTextView();
            tabTextView.setText(mDataList.get(i));
            tabTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(currentPosition);
                }
            });
            mTabContainer.addView(tabTextView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        setSelectedTabView(mCurrentTabPosition);
    }

    /**
     * textview的变化
     *
     * @param position
     */
    protected void setSelectedTabView(int position) {
        this.mCurrentTabPosition = position;
        for (int i = 0; i < mTabCount; i++) {
            View view = mTabContainer.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setSelected(position == i);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, position == i ? mSelectTextSize : mNormalTextSize);
                textView.setTypeface(position == i ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
                textView.setTextColor(position == i ? DEFAULT_SELECT_TEXT_COLOR : DEFAULT_NORMAL_TEXT_COLOR);
                if (mCurrentTabPosition == i) {
                    textView.setPadding(ScreenUtils.dipToPx(getContext(), 5), 0, ScreenUtils.dipToPx(getContext(), 5), ScreenUtils.dipToPx(getContext(), 6));
                } else {
                    textView.setPadding(ScreenUtils.dipToPx(getContext(), 5), 0, ScreenUtils.dipToPx(getContext(), 5), ScreenUtils.dipToPx(getContext(), 10));
                }
            }
        }
    }

    /**
     * pager监听
     */
    private class TabPagerChanger implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            final TextView selectedChild = (TextView) mTabContainer.getChildAt(position);
            final TextView nextChild = position + 1 < mTabContainer.getChildCount()
                    ? (TextView) mTabContainer.getChildAt(position + 1)
                    : null;

            if (selectedChild != null) {
                selectedChild.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectTextSize - (mSelectTextSize - mNormalTextSize) * positionOffset);
                //初始颜色值
                int bgColor = DEFAULT_SELECT_TEXT_COLOR;
                if (positionOffset == 0) {
                    //显示初始透明颜色
                    bgColor = DEFAULT_SELECT_TEXT_COLOR;
                } else if (positionOffset > 1) {
                    //滚动到一个定值后,颜色最深,而且不再加深
                    bgColor = DEFAULT_NORMAL_TEXT_COLOR;
                } else {
                    //滚动过程中渐变的颜色
                    bgColor = (int) argbEvaluator.evaluate(positionOffset, DEFAULT_SELECT_TEXT_COLOR, DEFAULT_NORMAL_TEXT_COLOR);
                }
                selectedChild.setTextColor(bgColor);

                int pad = 6;
                if (positionOffset == 0) {
                    //显示初始透明颜色
                    pad = 6;
                } else if (positionOffset > 1) {
                    //滚动到一个定值后,颜色最深,而且不再加深
                    pad = 10;
                } else {
                    //滚动过程中渐变的padding
                    pad = intEvaluator.evaluate(positionOffset, 6, 10);
                }
                selectedChild.setPadding(ScreenUtils.dipToPx(getContext(), 5), 0, ScreenUtils.dipToPx(getContext(), 5), ScreenUtils.dipToPx(getContext(), pad));

                if (positionOffset > 0.5) {
                    selectedChild.setTypeface(Typeface.DEFAULT);
                } else {
                    selectedChild.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }

            if (nextChild != null) {
                nextChild.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize + (mSelectTextSize - mNormalTextSize) * positionOffset);
                //初始颜色值
                int bgColor = DEFAULT_NORMAL_TEXT_COLOR;
                if (positionOffset == 0) {
                    //显示初始透明颜色
                    bgColor = DEFAULT_NORMAL_TEXT_COLOR;
                } else if (positionOffset > 1) {
                    //滚动到一个定值后,颜色最深,而且不再加深
                    bgColor = DEFAULT_SELECT_TEXT_COLOR;
                } else {
                    //滚动过程中渐变的颜色
                    bgColor = (int) argbEvaluator.evaluate(positionOffset, DEFAULT_NORMAL_TEXT_COLOR, DEFAULT_SELECT_TEXT_COLOR);
                }
                nextChild.setTextColor(bgColor);

                int pad = 10;
                if (positionOffset == 0) {
                    pad = 10;
                } else if (positionOffset > 1) {
                    pad = 6;
                } else {
                    //滚动过程中渐变的padding
                    pad = intEvaluator.evaluate(positionOffset, 10, 6);
                }
                nextChild.setPadding(ScreenUtils.dipToPx(getContext(), 5), 0, ScreenUtils.dipToPx(getContext(), 5), ScreenUtils.dipToPx(getContext(), pad));
                if (Math.abs(positionOffset) > 0.5) {
                    nextChild.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    nextChild.setTypeface(Typeface.DEFAULT);
                }
            }
            //解决点击间隔多个tab时 view的混乱变化
            for (int i = 0;i<mTabContainer.getChildCount();i++){
                View childView = mTabContainer.getChildAt(i);
                if (childView!=selectedChild&&childView!=nextChild){
                    TextView textView = (TextView) childView;
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextColor(DEFAULT_NORMAL_TEXT_COLOR);
                    textView.setPadding(ScreenUtils.dipToPx(getContext(), 5), 0, ScreenUtils.dipToPx(getContext(), 5), ScreenUtils.dipToPx(getContext(), 10));
                }
            }

        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 创建textView
     *
     * @return
     */
    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
        textView.setGravity(Gravity.BOTTOM);
        return textView;
    }
}
