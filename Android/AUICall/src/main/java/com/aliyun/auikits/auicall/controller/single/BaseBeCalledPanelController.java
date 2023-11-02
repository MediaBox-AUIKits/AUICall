package com.aliyun.auikits.auicall.controller.single;

import com.aliyun.auikits.auicall.model.AUICall1V1Model;

public abstract class BaseBeCalledPanelController extends BaseCallPanelController {

    private String callerId;

    public BaseBeCalledPanelController(AUICall1V1Model callModel) {
        super(callModel);
    }

    public final String getCallerId() {
        return this.callerId;
    }

    public final void setCallerId( String str) {
        this.callerId = str;
    }
}
