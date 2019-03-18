package com.zhangxiao.bus;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

public class Bus {

    /**
     * 非粘性
     * Object-->订阅宿主对象
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private static Map<Object, Map<String, OnPostListener>> sOnPostListenerMap = new HashMap<>();

    /**
     * 粘性
     * Object-->订阅宿主对象
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private static Map<Object, Map<String, OnPostListener>> sStickOnPostListenerMap = new HashMap<>();

    /**
     * 非粘性
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private Map<String, OnPostListener> onPostListenersMap = new HashMap<>();

    /**
     * 粘性
     * String-->eventName
     * OnPostListener-->事件回调
     */
    private Map<String, OnPostListener> stickOnPostListenersMap = new HashMap<>();

    /**
     * 粘性
     * String-->eventName
     * Object-->eventData
     */
    private static Map<String, Object> sLastStickEventMap = new HashMap<>();

    /**
     * 用于切换主线程
     */
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 事件回调接口
     */
    public interface OnPostListener<T> {
        void onPost(T eventData);
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
     * 发布事件
     * @param eventName
     * @param eventData
     * @param onPostListenerMap
     */
    private static void post(final String eventName, final Object eventData, Map<Object, Map<String, OnPostListener>> onPostListenerMap) {
        for (Map.Entry<Object, Map<String, OnPostListener>> objectMapEntry : onPostListenerMap.entrySet()) {
            OnPostListener onPostListener = objectMapEntry.getValue().get(eventName);
            if (onPostListener != null) {
                onPostListener.onPost(eventData);
            }
        }
    }

    /**
     * 订阅事件
     *
     * @param eventName
     * @param listener
     */
    public <T> void subscribe(String eventName, OnPostListener<T> listener) {
        onPostListenersMap.put(eventName, listener);
    }

    /**
     * 订阅粘性事件
     *
     * @param eventName
     * @param listener
     */
    public void subscribeStick(String eventName, OnPostListener listener) {
        stickOnPostListenersMap.put(eventName, listener);
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
