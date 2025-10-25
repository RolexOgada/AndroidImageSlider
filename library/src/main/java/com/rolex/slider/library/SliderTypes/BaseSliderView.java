package com.rolex.slider.library.SliderTypes;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.rolex.slider.library.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

/**
 * BaseSliderView provides common functionality for all custom slider views.
 * Extend this class to implement your own slider views.
 * Examples: {@link DefaultSliderView}, {@link TextSliderView}.
 *
 * If you want to show a progress bar, add a view with id @+id/loading_bar in your layout.
 */
public abstract class BaseSliderView {

    protected final Context mContext;

    private Bundle mBundle;
    private int mErrorPlaceHolderRes;
    private int mEmptyPlaceHolderRes;

    private String mUrl;
    private File mFile;
    private int mRes;

    protected OnSliderClickListener mOnSliderClickListener;
    private boolean mErrorDisappear;
    private ImageLoadListener mLoadListener;
    private String mDescription;

    private Picasso mPicasso;
    private ScaleType mScaleType = ScaleType.FIT;

    public enum ScaleType { CENTER_CROP, CENTER_INSIDE, FIT, FIT_CENTER_CROP }

    protected BaseSliderView(Context context) {
        this.mContext = context;
    }

    public BaseSliderView empty(int resId) {
        mEmptyPlaceHolderRes = resId;
        return this;
    }

    public BaseSliderView errorDisappear(boolean disappear) {
        mErrorDisappear = disappear;
        return this;
    }

    public BaseSliderView error(int resId) {
        mErrorPlaceHolderRes = resId;
        return this;
    }

    public BaseSliderView description(String description) {
        mDescription = description;
        return this;
    }

    public BaseSliderView image(String url) {
        checkSingleImageCall();
        mUrl = url;
        return this;
    }

    public BaseSliderView image(File file) {
        checkSingleImageCall();
        mFile = file;
        return this;
    }

    public BaseSliderView image(int res) {
        checkSingleImageCall();
        mRes = res;
        return this;
    }

    private void checkSingleImageCall() {
        if (mUrl != null || mFile != null || mRes != 0) {
            throw new IllegalStateException("Only one image source can be set (URL, File, or Resource).");
        }
    }

    public BaseSliderView bundle(Bundle bundle) {
        mBundle = bundle;
        return this;
    }

    public String getUrl() { return mUrl; }
    public boolean isErrorDisappear() { return mErrorDisappear; }
    public int getEmpty() { return mEmptyPlaceHolderRes; }
    public int getError() { return mErrorPlaceHolderRes; }
    public String getDescription() { return mDescription; }
    public Context getContext() { return mContext; }
    public Bundle getBundle() { return mBundle; }

    public BaseSliderView setOnSliderClickListener(OnSliderClickListener listener) {
        mOnSliderClickListener = listener;
        return this;
    }

    public BaseSliderView setScaleType(ScaleType type) {
        mScaleType = type;
        return this;
    }

    public ScaleType getScaleType() { return mScaleType; }

    public void setOnImageLoadListener(ImageLoadListener listener) {
        mLoadListener = listener;
    }

    public Picasso getPicasso() { return mPicasso; }
    public void setPicasso(Picasso picasso) { mPicasso = picasso; }

    /**
     * Bind click events and load image into target ImageView using Picasso.
     * Call this at the end of your getView() implementation.
     */
    protected void bindEventAndShow(final View view, ImageView targetImageView) {
        final BaseSliderView self = this;

        view.setOnClickListener(v -> {
            if (mOnSliderClickListener != null) mOnSliderClickListener.onSliderClick(self);
        });

        if (targetImageView == null) return;
        if (mLoadListener != null) mLoadListener.onStart(self);

        Picasso picasso = (mPicasso != null) ? mPicasso : Picasso.get();
        RequestCreator request = null;

        if (mUrl != null) request = picasso.load(mUrl);
        else if (mFile != null) request = picasso.load(mFile);
        else if (mRes != 0) request = picasso.load(mRes);
        else return;

        if (mEmptyPlaceHolderRes != 0) request.placeholder(mEmptyPlaceHolderRes);
        if (mErrorPlaceHolderRes != 0) request.error(mErrorPlaceHolderRes);

        switch (mScaleType) {
            case FIT -> request.fit();
            case CENTER_CROP -> request.fit().centerCrop();
            case CENTER_INSIDE -> request.fit().centerInside();
            case FIT_CENTER_CROP -> request.fit().centerCrop(); // optional custom behavior
        }

        request.into(targetImageView, new Callback() {
            @Override
            public void onSuccess() {
                View loadingBar = view.findViewById(R.id.loading_bar);
                if (loadingBar != null) loadingBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                if (mLoadListener != null) mLoadListener.onEnd(false, self);
                View loadingBar = view.findViewById(R.id.loading_bar);
                if (loadingBar != null) loadingBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public interface OnSliderClickListener {
        void onSliderClick(BaseSliderView slider);
    }

    public interface ImageLoadListener {
        void onStart(BaseSliderView target);
        void onEnd(boolean result, BaseSliderView target);
    }

    /**
     * Subclasses must implement this to provide the slider view layout.
     */
    public abstract View getView();
}
