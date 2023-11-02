package com.aliyun.auikits.auicall.controller.single;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.auikits.auicall.model.AUICall1V1Model;

public abstract class BaseCallPanelController {

    protected AUICall1V1Model mCallModel;

    protected View rootView;

    public abstract int getLayoutResId();

    public final AUICall1V1Model getMCallModel() {
        return this.mCallModel;
    }

    public BaseCallPanelController(AUICall1V1Model callModel) {
        this.mCallModel = callModel;
    }

    public View inflate( Context ctx,  View container) {
        View inflate = LayoutInflater.from(ctx).inflate(getLayoutResId(), (ViewGroup) container, false);
        this.rootView = inflate;
        return inflate;
    }

    public final boolean addToContainer( ViewGroup container) {
        if (container == null || this.rootView == null) {
            return false;
        }
        if (container.getChildAt(0) == this.rootView) {
            return true;
        }
        container.removeAllViews();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(this.rootView, lp);
        return true;
    }

    private final boolean contains(ViewGroup parent, View child) {
        int childCount = parent.getChildCount();
        int i = 0;
        while (i < childCount) {
            if (child == parent.getChildAt(i)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public final void removeFromContainer( ViewGroup container) {
        View view;
        if (container == null || (view = this.rootView) == null) {
            return;
        }
        if (contains(container, view)) {
            return;
        }
        container.removeView(this.rootView);
    }

    public void resetState() {
    }
}
