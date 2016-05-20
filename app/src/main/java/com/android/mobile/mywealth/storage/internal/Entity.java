package com.android.mobile.mywealth.storage.internal;

import java.text.SimpleDateFormat;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class Entity {
    /**
     * �����ߣ�"-"����������;"g+gid"�����У�����"g001";"u+uid"�������У�����"u001"
     */
    private String mOwner;
    /**
     * ��
     */
    protected String mGroup;
    /**
     * Url
     */
    private String mUrl;
    /**
     * ʹ�ô���
     */
    private int mUsedTime;
    /**
     * ��С
     */
    private long mSize;
    /**
     * �洢�����·��
     */
    private String mPath;
    /**
     * ����ʱ��
     */
    private long mCreateTime;
    /**
     * ��Ч����
     */
    private long mPeriod;
    /**
     * ��������
     */
    private String mContentType;

    public Entity(String owner, String group, String url, int usedTime, long size, String path,
                  long createTime, long period, String contentType) {
        if (owner == null) {
            mOwner = "-";
        } else {
            mOwner = owner;
        }
        if (group == null) {
            mGroup = "-";
        } else {
            mGroup = group;
        }
        if (url == null){
            mUrl = "";
        }else{
            mUrl = url;
        }
        mUsedTime = usedTime;
        mSize = size;

        if (path == null){
            mPath = "";
        }else{
            mPath = path;
        }

        mCreateTime = createTime;
        mPeriod = period;

        if ( contentType == null){
            mContentType = "";
        }else{
            mContentType = contentType;
        }
    }

    /**
     * �����ߣ�"-"����������;"g+gid"�����У�����"g001";"u+uid"�������У�����"u001"
     */
    public String getOwner() {
        return mOwner;
    }

    /**
     * @return ��
     */
    public String getGroup() {
        return mGroup;
    }

    /**
     * @return Url
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return ʹ�ô���
     */
    public int getUsedTime() {
        return mUsedTime;
    }

    /**
     * ʹ��
     */
    public void use() {
        mUsedTime++;
    }

    /**
     * @return ��С
     */
    public long getSize() {
        return mSize;
    }

    /**
     * @return �洢�����·��
     */
    public String getPath() {
        return mPath;
    }

    /**
     * @return ����ʱ��
     */
    public long getCreateTime() {
        return mCreateTime;
    }

    /**
     * @return ��Ч����
     */
    public long getPeriod() {
        return mPeriod;
    }

    /**
     * @return ��������
     */
    public String getContentType() {
        return mContentType;
    }

    /**
     * @return �����Ƿ����
     */
    public boolean expire() {
        return mCreateTime + mPeriod *1000< System.currentTimeMillis();
    }

    /**
     * ��Ȩ
     *
     * @param owner ��Ȩ���� �����ʶ|�û���ʶ��g001|u001��������һ
     * @return �Ƿ�Ϸ�
     */
    public boolean authenticate(String owner) {
        if (mOwner.equalsIgnoreCase("-")) {
            return true;
        }

        if (owner == null) {
            return false;
        } else if (owner.contains(mOwner)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String
                .format(
                        "url: %s usedTime: %d size: %d path: %s createTime:%s period: %d content-type: %s owner: %s",
                        mUrl, mUsedTime, mSize, mPath, sdf.format(mCreateTime), mPeriod, mContentType,
                        mOwner);
    }
}
