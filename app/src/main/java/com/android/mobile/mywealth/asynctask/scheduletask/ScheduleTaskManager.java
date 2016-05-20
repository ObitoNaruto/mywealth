package com.android.mobile.mywealth.asynctask.scheduletask;

import android.content.Context;
import android.util.Log;

import com.android.mobile.mywealth.ObitoApplication;
import com.android.mobile.mywealth.storage.sp.APSharedPreferences;
import com.android.mobile.mywealth.storage.sp.SharedPreferencesManager;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class ScheduleTaskManager {
    private static ScheduleTaskManager instance;
    private ScheduledExecutorService executorService;
    private Hashtable<ScheduleTask, ScheduledFuture> hashtable = new Hashtable<ScheduleTask, ScheduledFuture>();

    private static final int WIFI_REFRESH_DURATION = 5;
    private static final int NON_WIFI_REFRESH_DURATION = 15;

    private ScheduleTaskManager(){
        if (executorService == null){
            executorService = Executors.newScheduledThreadPool(2);
        }
    }

    public static ScheduleTaskManager getInstance(){
        if (instance == null){
            instance = new ScheduleTaskManager();
        }
        return instance;
    }

    public static int getInterval(){
        int interval = 0;
        try {
            if (NetworkUtils.isWifi(ObitoApplication.getInstance().getApplicationContext())) {
                interval = WIFI_REFRESH_DURATION;
            } else {
                APSharedPreferences sharedPreferences = SharedPreferencesManager.getInstance(ObitoApplication.getInstance().getApplicationContext(), "userId", Context.MODE_PRIVATE);
                if (sharedPreferences != null) {
                    interval = sharedPreferences.getInt("REFRESH_KEY", NON_WIFI_REFRESH_DURATION);
                } else {
                    interval = NON_WIFI_REFRESH_DURATION;
                }
            }
        } catch (Exception ex) {
            Log.e(ScheduleTaskManager.class.getSimpleName(), ex.toString());
        }
        Log.e("aa", "interval=" + interval);
        return interval;
    }

    /**
     * 延迟一个周期开始启动任务
     * @param task
     */
    public synchronized void addDelay(ScheduleTask task){
        addDelay(task, getInterval());
    }

    public synchronized void addDelay(ScheduleTask task,int refreshSeconds){
        if (task == null || hashtable.containsKey(task)) return;
        if (refreshSeconds != 0){
            hashtable.put(task, executorService.scheduleAtFixedRate(task, refreshSeconds, refreshSeconds, TimeUnit.SECONDS));
        }
    }

    /**
     * 即刻开始启动任务
     * @param task
     */
    public synchronized void add(ScheduleTask task){
        add(task, getInterval());
    }

    public synchronized void add(ScheduleTask task,int refreshSeconds){
        if (task == null || hashtable.containsKey(task)) return;
        if (refreshSeconds != 0){
            hashtable.put(task, executorService.scheduleAtFixedRate(task, 0, refreshSeconds, TimeUnit.SECONDS));
        }
    }

    public synchronized void remove(ScheduleTask task){
        if (task == null) return;
        ScheduledFuture future = hashtable.get(task);
        if (future != null){
            hashtable.remove(task);
            future.cancel(true);
        }
    }

    public synchronized void removeAll(){
        for(ScheduleTask scheduleTask : hashtable.keySet()){
            ScheduledFuture future = hashtable.get(scheduleTask);
            if (future != null){
//                hashtable.remove(scheduleTask);
                future.cancel(true);
            }
        }
        hashtable.clear();
    }

    public void destroy(){
        executorService.shutdown();
        executorService.shutdownNow();
    }

    public static interface ScheduleTask extends Runnable {

    }

    public synchronized void resetScheduleTask(){

        if (executorService != null && hashtable.size()>0){
            Set<ScheduleTask> scheduleTaskList = new HashSet<ScheduleTask>();
            int refreshDuration = getInterval();

            for(ScheduleTask scheduleTask : hashtable.keySet()){
                scheduleTaskList.add(scheduleTask);
            }
            removeAll();
            for(ScheduleTask scheduleTask : scheduleTaskList){
                add(scheduleTask, refreshDuration);
            }
        }
    }
}
