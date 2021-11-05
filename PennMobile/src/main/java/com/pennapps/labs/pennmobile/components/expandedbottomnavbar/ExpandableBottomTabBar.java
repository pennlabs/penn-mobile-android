package com.pennapps.labs.pennmobile.components.expandedbottomnavbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

import com.pennapps.labs.pennmobile.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalmath_v on 8/29/16.
 */

public class ExpandableBottomTabBar extends LinearLayout implements View.OnClickListener {

    private static final String SELECTED_TAB = "Selected Tab";
    private static final String TAG = "TabBarView";
    public static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    private int MAX_TABS_PER_ROW = 5;
    private int mMoreTabPosition = MAX_TABS_PER_ROW - 1;
    private Context mContext;
    private LinearLayout mRootContainer;
    private final List<LinearLayout> mTabContainers = new ArrayList<LinearLayout>();
    private int mTabCount = 20;
    private OnTabClickedListener mOnTabClickedListener;
    private boolean mAllTabsVisible = false;

    private int mPrimaryColor;
    private int mScreenWidth;
    private int mTenDp;
    private int mMaxFixedItemWidth;
    private int mMaxTabPerRow = MAX_TABS_PER_ROW;
    private final List<TabInformation> mTabInfoList = new ArrayList<>();
    private List<View> mTabViewList = new ArrayList<>();

    // XML Attributes
    private int mTabXmlResource;
    private float mTextSize = 16;
    private int mPadding = 20;
    private int mAnimationDuration = 300;
    private int mSelectedTab = 0;
    private int mTabContainerCount = 1;
    private int mBgColor;
    private int mTabTextColor;
    private int mSelectedTabTextColor;
    private int mMoreIconRecId = android.R.drawable.ic_menu_more;

    private static class TabInformation {
        int id;
        int iconResId;
        int iconPressedResId;
        String title;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ExpandableBottomTabBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ExpandableBottomTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ExpandableBottomTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void setOnTabClickedListener(OnTabClickedListener listener) {
        mOnTabClickedListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        populateAttributes(context, attrs);
        initializeViews();
    }

    private void populateAttributes(Context context, AttributeSet attrs) {
        mPrimaryColor = Utils.getColor(getContext(), R.attr.colorPrimary);
        mScreenWidth = Utils.getScreenWidth(getContext());
        mTenDp = Utils.dpToPixel(getContext(), 10);
        mMaxFixedItemWidth = Utils.dpToPixel(getContext(), 168);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.BottomBar, 0, 0);

        try {
            mTabXmlResource = ta.getResourceId(R.styleable.BottomBar_tab_resource, 0);
            mMaxTabPerRow = ta.getInt(R.styleable.BottomBar_max_tab_count, 5);
            mSelectedTabTextColor = ta.getColor(R.styleable.BottomBar_selected_tab_text_color, 0xffffffff);
            mMoreIconRecId = ta.getResourceId(R.styleable.BottomBar_more_icon_resource, android.R.drawable.ic_menu_more);
            mMoreTabPosition = mMaxTabPerRow - 1;
            String bgColor = attrs.getAttributeValue(ANDROID_NS, "background");
            if (bgColor.contains("@")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mBgColor = getResources().getColor(Integer.valueOf(bgColor.replace("@", "")), null);
                } else {
                    mBgColor = getResources().getColor(Integer.valueOf(bgColor.replace("@", "")));
                }
            } else if (bgColor.contains("#")) {
                mBgColor = Color.parseColor(bgColor);
            }
            String textColor = attrs.getAttributeValue(ANDROID_NS, "textColor");
            if (textColor.contains("@")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mTabTextColor = getResources().getColor(Integer.valueOf(textColor.replace("@", "")), null);
                } else {
                    mTabTextColor = getResources().getColor(Integer.valueOf(textColor.replace("@", "")));
                }
            } else if (textColor.contains("#")) {
                mTabTextColor = Color.parseColor(textColor);
            }
            String textSize = attrs.getAttributeValue(ANDROID_NS, "textSize");
            if (textSize.contains("sp")) {
                mTextSize = Float.valueOf(textSize.replace("sp", ""));
            } else if (textSize.contains("dp")) {
                mTextSize = Float.valueOf(textSize.replace("dp", ""));
            } else {
                mTextSize = Float.valueOf(textSize);
            }
            XmlResourceParser parser = context.getResources().getXml(mTabXmlResource);
            parse(parser);
        } finally {
            ta.recycle();
        }

    }

    private void parse(XmlResourceParser parser) {
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // instead of the following if/else if lines
                // you should custom parse your xml
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.i(TAG, "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    Log.i(TAG, "Start tag " + parser.getName());
                    if (parser.getName().equals("tab")) {
                        parseTabInfo(parser);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.i(TAG, "End tag " + parser.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.i(TAG, "Text " + parser.getText());
                }
                eventType = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            mTabCount = mTabInfoList.size();
            if (mTabCount > mMaxTabPerRow) {
                final TabInformation moreTab = new TabInformation();
                moreTab.title = "More";
                moreTab.iconResId = mMoreIconRecId;
                moreTab.iconPressedResId = mMoreIconRecId;
                mTabInfoList.add(mMoreTabPosition, moreTab);
            }
        }
    }

    private void parseTabInfo(XmlResourceParser parser) {
        TabInformation tab = new TabInformation();
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            switch (attrName) {
                case "id":
                    tab.id = parser.getIdAttributeResourceValue(i);
                    break;
                case "icon":
                    tab.iconResId = parser.getAttributeResourceValue(i, 0);
                    break;
                case "icon_pressed":
                    tab.iconPressedResId = parser.getAttributeResourceValue(i, 0) == 0
                            ? tab.iconResId : parser.getAttributeResourceValue(i, 0);
                    break;
                case "title":
                    tab.title = getTitleValue(i, parser);
                    break;
            }
        }

        if(tab.iconPressedResId == 0){
            tab.iconPressedResId = tab.iconResId;
        }

        mTabInfoList.add(tab);
    }

    private String getTitleValue(int attrIndex, XmlResourceParser parser) {
        int titleResource = parser.getAttributeResourceValue(attrIndex, 0);

        if (titleResource != 0) {
            return mContext.getString(titleResource);
        }

        return parser.getAttributeValue(attrIndex);
    }

    public void setTabCount(int count) {
        this.mTabCount = count;
    }

    /**
     * Set the selected tab
     * @param index
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setSelectedTab(int index) {
        this.mSelectedTab = index;
        setFocusOnTab(mSelectedTab);
        if (mSelectedTab > mMoreTabPosition) {
            ((TextView) mTabViewList.get(mMoreTabPosition)).setTextColor(mSelectedTabTextColor);
        }
    }

    /**
     * get Selected Tab
     * @return
     */
    public int getSelectedTab() {
        return mSelectedTab;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableBottomTabBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initializeViews() {
        int width = LayoutParams.MATCH_PARENT;
        int height = LayoutParams.WRAP_CONTENT;
        LayoutParams params = new LayoutParams(width, height);
        ViewCompat.setElevation(this, Utils.dpToPixel(getContext(), 8));

        mRootContainer = this;
        mRootContainer.setLayoutParams(params);
        mRootContainer.setOrientation(LinearLayout.VERTICAL);

        initializeTabContainers();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initializeTabContainers() {
        int layoutCount = 1;
        if (mTabCount > mMaxTabPerRow) {
            mTabCount = mTabCount + 1;
        }

        final int quotient = mTabCount / mMaxTabPerRow;
        final int remainder = mTabCount % mMaxTabPerRow;
        if (mTabCount < mMaxTabPerRow) {
            layoutCount = 1;
        } else if (quotient >= 1 && remainder > 0) {
            layoutCount = quotient + 1;
        } else if (remainder == 0) {
            layoutCount = quotient;
        }
        mTabContainerCount = layoutCount;

        int width = LayoutParams.MATCH_PARENT;
        int height = LayoutParams.WRAP_CONTENT;
        final LayoutParams params = new LayoutParams(width, height);
        for (int index = 0; index < layoutCount; index++) {
            final LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(params);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutDirection(LinearLayout.LAYOUT_DIRECTION_LOCALE);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setWeightSum(100);
            if (index > 0) {
                linearLayout.setVisibility(View.GONE);
            }
            mRootContainer.addView(linearLayout, index);
        }

        final LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params1.weight = 100f / (float) mMaxTabPerRow;
        for (int index = 0; index < mTabCount; index++) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(params1);
            textView.setPadding(mPadding, mPadding, mPadding, mPadding);
            textView.setTextSize(mTextSize);
            textView.setGravity(Gravity.CENTER);

            textView.setText(mTabInfoList.get(index).title);
            textView.setTextColor(mTabTextColor);

            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(index).iconResId, 0, 0);

            textView.setOnClickListener(this);

            ((LinearLayout) mRootContainer.getChildAt(getLayoutIndex(index))).addView(textView);
            mTabViewList.add(textView);

            setFocusOnTab(index);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setFocusOnTab(int index) {
        if (index == mSelectedTab) {
            ((TextView) mTabViewList.get(index)).setTextColor(mSelectedTabTextColor);
            ((TextView) mTabViewList.get(index))
                    .setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(index).iconPressedResId, 0, 0);
        }
        if (mSelectedTab > mMoreTabPosition) {
            if (index == mMoreTabPosition) {
                ((TextView) mTabViewList.get(index)).setTextColor(mSelectedTabTextColor);
                ((TextView) mTabViewList.get(index))
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(index).iconPressedResId, 0, 0);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void resetFocusOnAllTabs() {
        for (View textView : mTabViewList) {
            ((TextView) textView).setTextColor(mTabTextColor);
        }

        for(int i = 0; i < mTabViewList.size(); i++ ){
            ((TextView) mTabViewList.get(i))
                    .setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(i).iconResId, 0, 0);
        }
    }

    private int getLayoutIndex(int i) {
        return i / mMaxTabPerRow;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View view) {
        int pos = -1;
        if (mOnTabClickedListener != null) {
            pos = getClickedPosition(view);

            mOnTabClickedListener.onTabClicked(view, pos);
            final View topTabContainer = mRootContainer.getChildAt(0);
            final int btmTabContainerCount = mRootContainer.getChildCount() - 1;
            if (btmTabContainerCount > 0) {
                final View[] btmTabContainers = new View[btmTabContainerCount];
                for (int i = 0; i < btmTabContainerCount; i++) {
                    btmTabContainers[i] = mRootContainer.getChildAt(i + 1);
                }

                if (isAnyBottomContainerVisible(btmTabContainers)) {
                    ValueAnimator btm = ValueAnimator.ofFloat(topTabContainer.getHeight(), 0);
                    btm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float val = (float) valueAnimator.getAnimatedValue();
                            LayoutParams p = new LayoutParams(btmTabContainers[0].getLayoutParams().width, (int) val);
                            setLayoutParamsToBtmTabs(btmTabContainers, p);
                        }
                    });
                    btm.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            setBtmTabsVisibility(btmTabContainers, View.GONE);
                            mAllTabsVisible = false;
                        }
                    });

                    btm.setDuration(mAnimationDuration);
                    btm.start();
                } else if (pos == mMoreTabPosition &&
                        mAllTabsVisible == false) {
                    setBtmTabsVisibility(btmTabContainers, View.VISIBLE);
                    mAllTabsVisible = true;
                    ValueAnimator btm = ValueAnimator.ofFloat(0, topTabContainer.getHeight());
                    btm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float val = (float) valueAnimator.getAnimatedValue();
                            LayoutParams p = new LayoutParams(btmTabContainers[0].getLayoutParams().width, (int) val);
                            setLayoutParamsToBtmTabs(btmTabContainers, p);
                        }
                    });
                    btm.setDuration(mAnimationDuration);
                    btm.start();
                }
            }

            if (pos != mMoreTabPosition) {
                getTabViewAt(mSelectedTab).setBackgroundColor(mBgColor);
                ((TextView) getTabViewAt(mSelectedTab)).setTextColor(mTabTextColor);
                ((TextView) getTabViewAt(mSelectedTab))
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(mSelectedTab).iconResId, 0, 0);

                mSelectedTab = pos;

                ((TextView) getTabViewAt(pos)).setTextColor(mSelectedTabTextColor);
                ((TextView) getTabViewAt(pos)).setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(pos).iconPressedResId, 0, 0);
            }
            if (mSelectedTab > mMoreTabPosition) {
                ((TextView) getTabViewAt(mMoreTabPosition)).setTextColor(mSelectedTabTextColor);
                ((TextView) getTabViewAt(mMoreTabPosition))
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(mMoreTabPosition).iconPressedResId, 0, 0);
            }
            if (mSelectedTab < mMaxTabPerRow && mTabContainerCount > 1) {
                ((TextView) getTabViewAt(mMoreTabPosition)).setTextColor(mTabTextColor);
                ((TextView) getTabViewAt(mMoreTabPosition))
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, mTabInfoList.get(mMoreTabPosition).iconResId, 0, 0);
                getTabViewAt(mMoreTabPosition).setBackgroundColor(mBgColor);
            }
        }
    }

    private View getTabViewAt(int position) {
        return mTabViewList.get(position);
    }

    private int getClickedPosition(View view) {
        for (View tabView : mTabViewList) {
            if (view == tabView) {
                return mTabViewList.indexOf(tabView);
            }
        }
        return -1;
    }

    private void setBtmTabsVisibility(View[] btmTabContainers, int visibility) {
        for (View btmTabContainer : btmTabContainers) {
            btmTabContainer.setVisibility(visibility);
        }
    }

    private void setLayoutParamsToBtmTabs(View[] btmTabContainers, LayoutParams layoutParams) {
        for (View btmTabContainer : btmTabContainers) {
            btmTabContainer.setLayoutParams(layoutParams);
        }
    }

    private boolean isAnyBottomContainerVisible(View[] btmTabContainers) {
        for (View view : btmTabContainers) {
            if (view.getVisibility() == View.VISIBLE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_TAB, mSelectedTab);
        bundle.putParcelable("superstate", super.onSaveInstanceState());
        return bundle;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mSelectedTab = bundle.getInt(SELECTED_TAB);
            state = bundle.getParcelable("superstate");
        }
        super.onRestoreInstanceState(state);
        resetFocusOnAllTabs();
        setFocusOnTab(mSelectedTab);
        if (mSelectedTab > mMoreTabPosition) {
            ((TextView) mTabViewList.get(mMoreTabPosition)).setTextColor(mSelectedTabTextColor);
        }
    }

    public interface OnTabClickedListener {
        public void onTabClicked(View view, int tabPos);
    }
}
