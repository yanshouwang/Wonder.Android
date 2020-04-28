package dev.yanshouwang.wonder.material.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.Map;

public class Vary extends Transition {
    private static final String PROPERTY_INTERPOLATION = "dev.yanshouwang.wonder.material.transition:Vary:interpolation";

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        final Drawable drawable = transitionValues.view.getBackground();
        boolean valid = drawable instanceof MaterialShapeDrawable;
        if (!valid) {
            return;
        }
        final MaterialShapeDrawable shapeDrawable = (MaterialShapeDrawable) drawable;
        final float value = shapeDrawable.getInterpolation();
        final Map<String, Object> objects = transitionValues.values;
        objects.put(PROPERTY_INTERPOLATION, value);
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final Map<String, Object> startObjects = startValues.values;
        final Map<String, Object> endObjects = endValues.values;
        if (!startObjects.containsKey(PROPERTY_INTERPOLATION) || !endObjects.containsKey(PROPERTY_INTERPOLATION)) {
            return null;
        }

        final MaterialShapeDrawable drawable = (MaterialShapeDrawable) endValues.view.getBackground();
        float startValue = (float) startObjects.get(PROPERTY_INTERPOLATION);
        float endValue = (float) endObjects.get(PROPERTY_INTERPOLATION);

        return ObjectAnimator.ofFloat(drawable, "interpolation", startValue, endValue);
    }
}
