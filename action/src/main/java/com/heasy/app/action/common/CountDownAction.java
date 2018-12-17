package com.heasy.app.action.common;

import android.os.CountDownTimer;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.utils.ParameterUtil;
import com.heasy.app.core.webview.Action;
import com.heasy.app.action.ActionNames;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 倒计时
 */
@JSActionAnnotation(name = ActionNames.CountDown)
public class CountDownAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(CountDownAction.class);

    //参数
    public static final String p_millisInFuture = "millisInFuture"; //倒计时总秒数
    public static final String p_content = "content"; //倒计时显示的内容信息
    public static final String p_elementId = "elementId"; //显示内容信息的控件ID
    public static final String p_forwardPage = "forwardPage"; //超时后跳转到的目标页面
    public static final String p_isTimeoutPage = "isTimeoutPage"; //当前页面是否是超时后的过渡页面

    private static CountDownTimer countDownTimer;

    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        CountDownAction.cancel();

        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        final int millisInFuture = Integer.parseInt(FastjsonUtil.getString(jsonObject, p_millisInFuture));
        final String m_content = FastjsonUtil.getString(jsonObject, p_content);
        final String elementId = FastjsonUtil.getString(jsonObject, p_elementId);
        final String forwardPage = FastjsonUtil.getString(jsonObject, p_forwardPage);
        final String isTimeoutPage = FastjsonUtil.getString(jsonObject, p_isTimeoutPage);

        final long countDownInterval = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getCountDownInterval();
        final String countDownTimeoutPage = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getCountDownTimeoutPage();

        countDownTimer = new CountDownTimer(millisInFuture + 1050, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                String remainingSeconds = String.valueOf(millisUntilFinished / countDownInterval - 1);
                String content = m_content.replace("#SECONDS", remainingSeconds);

                String jsonData = FastjsonUtil.toJSONString(p_elementId, elementId, p_content, content);
                heasyContext.getJsInterface().executeFunction(ActionNames.CountDown, jsonData);
            }

            @Override
            public void onFinish() {
                CountDownAction.cancel();

                if("1".equals(isTimeoutPage)){
                    //跳转到目标页面
                    heasyContext.getJsInterface().pageTransfer(forwardPage, null);
                }else {
                    //跳转到超时过渡页面
                    String parameters = ParameterUtil.toParamString(p_forwardPage, forwardPage);
                    heasyContext.getJsInterface().pageTransfer(countDownTimeoutPage, parameters);
                }
            }
        }.start();

        return null;
    }

    public static void cancel(){
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
