package com.heasy.app.core.service;

import com.heasy.app.core.event.Event;
import com.heasy.app.core.service.AbstractService;
import com.heasy.app.core.service.EventService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/12/28.
 */
public class DefaultEventService extends AbstractService implements EventService {
    private EventBus eventBus;
    private ConcurrentHashMap registeredObject = new ConcurrentHashMap();

    @Override
    public void init() {
        registeredObject.clear();
        EventBusBuilder builder = EventBus.builder();
        eventBus = builder.build();
        successInit = true;
    }

    @Override
    public boolean isInit() {
        return successInit;
    }

    @Override
    public void unInit() {
        unregisterAll();

        if(eventBus != null){
            eventBus.clearCaches();
            eventBus.removeAllStickyEvents(); //黏性事件
            eventBus = null;
        }

        successInit = false;
    }

    @Override
    public void postEvent(Event event) {
        eventBus.post(event);
    }

    /**
     * 取消事件：仅限于ThreadMode.PostThread下才可以使用
     * @param event
     */
    @Override
    public void cancelEventDelivery(Event event) {
        eventBus.cancelEventDelivery(event);
    }

    /**
     * 订阅事件
     * @param subscriber
     */
    @Override
    public void register(Object subscriber) {
        if(registeredObject.containsKey(subscriber.getClass().getName())){
            eventBus.unregister(registeredObject.get(subscriber.getClass().getName()));
            registeredObject.remove(subscriber.getClass().getName());
        }

        eventBus.register(subscriber);
        registeredObject.put(subscriber.getClass().getName(), subscriber);
    }

    /**
     * 解除订阅
     * @param subscriber
     */
    @Override
    public void unregister(Object subscriber) {
        registeredObject.remove(subscriber.getClass().getName());
        eventBus.unregister(subscriber);
    }

    @Override
    public void unregisterAll() {
        for(Iterator it=registeredObject.values().iterator(); it.hasNext();){
            eventBus.unregister(it.next());
        }
        registeredObject.clear();
    }

}
