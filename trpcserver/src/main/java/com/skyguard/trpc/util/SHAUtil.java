package com.skyguard.trpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class SHAUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SHAUtil.class);

    public static String encode(String str){
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        }catch(Exception e){
            LOG.error("get message error",e);
        }

        byte[] bytes = str.getBytes(Charset.forName("utf-8"));
        byte[] data = messageDigest.digest(bytes);

        StringBuilder result = new StringBuilder();

        for(int i=0;i<data.length;i++){
            String data1 = String.format("%02x",data[i]);
            result.append(data1);
        }

        return result.toString();
    }




}
