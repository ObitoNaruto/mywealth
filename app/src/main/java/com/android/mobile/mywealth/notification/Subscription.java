package com.android.mobile.mywealth.notification;

import java.lang.ref.WeakReference;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class Subscription {
    private final WeakReference<ISubscriberCallback> mSubscribeCallback;

    private boolean mValidTag = true;

    public Subscription(ISubscriberCallback callback) {
        if (callback == null) {
            throw new NullPointerException();
        }
        this.mSubscribeCallback = new WeakReference<ISubscriberCallback>(callback);
    }

    public boolean invoke(Object data) {
        if (data != null) {
            ISubscriberCallback callback = mSubscribeCallback.get();
            if (callback != null && mValidTag) {
                callback.onDataChanged(data);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isInvokable() {
        return mSubscribeCallback.get() != null;
    }

    public int hashCode() {
        return mSubscribeCallback.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Subscription) {
            return (mSubscribeCallback.get() == ((Subscription) o).mSubscribeCallback.get());
        }
        return false;
    }

    void invaliate() {
        mValidTag = false;
    }

    public String toString() {
        if (mSubscribeCallback.get() == null) {
            return mSubscribeCallback.toString() + "@" +"NoSubscription";
        } else {
            return mSubscribeCallback.toString() + "@" + mSubscribeCallback.get().getClass().getSimpleName();
        }
    }
}
