package com.tabbie.android.radar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

public class FlingableTabHost extends TabHost {
  GestureDetector gestureDetector;

  Animation rightInAnimation;
  Animation rightOutAnimation;
  Animation leftInAnimation;
  Animation leftOutAnimation;

  public FlingableTabHost(Context context, AttributeSet attrs) {
    super(context, attrs);

    rightInAnimation = AnimationUtils.loadAnimation(context,
        R.anim.slide_in_right);
    rightOutAnimation = AnimationUtils.loadAnimation(context,
        android.R.anim.slide_out_right);
    leftInAnimation = AnimationUtils.loadAnimation(context,
        android.R.anim.slide_in_left);
    leftOutAnimation = AnimationUtils.loadAnimation(context,
        R.anim.slide_out_left);

    final int minScaledFlingVelocity = ViewConfiguration.get(context)
        .getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation

    gestureDetector = new GestureDetector(
        new GestureDetector.SimpleOnGestureListener() {
          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2,
              float velocityX, float velocityY) {
            int tabCount = getTabWidget().getTabCount();
            int currentTab = getCurrentTab();
            if (Math.abs(velocityX) > minScaledFlingVelocity
                && Math.abs(velocityY) < minScaledFlingVelocity) {

              final boolean right = velocityX < 0;
              final int newTab = MathUtils.constrain(currentTab
                  + (right ? 1 : -1), 0, tabCount - 1);
              if (newTab != currentTab) {
                // Somewhat hacky, depends on current implementation of TabHost:
                // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
                // f=core/java/android/widget/TabHost.java
                View currentView = getCurrentView();
                setCurrentTab(newTab);
                View newView = getCurrentView();

                newView.startAnimation(right ? rightInAnimation
                    : leftInAnimation);
                currentView.startAnimation(right ? rightOutAnimation
                    : leftOutAnimation);
              }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
          }
        });
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (gestureDetector.onTouchEvent(ev)) {
      return true;
    }
    return super.onInterceptTouchEvent(ev);
  }
}