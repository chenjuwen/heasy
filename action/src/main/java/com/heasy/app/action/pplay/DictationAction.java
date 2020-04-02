package com.heasy.app.action.pplay;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heasy.app.action.ActionNames;
import com.heasy.app.baiduai.api.SpeechAPI;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.datastorage.SQLCipherManager;
import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.DatetimeUtil;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.MP3Play;
import com.heasy.app.core.utils.ParameterUtil;
import com.heasy.app.core.utils.SQLiteUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 听写
 */
@JSActionAnnotation(name = "DictationAction")
public class DictationAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(DictationAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

        if("list".equalsIgnoreCase(extend)){
            JSONArray arr1 = new JSONArray();

            String sql = "select id,title,content,create_time,dictation from dictation order by create_time desc";
            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);

            if(dataList != null && dataList.size() > 0){
                for(int i=0; i<dataList.size(); i++){
                    Map<String, String> map = dataList.get(i);

                    JSONObject object = new JSONObject();
                    object.put("id", map.get("id"));
                    object.put("title", map.get("title"));
                    object.put("content", map.get("content"));
                    object.put("create_time", map.get("create_time"));
                    object.put("dictation", map.get("dictation"));
                    arr1.add(object);
                }
            }

            return arr1.toJSONString();

        }else if("save".equalsIgnoreCase(extend)){
            String title = FastjsonUtil.getString(jsonObject, "title");
            String content = FastjsonUtil.getString(jsonObject, "content");
            String create_time = String.valueOf(DatetimeUtil.nowTimeInMillis());

            Map dataMap = SQLiteUtil.toMap("title", title, "content", content,"create_time", create_time, "dictation", "否");
            cipherManager.insert("dictation", dataMap);

        } else if ("delete".equalsIgnoreCase(extend)) {
            String id = FastjsonUtil.getString(jsonObject, "id");
            cipherManager.delete("dictation", "id=?", new String[]{String.valueOf(id)});

        } else if ("view".equalsIgnoreCase(extend)) {
            String id = FastjsonUtil.getString(jsonObject, "id");
            String url = FastjsonUtil.getString(jsonObject, "url");

            String sql = "select id,title,content,create_time,dictation from dictation where id=" + id;

            Map<String, String> params = new HashMap<>();
            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);
            if (dataList != null && dataList.size() > 0) {
                Map<String, String> map = dataList.get(0);
                params.put("id", map.get("id"));
                params.put("title", map.get("title"));
                params.put("content", map.get("content"));
                params.put("dictation", map.get("dictation"));
            }

            return heasyContext.getJsInterface().pageTransfer(url, ParameterUtil.toParamString(params, false));

        } else if ("update".equalsIgnoreCase(extend)) {
            String id = FastjsonUtil.getString(jsonObject, "id");
            String title = FastjsonUtil.getString(jsonObject, "title");
            String content = FastjsonUtil.getString(jsonObject, "content");
            String dictation = FastjsonUtil.getString(jsonObject, "dictation");

            Map dataMap = SQLiteUtil.toMap("title", title, "content", content, "dictation", dictation);
            cipherManager.update("dictation", dataMap, "id=?", new String[]{String.valueOf(id)});

        } else if ("getListenData".equalsIgnoreCase(extend)) {
            String id = FastjsonUtil.getString(jsonObject, "id");
            String sql = "select content from dictation where id=" + id;
            String content = "";

            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);
            if (dataList != null && dataList.size() > 0) {
                Map<String, String> map = dataList.get(0);
                content = map.get("content");
            }

            return content;
        } else if ("voice".equalsIgnoreCase(extend)) {
            String word = FastjsonUtil.getString(jsonObject, "word");
            String rewrite = FastjsonUtil.getString(jsonObject, "rewrite"); //重写

            String basePath = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath() + "dictation/";
            File dir = new File(basePath);
            if(!dir.exists()){
                dir.mkdirs();
            }

            String filename = basePath + word + ".mp3";
            logger.info(filename);

            String result = "";
            File file = new File(filename);
            if("1".equalsIgnoreCase(rewrite) || !file.exists()){
                result = createVoice(heasyContext, jsonObject, filename);
            }

            if(StringUtil.isEmpty(result)) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MP3Play.play(filename);
            }
            return result;
        }

        return SUCCESS;
    }

    @NonNull
    private String createVoice(HeasyContext heasyContext, JSONObject jsonObject, String filename) {
        try {
            String word = FastjsonUtil.getString(jsonObject, "word");
            String person = FastjsonUtil.getString(jsonObject, "person"); //发音人
            String speed = FastjsonUtil.getString(jsonObject, "speed"); //语速

            AIConfigBean configBean = AIConfigFactory.getAIConfigBean(heasyContext.getServiceEngine().getAndroidContext());

            SpeechAPI tts = new SpeechAPI(configBean.getTtsAppid(), configBean.getTtsApikey(), configBean.getTtsSecretkey());
            tts.setAue("3"); //mp3
            tts.setPer(person);
            tts.setSpd(speed);
            tts.setText(word);
            tts.setDestFileName(filename);
            tts.generate();

            return "";
        }catch(Exception ex){
            logger.error("create voice error", ex);
            return "error";
        }
    }

}
