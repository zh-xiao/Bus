package com.zhangxiao.bus;

import android.os.Handler;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池工具类
 * create by zhangxiao on 2019年4月10日15:49:42
 */
public class ThreadPool {

    private static Handler sHandler = new Handler();

    private static Map<Object, List<Future>> sFutureMap = new HashMap<>();
    /**
     * fixed线程池,固定线程数为cpu核心数
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 通过任务标签获取Futures
     *
     * @param tag
     * @return
     */
    private static List<Future> getFuturesByTag(Object tag) {
        List<Future> futures = sFutureMap.get(tag);
        if (futures == null) {
            futures = new ArrayList<>();
            sFutureMap.put(tag, futures);
        }
        return futures;
    }

    /**
     * 添加runnable到线程池
     */
    public static void runOnSubThread(Object tag, Runnable runnable) {
        Future<?> future = executorService.submit(runnable);
        getFuturesByTag(tag).add(future);
    }

    /**
     * 主线程执行任务
     *
     * @param tag      任务标签
     * @param runnable
     */
    public static void runOnUiThread(Object tag, Runnable runnable) {
        sHandler.postAtTime(runnable, tag, SystemClock.uptimeMillis());
    }

    /**
     * 主线程执行任务
     *
     * @param tag         任务标签
     * @param runnable
     * @param delayMillis 延迟执行
     */
    public static void runOnUiThread(Object tag, Runnable runnable, long delayMillis) {
        sHandler.postAtTime(runnable, tag, SystemClock.uptimeMillis() + delayMillis);
    }

    /**
     * 取消任务
     *
     * @param tag
     */
    public static void cancel(Object tag) {
        //取消主线程任务
        sHandler.removeCallbacksAndMessages(tag);

        //取消子线程任务
        for (Future future : getFuturesByTag(tag)) {
            future.cancel(true);
        }

    }

}
