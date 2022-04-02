package com.example.backend.S3ImageUpload;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class GenerateHash {
    //비디오 저장할 때, 원본 이름과 다르게 MD5로 hash값 생성해서 저장
    private String hash=null;
    public GenerateHash(String input){


        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            SecureRandom rand=new SecureRandom();
            byte[] saltBytes=new byte[16];
            rand.nextBytes(saltBytes);
            String salt=new String(saltBytes);
            String msg=input+salt;

            digest.update(msg.getBytes(StandardCharsets.UTF_8));
            byte[] hashByte=digest.digest();
            StringBuilder builder = new StringBuilder();
            for(byte b : hashByte) {
                String hexString = String.format("%02x", b);
                builder.append(hexString);
            }
            hash = builder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String out(){
        return hash;
    }
}
