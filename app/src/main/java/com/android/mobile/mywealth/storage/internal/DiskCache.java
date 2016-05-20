package com.android.mobile.mywealth.storage.internal;

import com.android.mobile.mywealth.asynctask.asynctaskExecutor.AsyncTaskExecutor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xinming.xxm on 2016/5/5.
 */
public abstract class DiskCache {
    /**
     * �����Ŀ¼
     */
    protected String mDirectory;
    /**
     * ��������ռ�
     */
    protected long mMaxsize;
    /**
     * ����Ĵ�С
     */
    protected long mSize;
    /**
     * �洢ӳ��
     */
    protected HashMap<String, Entity> mEntities;
    /**
     * �������
     */
    protected HashMap<String, Set<Entity>> mGroup;
    /**
     * Ԫ����
     */
    protected Meta mMeta;
    /**
     * Meta�Ƿ�����
     */
    private AtomicBoolean mMetaProcessing = new AtomicBoolean();
    /**
     * �Ƿ��ʼ��
     */
    private AtomicBoolean mInited = new AtomicBoolean();

    protected DiskCache() {
        mMetaProcessing.set(false);
        mInited.set(false);
    }

    /**
     * �򿪻���
     */
    public void open() {
        if (mInited.get()) {// �Ѿ���ʼ����
            //"DiskCache", "DiskCache has inited
            return;
        }
        init();// ��ʼ��
        mMeta = new Meta(this);
        mMeta.init();
        mInited.set(true);
    }

    /**
     * �رջ���
     */
    public void close() {
        scheduleMeta();
    }

    /**
     * ��Serializable����ת��byte�ۣݣ�
     * Ȼ�����
     * put(String owner, String group, final String url, final byte[] data,long createTime, long period, String contentType)
     *
     * @param owner
     * @param group
     * @param url
     * @param serializable
     * @param createTime
     * @param period
     * @param contentType
     */
    public void putSerializable(String owner, String group, final String url, final Serializable serializable,
                                long createTime, long period, String contentType) {
        ByteArrayOutputStream bos   =   new   ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new   ObjectOutputStream(bos);
            oos.writeObject(serializable);
            byte[] objBytes = bos.toByteArray();
            this.put(owner, group, url, objBytes, createTime, period, contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bos.close();
                if(oos!=null) {
                    oos.close();
                }
            } catch (IOException e) {
                // can not do anything
            }
        }
    }
    /**
     * ��ӻ�������
     *
     * @param owner
     *            �����ߣ�g+gid����u+uid,����:u10000023213��������������޹ؿ��Դ�null������Ϊ-��
     * @param group
     *            �������������,null:ֻ�ŵ�������,not null:��groupΪkey���ö������,����Ϊ-
     * @param url
     *            ���ݵ�url
     * @param data
     *            ��������
     * @throws CacheException
     */
    public void put(String owner, String group, final String url, final byte[] data,
                    long createTime, long period, String contentType) {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        if (owner != null && owner.equalsIgnoreCase("-")) {
            throw new RuntimeException("owner can't be -");
        }
        if (group != null && group.equalsIgnoreCase("-")) {
            throw new RuntimeException("group can't be -");
        }

        final String key = obtainKey(url);

        Entity entity = new Entity(owner, group, url, 0, data.length, key, createTime, period,
                contentType);

        addEntity(entity);
        AsyncTaskExecutor.getInstance().executeSerially(new Runnable() {
            @Override
            public void run() {
                String path = getDirectory() + File.separatorChar + key;
                try {
                    writeFile(path, data);
                    scheduleMeta();
                } catch (CacheException e) {// ���ʧ��
                }
            }
        });
    }

    /**
     * ��ӵ���
     *
     * @param entity
     *            ʵ��
     */
    private void addGroup(Entity entity) {
        String group = entity.getGroup();
        if (group != null && !group.equalsIgnoreCase("-")) {
            Set<Entity> entitys = mGroup.get(group);
            if (entitys == null) {
                entitys = new HashSet<Entity>();
                mGroup.put(group, entitys);
            }
            entitys.add(entity);
        }
    }

    /**
     * �Ƴ���������
     *
     * @param url
     *            ���ݵ�url
     */
    public void remove(String url) {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
//        PerformanceLog.getInstance().log("diskCache start remove:" + url);
        removeLocalEntity(url);
    }

    /**
     * ����ָ����ɾ���������
     *
     * @param group
     *            ָ���飬����Ϊ-
     */
    public void removeByGroup(String group) {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        // ɾ��ָ��group��������
        if (group != null) {
            if (group.equalsIgnoreCase("-")) {
                throw new RuntimeException("group can't be -");
            }
            Set<Entity> entities = mGroup.get(group);
            if (entities != null) {
                Set<String> urls = new HashSet<String>();
                //FIXME ���Ż�
                for (Entity entity : entities) {
                    urls.add(entity.getUrl());
                }
                for (String url : urls) {
                    removeLocalEntity(url);
//                    PerformanceLog.getInstance().log(
//                        "diskCache start remove group:" + group + " url :[" + url + "]");
                }
            }
        }
    }

    /**
     * ɾ�����ػ�������
     *
     * @param url
     *            ����url
     */
    private void removeLocalEntity(String url) {
        if (url != null) {
            removeEntity(url);
            removeCacheFile(url);
        }
    }

    /**
     * �Ƴ������ض��������
     *
     * @param entity
     *            ʵ��
     */
    private void removeGroup(Entity entity) {
        String group = entity.getGroup();
        if (group != null && !group.equalsIgnoreCase("-")) {
            Set<Entity> entitys = mGroup.get(group);
            if (entitys != null) {
                entitys.remove(entity);
            }
        }
    }

    protected void removeCacheFile(final String url) {
        final String key = obtainKey(url);
        AsyncTaskExecutor.getInstance().executeSerially(new Runnable() {

            @Override
            public void run() {
                String path = getDirectory() + File.separatorChar + key;
                File file = new File(path);
                if(!file.exists()){
                    return;
                }
                boolean ret = file.delete();
                if (!ret) {// ɾ��ʧ��
                }
                scheduleMeta();
//                PerformanceLog.getInstance().log("diskCache finish remove:" + url);
            }
        });
    }

    /**
     * ����õ���byte[]Ϊnull���᷵�� null ����
     * @param owner
     * @param url
     * @return
     * @throws CacheException
     */
    public Serializable getSerializable(String owner, String url) throws CacheException {
        byte[] objBytes = get(owner,url);
        if(objBytes==null) {
            return null;
        }
        InputStream bis   =   new ByteArrayInputStream(objBytes);
        ObjectInputStream ois = null;
        try {
            ois = new   ObjectInputStream(bis);
            return (Serializable)  ois.readObject();
        } catch (StreamCorruptedException e) {
            throw new CacheException(e.getMessage());
        } catch (IOException e) {
            throw new CacheException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new CacheException(e.getMessage());
        } finally {
            try {
                bis.close();
                if(ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                // can not do anything --haitong
            }
        }

    }
    /**
     * ��ȡ��������
     *
     * @param owner
     *            �����ߣ�g+gid|u+uid,������һ������:u10000023213��������������޹ؿ��Դ�null������Ϊ-��
     * @param url
     *            ���ݵ�url
     * @return
     * @throws CacheException
     */
    public byte[] get(String owner, String url) throws CacheException {
        if (!mInited.get()) {
            throw new RuntimeException("DiskCache must call open() before");
        }
        if (owner != null && owner.equalsIgnoreCase("-")) {
            throw new RuntimeException("owner can't be -");
        }
//        PerformanceLog.getInstance().log("diskCache start get:" + url);

        // ���ڻ�����
        if (!containEntity(url))
            return null;
        Entity entity = getEntity(url);
        // ����
        if (entity.expire()) {
            remove(url);
            return null;
        }
        // �����ڸ��û�
        if (!entity.authenticate(owner)) {
            return null;
        }
        entity.use();// ����ʹ�ô���
        String key = obtainKey(entity.getUrl());
        String path = getDirectory() + File.separatorChar + key;
        byte[] data = readFile(path);
        return data;
    }

    /**
     * @return �����С����
     */
    public long getMaxsize() {
        return mMaxsize;
    }

    /**
     * @return ��ǰ��ռ�õĴ�С
     */
    public long getSize() {
        synchronized (mEntities) {
            return mSize;
        }
    }

    /**
     * ��ȡ����������
     *
     * @hide
     * @return
     */
    public int getCacheCount() {
        return mEntities.size();
    }

    /**
     * @param directory
     *            ����Ŀ¼
     */
    protected final void setDirectory(String directory) {
        mDirectory = directory;
        if (mDirectory == null)
            throw new IllegalArgumentException("Not set valid cache directory.");
        File file = new File(mDirectory);
        if (!file.exists() && !file.mkdir()) {
            throw new IllegalArgumentException("An Error occured while  cache directory.");
        } else if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not set valid cache directory.");
        }
    }

    /**
     * @param maxsize
     *            �����µĴ�С
     */
    protected final void setMaxsize(long maxsize) {
        mMaxsize = maxsize;
        if (mMaxsize <= 0) {
            throw new IllegalArgumentException("Not set valid cache size.");
        }
    }

    /**
     * @return ����Ŀ¼
     */
    public String getDirectory() {
        return mDirectory;
    }

    /**
     * ����ͨ��url��ȡ�ļ�·���ķ���
     * */
    public String getFileDirectory(String url) throws CacheException {
        // ���ڻ�����
        if (!containEntity(url))
            return null;
        Entity entity = getEntity(url);
        // ����
        if (entity.expire()) {
            remove(url);
            return null;
        }
        entity.use();// ����ʹ�ô���
        String key = obtainKey(entity.getUrl());
        String path = getDirectory() + File.separatorChar + key;
        return path;
    }

    /**
     * ��ӻ���ʵ��
     *
     * @param entity
     *            ����ʵ��
     */
    void addEntity(Entity entity) {
        synchronized (mEntities) {
            mEntities.put(entity.getUrl(), entity);
            addGroup(entity);
            mSize += entity.getSize();
        }
    }

    /**
     * �Ƴ�ʵ��
     *
     * @param url
     *            ʵ���url
     */
    void removeEntity(String url) {
        synchronized (mEntities) {
            Entity entity = mEntities.get(url);
            if (entity != null) {
                mEntities.remove(url);
                removeGroup(entity);
                mSize -= entity.getSize();
            }
        }
    }

    /**
     * �Ƿ����ʵ��
     *
     * @param url
     *            ʵ���url
     * @return
     */
    boolean containEntity(String url) {
        synchronized (mEntities) {
            return mEntities.containsKey(url);
        }
    }

    /**
     * ��ȡʵ��
     *
     * @param url
     *            ʵ���url
     * @return
     */
    Entity getEntity(String url) {
        synchronized (mEntities) {
            return mEntities.get(url);
        }
    }

    /**
     * �������
     */
    void clear() {
        AsyncTaskExecutor.getInstance().executeSerially(new Runnable() {

            @Override
            public void run() {
                File file = new File(getDirectory());
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files == null)
                        return;

                    for (File f : files) {
                        f.delete();
                    }
                }
            }
        });
    }

    protected String obtainKey(String url) {
        return Integer.toHexString(url.hashCode());
    }

    private byte[] readFile(String path) throws CacheException {
        File file = new File(path);
        FileInputStream inputStream = null;
        if (!file.exists()) {
            throw new CacheException(CacheException.ErrorCode.READ_IO_ERROR,
                    "cache file not found.");
        }
        try {
            inputStream = new FileInputStream(file);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return data;
        } catch (IOException e) {
            throw new CacheException(CacheException.ErrorCode.READ_IO_ERROR, e == null ? ""
                    : e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void writeFile(String path, byte[] data) throws CacheException {
        File file = new File(path);
        FileOutputStream outputStream = null;
        try {
            if (!file.exists() && !file.createNewFile()) {// not found
                throw new CacheException(CacheException.ErrorCode.WRITE_IO_ERROR,
                        "cache file create error.");
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            throw new CacheException(CacheException.ErrorCode.WRITE_IO_ERROR, e == null ? ""
                    : e.getMessage());
        } catch (IOException e) {
            throw new CacheException(CacheException.ErrorCode.WRITE_IO_ERROR, e == null ? ""
                    : e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void scheduleMeta() {
        if (mMetaProcessing.get())
            return;
        mMetaProcessing.set(true);// ���ȴ洢
        AsyncTaskExecutor.getInstance().executeSerially(new Runnable() {

            @Override
            public void run() {
                // �洢meta
                synchronized (mEntities) {
                    mMeta.writeMeta(mEntities);
                }
                mMetaProcessing.set(false);// �洢����ִ�����
            }
        });
    }

    /**
     * ��ʼ��
     */
    protected abstract void init();
}
