package com.heasy.app.baiduai.api;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.FaceVerifyRequest;
import com.baidu.aip.util.Base64Util;
import com.heasy.app.core.utils.FileUtil;
import com.heasy.app.core.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人脸识别
 */
public class FaceAPI {
    private static Logger logger = LoggerFactory.getLogger(FaceAPI.class);
    private Map<String, String> kvMap = new HashMap<>();
    private AipFace client = null;
    private String appId;
    private String apiKey;
    private String secretKey;

    public FaceAPI(String appId, String apiKey, String secretKey){
        this.appId = appId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;

        initKVData();
        init();
    }

    /**
     * 人脸检测
     * @param filePath
     * @return
     */
    public String detect(String filePath){
        JSONObject newObject = new JSONObject();
        try {
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("face_field", "age,gender,glasses,expression,race,beauty,faceshape,quality");
            options.put("max_face_num", "1");
            options.put("face_type", "LIVE");

            String image = Base64Util.encode(FileUtil.readByteFile(filePath));
            JSONObject response = client.detect(image, "BASE64", options);
            System.out.println(response.toString(4));

            newObject = parseResult(response);
            System.out.println(newObject.toString(4));

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return newObject.toString();
    }

    /**
     * 在线活体检测
     */
    public String verify(String filePath){
        JSONObject newObject = new JSONObject();
        try {
            String face_field = "age,gender,glasses,expression,race,beauty,faceshape,quality";
            String image = Base64Util.encode(FileUtil.readByteFile(filePath));

            List<FaceVerifyRequest> list = new ArrayList<>();
            FaceVerifyRequest request = new FaceVerifyRequest(image, "BASE64", face_field);
            list.add(request);

            JSONObject response = client.faceverify(list);
            System.out.println(response.toString(4));

            newObject = parseResult(response);
            System.out.println(newObject.toString(4));

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return newObject.toString();
    }

    private JSONObject parseResult(JSONObject mainObject) throws Exception {
        JSONObject newObject = new JSONObject();

        String error_code = JsonUtil.getValue(mainObject, "error_code");
        if("0".equalsIgnoreCase(error_code)){
            JSONObject resultObject = mainObject.getJSONObject("result");

            //活体分数值
            double face_liveness = JsonUtil.getDoubleValue(resultObject, "face_liveness");
            newObject.put("face_liveness", face_liveness);

            //阈值数据
            double frr_1e_2 = JsonUtil.getDoubleValue(resultObject, "thresholds", "frr_1e-2"); //百分之一误识率的阈值
            double frr_1e_3 = JsonUtil.getDoubleValue(resultObject, "thresholds", "frr_1e-3"); //千分之一误识率的阈值
            double frr_1e_4 = JsonUtil.getDoubleValue(resultObject, "thresholds", "frr_1e-4"); //万分之一误识率的阈值
            newObject.put("frr_1e_2", frr_1e_2);
            newObject.put("frr_1e_3", frr_1e_3);
            newObject.put("frr_1e_4", frr_1e_4);

            JSONArray newArray = new JSONArray();
            JSONArray array = resultObject.getJSONArray("face_list");
            for(int i=0; i<array.length(); i++){
                JSONObject jsonObject = (JSONObject)array.get(i);

                //性别：male:男性 female:女性
                String gender = JsonUtil.getValue(jsonObject, "gender", "type");
                gender = getValueByKey("", gender);

                //年龄
                int age = JsonUtil.getIntegerValue(jsonObject, "age"); //年龄

                //是否带眼镜：none:无眼镜，common:普通眼镜，sun:墨镜
                String glasses = JsonUtil.getValue(jsonObject, "glasses", "type");
                glasses = getValueByKey("", glasses);

                //表情：none:不笑；smile:微笑；laugh:大笑
                String expression = JsonUtil.getValue(jsonObject, "expression", "type");
                expression = getValueByKey("expression_", expression);

                //人脸位置
                int left = new Double(JsonUtil.getDoubleValue(jsonObject, "location", "left")).intValue();
                int top = new Double(JsonUtil.getDoubleValue(jsonObject, "location", "top")).intValue();
                int width = new Double(JsonUtil.getDoubleValue(jsonObject, "location", "width")).intValue();
                int height = new Double(JsonUtil.getDoubleValue(jsonObject, "location", "height")).intValue();

                //人脸置信度，范围【0~1】，代表这是一张人脸的概率，0最小、1最大。
                int face_probability = JsonUtil.getIntegerValue(jsonObject, "face_probability");

                //美丑打分，范围0-100，越大表示越美。
                double beauty = JsonUtil.getDoubleValue(jsonObject, "beauty");

                //脸型：square: 正方形 triangle:三角形 oval: 椭圆 heart: 心形 round: 圆形
                String face_shape = JsonUtil.getValue(jsonObject, "face_shape", "type");
                face_shape = getValueByKey("", face_shape);

                //人种：yellow: 黄种人 white: 白种人 black:黑种人 arabs: 阿拉伯人
                String race = JsonUtil.getValue(jsonObject, "race", "type");
                race = getValueByKey("", race);

                //人脸旋转角度
                //三维旋转之左右旋转角[-90(左), 90(右)]
                double yaw = JsonUtil.getDoubleValue(jsonObject, "angle", "yaw");
                //三维旋转之俯仰角度[-90(上), 90(下)]
                double pitch = JsonUtil.getDoubleValue(jsonObject, "angle", "pitch");
                //平面内旋转角[-180(逆时针), 180(顺时针)]
                double roll = JsonUtil.getDoubleValue(jsonObject, "angle", "roll");

                //human: 真实人脸 cartoon: 卡通人脸
                String face_type = JsonUtil.getValue(jsonObject, "face_type", "type");
                face_type = getValueByKey("", face_type);

                //活体分数值
                double liveness = JsonUtil.getDoubleValue(jsonObject, "liveness", "livemapscore");

                JSONObject obj = new JSONObject();
                obj.put("gender", gender);
                obj.put("age", age);
                obj.put("glasses", glasses);
                obj.put("expression", expression);
                obj.put("left", left);
                obj.put("top", top);
                obj.put("width", width);
                obj.put("height", height);
                obj.put("face_probability", face_probability);
                obj.put("beauty", beauty);
                obj.put("face_shape", face_shape);
                obj.put("race", race);
                obj.put("yaw", yaw);
                obj.put("pitch", pitch);
                obj.put("roll", roll);
                obj.put("face_type", face_type);
                obj.put("liveness", liveness);
                newArray.put(obj);
            }

            newObject.put("list", newArray);
            newObject.put("error_code", "0");
            newObject.put("error_msg", "");
        }else {
            newObject.put("error_code", error_code);
            newObject.put("error_msg", JsonUtil.getValue(mainObject, "error_msg"));
        }
        return newObject;
    }

    private void init(){
        try{
            if(client == null){
                client = new AipFace(appId, apiKey, secretKey);
                client.setConnectionTimeoutInMillis(10000);
                client.setSocketTimeoutInMillis(10000);
            }
        }catch(Exception ex){
            logger.error("", ex);
        }
    }

    private String getValueByKey(String prefix, String key){
        String newKey = prefix + key;
        if(kvMap.containsKey(newKey)){
            return kvMap.get(newKey);
        }else{
            return key;
        }
    }

    private void initKVData(){
        //表情
        kvMap.put("expression_none", "不笑");
        kvMap.put("expression_smile", "微笑");
        kvMap.put("expression_laugh", "大笑");
        //脸型
        kvMap.put("square", "正方形");
        kvMap.put("triangle", "三角形");
        kvMap.put("oval", "椭圆");
        kvMap.put("heart", "心形");
        kvMap.put("round", "圆形");
        //性别
        kvMap.put("male", "男性");
        kvMap.put("female", "女性");
        //是否带眼镜
        kvMap.put("none", "无眼镜");
        kvMap.put("common", "普通眼镜");
        kvMap.put("sun", "墨镜");
        //人种
        kvMap.put("yellow", "黄种人");
        kvMap.put("white", "白种人");
        kvMap.put("black", "黑种人");
        kvMap.put("arabs", "阿拉伯人");
        //真实人脸/卡通人脸
        kvMap.put("human", "真实人脸");
        kvMap.put("cartoon", "卡通人脸");
    }

}
