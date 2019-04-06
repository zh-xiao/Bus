package com.zhangxiao.bus;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Bus {

    /**
     * 用于切换子线程
     */
    private static Executor sExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 用于切换主线程
     */
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 标记事件回调在主线程还是子线程
     */
    private enum Scheduler{
        mainThread,
        subThread
    }

    /**
     * 非粘性
     * Object-->订阅宿主对象
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private static Map<Object, Map<String, OnPostListenerWrap>> sOnPostListenerMap = new HashMap<>();

    /**
     * 粘性
     * Object-->订阅宿主对象
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private static Map<Object, Map<String, OnPostListenerWrap>> sStickOnPostListenerMap = new HashMap<>();

    /**
     * 非粘性
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private Map<String, OnPostListenerWrap> onPostListenersMap = new HashMap<>();

    /**
     * 粘性
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private Map<String, OnPostListenerWrap> stickOnPostListenersMap = new HashMap<>();

    /**
     * 粘性
     * String-->eventName
     * Object-->eventData
     */
    private static Map<String, Object> sLastStickEventMap = new HashMap<>();

    /**
     * 事件回调接口
     */
    public interface OnPostListener<T> {
        void onPost(T eventData);
    }

    /**
     * OnPostListener的包装类,支持事件回调线程的标记
     * @param <T>
     */
    public class OnPostListenerWrap<T>{
        OnPostListener<T> onPostListener;
        Scheduler scheduler;

        public OnPostListenerWrap(OnPostListener<T> onPostListener,Scheduler scheduler){
            this.onPostListener=onPostListener;
            this.scheduler=scheduler;
        }
    }

    /**
     * 注册
     *
     * @param subscriber
     */
    public void regist(Object subscriber) {
        sOnPostListenerMap.put(subscriber, onPostListenersMap);
        sStickOnPostListenerMap.put(subscriber, stickOnPostListenersMap);
    }

    /**
     * 取消注册
     *
     * @param subscriber
     */
    public static void unRegist(Object subscriber) {
        sOnPostListenerMap.remove(subscriber);
        sStickOnPostListenerMap.remove(subscriber);
    }

    /**
     * 发布非粘性事件
     *
     * @param eventName
     * @param eventData
     */
    public static void post(final String eventName, final Object eventData) {
        if (eventName == null || eventData == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            post(eventName,eventData,sOnPostListenerMap);
        } else {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    post(eventName,eventData,sOnPostListenerMap);
                }
            });
        }
    }

    /**
     * 发布粘性事件
     *
     * @param eventName
     * @param eventData
     */
    public static void postStick(final String eventName, final Object eventData) {
        if (eventName == null || eventData == null) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            post(eventName,eventData,sStickOnPostListenerMap);
        } else {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    post(eventName,eventData,sStickOnPostListenerMap);
                }
            });
        }
        sLastStickEventMap.put(eventName, eventData);
    }

    /**
     * 判断是否主线程
     * @return
     */
    private static boolean isMainThread(){
        return Looper.myLooper()==Looper.getMainLooper();
    }

    /**
     * 发布事件
     * @param eventName
     * @param eventData
     * @param onPostListenerMap
     */
    private static void post(final String eventName, final Object eventData, Map<Object, Map<String, OnPostListenerWrap>> onPostListenerMap) {
        for (Map.Entry<Object, Map<String, OnPostListenerWrap>> objectMapEntry : onPostListenerMap.entrySet()) {
            final OnPostListenerWrap onPostListenerWrap = objectMapEntry.getValue().get(eventName);
            if (onPostListenerWrap != null) {
                /*******************当前线程和期望线程不一致begin*****************/
                if (onPostListenerWrap.scheduler==Scheduler.subThread&&isMainThread()){
                    sExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            onPostListenerWrap.onPostListener.onPost(eventData);
                        }
                    });
                    return;
                }
                if (onPostListenerWrap.scheduler==Scheduler.mainThread&&!isMainThread()){
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPostListenerWrap.onPostListener.onPost(eventData);
                        }
                    });
                    return;
                }
                /*******************当前线程和期望线程不一致end*****************/
                onPostListenerWrap.onPostListener.onPost(eventData);
            }
        }
    }

    /**
     * 订阅事件,事件回调在主线程
     * @param eventName
     * @param listener
     * @param <T>
     */
    public <T> void subscribeOnMainThread(String eventName, OnPostListener<T> listener) {
        onPostListenersMap.put(eventName, new OnPostListenerWrap(listener,Scheduler.mainThread));
    }

    /**
     * 订阅事件,事件回调在子线程
     * @param eventName
     * @param listener
     * @param <T>
     */
    public <T> void subscribeOnSubThread(String eventName, OnPostListener<T> listener) {
        onPostListenersMap.put(eventName, new OnPostListenerWrap(listener,Scheduler.subThread));
    }

    /**
     * 订阅粘性事件,事件回调在主线程
     * @param eventName
     * @param listener
     */
    public void subscribeStickOnMainThread(String eventName, OnPostListener listener) {
        stickOnPostListenersMap.put(eventName, new OnPostListenerWrap(listener,Scheduler.mainThread));
        postStick(eventName, sLastStickEventMap.get(eventName));
    }

    /**
     * 订阅粘性事件,事件回调在子线程
     * @param eventName
     * @param listener
     */
    public void subscribeStickOnSubThread(String eventName, OnPostListener listener) {
        stickOnPostListenersMap.put(eventName, new OnPostListenerWrap(listener,Scheduler.subThread));
        postStick(eventName, sLastStickEventMap.get(eventName));
    }

    /**
     * 移除单个粘性事件
     *
     * @param eventName
     */
    public static void removeStickEvent(String eventName) {
        sLastStickEventMap.remove(eventName);
    }

    /**
     * 移除全部的粘性事件
     */
    public static void removeAllStickEvent() {
        sLastStickEventMap.clear();
    }

}
