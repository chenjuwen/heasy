package com.heasy.app;

import android.content.Context;

import com.heasy.app.core.HeasyContext;
import com.heasy.app.core.service.ConfigurationService;
import com.heasy.app.core.service.DataService;
import com.heasy.app.core.service.DefaultConfigurationService;
import com.heasy.app.core.service.DefaultDataService;
import com.heasy.app.core.service.DefaultEventService;
import com.heasy.app.core.service.EventService;
import com.heasy.app.core.service.Service;
import com.heasy.app.core.service.ServiceEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/12/29.
 */
class ServiceEngineImpl implements ServiceEngine {
    private static Logger logger = LoggerFactory.getLogger(ServiceEngineImpl.class);
    private Context androidContext;
    private HeasyContext heasyContext;

    private ConfigurationService configurationService;
    private EventService eventService;
    private DataService dataService;

    private ConcurrentHashMap<String, Service> serviceMap = new ConcurrentHashMap<>();

    @Override
    public Context getAndroidContext() {
        return androidContext;
    }

    @Override
    public void setAndroidContext(Context androidContext) {
        this.androidContext = androidContext;
    }

    @Override
    public HeasyContext getHeasyContext() {
        return heasyContext;
    }

    @Override
    public void setHeasyContext(HeasyContext heasyContext) {
        this.heasyContext = heasyContext;
        this.heasyContext.setServiceEngine(this);
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        String serviceName = clazz.getSimpleName();
        if(serviceMap.containsKey(serviceName)){
            return (T)serviceMap.get(serviceName);
        }
        return null;
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public DataService getDataService() {
        return dataService;
    }

    @Override
    public void open() {
        this.configurationService = new DefaultConfigurationService();
        this.eventService = new DefaultEventService();
        this.dataService = new DefaultDataService();

        this.configurationService.setHeasyContext(getHeasyContext());
        this.eventService.setHeasyContext(getHeasyContext());
        this.dataService.setHeasyContext(getHeasyContext());

        this.configurationService.init();
        this.eventService.init();
        this.dataService.init();

        serviceMap.put(this.configurationService.getClass().getSimpleName(), this.configurationService);
        serviceMap.put(this.eventService.getClass().getSimpleName(), this.eventService);
        serviceMap.put(this.dataService.getClass().getSimpleName(), this.dataService);

        loadExtendService();

        for(Service service : serviceMap.values()){
            service.setHeasyContext(getHeasyContext());
            if(!service.isInit()) {
                service.init();
            }
        }
    }

    private void loadExtendService(){
        String serviceBasePackage = this.configurationService.getConfigBean().getServiceBasePackage();
        ServiceScanner scanner = new ServiceScanner();
        scanner.setContext(getHeasyContext().getServiceEngine().getAndroidContext());
        scanner.setBasePackages(serviceBasePackage);
        serviceMap = scanner.scan();
    }

    @Override
    public void close() {
        for(Service service : serviceMap.values()){
            service.unInit();
        }
        serviceMap.clear();

        this.androidContext = null;
        this.heasyContext = null;
    }

}
