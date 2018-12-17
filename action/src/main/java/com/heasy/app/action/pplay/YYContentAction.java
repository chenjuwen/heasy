package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heasy.app.action.ActionNames;
import com.heasy.app.baiduai.api.SpeechAPI;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.baiduai.listener.TTSSynthesizeFinishCallback;
import com.heasy.app.baiduai.service.DefaultTTSService;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.datastorage.SQLCipherManager;
import com.heasy.app.core.service.ServiceEngineFactory;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.FileUtil;
import com.heasy.app.core.utils.PCMPlay;
import com.heasy.app.core.utils.ParameterUtil;
import com.heasy.app.core.utils.SQLiteUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 英语课程内容的维护
 */
@JSActionAnnotation(name = ActionNames.YYContent)
public class YYContentAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(YYContentAction.class);
    public static String VOICE_FILE_BASE_PATH = "";
    private Thread batchCreateThread = null;
    private Thread batchPlayThread = null;

    public YYContentAction(){
        YYContentAction.VOICE_FILE_BASE_PATH = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getSdcardRootPath() + "english/";
        logger.debug(YYContentAction.VOICE_FILE_BASE_PATH);
    }

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        final SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

        if("getContentList".equalsIgnoreCase(extend)){
            String unitId = FastjsonUtil.getString(jsonObject, "unitId");

            StringBuilder sb = new StringBuilder();
            sb.append(" select id,title,case when type='3' then '' else content end content,type,file_path ");
            sb.append(" from yy_content ");
            sb.append(" where unit_id=" + unitId);
            sb.append(" order by type,id ");

            JSONArray array = new JSONArray();
            List<Map<String, String>> dataList = cipherManager.rawQuery(sb.toString(), null);
            if(dataList != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, String> map = dataList.get(i);
                    JSONObject obj = new JSONObject();
                    obj.put("id", map.get("id"));
                    obj.put("title", map.get("title"));
                    obj.put("content", map.get("content"));
                    obj.put("type", map.get("type"));
                    obj.put("filePath", StringUtil.trimToEmpty(map.get("file_path")));
                    array.add(obj);
                }
            }
            return array.toString();

        }else if("save".equalsIgnoreCase(extend)){//保存
            String title = FastjsonUtil.getString(jsonObject, "title");
            String content = FastjsonUtil.getString(jsonObject, "content");
            String type = FastjsonUtil.getString(jsonObject, "type");
            String unitId = FastjsonUtil.getString(jsonObject, "unitId");

            Map dataMap = SQLiteUtil.toMap("title", title, "content", content, "type", type, "unit_id", unitId);
            cipherManager.insert("yy_content", dataMap);

        }else if("edit".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String gradeId = FastjsonUtil.getString(jsonObject, "gradeId");
            String url = FastjsonUtil.getString(jsonObject, "url");

            String sql = "select id,title,content,unit_id,type from yy_content where id=" + id;

            Map<String, String> params = new HashMap<>();
            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);
            if(dataList != null && dataList.size() > 0) {
                Map<String, String> dataMap = dataList.get(0);
                params.put("id", dataMap.get("id"));
                params.put("title", dataMap.get("title"));

                if("3".equalsIgnoreCase(dataMap.get("type"))){
                    params.put("content", dataMap.get("content").replaceAll("\n", "\\\\n"));
                }else{
                    params.put("content", dataMap.get("content"));
                }

                params.put("unitId", dataMap.get("unit_id"));
                params.put("type", dataMap.get("type"));
                params.put("filePath", StringUtil.trimToEmpty(dataMap.get("file_path")));
                params.put("gradeId", gradeId);
            }

            return heasyContext.getJsInterface().pageTransfer(url, ParameterUtil.toParamString(params, false));

        }else if("update".equalsIgnoreCase(extend)){//更新
            String id = FastjsonUtil.getString(jsonObject, "id");
            String title = FastjsonUtil.getString(jsonObject, "title");
            String content = FastjsonUtil.getString(jsonObject, "content");

            String filename = id + ".pcm";
            String filePath = VOICE_FILE_BASE_PATH + filename;
            FileUtil.deleteFile(filePath);

            Map dataMap = SQLiteUtil.toMap("title", title, "content", content, "file_path", "");
            cipherManager.update("yy_content", dataMap, "id=?", new String[]{String.valueOf(id)});

        }else if("create_voice".equalsIgnoreCase(extend)){ //生成语音文件
            String iType = FastjsonUtil.getString(jsonObject, "iType");
            if("sdk".equalsIgnoreCase(iType)) {
                createVoice(heasyContext, jsonObject);
            }else{
                createVoiceWithAPI(heasyContext, jsonObject);
            }

        }else if("play_voice".equalsIgnoreCase(extend)){ //播放语音文件
            String id = FastjsonUtil.getString(jsonObject, "id");
            Map<String, String> dataMap = cipherManager.get("yy_content", "id", id, null);
            String filePath = dataMap.get("file_path");
            if(StringUtil.isNotEmpty(filePath)){
                filePath = VOICE_FILE_BASE_PATH + filePath;
                PCMPlay.play(filePath);
            }

        }else if("delete".equalsIgnoreCase(extend)){ //删除
            String id = FastjsonUtil.getString(jsonObject, "id");

            String filename = id + ".pcm";
            String filePath = VOICE_FILE_BASE_PATH + filename;
            FileUtil.deleteFile(filePath);

            String sql = "delete from yy_content where id=" + id;
            cipherManager.executeSQL(sql);

        }else if("batchImport".equalsIgnoreCase(extend)){ //批量导入
            String type = FastjsonUtil.getString(jsonObject, "type");
            if("1".equalsIgnoreCase(type)){ //单词
                return batchImportWord(cipherManager, jsonObject);
            }else{ //句子
                return batchImportSentence(cipherManager, jsonObject);
            }

        }else if("batchCreateVoice".equalsIgnoreCase(extend)){
            String unitId = FastjsonUtil.getString(jsonObject, "unitId");

            if(batchCreateThread != null){
                try {
                    batchCreateThread.interrupt();
                    batchCreateThread = null;
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            batchCreateThread = new BatchCreateThread(unitId, heasyContext);
            batchCreateThread.setDaemon(true);
            batchCreateThread.start();

        }else if("batchPlayVoice".equalsIgnoreCase(extend)){ //批量播放
            String unitId = FastjsonUtil.getString(jsonObject, "unitId");

            if(batchPlayThread != null){
                try {
                    batchPlayThread.interrupt();
                    batchPlayThread = null;
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            batchPlayThread = new BatchPlayThread(unitId, heasyContext);
            batchPlayThread.setDaemon(true);
            batchPlayThread.start();

        }else if("globalSearch".equalsIgnoreCase(extend)){//全局搜索
            String gradeId = FastjsonUtil.getString(jsonObject, "gradeId");
            String unitId = FastjsonUtil.getString(jsonObject, "unitId");
            String yyContent = FastjsonUtil.getString(jsonObject, "yyContent");

            StringBuilder sb = new StringBuilder();
            sb.append(" select a.id,a.title,case when a.type='3' then '' else a.content end content,a.file_path,a.type, ");
            sb.append(" c.name as categoryName1,b.name as categoryName2 ");
            sb.append(" from yy_content a ");
            sb.append(" left join yy_unit_setting b on a.unit_id=b.id ");
            sb.append(" left join yy_grade_setting c on b.grade_id=c.id ");
            sb.append(" where a.id is not null ");

            if(StringUtil.isNotEmpty(gradeId)){
                sb.append(" and c.id=" + gradeId);
            }

            if(StringUtil.isNotEmpty(unitId)){
                sb.append(" and b.id=" + unitId);
            }

            if(StringUtil.isNotEmpty(yyContent)) {
                sb.append(" and (a.title like '%" + yyContent + "%' or a.content like '%" + yyContent + "%') ");
            }

            sb.append(" order by c.name, b.name, a.type, a.id ");

            JSONArray array = new JSONArray();
            List<Map<String, String>> dataList = cipherManager.rawQuery(sb.toString(), null);
            if(dataList != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, String> map = dataList.get(i);
                    JSONObject obj = new JSONObject();
                    obj.put("id", map.get("id"));
                    obj.put("title", StringUtil.trimToEmpty(map.get("title")));
                    obj.put("content", StringUtil.trimToEmpty(map.get("content")));
                    obj.put("filePath", StringUtil.trimToEmpty(map.get("file_path")));
                    obj.put("type", StringUtil.trimToEmpty(map.get("type")));
                    obj.put("categoryName1", StringUtil.trimToEmpty(map.get("categoryName1")));
                    obj.put("categoryName2", StringUtil.trimToEmpty(map.get("categoryName2")));
                    array.add(obj);
                }
            }
            return array.toString();
        }

        return SUCCESS;
    }

    private void createVoice(HeasyContext heasyContext, JSONObject jsonObject){
        final String id = FastjsonUtil.getString(jsonObject, "id");
        final String isPlay = FastjsonUtil.getString(jsonObject, "isPlay");
        final SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

        Map<String, String> dataMap = cipherManager.get("yy_content", "id", id, null);

        String text = "";
        if("3".equalsIgnoreCase(dataMap.get("type"))){
            text = StringUtil.trimToEmpty(dataMap.get("content"));
        }else {
            text = StringUtil.trimToEmpty(dataMap.get("title"));
        }
        logger.debug(text);

        DefaultTTSService ttsService = heasyContext.getServiceEngine().getService(DefaultTTSService.class);
        ttsService.synthesize(text, new TTSSynthesizeFinishCallback() {
            @Override
            public void execute(byte[] bytes) {
                String filename = id + ".pcm";
                String filePath = VOICE_FILE_BASE_PATH + filename;
                FileUtil.writeFile(bytes, filePath);
                logger.debug(filePath);

                String sql = "update yy_content set file_path='" + filename + "' where id=" + id;
                cipherManager.executeSQL(sql);

                if("1".equalsIgnoreCase(isPlay)) {
                    PCMPlay.play(filePath);
                }
            }
        });
    }

    private void createVoiceWithAPI(HeasyContext heasyContext, JSONObject jsonObject){
        final String id = FastjsonUtil.getString(jsonObject, "id");
        final String isPlay = FastjsonUtil.getString(jsonObject, "isPlay");
        final SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

        Map<String, String> dataMap = cipherManager.get("yy_content", "id", id, null);
        String text = "";
        if("3".equalsIgnoreCase(dataMap.get("type"))){
            text = StringUtil.trimToEmpty(dataMap.get("title")) + "\n" + StringUtil.trimToEmpty(dataMap.get("content"));
        }else {
            text = StringUtil.trimToEmpty(dataMap.get("title"));
        }
        logger.debug(text);

        doCreateVoice(heasyContext, id, isPlay, text);
    }

    private void doCreateVoice(HeasyContext heasyContext, String id, String isPlay, String text) {
        AIConfigBean configBean = AIConfigFactory.getAIConfigBean(heasyContext.getServiceEngine().getAndroidContext());

        String filename = id + ".pcm";
        String filePath = VOICE_FILE_BASE_PATH + filename;

        SpeechAPI tts = new SpeechAPI(configBean.getTtsAppid(), configBean.getTtsApikey(), configBean.getTtsSecretkey());
        tts.setPer(configBean.getTtsPerson());
        tts.setSpd(configBean.getTtsSpeed());
        tts.setText(text);
        tts.setDestFileName(filePath);

        try {
            tts.generate();

            String sql = "update yy_content set file_path='" + filename + "' where id=" + id;
            heasyContext.getServiceEngine().getDataService().getSQLCipherManager().executeSQL(sql);

        } catch (Exception ex) {
            logger.error("", ex);
        }

        if("1".equalsIgnoreCase(isPlay)) {
            PCMPlay.play(filePath);
        }
    }

    private String batchImportWord(SQLCipherManager cipherManager, JSONObject jsonObject){
        String unitId = FastjsonUtil.getString(jsonObject, "unitId");
        String content = FastjsonUtil.getString(jsonObject, "content");

        if(StringUtil.isNotEmpty(content)) {
            List<Map<String, Object>> dataList = new ArrayList<>();

            String[] arr2 = content.split("\\n");
            for(String rowData : arr2){
                if(StringUtil.isNotEmpty(rowData)) {
                    String[] arr3 = rowData.split(":");
                    if(arr3.length == 2) {
                        String s1 = StringUtil.trimToEmpty(arr3[0]);
                        String s2 = StringUtil.trimToEmpty(arr3[1]);

                        if(StringUtil.isNotEmpty(s1) && StringUtil.isNotEmpty(s2)) {
                            Map dataMap = SQLiteUtil.toMap("unit_id", unitId,"title", s1, "content", s2, "type", "1");
                            dataList.add(dataMap);
                        }
                    }
                }
            }

            if(dataList.size() > 0) {
                cipherManager.batchInsert("yy_content", dataList);
                return SUCCESS;
            }else{
                return "数据格式有误";
            }
        }else{
            return "数据格式有误";
        }
    }

    private String batchImportSentence(SQLCipherManager cipherManager, JSONObject jsonObject){
        String unitId = FastjsonUtil.getString(jsonObject, "unitId");
        String content = FastjsonUtil.getString(jsonObject, "content");

        if(StringUtil.isNotEmpty(content)) {
            String[] arr2 = content.split("\\n");
            if(arr2.length % 2 != 0){
                return "数据格式有误";
            }else{
                List<Map<String, Object>> dataList = new ArrayList<>();

                for(int i=0; i<arr2.length; i=i+2){
                    String s1 = StringUtil.trimToEmpty(arr2[i]);
                    String s2 = StringUtil.trimToEmpty(arr2[i+1]);

                    if(StringUtil.isNotEmpty(s1) && StringUtil.isNotEmpty(s2)) {
                        Map dataMap = SQLiteUtil.toMap("unit_id", unitId, "title", s1, "content", s2, "type", "2");
                        dataList.add(dataMap);
                    }
                }

                if(dataList.size() > 0) {
                    cipherManager.batchInsert("yy_content", dataList);
                    return SUCCESS;
                }else{
                    return "数据格式有误";
                }
            }
        }else{
            return "数据格式有误";
        }
    }

    /**
     * 批量生成语音文件的线程
     */
    class BatchCreateThread extends Thread{
        private String unitId;
        private HeasyContext heasyContext;

        public BatchCreateThread(String unitId, HeasyContext heasyContext){
            this.unitId = unitId;
            this.heasyContext = heasyContext;
        }

        @Override
        public void run() {
            String sql = "select * from yy_content where unit_id=" + unitId + " order by type,id";

            List<Map<String, String>> dataList = heasyContext.getServiceEngine().getDataService().getSQLCipherManager().rawQuery(sql, null);
            if(dataList != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, String> dataMap = dataList.get(i);

                    String text = "";
                    if("3".equalsIgnoreCase(dataMap.get("type"))){
                        text = StringUtil.trimToEmpty(dataMap.get("title")) + "\n" + StringUtil.trimToEmpty(dataMap.get("content"));
                    }else {
                        text = StringUtil.trimToEmpty(dataMap.get("title"));
                    }

                    doCreateVoice(heasyContext, dataMap.get("id"), "1", text);
                }
            }

            String script = "javascript: try{ batchCreateFinish(); }catch(e){ }";
            heasyContext.getJsInterface().loadUrl(script);
        }
    }

    /**
     * 批量播放语音文件的线程
     */
    class BatchPlayThread extends Thread{
        private String unitId;
        private HeasyContext heasyContext;

        public BatchPlayThread(String unitId, HeasyContext heasyContext){
            this.unitId = unitId;
            this.heasyContext = heasyContext;
        }

        @Override
        public void run() {
            String sql = "select * from yy_content where unit_id=" + unitId + " order by type,id";

            List<Map<String, String>> dataList = heasyContext.getServiceEngine().getDataService().getSQLCipherManager().rawQuery(sql, null);
            if(dataList != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, String> dataMap = dataList.get(i);

                    String filePath = dataMap.get("file_path");
                    if(StringUtil.isNotEmpty(filePath)) {
                        filePath = VOICE_FILE_BASE_PATH + filePath;
                        PCMPlay.play(filePath);
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
