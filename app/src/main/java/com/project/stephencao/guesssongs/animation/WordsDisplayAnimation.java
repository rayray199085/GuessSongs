package com.project.stephencao.guesssongs.animation;

import android.view.animation.*;

public class WordsDisplayAnimation {
    public static AnimationSet addAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1500);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(1500);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        return animationSet;
    }
}
