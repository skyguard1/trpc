package com.skyguard.trpc.valid;

import com.skyguard.trpc.util.SHAUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Date;

public class CommonRpcValidator {

    private static final Logger LOG = LoggerFactory.getLogger(CommonRpcValidator.class);

    private static final String AUTH_CODE = "skyguard";

    public static String getToken(){

        String token = "";

        try {
            Date date = new Date();
            long dateTime = date.getTime();
            String time = String.valueOf(dateTime);
            String encryptStr = SHAUtil.encode(time + AUTH_CODE);
            String str = time + ";" + encryptStr;
            token = Base64.getEncoder().encodeToString(str.getBytes("UTF-8"));
        }catch (Exception e){
            LOG.error("get data error",e);
        }

        return token;
    }

    public static boolean isValid(String token){

        try {
            if(StringUtils.isEmpty(token)){
                return false;
            }
            byte[] bytes = Base64.getDecoder().decode(token);
            String str = new String(bytes,"UTF-8");
            String[] arr = str.split(";");
            if(arr.length!=2){
                return false;
            }
            String str1 = arr[0];
            String str2 = str1+AUTH_CODE;
            String code = arr[1];
            String encryptStr = SHAUtil.encode(str2);
            return encryptStr.equals(code);

        }catch (Exception e){
            LOG.error("get data error",e);
        }

        return false;
    }



}
