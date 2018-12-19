package com.project.stephencao.guesssongs.util;

import android.media.MediaPlayer;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class MyMusicPlayer {
    private static MediaPlayer mMusicPlayer;

    public static int playSong(String fileName) {
        int duration = 0;
        if (mMusicPlayer == null) {
            mMusicPlayer = new MediaPlayer();
        }
        mMusicPlayer.reset();
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {
            try {
                mMusicPlayer.setDataSource(file.getAbsolutePath());
                mMusicPlayer.prepare();
                mMusicPlayer.start();
                duration = mMusicPlayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return duration;
    }

    public static void stopMusic() {
        if (mMusicPlayer != null) {
            mMusicPlayer.stop();
        }
    }
}
