package com.project.stephencao.guesssongs.animation;

import android.view.animation.*;

public class WordsDisappearAnimation {
    public static AnimationSet addAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(500);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        return animationSet;
    }
}
