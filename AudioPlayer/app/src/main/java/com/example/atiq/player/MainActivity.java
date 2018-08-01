package com.example.atiq.player;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button playBtn;
    SeekBar positionBar;
    TextView elopledTimeLabel;
    TextView remaindingTimeLabel;

    // media player
    MediaPlayer mediaPlayer;// = MediaPlayer.create(this, R.raw.music);
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playBtn = (Button) findViewById(R.id.playBtn);
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        elopledTimeLabel = (TextView) findViewById(R.id.elopledTimeLabel);
        remaindingTimeLabel = (TextView) findViewById(R.id.remaindingTimeLabel);


        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f, 0.5f);
        totalTime = mediaPlayer.getDuration();

        positionBar.setMax(totalTime);


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


    // this part is not working in this section
    /*
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i("Instance state", "onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.i("Instance state", "onRestoreInstanceState");
    }
*/


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
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }
}
