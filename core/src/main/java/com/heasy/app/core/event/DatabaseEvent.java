package com.heasy.app.core.event;

/**
 * Created by Administrator on 2018/2/4.
 */
public class DatabaseEvent extends AbstractEvent {
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";

    private String opType;
    private String resultCode;
    private String resultDesc;

    public DatabaseEvent(Object source){
        super(source);
    }

    public DatabaseEvent(Object source, String opType, String resultCode, String resultDesc){
        super(source);
        this.opType = opType;
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

}
