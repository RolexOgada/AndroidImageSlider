package com.rolex.slider.library.Transformers;


import static com.nineoldandroids.view.ViewHelper.*;

import android.view.View;

public class AccordionTransformer extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        setPivotX(view,position < 0 ? 0 : view.getWidth());
        setScaleX(view,position < 0 ? 1f + position : 1f - position);
    }

}