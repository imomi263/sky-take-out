package com.sky.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;


@Slf4j
public class JwtUtil {
    /*
        生成jwt
        使用Hs256算法，私钥使用固定密钥
        @param secretKey jwt秘钥
        @param ttlMillis jwt过期时间(毫秒)
        @param claims    设置的信息
        @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String,Object> claims){
        // 使用的加密算法
        SignatureAlgorithm signatureAlgorithm=SignatureAlgorithm.HS256;
        long expMills=System.currentTimeMillis()+ttlMillis;
        Date exp=new Date(expMills);

        JwtBuilder builder= Jwts.builder()
                // 2. payload: 如果有私有声明，一定要先设置这个自己创建的私有的声明
                // 这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                .signWith( signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(exp);
        //compact() 方法是 jjwt 库中的 JwtBuilder 类的一个方法，用于生成最终的 JWT 字符串。
        //这个方法会将你之前设置的所有参数（如头部、载荷和签名）组合成一个完整的 JWT 字符串，并返回这个字符串。
        return builder.compact();

    }

    /*
        Token解析
        @param secretKey jwt秘钥
        @param token     加密后的token
        @return
     */
    public static Claims parseJWT(String secretKey, String token){
        //log.info("来到这里的校验token是否一致");
        Claims claims=Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }
}
