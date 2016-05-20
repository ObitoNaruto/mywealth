package com.android.mobile.mywealth.notification;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public class InternalMessage {
    private Subscription subscription;
    private Object data;

    private boolean recycleFlag = false;

    public static InternalMessage obtain(Subscription subscription, Object data) {
        InternalMessage message = InternalMessageAllocator.getAllocator().allocate();
        message.subscription = subscription;
        message.data = data;
        return message;
    }

    /**
     * 复用InternalMessage
     */
    public void recycle() {
        InternalMessageAllocator.getAllocator().recycle(this);
    }


    public Subscription getSubscription() {
        return subscription;
    }

    public Object getData() {
        return data;
    }

    static class InternalMessageAllocator {

        private static final String TAG = "InternalMessageAllocator";

        private static InternalMessageAllocator sAllocator;

        private LinkedBlockingQueue<InternalMessage> mMessagePool;

        private InternalMessageAllocator() {
            mMessagePool = new LinkedBlockingQueue<InternalMessage>();
        }

        public static InternalMessageAllocator getAllocator() {
            if (sAllocator == null) {
                sAllocator = new InternalMessageAllocator();
            }
            return sAllocator;
        }

        public InternalMessage allocate() {
            //poll()等价于get，然后remove
            InternalMessage message = mMessagePool.poll();
            if (message == null) {
                message = new InternalMessage();
            } else {
                //设置message复用标识状态，表示可复用
                message.recycleFlag = false;
            }
            return message;
        }

        public void recycle(InternalMessage message) {
            if (!message.recycleFlag) {
                message.recycleFlag = true;
                //复用，加入当前队列中
                mMessagePool.add(message);
            }
        }


    }
}
