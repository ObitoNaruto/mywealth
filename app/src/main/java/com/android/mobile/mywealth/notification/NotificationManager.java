package com.android.mobile.mywealth.notification;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class NotificationManager {

    private static final String TAG = NotificationManager.class.getSimpleName();
    private final Handler mHandler;
    private ConcurrentHashMap<String, List<Subscription>> mSubscriptionMap = new ConcurrentHashMap<String, List<Subscription>>();

    private static NotificationManager sInstance;
    private NotificationManager() {
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if (msg.obj instanceof InternalMessage) {
                        InternalMessage data = (InternalMessage) msg.obj;
                        data.getSubscription().invoke(data.getData());
                        data.recycle();
                    }
                }
            }
        };
    }

    public static NotificationManager getInstance() {
        if (sInstance == null) {
            sInstance = new NotificationManager();
        }
        return sInstance;
    }

    /**
     * 内部函数，不对外开放
     * @param target 待注册的bean类对象
     * @param tag 标识，用于同一Fragment复用为多个业务的场景
     * @param subscription
     * @return
     */
    private boolean subscribe(Class<?> target, String tag, Subscription subscription) {
        synchronized (this) {
            if (subscription == null) {
                return false;
            }

            String name;
            if (target != null) {
                name = target.getName();
                if (!TextUtils.isEmpty(tag)) {
                    name = name + "#" + tag;
                }
            } else if (!TextUtils.isEmpty(tag)){
                name = "#" + tag;
            } else {
                return false;
            }

            if (mSubscriptionMap.containsKey(name)) {
                List<Subscription> list = mSubscriptionMap.get(name);
                if (!list.contains(subscription)) {
                    list.add(subscription);
                }
            } else {
                List<Subscription> list = new CopyOnWriteArrayList<Subscription>();
                list.add(subscription);
                mSubscriptionMap.put(name, list);
            }
            return true;
        }
    }

    /**
     *对外提供的注册接口
     * @param target 待注册的bean类对象
     * @param callback 回调接口
     * @return
     */
    public boolean subscribe(Class<?> target, ISubscriberCallback callback) {
        if (target == null || callback == null) {
            return false;
        }
        Subscription subscription = new Subscription(callback);
        return subscribe(target, null, subscription);
    }

    public boolean subscribe(Class<?> target, String tag, ISubscriberCallback callback) {
        if (callback == null) {
            return false;
        }
        Subscription subscription = new Subscription(callback);
        return subscribe(target, tag, subscription);
    }

    private void unSubscribe(Class<?> target, String tag, Subscription subscription) {
        synchronized (this) {
            if (subscription == null) {
                return;
            }

            String name;
            if (target != null) {
                name = target.getName();
                if (!TextUtils.isEmpty(tag)) {
                    name = name + "#" + tag;
                }
            } else if (!TextUtils.isEmpty(tag)){
                name = "#" + tag;
            } else {
                return ;
            }

            if (mSubscriptionMap.containsKey(name)) {
                List<Subscription> list = mSubscriptionMap.get(name);
                int index = list.indexOf(subscription);
                if (index >= 0) {
                    Subscription origin = list.remove(index);
                    if (origin != null) {
                        origin.invaliate();
                    }
                    if (list.isEmpty()) {
                        mSubscriptionMap.remove(name);
                    }
                }
            }
        }
    }

    public void unSubscribe(Class<?> target, ISubscriberCallback callback) {
        if (target == null || callback == null) {
            return ;
        }
        Subscription subscription = new Subscription(callback);
        unSubscribe(target, null, subscription);
    }

    public void unSubscribe(Class<?> target, String tag, ISubscriberCallback callback) {
        if (callback == null) {
            return ;
        }
        Subscription subscription = new Subscription(callback);
        unSubscribe(target, tag, subscription);
    }

    public void post(final Object data) {

        if (data == null) {
            return ;
        }

        final String name = data.getClass().getName();

        post(name, data);

    }

    public void post(final Object data, String tag) {

        if (data == null) {
            return ;
        }

        String className = data.getClass().getName();
        if (!TextUtils.isEmpty(tag)) {
            className = className + "#" + tag;
        }
        final String name = className;

        post(name, data);

    }

    public void post(final Object data, String tag, boolean onlyTag) {
        if (data == null) {
            return ;
        }

        if (!onlyTag) {
            post(data, tag);
        } else {
            if (!TextUtils.isEmpty(tag)) {
                String name = "#" + tag;
                post(name, data);
            }
        }

    }

    private void post(String name, Object data) {
        List<Subscription> list = mSubscriptionMap.get(name);
        if (list == null) return ;
        for (Subscription subscription : list) {
            if (!subscription.isInvokable()) {
                list.remove(subscription);
            } else {
                InternalMessage internalMessage = InternalMessage.obtain(subscription, data);
                mHandler.sendMessage(Message.obtain(mHandler, 0, internalMessage));
            }
        }
    }
}
