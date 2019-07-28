package com.skyguard.trpc.config;

import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.invoker.TrpcInvoker;
import com.skyguard.trpc.util.PropertyUtil;
import com.skyguard.trpc.valid.CommonRpcValidator;
import org.apache.commons.lang3.StringUtils;

public class ReferenceConfig extends BaseRpcConfig{

    private static TrpcInvoker invoker = new TrpcInvoker();

    private static String enableSecurity = PropertyUtil.getValue(TrpcConfig.ENABLE_SECURITY);


    public <T> T referenceService(Class<T> tClass){

        String token = "";

        if(StringUtils.isEmpty(enableSecurity)||!enableSecurity.equals("false")){
            token = CommonRpcValidator.getToken();
        }


        return invoker.reference(tClass,token);
    }



}
