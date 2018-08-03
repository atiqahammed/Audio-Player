package com.example.atiq.player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Configuration;


public class MainActivity extends AppCompatActivity {

    private Button playBtn;
    private SeekBar positionBar;
    private SeekBar volumeBar;
    private SeekBar positionBarLand;
    private TextView elopledTimeLabel;
    private TextView remaindingTimeLabel;
    private Button songList;
    private int backButtonCount = 0;


    MediaPlayer mediaPlayer;
    private int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backButtonCount = 0;
        initiateComponent();

        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float valumeNumber = progress / 100f;
                        mediaPlayer.setVolume(valumeNumber, valumeNumber);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // thread update position bar
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mediaPlayer != null) {
                    Message message = new Message();
                    message.what = mediaPlayer.getCurrentPosition();
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initiateComponent() {
        songList = (Button) findViewById(R.id.song_list_btn);
        playBtn = (Button) findViewById(R.id.playBtn);
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        elopledTimeLabel = (TextView) findViewById(R.id.elopledTimeLabel);
        remaindingTimeLabel = (TextView) findViewById(R.id.remaindingTimeLabel);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f, 0.5f);
        totalTime = mediaPlayer.getDuration();
        positionBar.setMax(totalTime);
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            //playBtn.setBackgroundResource(R.drawable.play_icon);
            backButtonCount = 0;
            Toast.makeText(this, "Exited from Player." , Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            int currentPosition = msg.what;
            positionBar.setProgress(currentPosition);

            String elopseTime = createTimeLabel(currentPosition);
            String remindingTime = createTimeLabel(totalTime - currentPosition);
            elopledTimeLabel.setText(elopseTime);
            remaindingTimeLabel.setText("- "+remindingTime);

        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = (time / 1000) % 60;

        timeLabel = min + ":";
        if(sec < 10) {
            timeLabel += "0";
        }
            timeLabel += sec;

        return timeLabel;
    }

    public void playBtnClick(View view) {
        if(!mediaPlayer.isPlaying()) {
            backButtonCount = 0;
            playBtn.setBackgroundResource(R.drawable.pause_icon);
            mediaPlayer.start();
        } else {
            backButtonCount = 0;
            playBtn.setBackgroundResource(R.drawable.play_icon);
            mediaPlayer.pause();
        }
    }


    public void songListView(View view) {
        backButtonCount = 0;
        Intent intent = new Intent(this, SongList.class);
        startActivity(intent);

    }

    public void stopPlayer(View view) {
        mediaPlayer.pause();
        playBtn.setBackgroundResource(R.drawable.play_icon);
        backButtonCount = 0;
        Toast.makeText(this, "Player Stopped" , Toast.LENGTH_SHORT).show();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

        /*android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }
}
