package com.android.mobile.mywealth.asynctask.asynctaskExecutor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事务：使用TransactionExecutor去执行
 * Created by xinming.xxm on 2016/5/6.
 */
public abstract class Transaction implements Runnable{
    private static final AtomicInteger sCount = new AtomicInteger(0);

    final String id = "Transaction_" + sCount.getAndIncrement();

    public final String getId() {
        return id;
    }
}
