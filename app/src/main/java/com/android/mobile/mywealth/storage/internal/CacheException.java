package com.android.mobile.mywealth.storage.internal;

/**
 * Created by xinming.xxm on 2016/5/6.
 */
public class CacheException extends Exception{
    /**
     * �־û����쳣������
     *
     * @author sanping.li@alipay.com
     *
     */
    public static  enum ErrorCode{
        /**
         * дIO����
         */
        WRITE_IO_ERROR(0),
        /**
         * ��IO����
         */
        READ_IO_ERROR(1);

        private ErrorCode(int value) {
            mCode = value;
        }

        private int mCode;

        public int getValue() {
            return mCode;
        }
    }
    /**
     * �쳣��
     */
    private ErrorCode mCode;
    /**
     * �쳣��Ϣ
     */
    private String mMsg;
    /**
     * @param msg �쳣��Ϣ
     */
    public CacheException(String msg) {
        super(msg);
        mCode = ErrorCode.WRITE_IO_ERROR;
        mMsg = msg;
    }

    /**
     * @param code �쳣��
     * @param msg �쳣��Ϣ
     */
    public CacheException(ErrorCode code, String msg) {
        super(format(code, msg));
        mCode = code;
        mMsg = msg;
    }

    /**
     * �@ȡ�e�`�a
     *
     * @return �e�`�a
     */
    public ErrorCode getCode() {
        return mCode;
    }

    /**
     * �@ȡ�e�`��Ϣ
     *
     * @return �e�`��Ϣ
     */
    public String getMsg() {
        return mMsg;
    }

    private static String format(ErrorCode code, String message) {
        StringBuilder str = new StringBuilder();
        str.append("Cache error");
        if (code != null) {
            str.append("[").append(code.getValue()).append("]");
        }
        str.append(" : ");
        if (message != null) {
            str.append(message);
        }
        return str.toString();
    }
}
