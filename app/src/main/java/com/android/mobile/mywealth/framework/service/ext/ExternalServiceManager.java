package com.android.mobile.mywealth.framework.service.ext;

import com.android.mobile.mywealth.framework.service.CommonService;
import com.android.mobile.mywealth.framework.service.ServiceDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class ExternalServiceManager extends CommonService{
    /**
     * ע����չ����
     */
    public abstract void registerExtnernalService(ServiceDescription serviceDescription);

    /**
     * ��ȡ��չ����
     * @param className ����ӿ���
     * @return
     */
    public abstract ExternalService getExternalService(String className);

    /**
     * ����ext����
     * @param description
     * @return
     */
    public abstract boolean createExternalService(ServiceDescription description);

    /**
     * ע��ext����ֻregister��������
     * @param description
     */
    public abstract void registerExternalServiceOnly(ServiceDescription description);
}
