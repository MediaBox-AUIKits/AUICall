package com.alivc.auimessage.observable;

import com.alivc.auicommon.common.base.base.Consumer;
import com.alivc.auicommon.common.base.util.ThreadUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 通用观察者模式对象<hr>
 * 添加该通用类, 是为了弥补java和android系统包中默认观察者对象的不足<br>
 * 1. Java包中的{@link java.util.Observable}不支持泛型<br>
 * 2. Android包中的{@link android.database.Observable}检查模式太严格, 会直接抛出{@link IllegalArgumentException}<br>
 *
 * @author puke
 * @version 2022/6/13
 */
public class Observable<T> implements IObservable<T> {

    private final List<T> observers = new CopyOnWriteArrayList<>();

    /**
     * 向观察者分发事件
     *
     * @param consumer 具体的分发处理函数
     */
    protected void dispatch(Consumer<T> consumer) {
        if (consumer != null) {
            synchronized (observers) {
                for (T observer : observers) {
                    consumer.accept(observer);
                }
            }
        }
    }

    /**
     * 向观察者分发事件
     *
     * @param consumer 具体的分发处理函数
     */
    protected void dispatchOnUiThread(final Consumer<T> consumer) {
        if (consumer != null) {
            ThreadUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Observable.this.dispatch(consumer);
                }
            });
        }
    }

    /**
     * 注册观察者
     *
     * @param observer 观察者对象
     */
    @Override
    public void register(T observer) {
        if (observer != null) {
            synchronized (observers) {
                if (!observers.contains(observer)) {
                    observers.add(observer);
                }
            }
        }
    }

    /**
     * 取消注册观察者
     *
     * @param observer 观察者对象
     */
    @Override
    public void unregister(T observer) {
        if (observer != null) {
            synchronized (observers) {
                observers.remove(observer);
            }
        }
    }

    /**
     * 取消注册所有的观察者
     */
    @Override
    public void unregisterAll() {
        synchronized (observers) {
            observers.clear();
        }
    }
}
