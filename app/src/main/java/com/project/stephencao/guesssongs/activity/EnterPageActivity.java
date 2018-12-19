package com.project.stephencao.guesssongs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.project.stephencao.guesssongs.R;
import com.project.stephencao.guesssongs.util.ConstantValues;
import com.project.stephencao.guesssongs.util.MyMusicPlayer;
import com.project.stephencao.guesssongs.util.SharePreferencedUtil;

import java.util.Random;

public class EnterPageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mRankTextView, mIndexTextView;
    private ImageButton mAboutImageButton, mGuessImageButton, mBackToGameImageButton;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_page);
        Intent intent = getIntent();
        mIndex = intent.getIntExtra("index", 1);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog, null);
        ImageButton cancelButton = view.findViewById(R.id.ibtn_purchase_dialog_cancel);
        ImageButton confirmButton = view.findViewById(R.id.ibtn_purchase_dialog_confirm);
        TextView textView = view.findViewById(R.id.tv_purchase_dialog_notification);
        textView.setText("您确认要退出应用么?");
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
                finish();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void initData() {
        mIndexTextView.setText(mIndex + 1 + "");
        Random random = new Random();
        float defeatNum = random.nextInt(99) + random.nextInt(10) / 10.0f;
        mRankTextView.setText("您已经击败了全国" + defeatNum + "%的玩家。");
    }

    private void initView() {
        mRankTextView = findViewById(R.id.tv_enter_page_rank);
        mIndexTextView = findViewById(R.id.tv_enter_page_index_display);
        mAboutImageButton = findViewById(R.id.ibtn_enter_page_about);
        mAboutImageButton.setOnClickListener(this);
        mGuessImageButton = findViewById(R.id.ibtn_enter_page_guess);
        mGuessImageButton.setOnClickListener(this);
        mBackToGameImageButton = findViewById(R.id.ibtn_enter_page_to_game);
        mBackToGameImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_enter_page_about: {
                break;
            }
            case R.id.ibtn_enter_page_guess: {
                SharePreferencedUtil.putInteger(EnterPageActivity.this, ConstantValues.CURRENT_SONG_INDEX, -1);
                break;
            }
            case R.id.ibtn_enter_page_to_game: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("isFromEnterPage", true);
                intent.putExtra("currentIndex", mIndex - 1);
                startActivity(intent);
                finish();
                break;
            }
        }
    }
}
