package dev.yanshouwang.wonder.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

import java.util.Map;

public class Move extends Transition {
    private static final String PROPERTY_X = "dev.yanshouwang.wonder.transition:Move:x";
    private static final String PROPERTY_Y = "dev.yanshouwang.wonder.transition:Move:y";
    private static final String PROPERTY_Z = "dev.yanshouwang.wonder.transition:Move:z";

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        final float x = view.getTranslationX();
        final float y = view.getTranslationY();
        final float z = view.getTranslationZ();
        final Map<String, Object> objects = transitionValues.values;
        objects.put(PROPERTY_X, x);
        objects.put(PROPERTY_Y, y);
        objects.put(PROPERTY_Z, z);
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        //return super.createAnimator(sceneRoot, startValues, endValues);

        if (startValues == null || endValues == null) {
            return null;
        }
        final Map<String, Object> startObjects = startValues.values;
        final Map<String, Object> endObjects = endValues.values;
        if (!startObjects.containsKey(PROPERTY_X) ||
                !startObjects.containsKey(PROPERTY_Y) ||
                !startObjects.containsKey(PROPERTY_Z) ||
                !endObjects.containsKey(PROPERTY_X) ||
                !endObjects.containsKey(PROPERTY_Y) ||
                !endObjects.containsKey(PROPERTY_Z)) {
            return null;
        }

        final View view = endValues.view;
        final float startX = (float) startObjects.get(PROPERTY_X);
        final float startY = (float) startObjects.get(PROPERTY_Y);
        final float startZ = (float) startObjects.get(PROPERTY_Z);
        final float endX = (float) endObjects.get(PROPERTY_X);
        final float endY = (float) endObjects.get(PROPERTY_Y);
        final float endZ = (float) endObjects.get(PROPERTY_Z);

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        ObjectAnimator animatorZ = ObjectAnimator.ofFloat(view, "translationZ", startZ, endZ);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY, animatorZ);

        return animatorSet;
    }
}
