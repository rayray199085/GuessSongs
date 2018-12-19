package com.project.stephencao.guesssongs.bean;

import android.widget.Button;

public class GridViewButtonItems {
    private int mIndex;
    private boolean mIsVisible;
    private String mContent;
    private Button button;
    private int wordPosition;

    public int getWordPosition() {
        return wordPosition;
    }

    public void setWordPosition(int wordPosition) {
        this.wordPosition = wordPosition;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public boolean ismIsVisible() {
        return mIsVisible;
    }

    public void setmIsVisible(boolean mIsVisible) {
        this.mIsVisible = mIsVisible;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

}
