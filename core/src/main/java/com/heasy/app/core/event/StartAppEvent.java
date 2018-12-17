package com.heasy.app.core.event;

/**
 * Created by Administrator on 2017/12/28.
 */
public class StartAppEvent extends AbstractEvent {
    private String message;

    public StartAppEvent(Object source){
        super(source);
    }

    public StartAppEvent(Object source, String message){
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
