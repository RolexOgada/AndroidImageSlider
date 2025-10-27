package com.rolex.slider.demo;

import android.util.Log;
import android.view.View;

import com.daimajia.androidanimations.library.attention.StandUpAnimator;
import com.rolex.slider.library.Animations.BaseAnimationInterface;

public class ChildAnimationExample implements BaseAnimationInterface {

    private static final String TAG = "ChildAnimationExample";

    @Override
    public void onPrepareCurrentItemLeaveScreen(View current) {
        View descriptionLayout = current.findViewById(com.rolex.slider.library.R.id.description_layout);
        if (descriptionLayout != null) {
            descriptionLayout.setVisibility(View.INVISIBLE);
        }
        Log.d(TAG, "onPrepareCurrentItemLeaveScreen called");
    }

    @Override
    public void onPrepareNextItemShowInScreen(View next) {
        View descriptionLayout = next.findViewById(com.rolex.slider.library.R.id.description_layout);
        if (descriptionLayout != null) {
            descriptionLayout.setVisibility(View.INVISIBLE);
        }
        Log.d(TAG, "onPrepareNextItemShowInScreen called");
    }

    @Override
    public void onCurrentItemDisappear(View view) {
        Log.d(TAG, "onCurrentItemDisappear called");
    }

    @Override
    public void onNextItemAppear(View view) {
        View descriptionLayout = view.findViewById(com.rolex.slider.library.R.id.description_layout);
        if (descriptionLayout != null) {
            descriptionLayout.setVisibility(View.VISIBLE);
            new StandUpAnimator().animate();
        }
        Log.d(TAG, "onNextItemAppear called");
    }
}
