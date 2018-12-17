package com.heasy.app.baiduai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fujiayi on 2017/6/24.
 */
public class RecogResult {
    private static Logger logger = LoggerFactory.getLogger(RecogResult.class);

    private static final int ERROR_NONE = 0;

    private String origalJson;
    private String resultRecognition;
    private String desc;
    private String resultType;
    private int error = -1;
    private int subError = -1;

    public static RecogResult parseJson(String jsonStr) {
        logger.debug(jsonStr);

        RecogResult result = new RecogResult();
        result.setOrigalJson(jsonStr);

        try {
            JSONObject json = new JSONObject(jsonStr);

            int error = json.optInt("error");
            int subError = json.optInt("sub_error");
            result.setError(error);
            result.setSubError(subError);

            result.setDesc(json.optString("desc"));
            result.setResultType(json.optString("result_type"));

            if (error == ERROR_NONE) {
                JSONArray arr = json.optJSONArray("results_recognition");
                if (arr != null && arr.length() > 0) {
                    result.setResultRecognition(arr.getString(0));
                }
            }

        } catch (JSONException ex) {
            logger.error("", ex);
        }

        return result;
    }

    public boolean hasError() {
        return error != ERROR_NONE;
    }

    public boolean isFinalResult() {
        return "final_result".equals(resultType);
    }


    public boolean isPartialResult() {
        return "partial_result".equals(resultType);
    }

    public String getOrigalJson() {
        return origalJson;
    }

    public void setOrigalJson(String origalJson) {
        this.origalJson = origalJson;
    }

    public String getResultsRecognition() {
        return resultRecognition;
    }

    public void setResultRecognition(String resultRecognition) {
        this.resultRecognition = resultRecognition;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getSubError() {
        return subError;
    }

    public void setSubError(int subError) {
        this.subError = subError;
    }
}
