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
     * ����InternalMessage
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
            //poll()�ȼ���get��Ȼ��remove
            InternalMessage message = mMessagePool.poll();
            if (message == null) {
                message = new InternalMessage();
            } else {
                //����message���ñ�ʶ״̬����ʾ�ɸ���
                message.recycleFlag = false;
            }
            return message;
        }

        public void recycle(InternalMessage message) {
            if (!message.recycleFlag) {
                message.recycleFlag = true;
                //���ã����뵱ǰ������
                mMessagePool.add(message);
            }
        }


    }
}
