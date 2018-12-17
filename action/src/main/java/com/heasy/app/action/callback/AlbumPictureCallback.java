package com.heasy.app.action.callback;

import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.webview.PictureCropCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 相册选择图片裁剪后的回调类
 */
public class AlbumPictureCallback implements PictureCropCallback {
    private static Logger logger = LoggerFactory.getLogger(AlbumPictureCallback.class);

    @Override
    public void execute(final HeasyContext heasyContext, String jsonData, String imagePath) {
        String script = "javascript: try{ pictureCallback(\"" + imagePath + "\"); }catch(e){ }";
        heasyContext.getJsInterface().loadUrl(script);
    }
}
