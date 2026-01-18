package com.paiad.smartagriculture.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtil {
    private static final String SECRET = "smart-agriculture-secret-key";
    private static final long EXPIRE_TIME = 3600 * 1000; // 1小时

    public static String createToken(Integer userId) {
        return JWT.create()
                .withClaim("id", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    public static Integer getUserId(String token) {
        return verify(token).getClaim("id").asInt();
    }
}
