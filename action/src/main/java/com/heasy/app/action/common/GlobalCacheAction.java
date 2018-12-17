package com.heasy.app.action.common;

import com.alibaba.fastjson.JSONObject;
import com.heasy.app.action.ActionNames;
import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.annotation.JSActionAnnotation;
import com.heasy.app.core.datastorage.DataCache;
import com.heasy.app.core.utils.FastjsonUtil;
import com.heasy.app.core.webview.Action;

/**
 * 全局参数缓存设置
 */
@JSActionAnnotation(name = ActionNames.GlobalCache)
public class GlobalCacheAction implements Action {
    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        String key = FastjsonUtil.getString(jsonObject, "key");
        String value = FastjsonUtil.getString(jsonObject, "value");

        DataCache cache = heasyContext.getServiceEngine().getDataService().getGlobalMemoryDataCache();

        if("get".equalsIgnoreCase(extend)){
            return cache.get(key) == null ? "" : (String)cache.get(key);
        }else if("set".equalsIgnoreCase(extend)){
            cache.set(key, value);
        }else if("add".equalsIgnoreCase(extend)){
            cache.add(key, value);
        }else if("del".equalsIgnoreCase(extend)){
            cache.decr(key);
        }

        return SUCCESS;
    }

}
