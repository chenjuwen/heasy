package com.heasy.app.core.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Administrator on 2018/10/28.
 */
public class PCMPlay {
   public static void play(String filePath){
       AudioTrack audioTrack = null;
       try {
           byte[] dataByte = FileUtil.readByteFile(filePath);

           int bufferSize = AudioTrack.getMinBufferSize(
                   16000,
                   AudioFormat.CHANNEL_OUT_MONO,
                   AudioFormat.ENCODING_PCM_16BIT);

           audioTrack = new AudioTrack(
                   AudioManager.STREAM_MUSIC,          //音频类型
                   16000,                //采样率
                   AudioFormat.CHANNEL_OUT_MONO,       //声道数
                   AudioFormat.ENCODING_PCM_16BIT,     //采样点位数
                   bufferSize,                           //缓冲区大小
                   AudioTrack.MODE_STREAM);

           audioTrack.play();
           audioTrack.write(dataByte, 0, dataByte.length);
           audioTrack.stop();

       }catch(Exception ex){
           ex.printStackTrace();
       }finally{
           if(audioTrack != null){
               audioTrack.release();
               audioTrack = null;
           }
       }
   }

}
