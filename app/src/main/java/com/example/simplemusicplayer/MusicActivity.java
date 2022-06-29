package com.example.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import com.example.simplemusicplayer.databinding.ActivityMusicBinding;

import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    private int totalTime;
    private Handler handler;
    private Runnable runnable;

    private int position;
    private String filePath, title;
    private ArrayList<String> songList;
    private MediaPlayer mediaPlayer;

    private Animation anim;
    private ActivityMusicBinding musicBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicBinding = ActivityMusicBinding.inflate(getLayoutInflater());
        setContentView(musicBinding.getRoot());

        position = getIntent().getIntExtra("position", 0);
        filePath = getIntent().getStringExtra("file_path");
        title = getIntent().getStringExtra("title");
        songList = getIntent().getStringArrayListExtra("list");

        anim = AnimationUtils.loadAnimation(MusicActivity.this,R.anim.translate_anim);
        musicBinding.textViewMusicSongName.setAnimation(anim);
        setMediaPlayer();
        musicBinding.textViewMusicSongName.setText(title);
        musicBinding.musicBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset(); //prevent app will crash
                if (position == 0) {
                    position = songList.size() - 1; //switch to the last song
                } else {
                    position--;
                }
                String newFilePath = songList.get(position);
                updateMediaPlayer(newFilePath);
                musicBinding.musicBtnPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                String newTitle = newFilePath.substring(newFilePath.lastIndexOf("/") + 1);
                musicBinding.textViewMusicSongName.setText(newTitle);
                musicBinding.textViewMusicSongName.clearAnimation();
                musicBinding.textViewMusicSongName.startAnimation(anim);
            }
        });
        musicBinding.musicBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    musicBinding.musicBtnPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    mediaPlayer.start();
                    musicBinding.musicBtnPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                }
            }
        });
        musicBinding.musicBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        musicBinding.seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress); //move to the new point
                    musicBinding.seekBarMusic.setProgress(progress); //update progress
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        musicBinding.seekBarMusicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) { //user made some changes
                    musicBinding.seekBarMusicVolume.setProgress(progress); //update progress
                    float volumeLevel = progress / 100f;
                    mediaPlayer.setVolume(volumeLevel, volumeLevel);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //for music seek bar
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                totalTime = mediaPlayer.getDuration();
                musicBinding.seekBarMusic.setMax(totalTime);

                int currentPosition = mediaPlayer.getCurrentPosition();
                musicBinding.seekBarMusic.setProgress(currentPosition);
                handler.postDelayed(runnable, 1000); //1s later run again
                String elapsedTime = timeLabel(currentPosition);
                String lastTime = timeLabel(totalTime);
                Log.d("total time:",lastTime);
                String remainTime = timeLabel(totalTime-currentPosition);
                musicBinding.textViewMusicStart.setText(elapsedTime);
                musicBinding.textViewMusicEnd.setText(remainTime);
                if (elapsedTime.endsWith(lastTime)) {
                    playNext();
                }
            }
        };
        handler.post(runnable);
    }

    private void setMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMediaPlayer(String newFilePath) {
        try {
            mediaPlayer.setDataSource(newFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String timeLabel(int currentPosition) {
        String timeLabel;
        int min, sec;
        min = (currentPosition / 1000) / 60; // currentPosition in millisecond => second => min
        sec = (currentPosition / 1000) % 60; // currentPosition in millisecond => second
        if (sec < 10) {
            timeLabel = min + ":0" + sec;
        } else {
            timeLabel = min + ":" + sec;
        }
        return timeLabel;
    }

    private void playNext() {
        mediaPlayer.reset(); //prevent app will crash
        if (position == songList.size() - 1) {
            position = 0;
        } else {
            position++;
        }
        String newFilePath = songList.get(position);
        updateMediaPlayer(newFilePath);
        musicBinding.musicBtnPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        String newTitle = newFilePath.substring(newFilePath.lastIndexOf("/") + 1);
        musicBinding.textViewMusicSongName.setText(newTitle);
        musicBinding.textViewMusicSongName.clearAnimation();
        musicBinding.textViewMusicSongName.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}