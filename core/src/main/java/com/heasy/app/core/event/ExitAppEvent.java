package com.heasy.app.core.event;

/**
 * Created by Administrator on 2017/12/28.
 */
public class ExitAppEvent extends AbstractEvent {
    private String message;

    public ExitAppEvent(Object source){
        super(source);
    }

    public ExitAppEvent(Object source, String message){
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
