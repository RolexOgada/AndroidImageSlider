package com.rolex.slider.library.Indicators;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.rolex.slider.library.R;
import com.rolex.slider.library.Tricks.InfinitePagerAdapter;
import com.rolex.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;

/**
 * Pager Indicator for ViewPagerEx.
 */
public class PagerIndicator extends LinearLayout implements ViewPagerEx.OnPageChangeListener {

    private Context mContext;
    private ViewPagerEx mPager;
    private ImageView mPreviousSelectedIndicator;
    private int mPreviousSelectedPosition;
    private int mUserSetUnSelectedIndicatorResId;
    private int mUserSetSelectedIndicatorResId;

    private Drawable mSelectedDrawable;
    private Drawable mUnselectedDrawable;

    private int mItemCount = 0;
    private Shape mIndicatorShape = Shape.Oval;
    private IndicatorVisibility mVisibility = IndicatorVisibility.Visible;

    private int mDefaultSelectedColor;
    private int mDefaultUnSelectedColor;
    private float mDefaultSelectedWidth;
    private float mDefaultSelectedHeight;
    private float mDefaultUnSelectedWidth;
    private float mDefaultUnSelectedHeight;

    private GradientDrawable mUnSelectedGradientDrawable;
    private GradientDrawable mSelectedGradientDrawable;

    private LayerDrawable mSelectedLayerDrawable;
    private LayerDrawable mUnSelectedLayerDrawable;

    private float mPaddingLeft, mPaddingRight, mPaddingTop, mPaddingBottom;
    private float mSelectedPaddingLeft, mSelectedPaddingRight, mSelectedPaddingTop, mSelectedPaddingBottom;
    private float mUnSelectedPaddingLeft, mUnSelectedPaddingRight, mUnSelectedPaddingTop, mUnSelectedPaddingBottom;

    private final ArrayList<ImageView> mIndicators = new ArrayList<>();

    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicator, 0, 0);

        mVisibility = IndicatorVisibility.values()[attributes.getInt(R.styleable.PagerIndicator_visibility, IndicatorVisibility.Visible.ordinal())];
        mIndicatorShape = Shape.values()[attributes.getInt(R.styleable.PagerIndicator_shape, Shape.Oval.ordinal())];

        mUserSetSelectedIndicatorResId = attributes.getResourceId(R.styleable.PagerIndicator_selected_drawable, 0);
        mUserSetUnSelectedIndicatorResId = attributes.getResourceId(R.styleable.PagerIndicator_unselected_drawable, 0);

        mDefaultSelectedColor = attributes.getColor(R.styleable.PagerIndicator_selected_color, Color.WHITE);
        mDefaultUnSelectedColor = attributes.getColor(R.styleable.PagerIndicator_unselected_color, Color.argb(33, 255, 255, 255));

        mDefaultSelectedWidth = attributes.getDimension(R.styleable.PagerIndicator_selected_width, pxFromDp(6));
        mDefaultSelectedHeight = attributes.getDimension(R.styleable.PagerIndicator_selected_height, pxFromDp(6));
        mDefaultUnSelectedWidth = attributes.getDimension(R.styleable.PagerIndicator_unselected_width, pxFromDp(6));
        mDefaultUnSelectedHeight = attributes.getDimension(R.styleable.PagerIndicator_unselected_height, pxFromDp(6));

        mSelectedGradientDrawable = new GradientDrawable();
        mUnSelectedGradientDrawable = new GradientDrawable();

        mPaddingLeft = attributes.getDimension(R.styleable.PagerIndicator_padding_left, pxFromDp(3));
        mPaddingRight = attributes.getDimension(R.styleable.PagerIndicator_padding_right, pxFromDp(3));
        mPaddingTop = attributes.getDimension(R.styleable.PagerIndicator_padding_top, pxFromDp(0));
        mPaddingBottom = attributes.getDimension(R.styleable.PagerIndicator_padding_bottom, pxFromDp(0));

        mSelectedPaddingLeft = attributes.getDimension(R.styleable.PagerIndicator_selected_padding_left, mPaddingLeft);
        mSelectedPaddingRight = attributes.getDimension(R.styleable.PagerIndicator_selected_padding_right, mPaddingRight);
        mSelectedPaddingTop = attributes.getDimension(R.styleable.PagerIndicator_selected_padding_top, mPaddingTop);
        mSelectedPaddingBottom = attributes.getDimension(R.styleable.PagerIndicator_selected_padding_bottom, mPaddingBottom);

        mUnSelectedPaddingLeft = attributes.getDimension(R.styleable.PagerIndicator_unselected_padding_left, mPaddingLeft);
        mUnSelectedPaddingRight = attributes.getDimension(R.styleable.PagerIndicator_unselected_padding_right, mPaddingRight);
        mUnSelectedPaddingTop = attributes.getDimension(R.styleable.PagerIndicator_unselected_padding_top, mPaddingTop);
        mUnSelectedPaddingBottom = attributes.getDimension(R.styleable.PagerIndicator_unselected_padding_bottom, mPaddingBottom);

        mSelectedLayerDrawable = new LayerDrawable(new Drawable[]{mSelectedGradientDrawable});
        mUnSelectedLayerDrawable = new LayerDrawable(new Drawable[]{mUnSelectedGradientDrawable});

        setIndicatorStyleResource(mUserSetSelectedIndicatorResId, mUserSetUnSelectedIndicatorResId);
        setDefaultIndicatorShape(mIndicatorShape);
        setDefaultSelectedIndicatorSize(mDefaultSelectedWidth, mDefaultSelectedHeight, Unit.Px);
        setDefaultUnselectedIndicatorSize(mDefaultUnSelectedWidth, mDefaultUnSelectedHeight, Unit.Px);
        setDefaultIndicatorColor(mDefaultSelectedColor, mDefaultUnSelectedColor);
        setIndicatorVisibility(mVisibility);

        attributes.recycle();
    }

    public enum Shape { Oval, Rectangle }
    public enum IndicatorVisibility { Visible, Invisible }
    public enum Unit { DP, Px }

    private float pxFromDp(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float dpFromPx(float px) {
        return px / getResources().getDisplayMetrics().density;
    }

    public void setDefaultIndicatorShape(Shape shape) {
        if (mUserSetSelectedIndicatorResId == 0) {
            mSelectedGradientDrawable.setShape(shape == Shape.Oval ? GradientDrawable.OVAL : GradientDrawable.RECTANGLE);
        }
        if (mUserSetUnSelectedIndicatorResId == 0) {
            mUnSelectedGradientDrawable.setShape(shape == Shape.Oval ? GradientDrawable.OVAL : GradientDrawable.RECTANGLE);
        }
        resetDrawable();
    }

    public void setIndicatorStyleResource(int selected, int unselected) {
        mUserSetSelectedIndicatorResId = selected;
        mUserSetUnSelectedIndicatorResId = unselected;

        mSelectedDrawable = (selected == 0) ? mSelectedLayerDrawable : ContextCompat.getDrawable(mContext, selected);
        mUnselectedDrawable = (unselected == 0) ? mUnSelectedLayerDrawable : ContextCompat.getDrawable(mContext, unselected);

        resetDrawable();
    }

    public void setDefaultIndicatorColor(int selectedColor, int unselectedColor) {
        if (mUserSetSelectedIndicatorResId == 0) mSelectedGradientDrawable.setColor(selectedColor);
        if (mUserSetUnSelectedIndicatorResId == 0) mUnSelectedGradientDrawable.setColor(unselectedColor);
        resetDrawable();
    }

    public void setDefaultSelectedIndicatorSize(float width, float height, Unit unit) {
        if (mUserSetSelectedIndicatorResId == 0) {
            float w = (unit == Unit.DP) ? pxFromDp(width) : width;
            float h = (unit == Unit.DP) ? pxFromDp(height) : height;
            mSelectedGradientDrawable.setSize((int) w, (int) h);
            resetDrawable();
        }
    }

    public void setDefaultUnselectedIndicatorSize(float width, float height, Unit unit) {
        if (mUserSetUnSelectedIndicatorResId == 0) {
            float w = (unit == Unit.DP) ? pxFromDp(width) : width;
            float h = (unit == Unit.DP) ? pxFromDp(height) : height;
            mUnSelectedGradientDrawable.setSize((int) w, (int) h);
            resetDrawable();
        }
    }

    public void setIndicatorVisibility(IndicatorVisibility visibility) {
        setVisibility(visibility == IndicatorVisibility.Visible ? View.VISIBLE : View.INVISIBLE);
        resetDrawable();
    }

    public void destroySelf() {
        if (mPager == null || mPager.getAdapter() == null) return;
        InfinitePagerAdapter wrapper = (InfinitePagerAdapter) mPager.getAdapter();
        PagerAdapter adapter = wrapper.getRealAdapter();
        if (adapter != null) adapter.unregisterDataSetObserver(dataChangeObserver);
        removeAllViews();
    }

    public void setViewPager(ViewPagerEx pager) {
        if (pager.getAdapter() == null) throw new IllegalStateException("ViewPager does not have adapter instance");
        mPager = pager;
        mPager.addOnPageChangeListener(this);
        ((InfinitePagerAdapter) mPager.getAdapter()).getRealAdapter().registerDataSetObserver(dataChangeObserver);
    }

    private void resetDrawable() {
        for (View view : mIndicators) {
            ImageView imageView = (ImageView) view;
            if (mPreviousSelectedIndicator != null && mPreviousSelectedIndicator.equals(view)) {
                imageView.setImageDrawable(mSelectedDrawable);
            } else {
                imageView.setImageDrawable(mUnselectedDrawable);
            }
        }
    }

    public void redraw() {
        mItemCount = getShouldDrawCount();
        mPreviousSelectedIndicator = null;
        mIndicators.clear();
        removeAllViews();

        for (int i = 0; i < mItemCount; i++) {
            ImageView indicator = new ImageView(mContext);
            indicator.setImageDrawable(mUnselectedDrawable);
            indicator.setPadding((int) mUnSelectedPaddingLeft, (int) mUnSelectedPaddingTop, (int) mUnSelectedPaddingRight, (int) mUnSelectedPaddingBottom);
            addView(indicator);
            mIndicators.add(indicator);
        }
        setItemAsSelected(mPreviousSelectedPosition);
    }

    private int getShouldDrawCount() {
        PagerAdapter adapter = mPager.getAdapter();
        if (adapter instanceof InfinitePagerAdapter) {
            return ((InfinitePagerAdapter) adapter).getRealCount();
        }
        return adapter.getCount();
    }

    private final DataSetObserver dataChangeObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            int count = getShouldDrawCount();
            if (count > mItemCount) {
                for (int i = 0; i < count - mItemCount; i++) {
                    ImageView indicator = new ImageView(mContext);
                    indicator.setImageDrawable(mUnselectedDrawable);
                    indicator.setPadding((int) mUnSelectedPaddingLeft, (int) mUnSelectedPaddingTop,
                            (int) mUnSelectedPaddingRight, (int) mUnSelectedPaddingBottom);
                    addView(indicator);
                    mIndicators.add(indicator);
                }
            } else if (count < mItemCount) {
                for (int i = 0; i < mItemCount - count; i++) {
                    removeView(mIndicators.get(0));
                    mIndicators.remove(0);
                }
            }
            mItemCount = count;
            mPager.setCurrentItem(mItemCount * 20 + mPager.getCurrentItem());
        }

        @Override
        public void onInvalidated() {
            redraw();
        }
    };

    private void setItemAsSelected(int position) {
        if (mPreviousSelectedIndicator != null) {
            mPreviousSelectedIndicator.setImageDrawable(mUnselectedDrawable);
            mPreviousSelectedIndicator.setPadding((int) mUnSelectedPaddingLeft, (int) mUnSelectedPaddingTop,
                    (int) mUnSelectedPaddingRight, (int) mUnSelectedPaddingBottom);
        }

        ImageView currentSelected = (ImageView) getChildAt(position + 1);
        if (currentSelected != null) {
            currentSelected.setImageDrawable(mSelectedDrawable);
            currentSelected.setPadding((int) mSelectedPaddingLeft, (int) mSelectedPaddingTop,
                    (int) mSelectedPaddingRight, (int) mSelectedPaddingBottom);
            mPreviousSelectedIndicator = currentSelected;
        }
        mPreviousSelectedPosition = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        if (mItemCount == 0) return;
        setItemAsSelected(position - 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    public IndicatorVisibility getIndicatorVisibility() {
        return mVisibility;
    }

    public int getSelectedIndicatorResId() {
        return mUserSetSelectedIndicatorResId;
    }

    public int getUnSelectedIndicatorResId() {
        return mUserSetUnSelectedIndicatorResId;
    }
}
