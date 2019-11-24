package com.example.anagramchecker.fragment;


import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anagramchecker.R;
import com.example.anagramchecker.activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        SeekBar.OnSeekBarChangeListener, MainActivity.OnCallBack {

    private static VideoFragment mVideoFragment;

    String demoVideoPath;
    String demoVideoName;

    TextView videoName;
    TextView startTimeTextView, endTimeTextView;
    ImageView backPressedImageView, playVideoImageView;
    ImageView audioTrack;
    ImageView fastFrowardImageView, fastRewindImageView;

    MediaPlayer mediaPlayer;

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    SeekBar videoSeekBar;

    Handler videoHandler;
    Runnable videoRunnable;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mVideoFragment = this;

        Bundle bundle = getArguments();
        if (bundle != null) {
            demoVideoPath = bundle.getString("Path");
        }

        surfaceView = view.findViewById(R.id.surfaceView);
        backPressedImageView = view.findViewById(R.id.backPressed);
        playVideoImageView = view.findViewById(R.id.playVideo);
        videoName = view.findViewById(R.id.videoName);
        startTimeTextView = view.findViewById(R.id.startTime);
        videoSeekBar = view.findViewById(R.id.videoSeekBar);
        endTimeTextView = view.findViewById(R.id.endTime);
        audioTrack = view.findViewById(R.id.audioTrack);
        fastFrowardImageView = view.findViewById(R.id.fastForward);
        fastRewindImageView = view.findViewById(R.id.fastRewind);

        mediaPlayer = new MediaPlayer();

        surfaceHolder = surfaceView.getHolder();
        videoName.setText(demoVideoName);
        surfaceHolder.addCallback(this);

        surfaceView.setKeepScreenOn(true); // TO Keep screen on while playing video

        videoSeekBar.setProgress(0);
        videoSeekBar.setOnSeekBarChangeListener(this);

        backPressedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        audioTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMultiAudioTrack();
            }
        });

        fastRewindImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mediaPlayer.getCurrentPosition() - 5000) > 0) {
                    mediaPlayer.seekTo((mediaPlayer.getCurrentPosition() - 5000));
                }
            }
        });

        playVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playVideoImageView.setImageResource(R.drawable.ic_action_play);
                } else {
                    mediaPlayer.start();
                    playVideoImageView.setImageResource(R.drawable.ic_action_pause);
                }
            }
        });

        fastFrowardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.getCurrentPosition() + 5000 < mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo((mediaPlayer.getCurrentPosition() + 5000));
                }
            }
        });

        setHandler();

        return view;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NORMAL, R.style.AppTheme);
//    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        releaseMediaPlayer();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
        try {
            mediaPlayer.setDataSource(demoVideoPath);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        /*
         * Now to set the max Value of SeekBar
         * */
        videoSeekBar.setMax(mediaPlayer.getDuration());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.videoSeekBar:
                if (fromUser) { // only if user manually seeks the seekBar....Important
                    mediaPlayer.seekTo(progress);
                    int currentVideoDuration = mediaPlayer.getCurrentPosition();
                    startTimeTextView.setText("" + convertIntoTime(currentVideoDuration));
                    endTimeTextView.setText("-" + convertIntoTime(mediaPlayer.getDuration() - currentVideoDuration));
                }
                break;
        }

        if (progress == mediaPlayer.getDuration()) {
            playVideoImageView.setImageResource(R.drawable.ic_action_play);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /*
     * Now We will create a handler for startTimeTextView, endTimeTextView and for seeking the videoSeekBar with MediaPlayer
     * */
    private void setHandler() {
        videoHandler = new Handler();
        videoRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.getDuration() > 0) {
                    int currentVideoDuration = mediaPlayer.getCurrentPosition();
                    videoSeekBar.setProgress(currentVideoDuration);
                    startTimeTextView.setText("" + convertIntoTime(currentVideoDuration));
                    endTimeTextView.setText("-" + convertIntoTime(mediaPlayer.getDuration() - currentVideoDuration));

                    /*
                     * mediaPlayer.getDuration() = Total Time of Video
                     * currentVideoDuration = currentTime of Video
                     * */
                }

                videoHandler.postDelayed(this, 0);
            }
        };

        /*
         * To Start the handler
         * */
        videoHandler.postDelayed(videoRunnable, 500);
    }

    /*
     * This function can convert the time from int milliSecond/ long milliSecond to String format like 12:00, 23:00
     * */
    private String convertIntoTime(int ms) {
        String time;
        int x, seconds, minutes, hours;
        x = (int) (ms / 1000);
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        if (hours != 0)
            time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        else time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        return time;
    }

    private void addMultiAudioTrack() {
        MediaPlayer.TrackInfo trackInfos[] = mediaPlayer.getTrackInfo();
        ArrayList<Integer> audioTracksIndex = new ArrayList<>();

        for (int i = 0; i < trackInfos.length; i++) {
            if (trackInfos[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO){
                audioTracksIndex.add(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Audio Tracks");

        String values[] = new String[audioTracksIndex.size()];
        for (int i = 0; i < audioTracksIndex.size(); i++) {
            values[i] = String.valueOf("Track " + i);
        }

        /*
         * SingleChoice means RadioGroup
         * */
        builder.setSingleChoiceItems(values, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.selectTrack(which);
                Toast.makeText(getContext(), "Track " + which + " Selected", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            /*
             * Remove Callback from the handler...Important
             * */
            videoHandler.removeCallbacks(videoRunnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static VideoFragment getInstance() {
        return mVideoFragment;
    }
}
