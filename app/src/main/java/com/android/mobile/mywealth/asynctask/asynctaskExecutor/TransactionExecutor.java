package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import android.util.Log;

import java.util.ArrayDeque;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class TransactionExecutor {

    static final String TAG = TransactionExecutor.class.getSimpleName();

    final ArrayDeque<Transaction> mTransactions = new ArrayDeque<Transaction>();

    /**
     * ��ǰ����ִ�е�AsyncTask����
     */
    volatile Transaction mActive;

    /**
     * ���һ������Tapeִ�����С�Tapeִ������������ӵ��Ⱥ�˳��ִ������
     *
     * @param transaction
     * @return �����ID
     */
    public String addTransaction(Transaction transaction) {
        synchronized (mTransactions) {
            mTransactions.offer(transaction);
        }
        if (mActive == null) {
            scheduleNext();
        } else {
            Log.v(TAG, "TransactionExecutor.execute(a transaction is running, so don't call scheduleNext())");
        }
        return transaction.id;
    }

    /**
     *
     *
     * @param id
     */
    public void removeTransaction(String id) {
        for (Transaction transaction : mTransactions) {
            if (transaction.id.equals(id)) {
                synchronized (mTransactions) {
                    mTransactions.remove(transaction);
                }
                break;
            }
        }
        if (null != mActive && mActive.id.equals(id)) {
            mActive = null;
        }
        if (mActive == null) {
            scheduleNext();
        } else {
            Log.v(TAG, "TransactionExecutor.execute(a transaction is running, so don't call scheduleNext())");
        }
    }

    /**
     * ִ����һ��AsyncTask
     */
    private void scheduleNext() {
        Transaction transaction;
        synchronized (mTransactions) {
            mActive = mTransactions.poll();
            transaction = mActive;
        }
        if (mActive != null) {
            Log.d(TAG, "TransactionExecutor.scheduleNext()");
            transaction.run();
        } else {
            Log.d(TAG, "TransactionExecutor.scheduleNext(mTransactions is empty)");
        }
    }

    /**
     * �ر�ִ����
     */
    public void shutdown() {
        mTransactions.clear();
    }
}
