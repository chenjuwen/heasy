package com.heasy.app;

import com.heasy.app.core.configuration.AbstractComponentScanner;
import com.heasy.app.core.service.AbstractService;
import com.heasy.app.core.service.Service;
import com.heasy.app.core.utils.ClassUtil;
import com.heasy.app.core.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫描、加载Service类
 */
public class ServiceScanner extends AbstractComponentScanner<ConcurrentHashMap<String, Service>> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceScanner.class);

    @Override
    public ConcurrentHashMap<String, Service> scan(){
        ConcurrentHashMap<String, Service> serviceMap = new ConcurrentHashMap<String, Service>();

        if(StringUtil.isNotEmpty(getBasePackages())){
            String[] packagesArray = getBasePackages().split(";");

            try {
                List<String> classNameList = ClassUtil.getClassFiles(getContext(), packagesArray);
                List<Class> classList = ClassUtil.filterBySuperclass(classNameList, AbstractService.class);
                for(Class clazz : classList){
                    try {
                        String serviceName = clazz.getSimpleName();
                        if(!serviceMap.containsKey(serviceName)){
                            Service service = (Service)clazz.newInstance();
                            serviceMap.put(serviceName, service);
                            logger.debug(serviceName);
                        }
                    } catch (Exception ex) {
                        logger.error("", ex);
                    }
                }

            }catch (Exception ex){
                serviceMap.clear();
                logger.error(ex.toString());
            }
        }

        return serviceMap;
    }

}
