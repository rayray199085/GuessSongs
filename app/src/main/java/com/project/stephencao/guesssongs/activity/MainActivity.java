package com.project.stephencao.guesssongs.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.project.stephencao.guesssongs.R;
import com.project.stephencao.guesssongs.adapter.MyGridViewAdapter;
import com.project.stephencao.guesssongs.animation.WordsDisappearAnimation;
import com.project.stephencao.guesssongs.animation.WordsDisplayAnimation;
import com.project.stephencao.guesssongs.bean.GridViewButtonItems;
import com.project.stephencao.guesssongs.bean.Song;
import com.project.stephencao.guesssongs.util.ConstantValues;
import com.project.stephencao.guesssongs.util.MyMusicPlayer;
import com.project.stephencao.guesssongs.util.SharePreferencedUtil;
import com.project.stephencao.guesssongs.view.MyGridView;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton mStartPlayImageButton, mShareImageButton, mPassTaskNextImageButton,
            mPassTaskShareImageButton, mWinPageReturnImageButton, mWinPageWechatImageButton,
            mWinPageSupportImageButton, mReturnImageButton;
    private RelativeLayout mRemoveAWrongWordRL, mGetATipRl;
    private ImageView mDiscRotateImageView, mPinRotateImageView;
    private List<GridViewButtonItems> mGridViewButtonItemsList;
    private List<GridViewButtonItems> mAnswerDisplayItemsList;
    private MyGridViewAdapter myGridViewAdapter;
    private MyGridView myGridView;
    private int mCoinCount = 0;
    private List<Integer> mAvailableRemoveWordList;
    private int nonEmptyAnswerCount = 0;
    private LinearLayout mSelectedWordsLinearLayout, mWinPageLinearLayout;
    private Song mCurrentSong;
    private int mDuration;
    private boolean mDoNeedChangeWordColor = true;
    private int mFlashTime = 7;
    private TextView mTotalCoinCountTextView, mLevelNumTextView, mPassTaskTitleTV, mPassTaskSongNumTV, mPassTaskSongNameTV;
    private LinearLayout mPassTaskLinearLayout;
    private int currentSongIndex = -1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantValues.FLASH_ANSWERS: {
                    for (int i = 0; i < mAnswerDisplayItemsList.size(); i++) {
                        Button button = mAnswerDisplayItemsList.get(i).getButton();
                        if (mDoNeedChangeWordColor) {
                            button.setTextColor(Color.RED);
                        } else {
                            button.setTextColor(Color.WHITE);
                        }
                    }
                    mDoNeedChangeWordColor = !mDoNeedChangeWordColor;
                    break;
                }
                case ConstantValues.IS_PLAYING: {
                    RotateAnimation pinRotateAnimationOff = new RotateAnimation(10.0f, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
                    pinRotateAnimationOff.setDuration(500);
                    pinRotateAnimationOff.setFillAfter(true);
                    mPinRotateImageView.clearAnimation();
                    pinRotateAnimationOff.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mStartPlayImageButton.setVisibility(View.VISIBLE);
                            mDiscRotateImageView.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mPinRotateImageView.startAnimation(pinRotateAnimationOff);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentSongIndex = SharePreferencedUtil.getInteger(this,ConstantValues.CURRENT_SONG_INDEX,-1);
        initView();
        initData();
        initAnswer();

    }

    private Song loadCurrentSongInfo(int index) {
        Song currentSong = new Song();
        String[] stage = ConstantValues.SONG_INFO[index];
        currentSong.setFileName(stage[ConstantValues.FILE_NAME]);
        currentSong.setSongName(stage[ConstantValues.SONG_NAME]);
        currentSong.setSongNameLength(currentSong.getSongName().length());
        return currentSong;
    }

    private void initAnswer() {
        mAnswerDisplayItemsList = new ArrayList<>();
        for (int i = 0; i < mCurrentSong.getSongNameLength(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_word_button, null);
            Button wordAnswerButton = view.findViewById(R.id.btn_grid_view_item);
            final GridViewButtonItems gridViewButtonItems = new GridViewButtonItems();
            gridViewButtonItems.setmContent("");
            gridViewButtonItems.setmIsVisible(true);
            gridViewButtonItems.setmIndex(i);
            gridViewButtonItems.setButton(wordAnswerButton);
            gridViewButtonItems.setWordPosition(-1);
            wordAnswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gridViewButtonItems.getmContent().length() > 0) {
                        clearSelectedAnswer(gridViewButtonItems);
                    }
                }
            });
            mAnswerDisplayItemsList.add(gridViewButtonItems);
        }
        for (GridViewButtonItems items : mAnswerDisplayItemsList) {
            Button wordAnswerButton = items.getButton();
            wordAnswerButton.setTextColor(Color.WHITE);
            wordAnswerButton.setText(items.getmContent());
            if (!items.ismIsVisible()) {
                wordAnswerButton.setVisibility(View.INVISIBLE);
            } else {
                wordAnswerButton.setVisibility(View.VISIBLE);
            }
            wordAnswerButton.setBackgroundResource(R.drawable.game_wordblank);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(100, 100);
            mSelectedWordsLinearLayout.addView(wordAnswerButton, params);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        boolean isFromEnterPage = intent.getBooleanExtra("isFromEnterPage", false);
        if (isFromEnterPage) {
            currentSongIndex = intent.getIntExtra("currentIndex", -1);
        }
        intent.removeExtra("isFromEnterPage");
        initCoinCount();
        mTotalCoinCountTextView.setText(String.valueOf(mCoinCount));
        mCurrentSong = loadCurrentSongInfo(++currentSongIndex);
        mLevelNumTextView.setText(String.valueOf(currentSongIndex + 1));
        String[] words = generateWords();
        mGridViewButtonItemsList = new ArrayList<>();
        for (int i = 0; i < ConstantValues.GRID_VIEW_COUNT; i++) {
            GridViewButtonItems buttonItems = new GridViewButtonItems();
            buttonItems.setmIndex(i);
            buttonItems.setmIsVisible(true);
            buttonItems.setmContent(words[i]);
            mGridViewButtonItemsList.add(buttonItems);
        }
        myGridViewAdapter = new MyGridViewAdapter(mGridViewButtonItemsList, this, myGridView);
        myGridView.setAdapter(myGridViewAdapter);
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Button button = view.findViewById(R.id.btn_grid_view_item);
                AnimationSet animationSet = WordsDisappearAnimation.addAnimation();
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setSelectedWords(mGridViewButtonItemsList.get(position), button);
                        mGridViewButtonItemsList.get(position).setButton(button);
                        int answerStatus = checkInputAnswer();
                        switch (answerStatus) {
                            case ConstantValues.ANSWER_STATUS_CORRECT: {
                                handlePassTaskEvent();
                                break;
                            }
                            case ConstantValues.ANSWER_STATUS_INCORRECT: {
                                incorrectAnswerFlashWords();
                                break;
                            }
                            case ConstantValues.ANSWER_STATUS_INCOMPLETE: {
                                System.out.println("need more");
                                break;
                            }
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                button.startAnimation(animationSet);
            }
        });
        mAvailableRemoveWordList = checkSafelyRemoveWord(getSongNameArray());
    }

    private String[] getSongNameArray() {
        String[] songName = new String[mCurrentSong.getSongNameLength()];
        for (int i = 0; i < mCurrentSong.getSongNameLength(); i++) {
            songName[i] = String.valueOf(mCurrentSong.getSongNameCharacters()[i]);
        }
        return songName;
    }

    private void handlePassTaskEvent() {
        MyMusicPlayer.playSong(ConstantValues.COIN_AUDIO);
        mDiscRotateImageView.clearAnimation();
        mPinRotateImageView.clearAnimation();
        mPassTaskLinearLayout.setVisibility(View.VISIBLE);
        Random random = new Random();
        float defeatNum = random.nextInt(99) + random.nextInt(10) / 10.0f;
        mPassTaskTitleTV.setText("您已经击败了全国" + defeatNum + "%的玩家。");
        mPassTaskSongNumTV.setText(String.valueOf(currentSongIndex + 1));
        String[] songNameArray = getSongNameArray();
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : songNameArray) {
            stringBuffer.append(s);
        }
        mPassTaskSongNameTV.setText(stringBuffer.toString());
    }

    private void incorrectAnswerFlashWords() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mFlashTime; i++) {
                    try {
                        Thread.sleep(100);
                        mHandler.sendEmptyMessage(ConstantValues.FLASH_ANSWERS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void clearSelectedAnswer(final GridViewButtonItems gridViewButtonItems) {
        gridViewButtonItems.getButton().setText("");
        gridViewButtonItems.getButton().setTextColor(Color.WHITE);
        mDoNeedChangeWordColor = true;
        final Button button = mGridViewButtonItemsList.get(gridViewButtonItems.getWordPosition()).getButton();
        AnimationSet animationSet = WordsDisplayAnimation.addAnimation();
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button.setVisibility(View.VISIBLE);
                gridViewButtonItems.setmContent("");
                nonEmptyAnswerCount--;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        button.startAnimation(animationSet);

    }

    /**
     * hide an available button and show one answer word
     *
     * @param items  for answer list
     * @param button in grid view
     */
    private void setSelectedWords(GridViewButtonItems items, Button button) {
        for (int i = 0; i < mAnswerDisplayItemsList.size(); i++) {
            if (mAnswerDisplayItemsList.get(i).getmContent().equals("")) {
                nonEmptyAnswerCount++;
                mAnswerDisplayItemsList.get(i).setmContent(items.getmContent());
                mAnswerDisplayItemsList.get(i).getButton().setText(items.getmContent());
                mAnswerDisplayItemsList.get(i).setWordPosition(items.getmIndex());
                button.setVisibility(View.INVISIBLE);
                return;
            }
        }
    }


    @Override
    protected void onPause() {
        mDiscRotateImageView.clearAnimation();
        mPinRotateImageView.clearAnimation();
        MyMusicPlayer.stopMusic();
        SharePreferencedUtil.putInteger(this,ConstantValues.CURRENT_SONG_INDEX, currentSongIndex -1);
        super.onPause();
    }


    /**
     * Initialize the User Interface
     */
    private void initView() {
        mReturnImageButton = findViewById(R.id.ibtn_top_bar_return);
        mReturnImageButton.setOnClickListener(this);
        mWinPageLinearLayout = findViewById(R.id.ll_win_page_view);
        mWinPageReturnImageButton = findViewById(R.id.ibtn_win_page_return);
        mWinPageReturnImageButton.setOnClickListener(this);
        mWinPageWechatImageButton = findViewById(R.id.ibtn_winpage_wechat);
        mWinPageWechatImageButton.setOnClickListener(this);
        mWinPageSupportImageButton = findViewById(R.id.ibtn_win_page_support);
        mWinPageSupportImageButton.setOnClickListener(this);
        mPassTaskSongNameTV = findViewById(R.id.tv_pass_task_song_name);
        mPassTaskNextImageButton = findViewById(R.id.ibtn_pass_task_next);
        mPassTaskNextImageButton.setOnClickListener(this);
        mPassTaskShareImageButton = findViewById(R.id.ibtn_pass_task_share);
        mPassTaskShareImageButton.setOnClickListener(this);
        mPassTaskSongNumTV = findViewById(R.id.tv_pass_task_song_num);
        mPassTaskTitleTV = findViewById(R.id.tv_pass_task_title);
        mLevelNumTextView = findViewById(R.id.tv_float_button_task_number);
        mRemoveAWrongWordRL = findViewById(R.id.rl_float_button_remove);
        mRemoveAWrongWordRL.setOnClickListener(this);
        mGetATipRl = findViewById(R.id.rl_float_button_hint);
        mGetATipRl.setOnClickListener(this);
        mShareImageButton = findViewById(R.id.ibtn_float_button_share);

        mTotalCoinCountTextView = findViewById(R.id.tv_top_bar_total_coin_display);
        mPassTaskLinearLayout = findViewById(R.id.ll_pass_task_view);
        mSelectedWordsLinearLayout = findViewById(R.id.ll_word_selection_layout);
        myGridView = findViewById(R.id.gv_word_selection_grid);
        mStartPlayImageButton = findViewById(R.id.ibtn_disc_start);
        mDiscRotateImageView = findViewById(R.id.iv_disc_light);
        mPinRotateImageView = findViewById(R.id.iv_disc_pin);
        mStartPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDuration = MyMusicPlayer.playSong( ConstantValues.SONG_INFO[currentSongIndex][0]);
                final RotateAnimation discRotateAnimation = new RotateAnimation(0.0f,
                        360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                discRotateAnimation.setRepeatCount(Animation.INFINITE);
                discRotateAnimation.setDuration(200);

                RotateAnimation pinRotateAnimationOn = new RotateAnimation(0.0f, 10.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
                pinRotateAnimationOn.setDuration(500);
                pinRotateAnimationOn.setFillAfter(true);
                mPinRotateImageView.clearAnimation();
                pinRotateAnimationOn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mStartPlayImageButton.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mDiscRotateImageView.startAnimation(discRotateAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mPinRotateImageView.startAnimation(pinRotateAnimationOn);
                mHandler.sendEmptyMessageDelayed(ConstantValues.IS_PLAYING, mDuration);
            }
        });
    }

    private char getRandomChar() {
        int highPos;
        int lowPos;
        Random random = new Random();
        highPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] bytes = new byte[2];
        bytes[0] = (Integer.valueOf(highPos)).byteValue();
        bytes[1] = (Integer.valueOf(lowPos)).byteValue();
        String word = "";
        try {

            word = new String(bytes, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return word.charAt(0);
    }

    private String[] generateWords() {
        List<String> wordList = new ArrayList<>();
        String[] words = new String[ConstantValues.GRID_VIEW_COUNT];
        for (int i = 0; i < mCurrentSong.getSongNameLength(); i++) {
            wordList.add(String.valueOf(mCurrentSong.getSongNameCharacters()[i]));
        }
        for (int i = mCurrentSong.getSongNameLength(); i < ConstantValues.GRID_VIEW_COUNT; i++) {
            wordList.add(String.valueOf(getRandomChar()));
        }
        Collections.shuffle(wordList);
        for (int i = 0; i < wordList.size(); i++) {
            words[i] = wordList.get(i);
        }
        return words;
    }

    private int checkInputAnswer() {
        if (nonEmptyAnswerCount < mAnswerDisplayItemsList.size()) {
            return ConstantValues.ANSWER_STATUS_INCOMPLETE;
        } else {
            StringBuffer answers = new StringBuffer();
            for (int i = 0; i < mAnswerDisplayItemsList.size(); i++) {
                answers.append(mAnswerDisplayItemsList.get(i).getmContent());
            }
            if (mCurrentSong.getSongName().equals(answers.toString())) {
                return ConstantValues.ANSWER_STATUS_CORRECT;
            } else {
                return ConstantValues.ANSWER_STATUS_INCORRECT;
            }
        }
    }

    private void initCoinCount() {
        boolean isFirstTime = SharePreferencedUtil.getBoolean(this, ConstantValues.IS_FIRST_TIME_PLAY, true);
        if (isFirstTime) {
            SharePreferencedUtil.putInteger(this, ConstantValues.COIN_COUNT, 100);
            mCoinCount = 100;
            SharePreferencedUtil.putBoolean(this, ConstantValues.IS_FIRST_TIME_PLAY, false);
        } else {
            mCoinCount = SharePreferencedUtil.getInteger(this, ConstantValues.COIN_COUNT, 100);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_float_button_remove: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final AlertDialog alertDialog = builder.create();
                View view = LayoutInflater.from(this).inflate(R.layout.view_dialog, null);
                ImageButton cancelButton = view.findViewById(R.id.ibtn_purchase_dialog_cancel);
                ImageButton confirmButton = view.findViewById(R.id.ibtn_purchase_dialog_confirm);
                TextView textView = view.findViewById(R.id.tv_purchase_dialog_notification);
                textView.setText("确认花费30个金币去掉一个错误答案?");
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMusicPlayer.playSong(ConstantValues.CANCEL_AUDIO);
                        alertDialog.dismiss();
                    }
                });
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMusicPlayer.playSong(ConstantValues.ENTER_AUDIO);
                        alertDialog.dismiss();
                        Collections.shuffle(mAvailableRemoveWordList);
                        if (mCoinCount > ConstantValues.REMOVE_A_WORD
                                && mAvailableRemoveWordList.size() > 0 && checkInputAnswer() == ConstantValues.ANSWER_STATUS_INCOMPLETE) {
                            mGridViewButtonItemsList.get(mAvailableRemoveWordList.get(0)).getButton().setVisibility(View.INVISIBLE);
                            mAvailableRemoveWordList.remove(0);
                            mCoinCount -= ConstantValues.REMOVE_A_WORD;
                            mTotalCoinCountTextView.setText(String.valueOf(mCoinCount));
                        } else if (mCoinCount < ConstantValues.REMOVE_A_WORD) {
                            Toast.makeText(MainActivity.this, "您的余额已不足，请及时充值。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();

                break;
            }
            case R.id.rl_float_button_hint: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final AlertDialog alertDialog = builder.create();
                View view = LayoutInflater.from(this).inflate(R.layout.view_dialog, null);
                ImageButton cancelButton = view.findViewById(R.id.ibtn_purchase_dialog_cancel);
                ImageButton confirmButton = view.findViewById(R.id.ibtn_purchase_dialog_confirm);
                TextView textView = view.findViewById(R.id.tv_purchase_dialog_notification);
                textView.setText("确认花费90个金币获得一个文字提示?");
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMusicPlayer.playSong(ConstantValues.CANCEL_AUDIO);
                        alertDialog.dismiss();
                    }
                });
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMusicPlayer.playSong(ConstantValues.ENTER_AUDIO);
                        alertDialog.dismiss();
                        int position = getAnswerPosition();
                        if (mCoinCount > ConstantValues.GET_A_TIP && position != -1
                                && checkInputAnswer() == ConstantValues.ANSWER_STATUS_INCOMPLETE) {
                            setSelectedWords(mGridViewButtonItemsList.get(position), mGridViewButtonItemsList.get(position).getButton());
                            mCoinCount -= ConstantValues.GET_A_TIP;
                            mTotalCoinCountTextView.setText(String.valueOf(mCoinCount));
                            if (ConstantValues.ANSWER_STATUS_CORRECT == checkInputAnswer()) {
                                handlePassTaskEvent();
                            }
                            if (ConstantValues.ANSWER_STATUS_INCORRECT == checkInputAnswer()) {
                                incorrectAnswerFlashWords();
                            } else if (ConstantValues.ANSWER_STATUS_CORRECT == checkInputAnswer()) {
                                handlePassTaskEvent();
                            }
                        } else if (mCoinCount < ConstantValues.GET_A_TIP) {
                            Toast.makeText(MainActivity.this, "您的余额已不足，请及时充值。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
                break;
            }
            case R.id.ibtn_float_button_share: {
                break;
            }
            case R.id.ibtn_pass_task_next: {
                if (currentSongIndex < ConstantValues.SONG_INFO.length - 1) {
                    prepareNextTaskData();
                } else {
                    mWinPageLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.ibtn_pass_task_share: {
                break;
            }
            case R.id.ibtn_win_page_return: {
                mWinPageLinearLayout.setVisibility(View.INVISIBLE);
                currentSongIndex = -1;
                prepareNextTaskData();
                break;
            }
            case R.id.ibtn_top_bar_return: {
                Intent intent = new Intent(this, EnterPageActivity.class);
                intent.putExtra("index", currentSongIndex);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    /**
     * Reset or clear previous task data
     */
    private void prepareNextTaskData() {
        mSelectedWordsLinearLayout.removeAllViews();
        mGridViewButtonItemsList.clear();
        mAnswerDisplayItemsList.clear();
        SharePreferencedUtil.putInteger(this, ConstantValues.COIN_COUNT, mCoinCount + 10);
        initView();
        initData();
        initAnswer();
        nonEmptyAnswerCount = 0;
        mPassTaskLinearLayout.setVisibility(View.INVISIBLE);
    }

    private int getAnswerPosition() {
        String targetWord = String.valueOf(mCurrentSong.getSongNameCharacters()[nonEmptyAnswerCount]);
        for (int i = 0; i < mGridViewButtonItemsList.size(); i++) {
            if (targetWord.equals(mGridViewButtonItemsList.get(i).getmContent())) {
                return i;
            }
        }
        return -1;
    }

    private List<Integer> checkSafelyRemoveWord(String[] songName) {
        List<Integer> availableWordList = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < mGridViewButtonItemsList.size(); i++) {
            for (String character : songName) {
                if (mGridViewButtonItemsList.get(i).getmContent().equals(character)) {
                    flag = true;
                }
            }
            if (flag) {
                flag = false;
                continue;
            } else {
                availableWordList.add(i);
            }
        }
        return availableWordList;
    }

}
