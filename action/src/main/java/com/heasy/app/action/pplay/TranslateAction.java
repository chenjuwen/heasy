package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heasy.app.baiduai.api.TranslateAPI;
import com.heasy.app.baiduai.config.AIConfigBean;
import com.heasy.app.baiduai.config.AIConfigFactory;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.StringUtil;
import com.heasy.app.core.webview.Action;
import com.heasy.app.action.ActionNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * 翻译
 */
@JSActionAnnotation(name = ActionNames.Translate)
public class TranslateAction implements Action {
    private static Logger logger = LoggerFactory.getLogger(TranslateAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        String content = FastjsonUtil.getString(jsonObject, "content");
        String transType = FastjsonUtil.getString(jsonObject, "transType");

        StringBuilder sb = new StringBuilder();
        try{
            AIConfigBean configBean = AIConfigFactory.getAIConfigBean(heasyContext.getServiceEngine().getAndroidContext());

            TranslateAPI api = new TranslateAPI(configBean.getTransAppid(), configBean.getTransSecretkey());
            String result = api.getTransResult(content, "auto", transType);
            logger.debug(result);

            if(StringUtil.isNotEmpty(result)){
                JSONObject object = JSONObject.parseObject(result);
                if(object.get("trans_result") != null) {
                    JSONArray jsonArray = (JSONArray) object.get("trans_result");

                    for (int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        sb.append(URLDecoder.decode(jsonObject.getString("dst"), "utf-8") + "\n");
                    }
                }else{
                    sb.append(object.getString("error_msg"));
                }
            }

        }catch(Exception ex){
            logger.error("", ex);
        }

        return sb.toString();
    }

}
