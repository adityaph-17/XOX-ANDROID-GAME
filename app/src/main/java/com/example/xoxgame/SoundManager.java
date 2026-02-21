package com.example.xoxgame;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {

    public static boolean isSoundOn = true;
    private static MediaPlayer mediaPlayer;

    public static void playSound(Context context, int soundResId) {

        if (!isSoundOn) return;

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(context, soundResId);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
        });
    }
}