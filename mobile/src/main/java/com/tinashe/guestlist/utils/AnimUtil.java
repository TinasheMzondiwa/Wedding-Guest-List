package com.tinashe.guestlist.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by tinashe on 2016/02/17.
 */
@TargetApi(21)
public class AnimUtil {

    private static Interpolator fastOutSlowIn;
    private static Interpolator fastOutLinearIn;

    public AnimUtil() {
    }

    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }

    public static Interpolator getFastOutLinearInInterpolator(Context context) {
        if (fastOutLinearIn == null) {
            fastOutLinearIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_linear_in);
        }
        return fastOutLinearIn;
    }

    public static void slideInEnterAnimation(Context context, View view, Animator.AnimatorListener listener) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int screenHeight = display.heightPixels;

        ViewHelper.setTranslationY(view, screenHeight);
        ViewPropertyAnimator
                .animate(view)
                .translationY(0)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator(3f))
                .setListener(listener)
                .start();
    }



    /**
     * Taken from Plaid App by Nick Butcher
     *
     * An implementation of {@link Property} to be used specifically with fields of
     * type
     * <code>float</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Float) set()} function that takes the primitive
     * <code>float</code> type and avoids autoboxing and other overhead associated with the
     * <code>Float</code> class.
     *
     * @param <T> The class on which the Property is declared.
     **/
    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Float)} that is faster when dealing
         * with fields of type <code>float</code>.
         */
        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }

    /**
     * Taken from Plaid App by Nick Butcher
     *
     * An implementation of {@link Property} to be used specifically with fields of
     * type
     * <code>int</code>. This type-specific subclass enables performance benefit by allowing
     * calls to a {@link #set(Object, Integer) set()} function that takes the primitive
     * <code>int</code> type and avoids autoboxing and other overhead associated with the
     * <code>Integer</code> class.
     *
     * @param <T> The class on which the Property is declared.
     */
    public static abstract class IntProperty<T> extends Property<T, Integer> {

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        /**
         * A type-specific override of the {@link #set(Object, Integer)} that is faster when dealing
         * with fields of type <code>int</code>.
         */
        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value) {
            setValue(object, value.intValue());
        }

    }


}
