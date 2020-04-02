package com.heasy.app.core.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/10/30.
 */
public class MP3Play {
    private static Logger logger = LoggerFactory.getLogger(MP3Play.class);
    private static MediaPlayer mediaPlayer = null;

    public static void play(String filePath){
        try{
            init();

            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            logger.debug("media start play!!");

            /*
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mMediaPlayer) {
                    destroy();
                }
            });
            */

        }catch(Exception ex){
            logger.error("", ex);
            destroy();
        }
    }

    public static void init(){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
    }

    public static void destroy(){
        if(mediaPlayer != null) {
            try {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                mediaPlayer.release();
                mediaPlayer = null;
                logger.debug("media stop play!!");

            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

}
