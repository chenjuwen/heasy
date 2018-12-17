package com.heasy.app.action.pplay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.datastorage.SQLCipherManager;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.webview.Action;
import com.heasy.app.action.ActionNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 英语课程分类：年级、Unit
 */
@JSActionAnnotation(name = ActionNames.YYGrade)
public class YYGradeAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(YYGradeAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        SQLCipherManager cipherManager = heasyContext.getServiceEngine().getDataService().getSQLCipherManager();

        if("getGradeList".equalsIgnoreCase(extend)){
            JSONArray arr1 = new JSONArray();

            String sql = "select id,name from yy_grade_setting order by id asc";
            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);

            if(dataList != null && dataList.size() > 0){
                for(int i=0; i<dataList.size(); i++){
                    Map<String, String> map = dataList.get(i);

                    JSONObject object = new JSONObject();
                    object.put("id", map.get("id"));
                    object.put("name", map.get("name"));

                    arr1.add(object);
                }
            }

            logger.debug(arr1.toJSONString());
            return arr1.toJSONString();

        }else if("getUnitList".equalsIgnoreCase(extend)){
            String gradeId = FastjsonUtil.getString(jsonObject, "gradeId");
            JSONArray arr1 = new JSONArray();

            String sql = "select id,name from yy_unit_setting where grade_id=" + gradeId + " order by id asc";
            List<Map<String, String>> dataList = cipherManager.rawQuery(sql, null);

            if(dataList != null && dataList.size() > 0){
                for(int i=0; i<dataList.size(); i++){
                    Map<String, String> map = dataList.get(i);

                    JSONObject object = new JSONObject();
                    object.put("id", map.get("id"));
                    object.put("name", map.get("name"));

                    arr1.add(object);
                }
            }

            logger.debug(arr1.toJSONString());
            return arr1.toJSONString();
        }

        return SUCCESS;
    }

}
