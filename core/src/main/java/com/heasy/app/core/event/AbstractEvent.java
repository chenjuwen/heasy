package com.heasy.app.core.event;

/**
 * Created by Administrator on 2017/12/28.
 */
public abstract class AbstractEvent implements Event {
    private Object source;

    public AbstractEvent(Object source){
        this.source = source;
    }

    @Override
    public Object getSource() {
        return source;
    }

}
